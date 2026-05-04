import { inject, Injectable, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, finalize, forkJoin, map, Observable, of, tap } from 'rxjs';
import { LoginRequest, LoginResponse, LoginResult, LoginService } from '../../services/login.service';
import { Person } from '../../models/person.model';
import { Professor } from '../../models/professor.model';
import { Student } from '../../models/student.model';
import { PersonService } from '../../services/person.service';
import { ProfessorService } from '../../services/professor.service';
import { StudentService } from '../../services/student.service';
import { TokenService } from '../../services/token.service';

interface AuthSnapshot {
  isAuthenticated: boolean;
  role: string | null;
  userId: string | null;
  email: string | null;
}

const STORAGE_KEY = 'demo-app-auth';

@Injectable({ providedIn: 'root' })
export class LoginStore {
  private readonly loginService = inject(LoginService);
  private readonly tokenService = inject(TokenService);
  private readonly personService = inject(PersonService);
  private readonly studentService = inject(StudentService);
  private readonly professorService = inject(ProfessorService);

  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly isAuthenticated = signal(false);
  readonly role = signal<string | null>(null);
  readonly userId = signal<string | null>(null);
  readonly email = signal<string | null>(null);

  constructor() {
    this.restoreAuthState();
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    this.errorMessage.set(null);
    this.isSubmitting.set(true);
    this.email.set(request.email.trim());

    return this.loginService.login(request).pipe(
      tap((result) => this.applyLoginResult(result)),
      map((result) => result.body),
      catchError((error: unknown) => {
        const response = this.normalizeError(error);
        this.applyResponse(response);
        return of(response);
      }),
      finalize(() => this.isSubmitting.set(false)),
    );
  }

  logout(): void {
    this.clearSession();
  }

  private applyLoginResult(result: LoginResult): void {
    const { body, authorizationHeader } = result;
    this.applyResponse(body, authorizationHeader);
  }

  private applyResponse(response: LoginResponse, authorizationHeader: string | null = null): void {
    if (response.success) {
      this.isAuthenticated.set(true);
      this.role.set(response.role);
      this.userId.set(this.resolveUserId(response, authorizationHeader));
      this.errorMessage.set(null);
      const accessToken = this.resolveAccessToken(response, authorizationHeader);
      const expiresAt = this.resolveExpiresAt(response, accessToken);
      if (accessToken && expiresAt) {
        this.tokenService.setToken({
          accessToken,
          expiresAt,
        });
      }
      this.persistAuthState();
      return;
    }

    this.clearSession(response.errorMessage ?? 'Login failed. Please try again.');
  }

  private normalizeError(error: unknown): LoginResponse {
    if (error instanceof HttpErrorResponse && error.error) {
      const maybeError = error.error as Partial<LoginResponse>;
      if (typeof maybeError.success === 'boolean') {
        return {
          success: maybeError.success,
          role: maybeError.role ?? null,
          errorMessage:
            maybeError.errorMessage ??
            (error.status === 401
              ? 'Invalid email or password.'
              : 'Unable to complete login. Please try again.'),
        };
      }
    }

    return {
      success: false,
      role: null,
      errorMessage: 'Unable to complete login. Please try again.',
    };
  }

  private restoreAuthState(): void {
    const stored = sessionStorage.getItem(STORAGE_KEY);
    if (!stored) {
      return;
    }

    try {
      const snapshot = JSON.parse(stored) as AuthSnapshot;
      this.isAuthenticated.set(snapshot.isAuthenticated);
      this.role.set(snapshot.role ?? null);
      this.userId.set(snapshot.userId ?? null);
      this.email.set(snapshot.email ?? null);
    } catch {
      this.clearSession();
    }
  }

  private persistAuthState(): void {
    const snapshot: AuthSnapshot = {
      isAuthenticated: this.isAuthenticated(),
      role: this.role(),
      userId: this.userId(),
      email: this.email(),
    };

    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(snapshot));
  }

  private clearSession(errorMessage: string | null = null): void {
    this.isAuthenticated.set(false);
    this.role.set(null);
    this.userId.set(null);
    this.email.set(null);
    this.errorMessage.set(errorMessage);
    this.tokenService.clearToken();
    sessionStorage.removeItem(STORAGE_KEY);
  }

  private resolveAccessToken(response: LoginResponse, authorizationHeader: string | null): string | null {
    if (response.accessToken) {
      return response.accessToken;
    }

    if (!authorizationHeader) {
      return null;
    }

    const bearerPrefix = 'Bearer ';
    return authorizationHeader.startsWith(bearerPrefix)
      ? authorizationHeader.slice(bearerPrefix.length).trim()
      : authorizationHeader.trim();
  }

  private resolveExpiresAt(response: LoginResponse, accessToken: string | null): number | null {
    if (typeof response.expiresAt === 'number') {
      return response.expiresAt;
    }

    if (!accessToken) {
      return null;
    }

    return this.decodeJwtExpiry(accessToken);
  }

  private resolveUserId(response: LoginResponse, authorizationHeader: string | null): string | null {
    const responseUserId = response.personId ?? response.uuid ?? response.id ?? null;
    if (responseUserId) {
      return responseUserId;
    }

    const accessToken = this.resolveAccessToken(response, authorizationHeader);
    if (!accessToken) {
      return null;
    }

    return this.decodeJwtUserId(accessToken);
  }

  ensureUserId(): Observable<string | null> {
    const existingUserId = this.userId();
    if (existingUserId) {
      return of(existingUserId);
    }

    const email = this.email()?.trim().toLowerCase();
    if (!email) {
      return of(null);
    }

    const normalizedRole = this.role()?.trim().toLowerCase();
    const request$ =
      normalizedRole === 'student'
        ? this.studentService.getAll().pipe(map((items) => this.findIdByEmail(items, email)))
        : normalizedRole === 'professor'
          ? this.professorService.getAll().pipe(map((items) => this.findIdByEmail(items, email)))
          : forkJoin({
              persons: this.personService.getAll(),
              students: this.studentService.getAll(),
              professors: this.professorService.getAll(),
            }).pipe(
              map(({ persons, students, professors }) =>
                this.findIdByEmail(persons, email) ??
                this.findIdByEmail(students, email) ??
                this.findIdByEmail(professors, email),
              ),
            );

    return request$.pipe(
      tap((resolvedUserId) => {
        if (!resolvedUserId) {
          return;
        }

        this.userId.set(resolvedUserId);
        this.persistAuthState();
      }),
      catchError(() => of(null)),
    );
  }

  private findIdByEmail(items: Array<Person | Student | Professor>, email: string): string | null {
    const match = items.find((item) => item.email.trim().toLowerCase() === email);
    return match?.id ?? null;
  }

  private decodeJwtExpiry(token: string): number | null {
    const payload = this.decodeJwtPayload(token);
    return typeof payload?.exp === 'number' ? payload.exp * 1000 : null;
  }

  private decodeJwtUserId(token: string): string | null {
    const payload = this.decodeJwtPayload(token);
    if (!payload) {
      return null;
    }

    const candidates = [payload.userId, payload.uuid, payload.id, payload.sub];
    const uuidPattern =
      /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

    for (const candidate of candidates) {
      if (typeof candidate === 'string' && uuidPattern.test(candidate.trim())) {
        return candidate.trim();
      }
    }

    return null;
  }

  private decodeJwtPayload(token: string): { exp?: number; userId?: unknown; uuid?: unknown; id?: unknown; sub?: unknown } | null {
    const parts = token.split('.');
    if (parts.length < 2) {
      return null;
    }

    try {
      return JSON.parse(atob(this.toBase64(parts[1]))) as {
        exp?: number;
        userId?: unknown;
        uuid?: unknown;
        id?: unknown;
        sub?: unknown;
      };
    } catch {
      return null;
    }
  }

  private toBase64(value: string): string {
    const normalized = value.replace(/-/g, '+').replace(/_/g, '/');
    const padding = normalized.length % 4;
    return padding === 0 ? normalized : normalized.padEnd(normalized.length + (4 - padding), '=');
  }
}

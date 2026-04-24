import { inject, Injectable, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, finalize, map, Observable, of, tap } from 'rxjs';
import { LoginRequest, LoginResponse, LoginResult, LoginService } from '../../services/login.service';
import { TokenService } from '../../services/token.service';

interface AuthSnapshot {
  isAuthenticated: boolean;
  role: string | null;
}

const STORAGE_KEY = 'demo-app-auth';

@Injectable({ providedIn: 'root' })
export class LoginStore {
  private readonly loginService = inject(LoginService);
  private readonly tokenService = inject(TokenService);

  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly isAuthenticated = signal(false);
  readonly role = signal<string | null>(null);

  constructor() {
    this.restoreAuthState();
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    this.errorMessage.set(null);
    this.isSubmitting.set(true);

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
    } catch {
      this.clearSession();
    }
  }

  private persistAuthState(): void {
    const snapshot: AuthSnapshot = {
      isAuthenticated: this.isAuthenticated(),
      role: this.role(),
    };

    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(snapshot));
  }

  private clearSession(errorMessage: string | null = null): void {
    this.isAuthenticated.set(false);
    this.role.set(null);
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

  private decodeJwtExpiry(token: string): number | null {
    const parts = token.split('.');
    if (parts.length < 2) {
      return null;
    }

    try {
      const payload = JSON.parse(atob(this.toBase64(parts[1]))) as { exp?: number };
      return typeof payload.exp === 'number' ? payload.exp * 1000 : null;
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

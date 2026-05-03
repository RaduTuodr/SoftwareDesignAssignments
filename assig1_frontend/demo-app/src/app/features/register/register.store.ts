import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { catchError, finalize, Observable, of, tap } from 'rxjs';
import { RegisterRequest, RegisterResponse, RegisterResult, RegisterService } from '../../services/register.service';

@Injectable({ providedIn: 'root' })
export class RegisterStore {
  private readonly registerService = inject(RegisterService);

  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  register(request: RegisterRequest): Observable<RegisterResult> {
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.isSubmitting.set(true);

    return this.registerService.register(request).pipe(
      tap((response) => this.applyResponse(response)),
      catchError((error: unknown) => {
        const response = this.normalizeError(error);
        this.applyResponse(response);
        return of(response);
      }),
      finalize(() => this.isSubmitting.set(false)),
    );
  }

  private applyResponse(response: RegisterResult): void {
    if (response.body.success) {
      this.successMessage.set('Registration successful! You can now log in.');
      this.errorMessage.set(null);
      return;
    }

    this.errorMessage.set(response.body.errorMessage ?? 'Registration failed. Please try again.');
    this.successMessage.set(null);
  }

  private normalizeError(error: unknown): RegisterResult {
    if (error instanceof HttpErrorResponse && error.error) {
      const maybeError = error.error as Partial<RegisterResponse>;
      if (typeof maybeError.success === 'boolean') {
        return {
          body: {
            success: maybeError.success,
            errorMessage: maybeError.errorMessage ?? 'Unable to complete registration. Please try again.',
          }
        };
      }
    }

    return {
      body: {
        success: false,
        errorMessage: 'Unable to complete registration. Please try again.',
      }
    };
  }
}

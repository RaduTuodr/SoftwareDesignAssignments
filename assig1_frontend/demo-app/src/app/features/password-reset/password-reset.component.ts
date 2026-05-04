import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { RouterLink } from '@angular/router';
import { of, switchMap } from 'rxjs';
import { LoginStore } from '../login/login.store';
import { PasswordResetService } from '../../services/password-reset.service';

@Component({
  selector: 'app-password-reset',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './password-reset.component.html',
  styleUrl: './password-reset.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PasswordResetComponent {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly passwordResetService = inject(PasswordResetService);
  private readonly loginStore = inject(LoginStore);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly isRequestSubmitting = signal(false);
  protected readonly isConfirmSubmitting = signal(false);
  protected readonly requestCompleted = signal(false);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly isOldPasswordVisible = signal(false);
  protected readonly isNewPasswordVisible = signal(false);
  protected readonly userId = this.loginStore.userId;
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly requestForm = this.formBuilder.group({
    phoneNumber: ['', [Validators.required]],
  });

  protected readonly confirmForm = this.formBuilder.group({
    code: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]],
    oldPassword: ['', [Validators.required]],
    newPassword: ['', [Validators.required]],
  });

  protected toggleOldPasswordVisibility(): void {
    this.isOldPasswordVisible.update((visible) => !visible);
  }

  protected toggleNewPasswordVisibility(): void {
    this.isNewPasswordVisible.update((visible) => !visible);
  }

  protected submitRequest(): void {
    if (this.requestForm.invalid || this.isRequestSubmitting()) {
      this.requestForm.markAllAsTouched();
      return;
    }

    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.isRequestSubmitting.set(true);

    const { phoneNumber } = this.requestForm.getRawValue();
    (this.userId() ? of(this.userId()) : this.loginStore.ensureUserId())
      .pipe(
        switchMap((personId) => {
          if (!personId) {
            throw new Error('No user id found in the current session. Please sign in again.');
          }

          return this.passwordResetService.requestReset(
            personId,
            { phoneNumber: phoneNumber.trim() },
          );
        }),
      )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.requestCompleted.set(true);
          this.successMessage.set('Verification code sent. Enter the code and your new password.');
          this.isRequestSubmitting.set(false);
        },
        error: () => {
          this.errorMessage.set('Invalid phone number.');
          this.isRequestSubmitting.set(false);
        },
      });
  }

  protected submitConfirmation(): void {
    if (this.confirmForm.invalid || this.isConfirmSubmitting()) {
      this.confirmForm.markAllAsTouched();
      return;
    }

    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.isConfirmSubmitting.set(true);

    const { code, oldPassword, newPassword } = this.confirmForm.getRawValue();

    (this.userId() ? of(this.userId()) : this.loginStore.ensureUserId())
      .pipe(
        switchMap((personId) => {
          if (!personId) {
            throw new Error('No user id found in the current session. Please sign in again.');
          }

          return this.passwordResetService.confirmReset(
            personId,
            {
              code: code.trim(),
              oldPassword,
              newPassword,
            },
          );
        }),
      )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.successMessage.set('Password updated successfully. You can sign in now.');
          this.confirmForm.reset({ code: '', oldPassword: '', newPassword: '' });
          this.isConfirmSubmitting.set(false);
        },
        error: () => {
          this.errorMessage.set('Invalid verification code or password.');
          this.isConfirmSubmitting.set(false);
        },
      });
  }
}

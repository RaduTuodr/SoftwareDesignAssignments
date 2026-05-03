import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router, RouterLink } from '@angular/router';
import { RegisterStore } from './register.store';

@Component({
  selector: 'app-register',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterComponent {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly registerStore = inject(RegisterStore);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly isSubmitting = this.registerStore.isSubmitting;
  protected readonly errorMessage = this.registerStore.errorMessage;
  protected readonly successMessage = this.registerStore.successMessage;

  protected readonly registerForm = this.formBuilder.group({
    name: ['', [Validators.required]],
    age: [18, [Validators.required, Validators.min(1)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
    role: ['student', [Validators.required]],
  });

  protected readonly isPasswordVisible = signal(false);

  protected togglePasswordVisibility(): void {
    this.isPasswordVisible.update((visible) => !visible);
  }

  protected submit(): void {
    if (this.registerForm.invalid || this.isSubmitting()) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const { name, age, email, password, role } = this.registerForm.getRawValue();
    this.registerStore
      .register({
        name: name.trim(),
        age,
        email: email.trim(),
        password,
        role: role.trim(),
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => {
        if (!response.body.success) {
          return;
        }

        void this.router.navigate(['/login']);
      });
  }
}

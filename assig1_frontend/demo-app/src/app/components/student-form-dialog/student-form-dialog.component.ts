import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  signal,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { PersonFormValue } from '../person-form-dialog/person-form-dialog.component';

export interface StudentFormDialogData {
  title: string;
  submitLabel?: string;
  initialValue?: StudentFormInitialValue | null;
}

export interface StudentFormValue extends PersonFormValue {
  registrationNumber: string;
  graduationYear: number;
}

export interface StudentFormInitialValue {
  name: string;
  age: number;
  email: string;
  registrationNumber: string;
  graduationYear: number;
}

export type StudentFormDialogResult = StudentFormValue | undefined;

@Component({
  selector: 'app-student-form-dialog',
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './student-form-dialog.component.html',
  styleUrl: './student-form-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StudentFormDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<StudentFormDialogComponent>);
  protected readonly data = inject<StudentFormDialogData>(MAT_DIALOG_DATA);

  protected readonly isPasswordVisible = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    age: [0, [Validators.required, Validators.min(18), Validators.max(200)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
    registrationNumber: ['', [Validators.required, Validators.minLength(3)]],
    graduationYear: [new Date().getFullYear(), [Validators.required, Validators.min(1900), Validators.max(2100)]],
  });

  ngOnInit(): void {
    if (this.data.initialValue) {
      this.form.patchValue({
        ...this.data.initialValue,
        password: '', // Don't show existing password
      });
    }
  }

  protected togglePasswordVisibility(): void {
    this.isPasswordVisible.update((v) => !v);
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { name, age, email, password, registrationNumber, graduationYear } = this.form.getRawValue();
    const result: StudentFormValue = {
      name,
      age,
      email,
      password,
      registrationNumber,
      graduationYear,
    };

    this.dialogRef.close(result);
  }

  protected cancel(): void {
    this.dialogRef.close(undefined);
  }
}
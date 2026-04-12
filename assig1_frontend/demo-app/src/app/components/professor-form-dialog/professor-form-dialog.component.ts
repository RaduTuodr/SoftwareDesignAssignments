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
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { PersonFormValue } from '../person-form-dialog/person-form-dialog.component';

export interface ProfessorFormDialogData {
  title: string;
  submitLabel?: string;
  initialValue?: ProfessorFormInitialValue | null;
}

export interface ProfessorFormValue extends PersonFormValue {
  department: string;
  academicRank: string;
}

export interface ProfessorFormInitialValue {
  name: string;
  age: number;
  email: string;
  department: string;
  academicRank: string;
}

export type ProfessorFormDialogResult = ProfessorFormValue | undefined;

@Component({
  selector: 'app-professor-form-dialog',
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatIconModule,
  ],
  templateUrl: './professor-form-dialog.component.html',
  styleUrl: './professor-form-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfessorFormDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<ProfessorFormDialogComponent>);
  protected readonly data = inject<ProfessorFormDialogData>(MAT_DIALOG_DATA);

  protected readonly isPasswordVisible = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    age: [0, [Validators.required, Validators.min(18), Validators.max(200)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
    department: ['', [Validators.required, Validators.minLength(2)]],
    academicRank: ['', [Validators.required]],
  });

  protected readonly academicRanks = [
    'Assistant Professor',
    'Associate Professor',
    'Professor',
    'Distinguished Professor',
  ];

  ngOnInit(): void {
    if (this.data.initialValue) {
      this.form.patchValue({
        ...this.data.initialValue,
        password: '',
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

    const { name, age, email, password, department, academicRank } = this.form.getRawValue();
    const result: ProfessorFormValue = {
      name,
      age,
      email,
      password,
      department,
      academicRank,
    };

    this.dialogRef.close(result);
  }

  protected cancel(): void {
    this.dialogRef.close(undefined);
  }
}
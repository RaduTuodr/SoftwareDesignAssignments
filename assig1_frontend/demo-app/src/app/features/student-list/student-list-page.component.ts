import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatToolbar } from '@angular/material/toolbar';
import { ConfirmDeleteDialogComponent } from '../../components/confirm-delete-dialog/confirm-delete-dialog.component';
import {
  StudentFormDialogComponent,
  StudentFormDialogData,
  StudentFormDialogResult,
} from '../../components/student-form-dialog/student-form-dialog.component';
import { Student, UpdateStudentDto, CreateStudentDto } from '../../models/student.model';
import { StudentListStore } from './student-list.store';
import { Router } from '@angular/router';
import { LoginStore } from '../login/login.store';

@Component({
  selector: 'app-student-list-page',
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatToolbar,
  ],
  templateUrl: './student-list-page.component.html',
  styleUrl: './student-list-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StudentListPageComponent {
  private readonly dialog = inject(MatDialog);
  private readonly store = inject(StudentListStore);
  private readonly destroyRef = inject(DestroyRef);

  private readonly loginStore = inject(LoginStore);
  private readonly router = inject(Router);

  protected readonly students = this.store.students;
  protected readonly hasError = this.store.hasError;
  protected readonly error = this.store.error;
  protected readonly isLoading = this.store.isLoading;
  protected readonly displayedColumns = [
    'name',
    'age',
    'email',
    'registrationNumber',
    'graduationYear',
    'actions',
  ];

  protected readonly pageTitle = 'Manage Students';
  protected readonly pageSubtitle = 'View, create, edit, and delete student records.';

  constructor() {
    this.store.load();
  }

  protected getErrorDetails(): string[] {
    const err = this.error();
    if (err?.error && typeof err.error === 'object') {
      return Object.entries(err.error).map(([key, value]) => `${key}: ${value}`);
    }
    return [];
  }

  protected openCreateDialog(): void {
    if (this.isLoading()) {
      return;
    }

    this.dialog
      .open<StudentFormDialogComponent, StudentFormDialogData, StudentFormDialogResult>(
        StudentFormDialogComponent,
        { data: { title: 'Create Student', submitLabel: 'Create' } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((result) => {
        if (!result) return;
        this.store.create(result as CreateStudentDto);
      });
  }

  protected openEditDialog(student: Student): void {
    if (this.isLoading()) {
      return;
    }

    this.dialog
      .open<StudentFormDialogComponent, StudentFormDialogData, StudentFormDialogResult>(
        StudentFormDialogComponent,
        { data: { title: 'Edit Student', submitLabel: 'Save', initialValue: student } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((result) => {
        if (!result) return;
        this.store.update(student.id, result as UpdateStudentDto);
      });
  }

  protected openDeleteDialog(student: Student): void {
    if (this.isLoading()) {
      return;
    }

    this.dialog
      .open<ConfirmDeleteDialogComponent, { person: Student }, boolean>(
        ConfirmDeleteDialogComponent,
        { data: { person: student } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((confirmed) => {
        if (!confirmed) return;
        this.store.remove(student.id);
      });
  }

  protected logout(): void {
    this.loginStore.logout();
    void this.router.navigate(['/login']);
  }

  protected openPasswordUpdate(): void {
    void this.router.navigate(['/reset-password']);
  }
}

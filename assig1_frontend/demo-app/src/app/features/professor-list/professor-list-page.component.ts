import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatToolbar } from '@angular/material/toolbar';
import { Router } from '@angular/router';
import { ConfirmDeleteDialogComponent } from '../../components/confirm-delete-dialog/confirm-delete-dialog.component';
import {
  ProfessorFormDialogComponent,
  ProfessorFormDialogData,
  ProfessorFormDialogResult,
} from '../../components/professor-form-dialog/professor-form-dialog.component';
import { Professor, UpdateProfessorDto, CreateProfessorDto } from '../../models/professor.model';
import { LoginStore } from '../login/login.store';
import { ProfessorListStore } from './professor-list.store';

@Component({
  selector: 'app-professor-list-page',
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatToolbar,
  ],
  templateUrl: './professor-list-page.component.html',
  styleUrl: './professor-list-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfessorListPageComponent {
  private readonly dialog = inject(MatDialog);
  private readonly store = inject(ProfessorListStore);
  private readonly destroyRef = inject(DestroyRef);

  private readonly loginStore = inject(LoginStore);
  private readonly router = inject(Router);

  protected readonly professors = this.store.professors;
  protected readonly filteredProfessors = this.store.filteredProfessors;
  protected readonly rankOptions = this.store.rankOptions;
  protected readonly departmentSearch = this.store.departmentSearch;
  protected readonly selectedRank = this.store.selectedRank;
  protected readonly sortDirection = this.store.sortDirection;
  protected readonly hasError = this.store.hasError;
  protected readonly error = this.store.error;
  protected readonly isLoading = this.store.isLoading;
  protected readonly displayedColumns = ['name', 'age', 'email', 'department', 'academicRank', 'actions'];

  protected readonly pageTitle = 'Manage Professors';
  protected readonly pageSubtitle = 'View, edit, and delete professor records.';

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
      .open<ProfessorFormDialogComponent, ProfessorFormDialogData, ProfessorFormDialogResult>(
        ProfessorFormDialogComponent,
        { data: { title: 'Create Professor', submitLabel: 'Create' } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((result) => {
        if (!result) return;
        this.store.create(result as CreateProfessorDto);
      });
  }

  protected openEditDialog(professor: Professor): void {
    if (this.isLoading()) {
      return;
    }

    this.dialog
      .open<ProfessorFormDialogComponent, ProfessorFormDialogData, ProfessorFormDialogResult>(
        ProfessorFormDialogComponent,
        { data: { title: 'Edit Professor', submitLabel: 'Save', initialValue: professor } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((result) => {
        if (!result) return;
        this.store.update(professor.id, result as UpdateProfessorDto);
      });
  }

  protected openDeleteDialog(professor: Professor): void {
    if (this.isLoading()) {
      return;
    }

    this.dialog
      .open<ConfirmDeleteDialogComponent, { person: Professor }, boolean>(
        ConfirmDeleteDialogComponent,
        { data: { person: professor } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((confirmed) => {
        if (!confirmed) return;
        this.store.remove(professor.id);
      });
  }

  protected updateDepartmentSearch(value: string): void {
    this.store.setDepartmentSearch(value);
  }

  protected updateRankFilter(value: string): void {
    this.store.setRankFilter(value);
  }

  protected toggleSortDirection(): void {
    this.store.toggleSortDirection();
  }

  protected logout(): void {
    this.loginStore.logout();
    void this.router.navigate(['/login']);
  }

  protected openPasswordUpdate(): void {
    void this.router.navigate(['/reset-password']);
  }
}

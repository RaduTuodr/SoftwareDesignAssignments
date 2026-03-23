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
  PersonFormDialogComponent,
  PersonFormDialogData,
  PersonFormDialogResult,
} from '../../components/person-form-dialog/person-form-dialog.component';
import { CreatePersonDto, Person, UpdatePersonDto } from '../../models/person.model';
import { PersonListStore } from './person-list.store';

@Component({
  selector: 'app-person-list-page',
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatDialogModule, MatToolbar],
  templateUrl: './person-list-page.component.html',
  styleUrl: './person-list-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PersonListPageComponent {
  private readonly dialog = inject(MatDialog);
  private readonly store = inject(PersonListStore);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly persons = this.store.persons;
  protected readonly hasError = this.store.hasError;
  protected readonly error = this.store.error;
  protected readonly isLoading = this.store.isLoading;
  protected readonly displayedColumns = ['name', 'age', 'email', 'personType', 'actions'];

  protected getPersonType(person: Person): 'Person' | 'Student' | 'Professor' {
    const maybeStudent = person as Person & { registrationNumber: string };
    if (maybeStudent.registrationNumber !== undefined) {
      return 'Student';
    }

    const maybeProfessor = person as Person & { department: string };

    if (maybeProfessor.department !== undefined) {
      return 'Professor';
    }

    return 'Person';
  }

  protected getPersonTypeClass(person: Person): 'role-person' | 'role-student' | 'role-professor' {
    const type = this.getPersonType(person);
    if (type === 'Student') {
      return 'role-student';
    }
    if (type === 'Professor') {
      return 'role-professor';
    }
    return 'role-person';
  }

  protected getErrorDetails(): string[] {
    const err = this.error();
    if (err?.error && typeof err.error === 'object') {
      return Object.entries(err.error).map(([key, value]) => `${key}: ${value}`);
    }
    return [];
  }

  constructor() {
    this.store.load();
  }

  protected openCreateDialog(): void {
    if (this.isLoading()) {
      return;
    }

    this.dialog
      .open<PersonFormDialogComponent, PersonFormDialogData, PersonFormDialogResult>(
        PersonFormDialogComponent,
        { data: { title: 'Create Person', submitLabel: 'Create', showPasswordField: true } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((result) => {
        if (!result) return;
        this.store.create(result as CreatePersonDto);
      });
  }

  protected openEditDialog(person: Person): void {
    if (this.isLoading()) {
      return;
    }

    this.dialog
      .open<PersonFormDialogComponent, PersonFormDialogData, PersonFormDialogResult>(
        PersonFormDialogComponent,
        { data: { title: 'Edit Person', submitLabel: 'Save', initialValue: person } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((result) => {
        if (!result) return;
        this.store.update(person.id, result as UpdatePersonDto);
      });
  }

  protected openDeleteDialog(person: Person): void {
    if (this.isLoading()) {
      return;
    }

    this.dialog
      .open<ConfirmDeleteDialogComponent, { person: Person }, boolean>(
        ConfirmDeleteDialogComponent,
        { data: { person } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((confirmed) => {
        if (!confirmed) return;
        this.store.remove(person.id);
      });
  }
}

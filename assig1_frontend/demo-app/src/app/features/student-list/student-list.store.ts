import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';
import { CreateStudentDto, Student, UpdateStudentDto } from '../../models/student.model';
import { CreatePersonDto, Person } from '../../models/person.model';
import { PersonService } from '../../services/person.service';

@Injectable({ providedIn: 'root' })
export class StudentListStore {
  private readonly personService = inject(PersonService);
  private readonly pendingRequests = signal(0);

  readonly students = signal<Student[]>([]);
  readonly hasError = signal(false);
  readonly error = signal<HttpErrorResponse | null>(null);
  readonly isLoading = computed(() => this.pendingRequests() > 0);

  private beginRequest(): void {
    this.pendingRequests.update((count) => count + 1);
  }

  private endRequest(): void {
    this.pendingRequests.update((count) => Math.max(0, count - 1));
  }

  private isStudent(person: Person): person is Student {
    return 'registrationNumber' in person && 'graduationYear' in person;
  }

  load(): void {
    this.hasError.set(false);
    this.beginRequest();
    this.personService
      .getAll()
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (data) => this.students.set(data.filter(this.isStudent)),
        error: (err) => {
          this.hasError.set(true);
          this.error.set(err);
        }
      });
  }

  create(dto: CreateStudentDto): void {
    this.hasError.set(false);
    this.beginRequest();
    this.personService
      .create(dto)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (created) => {
          if (this.isStudent(created)) {
            this.students.update((list) => [...list, created]);
          }
        },
        error: (err) => {
          this.hasError.set(true);
          this.error.set(err);
        }
      });
  }

  update(id: string, dto: UpdateStudentDto): void {
    const existing = this.students().find((item) => item.id === id);
    if (!existing) return;

    const payload: CreateStudentDto = { ...existing, ...dto, password: existing.password };

    this.hasError.set(false);
    this.beginRequest();
    this.personService
      .update(id, payload)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (updated) =>
          this.students.update((list) =>
            list.map((item) => (item.id === updated.id && this.isStudent(updated) ? updated : item)),
          ),
        error: (err) => {
          this.hasError.set(true);
          this.error.set(err);
        }
      });
  }

  remove(id: string): void {
    this.hasError.set(false);
    this.beginRequest();
    this.personService
      .delete(id)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: () =>
          this.students.update((list) => list.filter((student) => student.id !== id)),
        error: (err) => {
          this.hasError.set(true);
          this.error.set(err);
        }
      });
  }
}

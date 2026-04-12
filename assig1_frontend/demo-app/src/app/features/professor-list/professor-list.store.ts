import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';
import {
  CreateProfessorDto,
  Professor,
  UpdateProfessorDto,
} from '../../models/professor.model';
import { ProfessorService } from '../../services/professor.service';

@Injectable({ providedIn: 'root' })
export class ProfessorListStore {
  private readonly professorService = inject(ProfessorService);
  private readonly pendingRequests = signal(0);

  readonly professors = signal<Professor[]>([]);
  readonly departmentSearch = signal('');
  readonly selectedRank = signal('All');
  readonly sortDirection = signal<'asc' | 'desc'>('asc');

  private static readonly availableRanks = [
    'Assistant Professor',
    'Associate Professor',
    'Professor',
    'Distinguished Professor',
  ] as const;

  readonly rankOptions = computed(() => ['All', ...ProfessorListStore.availableRanks]);

  readonly filteredProfessors = computed(() => {
    const search = this.departmentSearch().trim().toLowerCase();
    const selectedRank = this.selectedRank();
    const sorted = [...this.professors()]
      .filter((professor) => professor.department.toLowerCase().includes(search))
      .filter((professor) => selectedRank === 'All' || professor.academicRank === selectedRank)
      .sort((a, b) => a.name.localeCompare(b.name, undefined, { sensitivity: 'base' }));

    return this.sortDirection() === 'asc' ? sorted : sorted.reverse();
  });

  readonly hasError = signal(false);
  readonly error = signal<HttpErrorResponse | null>(null);
  readonly isLoading = computed(() => this.pendingRequests() > 0);

  private beginRequest(): void {
    this.pendingRequests.update((count) => count + 1);
  }

  private endRequest(): void {
    this.pendingRequests.update((count) => Math.max(0, count - 1));
  }

  private isProfessor(professor: any): professor is Professor {
    return professor && typeof professor === 'object' &&
           'id' in professor && 'name' in professor && 'department' in professor && 'academicRank' in professor;
  }

  load(): void {
    this.hasError.set(false);
    this.beginRequest();
    this.professorService
      .getAll()
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (data) => this.professors.set(data),
        error: (err) => {
          this.hasError.set(true);
          this.error.set(err);
        },
      });
  }

  setDepartmentSearch(value: string): void {
    this.departmentSearch.set(value);
  }

  setRankFilter(value: string): void {
    this.selectedRank.set(value);
  }

  toggleSortDirection(): void {
    this.sortDirection.update((current) => (current === 'asc' ? 'desc' : 'asc'));
  }

  create(dto: CreateProfessorDto): void {
    this.hasError.set(false);
    this.beginRequest();
    this.professorService
      .create(dto)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (created) => {
          this.professors.update((list) => [...list, created]);
        },
        error: (err) => {
          this.hasError.set(true);
          this.error.set(err);
        },
      });
  }

  update(id: string, dto: UpdateProfessorDto): void {
    const existing = this.professors().find((item) => item.id === id);
    if (!existing) return;

    this.hasError.set(false);
    this.beginRequest();
    this.professorService
      .update(id, dto)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (updated) =>
          this.professors.update((list) =>
            list.map((item) => (item.id === updated.id ? updated : item)),
          ),
        error: (err) => {
          this.hasError.set(true);
          this.error.set(err);
        },
      });
  }

  remove(id: string): void {
    this.hasError.set(false);
    this.beginRequest();
    this.professorService
      .delete(id)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: () => this.professors.update((list) => list.filter((professor) => professor.id !== id)),
        error: (err) => {
          this.hasError.set(true);
          this.error.set(err);
        },
      });
  }
}

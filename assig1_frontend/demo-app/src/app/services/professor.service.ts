import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateProfessorDto, Professor, UpdateProfessorDto } from '../models/professor.model';

const API_URL = 'http://localhost:8080/professor';

@Injectable({ providedIn: 'root' })
export class ProfessorService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<Professor[]> {
    return this.http.get<Professor[]>(API_URL);
  }

  create(dto: CreateProfessorDto): Observable<Professor> {
    return this.http.post<Professor>(API_URL, dto);
  }

  update(id: string, dto: UpdateProfessorDto): Observable<Professor> {
    return this.http.put<Professor>(`${API_URL}/${id}`, dto);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}
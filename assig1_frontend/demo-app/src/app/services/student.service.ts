import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateStudentDto, Student, UpdateStudentDto } from '../models/student.model';

const API_URL = 'http://localhost:8080/student';

@Injectable({ providedIn: 'root' })
export class StudentService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<Student[]> {
    return this.http.get<Student[]>(API_URL);
  }

  create(dto: CreateStudentDto): Observable<Student> {
    return this.http.post<Student>(API_URL, dto);
  }

  update(id: string, dto: UpdateStudentDto): Observable<Student> {
    return this.http.put<Student>(`${API_URL}/${id}`, dto);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}
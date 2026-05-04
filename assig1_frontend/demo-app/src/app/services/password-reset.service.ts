import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/person';

export interface PasswordResetRequestDto {
  phoneNumber: string;
}

export interface PasswordResetConfirmDto {
  code: string;
  oldPassword: string;
  newPassword: string;
}

@Injectable({ providedIn: 'root' })
export class PasswordResetService {
  private readonly http = inject(HttpClient);

  requestReset(personId: string, dto: PasswordResetRequestDto): Observable<string> {
    return this.http.put(`${API_URL}/${personId}/password/request`, dto, {
      responseType: 'text',
    });
  }

  confirmReset(personId: string, dto: PasswordResetConfirmDto): Observable<string> {
    return this.http.put(`${API_URL}/${personId}/password/confirm`, dto, {
      responseType: 'text',
    });
  }
}

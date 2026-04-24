import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/login';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  success: boolean;
  role: string | null;
  errorMessage: string | null;
  accessToken?: string | null;
  expiresAt?: number | null;
}

export interface LoginResult {
  body: LoginResponse;
  authorizationHeader: string | null;
}

@Injectable({ providedIn: 'root' })
export class LoginService {
  private readonly http = inject(HttpClient);

  login(request: LoginRequest): Observable<LoginResult> {
    return this.http.post<LoginResponse>(API_URL, request, { observe: 'response' }).pipe(
      map((response: HttpResponse<LoginResponse>) => ({
        body: response.body ?? {
          success: false,
          role: null,
          errorMessage: 'Empty login response.',
        },
        authorizationHeader: response.headers.get('Authorization'),
      })),
    );
  }
}

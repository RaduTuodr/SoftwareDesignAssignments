import { HttpClient, HttpResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/auth/register';

export interface RegisterRequest {
  name: string;
  age: number;
  email: string;
  password: string;
  role: string;
}

export interface RegisterResponse {
  success: boolean;
  errorMessage: string | null;
}

export interface RegisterResult {
  body: RegisterResponse;
}

@Injectable({ providedIn: 'root' })
export class RegisterService {
  private readonly http = inject(HttpClient);

  register(request: RegisterRequest): Observable<RegisterResult> {
    console.log("[register service] Sending registration request:", request);
    return this.http.post<RegisterResponse>(API_URL, request, { observe: 'response' }).pipe(
      map((response: HttpResponse<RegisterResponse>) => ({
        body: response.body ?? {
          success: false,
          errorMessage: 'Empty registration response.',
        },
      })),
    );
  }
}

import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';


interface TokenPair {
  accessToken: string;
  expiresAt: number;
}

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  private static readonly STORAGE_KEY = 'demo-app-token';

  private readonly tokenSubject = new BehaviorSubject<string | null>(null);
  private tokenExpiry: number | null = null;

  constructor() {
    this.restoreToken();
  }

  get token$(): Observable<string | null> {
    return this.tokenSubject.asObservable();
  }

  get accessToken(): string | null {
    if (this.isTokenExpired()) {
      this.clearToken();
      return null;
    }
    return this.tokenSubject.value;
  }

  setToken(tokenPair: TokenPair): void {
    this.tokenSubject.next(tokenPair.accessToken);
    this.tokenExpiry = this.normalizeExpiry(tokenPair.expiresAt);
    sessionStorage.setItem(
      TokenService.STORAGE_KEY,
      JSON.stringify({
        ...tokenPair,
        expiresAt: this.tokenExpiry,
      }),
    );
  }

  clearToken(): void {
    this.tokenSubject.next(null);
    this.tokenExpiry = null;
    sessionStorage.removeItem(TokenService.STORAGE_KEY);
  }

  private isTokenExpired(): boolean {
    if (!this.tokenExpiry) {
      return true;
    }

    return Date.now() >= this.tokenExpiry;
  }

  private restoreToken(): void {
    const storedToken = sessionStorage.getItem(TokenService.STORAGE_KEY);
    if (!storedToken) {
      return;
    }

    try {
      const tokenPair = JSON.parse(storedToken) as TokenPair;
      if (!tokenPair.accessToken || typeof tokenPair.expiresAt !== 'number') {
        this.clearToken();
        return;
      }

      const normalizedExpiry = this.normalizeExpiry(tokenPair.expiresAt);

      if (Date.now() >= normalizedExpiry) {
        this.clearToken();
        return;
      }

      this.tokenSubject.next(tokenPair.accessToken);
      this.tokenExpiry = normalizedExpiry;
    } catch {
      this.clearToken();
    }
  }

  private normalizeExpiry(expiresAt: number): number {
    return expiresAt < 1_000_000_000_000 ? expiresAt * 1000 : expiresAt;
  }
}





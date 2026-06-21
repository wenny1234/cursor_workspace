import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  type: string;
  id: number;
  username: string;
  email: string;
  // backend の User.Role など、型が未確定なので暫定的に any にします
  role?: any;
  user?: any;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // backend: application.yml の context-path が /api なので、URL は /api/auth/login です
  private readonly loginUrl = 'http://localhost:8080/api/auth/login';

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.loginUrl, request);
  }

  setTokens(response: LoginResponse): void {
    localStorage.setItem('accessToken', response.token);
    localStorage.setItem('refreshToken', response.refreshToken);
    if (response.role) {
      localStorage.setItem('userRole', String(response.role));
    }
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRole(): string | null {
    return localStorage.getItem('userRole');
  }

  canManageOrders(): boolean {
    const role = this.getRole();
    return role === 'ADMIN' || role === 'STAFF';
  }

  clearTokens(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userRole');
  }
}


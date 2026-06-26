import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface UserProfile {
  id?: number;
  username: string;
  email?: string;
  role?: string;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  type: string;
  id: number;
  username: string;
  email: string;
  role?: string;
  user?: UserProfile;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiBase = 'http://localhost:8080/api';
  private readonly loginUrl = `${this.apiBase}/auth/login`;
  private readonly profileUrl = `${this.apiBase}/auth/profile`;

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
    this.saveUserProfile({
      id: response.id,
      username: response.username,
      email: response.email,
      role: response.role ? String(response.role) : undefined,
    });
  }

  saveUserProfile(profile: UserProfile): void {
    localStorage.setItem('userProfile', JSON.stringify(profile));
  }

  getUserProfile(): UserProfile | null {
    const raw = localStorage.getItem('userProfile');
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as UserProfile;
    } catch {
      return null;
    }
  }

  fetchProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(this.profileUrl).pipe(
      tap((profile) => {
        const role = profile.role ? String(profile.role) : this.getRole() ?? undefined;
        const saved: UserProfile = {
          id: profile.id,
          username: profile.username,
          email: profile.email,
          role,
        };
        this.saveUserProfile(saved);
        if (role) {
          localStorage.setItem('userRole', role);
        }
      })
    );
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRole(): string | null {
    return localStorage.getItem('userRole');
  }

  roleLabel(role?: string | null): string {
    switch (role) {
      case 'ADMIN':
        return '管理者';
      case 'STAFF':
        return 'スタッフ';
      case 'VIEWER':
        return '会員';
      default:
        return role ?? '—';
    }
  }

  canManageOrders(): boolean {
    const role = this.getRole();
    return role === 'ADMIN' || role === 'STAFF';
  }

  clearTokens(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userProfile');
  }

  isLoggedIn(): boolean {
    return !!this.getAccessToken();
  }
}


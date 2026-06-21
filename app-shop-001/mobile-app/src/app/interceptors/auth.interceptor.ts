import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService, private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.auth.getAccessToken();

    // 認証不要（もしくは認証が不要な）APIは Authorization を付けない
    const isAuthPublicEndpoint =
      req.url.includes('/api/auth/login') ||
      req.url.includes('/api/auth/refresh') ||
      req.url.includes('/api/auth/register') ||
      req.url.includes('/api/auth/check');

    if (!token || isAuthPublicEndpoint || req.headers.has('Authorization')) {
      return next.handle(req);
    }

    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    return next.handle(authReq).pipe(
      catchError((err) => {
        if (err?.status === 401 || err?.status === 403) {
          // トークンが無効/期限切れの可能性が高いため、いったんログアウト状態に戻します
          this.auth.clearTokens();
          this.router.navigate(['/login']);
        }
        return throwError(() => err);
      })
    );
  }
}


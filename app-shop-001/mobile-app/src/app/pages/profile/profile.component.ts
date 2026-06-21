import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent {
  testResult: string | null = null;

  // backend に合わせて /api を含む URL にしています（application.yml の context-path が /api のため）
  private readonly statsUrl = 'http://localhost:8080/api/products/stats';

  constructor(
    private auth: AuthService,
    private router: Router,
    private http: HttpClient
  ) {}

  onLogout(): void {
    this.auth.clearTokens();
    this.router.navigate(['/login']);
  }

  // ログアウト後に叩くと 401 になり、AuthInterceptor が /login に遷移します
  onTestAuthRequiredApi(): void {
    this.testResult = null;
    this.http.get(this.statsUrl).subscribe({
      next: () => {
        this.testResult = '認証APIに成功しました（ログイン状態の可能性）';
      },
      error: () => {
        // ここに来る前に AuthInterceptor が /login に遷移することがあります
        this.testResult = '認証が必要なため失敗しました';
      }
    });
  }
}

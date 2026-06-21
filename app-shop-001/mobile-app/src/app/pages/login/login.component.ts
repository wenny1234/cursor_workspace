import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService, LoginRequest } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  errorMessage: string | null = null;
  isLoading = false;

  form = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (this.form.invalid || this.isLoading) return;

    this.errorMessage = null;
    this.isLoading = true;

    const payload: LoginRequest = this.form.value as LoginRequest;

    this.auth.login(payload).subscribe({
      next: (res) => {
        this.auth.setTokens(res);
        this.router.navigate(['/']);
      },
      error: (err: HttpErrorResponse) => {
        // backend が JSON で返さない場合もあるため、最低限のメッセージで表示します
        this.errorMessage =
          err.error?.message ||
          (typeof err.error === 'string' ? err.error : null) ||
          'ログインに失敗しました。ユーザー名/パスワードを確認してください。';
        this.isLoading = false;
      }
    });
  }
}

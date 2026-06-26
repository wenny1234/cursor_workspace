import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, UserProfile } from '../../services/auth.service';
import { OrderService, UserPointsSummary } from '../../services/order.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  userProfile: UserProfile | null = null;
  userPoints: UserPointsSummary | null = null;
  isLoading = true;
  isLoadingPoints = false;
  errorMessage: string | null = null;

  constructor(
    private auth: AuthService,
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  userInitial(): string {
    const name = this.userProfile?.username ?? '?';
    return name.charAt(0).toUpperCase();
  }

  roleLabel(): string {
    return this.auth.roleLabel(this.userProfile?.role ?? this.auth.getRole());
  }

  onLogout(): void {
    this.auth.clearTokens();
    this.router.navigate(['/login']);
  }

  private loadProfile(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.auth.fetchProfile().subscribe({
      next: (profile) => {
        this.userProfile = profile;
        this.isLoading = false;
        this.loadUserPoints();
      },
      error: () => {
        this.userProfile = this.auth.getUserProfile();
        this.isLoading = false;
        if (!this.userProfile) {
          this.errorMessage = 'ユーザー情報の取得に失敗しました';
        } else {
          this.loadUserPoints();
        }
      }
    });
  }

  private loadUserPoints(): void {
    this.isLoadingPoints = true;
    this.orderService.getMyOrders().subscribe({
      next: (res) => {
        this.userPoints = this.orderService.calculateUserPoints(res.orders ?? []);
        this.isLoadingPoints = false;
      },
      error: () => {
        this.userPoints = { points: 0, purchasedItemCount: 0, orderCount: 0 };
        this.isLoadingPoints = false;
      }
    });
  }
}

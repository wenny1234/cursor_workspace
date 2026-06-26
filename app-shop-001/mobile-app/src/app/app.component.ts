import { Component, OnDestroy, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { AuthService, UserProfile } from './services/auth.service';
import { OrderService, UserPointsSummary } from './services/order.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'Shop';
  userProfile: UserProfile | null = null;
  userPoints: UserPointsSummary | null = null;
  isLoadingPoints = false;
  showUserPanel = false;
  isLoginPage = false;

  private readonly destroy$ = new Subject<void>();

  constructor(
    private auth: AuthService,
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userProfile = this.auth.getUserProfile();
    this.updateLoginPageFlag(this.router.url);

    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        takeUntil(this.destroy$)
      )
      .subscribe((event) => {
        const url = (event as NavigationEnd).urlAfterRedirects;
        this.updateLoginPageFlag(url);
        this.showUserPanel = false;
      });

    if (this.auth.isLoggedIn()) {
      this.refreshUserProfile();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  toggleUserPanel(): void {
    this.showUserPanel = !this.showUserPanel;
    if (this.showUserPanel) {
      this.loadUserPoints();
    }
  }

  closeUserPanel(): void {
    this.showUserPanel = false;
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
    this.orderService.invalidateOrdersCache();
    this.userProfile = null;
    this.userPoints = null;
    this.showUserPanel = false;
    this.router.navigate(['/login']);
  }

  goToProfile(): void {
    this.showUserPanel = false;
    this.router.navigate(['/profile']);
  }

  private updateLoginPageFlag(url: string): void {
    this.isLoginPage = url.startsWith('/login');
  }

  private refreshUserProfile(): void {
    this.auth.fetchProfile()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (profile) => {
          this.userProfile = profile;
        },
        error: () => {
          this.userProfile = this.auth.getUserProfile();
        }
      });
  }

  private loadUserPoints(): void {
    if (!this.auth.isLoggedIn()) {
      this.userPoints = null;
      return;
    }

    this.isLoadingPoints = true;
    this.orderService.getMyOrders()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
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

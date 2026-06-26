import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Order, OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss']
})
export class OrdersComponent implements OnInit, OnDestroy {
  orders: Order[] = [];
  isLoading = false;
  errorMessage: string | null = null;

  private readonly destroy$ = new Subject<void>();

  constructor(
    private orderService: OrderService,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.auth.getAccessToken()) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadOrders();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadOrders(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.orderService.getMyOrders()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res) => {
          this.orders = res.orders ?? [];
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
          this.errorMessage = '注文履歴の取得に失敗しました';
        }
      });
  }

  statusLabel(status: string): string {
    return this.orderService.statusLabel(status);
  }

  statusClass(status?: string): string {
    return this.orderService.statusClass(status);
  }

  orderPoints(order: Order): number {
    const eligible = ['PAID', 'SHIPPING', 'COMPLETED'].includes(order.status);
    return eligible ? Math.floor((order.totalAmount ?? 0) / 100) : 0;
  }
}

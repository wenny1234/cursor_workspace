import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Order, OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss']
})
export class OrdersComponent implements OnInit {
  orders: Order[] = [];
  isLoading = false;
  errorMessage: string | null = null;

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

  loadOrders(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.orderService.getMyOrders().subscribe({
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
}

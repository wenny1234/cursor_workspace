import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Order, OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-order-detail',
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.scss']
})
export class OrderDetailComponent implements OnInit {
  order: Order | null = null;
  isLoading = false;
  isUpdating = false;
  errorMessage: string | null = null;
  canManage = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    if (!this.auth.getAccessToken()) {
      this.router.navigate(['/login']);
      return;
    }

    this.canManage = this.auth.canManageOrders();

    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? Number(idParam) : NaN;
    if (!id || Number.isNaN(id)) {
      this.errorMessage = '注文IDが不正です';
      return;
    }
    this.loadOrder(id);
  }

  loadOrder(id: number): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.orderService.getOrder(id).subscribe({
      next: (res) => {
        this.order = res;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = '注文詳細の取得に失敗しました';
      }
    });
  }

  statusLabel(status: string): string {
    return this.orderService.statusLabel(status);
  }

  onUpdateStatus(status: 'SHIPPING' | 'COMPLETED'): void {
    if (!this.order || this.isUpdating) return;

    this.isUpdating = true;
    this.errorMessage = null;

    this.orderService.updateStatus(this.order.id, status).subscribe({
      next: (res) => {
        this.order = res.order;
        this.isUpdating = false;
      },
      error: (err) => {
        this.isUpdating = false;
        this.errorMessage =
          err.error?.message ||
          (typeof err.error === 'string' ? err.error : null) ||
          'ステータス更新に失敗しました';
      }
    });
  }

  onBack(): void {
    this.router.navigate(['/orders']);
  }
}

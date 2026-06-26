import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin, of, Subject } from 'rxjs';
import { catchError, map, takeUntil } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { CartItem, CartService } from '../../services/cart.service';
import { Order, OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';

interface CartLine {
  productId: number;
  qty: number;
  name: string;
  price: number;
  lineTotal: number;
}

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit, OnDestroy {
  lines: CartLine[] = [];
  cancellableOrders: Order[] = [];
  totalAmount = 0;
  isLoading = false;
  isLoadingOrders = false;
  isSubmitting = false;
  isLoggedIn = false;
  cancellingOrderId: number | null = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  private readonly destroy$ = new Subject<void>();
  private readonly productUrlBase = 'http://localhost:8080/api/products';

  get expectedPoints(): number {
    return Math.floor(this.totalAmount / 100);
  }

  constructor(
    private cart: CartService,
    private http: HttpClient,
    private orderService: OrderService,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.auth.isLoggedIn();
    this.loadCart();
    this.loadCancellableOrders();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadCart(): void {
    const items = this.cart.getItems();
    if (items.length === 0) {
      this.lines = [];
      this.totalAmount = 0;
      this.isLoading = false;
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    forkJoin(
      items.map((item) =>
        this.http.get<any>(`${this.productUrlBase}/${item.productId}`).pipe(
          map((product) => this.toLine(item, product)),
          catchError(() => of(null))
        )
      )
    )
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (results) => {
          this.lines = results.filter((x): x is CartLine => x !== null);
          this.totalAmount = this.lines.reduce((sum, line) => sum + line.lineTotal, 0);
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
          this.errorMessage = 'カート内容の読み込みに失敗しました';
        }
      });
  }

  onRemoveLine(line: CartLine): void {
    this.cart.remove(line.productId);
    this.loadCart();
    this.successMessage = `「${line.name}」をカートから削除しました`;
  }

  onCheckout(): void {
    if (!this.auth.getAccessToken()) {
      this.router.navigate(['/login']);
      return;
    }

    if (this.lines.length === 0) {
      this.errorMessage = 'カートが空です';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = null;
    this.successMessage = null;

    const payload = this.lines.map((line) => ({
      productId: line.productId,
      qty: line.qty
    }));

    this.orderService.createOrder(payload)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res) => {
          this.cart.clear();
          this.isSubmitting = false;
          this.successMessage = `注文 ${res.order.orderNumber} を作成しました。キャンセルする場合は下の注文一覧から操作できます。`;
          this.loadCart();
          this.loadCancellableOrders(true);
        },
        error: (err) => {
          this.isSubmitting = false;
          this.errorMessage =
            err.error?.message ||
            (typeof err.error === 'string' ? err.error : null) ||
            '注文の作成に失敗しました';
        }
      });
  }

  onCancelOrder(order: Order): void {
    if (!this.orderService.canCancel(order.status)) {
      return;
    }

    const confirmed = window.confirm(
      `注文 ${order.orderNumber} をキャンセルしますか？\n在庫は元に戻ります。`
    );
    if (!confirmed) {
      return;
    }

    this.cancellingOrderId = order.id;
    this.errorMessage = null;
    this.successMessage = null;

    this.orderService.cancelOrder(order.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res) => {
          this.cancellingOrderId = null;
          this.successMessage = res.message || `注文 ${order.orderNumber} をキャンセルしました`;
          this.loadCancellableOrders(true);
        },
        error: (err) => {
          this.cancellingOrderId = null;
          this.errorMessage =
            err.error?.message ||
            (typeof err.error === 'string' ? err.error : null) ||
            '注文のキャンセルに失敗しました';
        }
      });
  }

  statusLabel(status: string): string {
    return this.orderService.statusLabel(status);
  }

  isCancelling(orderId: number): boolean {
    return this.cancellingOrderId === orderId;
  }

  private loadCancellableOrders(refresh = false): void {
    if (!this.auth.getAccessToken()) {
      this.cancellableOrders = [];
      this.isLoadingOrders = false;
      return;
    }

    this.isLoadingOrders = true;
    this.orderService.getMyOrders(refresh)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res) => {
          this.cancellableOrders = (res.orders ?? []).filter((order) =>
            this.orderService.canCancel(order.status)
          );
          this.isLoadingOrders = false;
        },
        error: () => {
          this.cancellableOrders = [];
          this.isLoadingOrders = false;
        }
      });
  }

  private toLine(item: CartItem, product: any): CartLine {
    const price = Number(product.price ?? 0);
    return {
      productId: item.productId,
      qty: item.qty,
      name: product.name,
      price,
      lineTotal: price * item.qty
    };
  }
}

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { CartItem, CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
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
export class CartComponent implements OnInit {
  lines: CartLine[] = [];
  totalAmount = 0;
  isLoading = false;
  isSubmitting = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  private readonly productUrlBase = 'http://localhost:8080/api/products';

  constructor(
    private cart: CartService,
    private http: HttpClient,
    private orderService: OrderService,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    const items = this.cart.getItems();
    if (items.length === 0) {
      this.lines = [];
      this.totalAmount = 0;
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
    ).subscribe({
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

    this.orderService.createOrder(payload).subscribe({
      next: (res) => {
        this.cart.clear();
        this.isSubmitting = false;
        this.successMessage = `注文 ${res.order.orderNumber} を作成しました`;
        setTimeout(() => this.router.navigate(['/orders']), 800);
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

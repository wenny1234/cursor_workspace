import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { shareReplay } from 'rxjs/operators';

export interface OrderItemRequest {
  productId: number;
  qty: number;
}

export interface OrderItem {
  id: number;
  orderId: number;
  productId: number;
  productName: string;
  unitPrice: number;
  quantity: number;
  lineTotal: number;
}

export interface Order {
  id: number;
  orderNumber: string;
  userId: number;
  totalAmount: number;
  status: string;
  shippingAddress?: string;
  createdAt?: string;
  updatedAt?: string;
  items?: OrderItem[];
}

export interface OrdersResponse {
  orders: Order[];
  count: number;
}

export interface CreateOrderResponse {
  message: string;
  order: Order;
}

export interface UserPointsSummary {
  points: number;
  purchasedItemCount: number;
  orderCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly baseUrl = 'http://localhost:8080/api/orders';
  private static readonly POINT_ELIGIBLE_STATUSES = ['PAID', 'SHIPPING', 'COMPLETED'];
  private ordersCache$: Observable<OrdersResponse> | null = null;

  constructor(private http: HttpClient) {}

  createOrder(items: OrderItemRequest[]): Observable<CreateOrderResponse> {
    return this.http.post<CreateOrderResponse>(this.baseUrl, { items }).pipe(
      tap(() => this.invalidateOrdersCache())
    );
  }

  getMyOrders(refresh = false): Observable<OrdersResponse> {
    if (refresh) {
      this.invalidateOrdersCache();
    }
    if (!this.ordersCache$) {
      this.ordersCache$ = this.http.get<OrdersResponse>(this.baseUrl).pipe(
        shareReplay(1)
      );
    }
    return this.ordersCache$;
  }

  invalidateOrdersCache(): void {
    this.ordersCache$ = null;
  }

  getOrder(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.baseUrl}/${id}`);
  }

  updateStatus(id: number, status: string): Observable<{ message: string; order: Order }> {
    return this.http.patch<{ message: string; order: Order }>(
      `${this.baseUrl}/${id}/status`,
      { status }
    ).pipe(tap(() => this.invalidateOrdersCache()));
  }

  cancelOrder(id: number): Observable<{ message: string; order: Order }> {
    return this.http.post<{ message: string; order: Order }>(
      `${this.baseUrl}/${id}/cancel`,
      {}
    ).pipe(tap(() => this.invalidateOrdersCache()));
  }

  canCancel(status: string): boolean {
    return status === 'PAID';
  }

  statusClass(status?: string): string {
    return status ? status.toLowerCase() : 'unknown';
  }

  statusLabel(status: string): string {
    switch (status) {
      case 'PENDING':
        return '処理待ち';
      case 'PAID':
        return '支払済';
      case 'SHIPPING':
        return '配送中';
      case 'COMPLETED':
        return '完了';
      case 'CANCELLED':
        return 'キャンセル';
      default:
        return status;
    }
  }

  /** 購入済み注文からポイントを算出（100円 = 1ポイント） */
  calculateUserPoints(orders: Order[]): UserPointsSummary {
    let points = 0;
    let purchasedItemCount = 0;
    let orderCount = 0;

    for (const order of orders) {
      if (!OrderService.POINT_ELIGIBLE_STATUSES.includes(order.status)) {
        continue;
      }
      orderCount += 1;
      points += Math.floor((order.totalAmount ?? 0) / 100);
      for (const item of order.items ?? []) {
        purchasedItemCount += item.quantity ?? 0;
      }
    }

    return { points, purchasedItemCount, orderCount };
  }
}

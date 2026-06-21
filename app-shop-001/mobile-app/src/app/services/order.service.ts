import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly baseUrl = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) {}

  createOrder(items: OrderItemRequest[]): Observable<CreateOrderResponse> {
    return this.http.post<CreateOrderResponse>(this.baseUrl, { items });
  }

  getMyOrders(): Observable<OrdersResponse> {
    return this.http.get<OrdersResponse>(this.baseUrl);
  }

  getOrder(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.baseUrl}/${id}`);
  }

  updateStatus(id: number, status: string): Observable<{ message: string; order: Order }> {
    return this.http.patch<{ message: string; order: Order }>(
      `${this.baseUrl}/${id}/status`,
      { status }
    );
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
}

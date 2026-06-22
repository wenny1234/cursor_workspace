export const API_BASE = 'http://localhost:8080/api';

export interface LoginResponse {
  token: string;
  refreshToken: string;
  username: string;
  role: string;
}

export interface OrderItem {
  id: number;
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
  status: string;
}

const TOKEN_KEY = 'wms_token';

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY);
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = getToken();
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string> | undefined),
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const text = await response.text();
    let message = text;
    try {
      const json = JSON.parse(text);
      message = json.message || json.error || text;
    } catch {
      // keep text
    }
    throw new Error(message || `HTTP ${response.status}`);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export function login(username: string, password: string): Promise<LoginResponse> {
  return request<LoginResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });
}

export function fetchOrders(status: 'PAID' | 'SHIPPING'): Promise<OrdersResponse> {
  return request<OrdersResponse>(`/wms/orders?status=${status}`);
}

export function shipOrder(id: number, shippingAddress: string): Promise<{ message: string; order: Order }> {
  return request<{ message: string; order: Order }>(`/wms/orders/${id}/ship`, {
    method: 'POST',
    body: JSON.stringify({ shippingAddress }),
  });
}

export function statusLabel(status: string): string {
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

export function formatYen(amount: number): string {
  return new Intl.NumberFormat('ja-JP', {
    style: 'currency',
    currency: 'JPY',
    maximumFractionDigits: 0,
  }).format(amount);
}

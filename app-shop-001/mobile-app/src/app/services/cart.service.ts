import { Injectable } from '@angular/core';

export interface CartItem {
  productId: number;
  qty: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly key = 'cartItems';

  private read(): CartItem[] {
    const raw = localStorage.getItem(this.key);
    if (!raw) return [];
    try {
      const parsed = JSON.parse(raw) as CartItem[];
      return Array.isArray(parsed) ? parsed : [];
    } catch {
      return [];
    }
  }

  private write(items: CartItem[]): void {
    localStorage.setItem(this.key, JSON.stringify(items));
  }

  getItems(): CartItem[] {
    return this.read();
  }

  getTotalQty(): number {
    return this.read().reduce((sum, it) => sum + (it.qty ?? 0), 0);
  }

  add(productId: number, qty: number = 1): void {
    if (!productId || productId <= 0) return;
    const items = this.read();

    const idx = items.findIndex((x) => x.productId === productId);
    if (idx >= 0) {
      items[idx] = { ...items[idx], qty: items[idx].qty + qty };
    } else {
      items.push({ productId, qty });
    }

    this.write(items);
  }

  clear(): void {
    this.write([]);
  }

  remove(productId: number): void {
    const items = this.read().filter((x) => x.productId !== productId);
    this.write(items);
  }
}


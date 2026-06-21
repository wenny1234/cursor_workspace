import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { resolveApiUrl } from '../../config/api.config';

export interface Product {
  id: number;
  name: string;
  description?: string;
  price: number;
  stock?: number;
  category?: string;
  imageUrl?: string;
  inStock?: boolean;
  formattedPrice?: string;
}

export interface ProductsResponse {
  products: Product[];
  count: number;
  timestamp: string;
}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  products: Product[] = [];
  isLoading = false;
  errorMessage: string | null = null;

  // backend の application.yml の context-path が /api のため /api を含めています
  private readonly productsUrl = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  imageUrl(path?: string | null): string {
    return resolveApiUrl(path);
  }

  ngOnInit(): void {
    this.loadProducts();
  }

  private loadProducts(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.http.get<ProductsResponse>(this.productsUrl).subscribe({
      next: (res) => {
        this.products = res.products ?? [];
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage =
          err?.error?.message ||
          (typeof err?.error === 'string' ? err.error : null) ||
          '商品一覧の取得に失敗しました。';
      }
    });
  }
}

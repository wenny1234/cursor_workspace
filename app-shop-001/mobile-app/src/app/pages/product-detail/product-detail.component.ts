import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CartService } from '../../services/cart.service';
import { resolveApiUrl } from '../../config/api.config';

export interface ProductDetail {
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

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss']
})
export class ProductDetailComponent implements OnInit {
  product: ProductDetail | null = null;
  isLoading = false;
  errorMessage: string | null = null;

  // backend の application.yml の context-path が /api のため /api を含めています
  private readonly productUrlBase = 'http://localhost:8080/api/products';

  addResult: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private cart: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? Number(idParam) : NaN;
    if (!id || Number.isNaN(id)) {
      this.errorMessage = '商品IDが不正です';
      return;
    }
    this.loadProduct(id);
  }

  private loadProduct(id: number): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.addResult = null;

    this.http.get<ProductDetail>(`${this.productUrlBase}/${id}`).subscribe({
      next: (res) => {
        this.product = res;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = '商品情報の取得に失敗しました';
      }
    });
  }

  onAddToCart(): void {
    if (!this.product) return;
    this.cart.add(this.product.id, 1);
    this.addResult = 'カートに追加しました';
  }

  onBack(): void {
    this.router.navigate(['/']);
  }

  productImageUrl(path?: string | null): string {
    return resolveApiUrl(path);
  }
}

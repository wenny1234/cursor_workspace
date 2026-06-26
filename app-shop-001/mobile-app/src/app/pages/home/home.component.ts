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
  createdAt?: string;
}

export interface ProductsResponse {
  products: Product[];
  count: number;
  timestamp: string;
}

interface MainCategory {
  id: string;
  label: string;
  icon: string;
}

interface SubFilter {
  id: string;
  label: string;
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
  selectedMain = 'all';
  selectedSub = 'all';
  favorites = new Set<number>();

  readonly mainCategories: MainCategory[] = [
    { id: '家電', label: '家電', icon: '🔌' },
    { id: '小家具', label: '小家具', icon: '🪑' },
    { id: '生活雑貨', label: '生活雑貨', icon: '📦' },
  ];

  readonly subFilters: Record<string, SubFilter[]> = {
    家電: [
      { id: 'all', label: 'すべて' },
      { id: '冷蔵庫', label: '冷蔵庫' },
      { id: '洗濯機', label: '洗濯機' },
      { id: '炊飯器', label: '炊飯器' },
      { id: 'エアコン', label: 'エアコン' },
      { id: '電子レンジ', label: '電子レンジ' },
      { id: '掃除機', label: '掃除機' },
    ],
    小家具: [
      { id: 'all', label: 'すべて' },
      { id: '椅子', label: '椅子' },
      { id: 'テーブル', label: 'テーブル' },
      { id: 'ベッド', label: 'ベッド' },
    ],
    生活雑貨: [
      { id: 'all', label: 'すべて' },
      { id: '収納', label: '収納' },
      { id: 'キッチン小物', label: 'キッチン小物' },
      { id: '清掃', label: '清掃' },
      { id: 'その他', label: 'その他' },
    ],
  };

  private readonly locations = [
    '東京都江東区',
    '東京都墨田区',
    '東京都世田谷区',
    '東京都杉並区',
    '東京都練馬区',
    '東京都足立区',
    '神奈川県横浜市',
    '埼玉県さいたま市',
  ];

  private readonly productsUrl = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  imageUrl(path?: string | null): string {
    return resolveApiUrl(path);
  }

  get activeSubFilters(): SubFilter[] {
    if (this.selectedMain === 'all') {
      return [];
    }
    return this.subFilters[this.selectedMain] ?? [];
  }

  get filteredProducts(): Product[] {
    return this.products.filter((p) => this.matchesFilter(p));
  }

  get onlineProducts(): Product[] {
    return this.filteredProducts.filter((p) => p.inStock !== false).slice(0, 10);
  }

  get feedProducts(): Product[] {
    return this.filteredProducts;
  }

  selectMain(mainId: string): void {
    this.selectedMain = this.selectedMain === mainId ? 'all' : mainId;
    this.selectedSub = 'all';
  }

  selectSub(subId: string): void {
    this.selectedSub = subId;
  }

  matchesFilter(product: Product): boolean {
    const category = product.category ?? '';
    if (this.selectedMain === 'all') {
      return true;
    }
    if (!category.startsWith(this.selectedMain)) {
      return false;
    }
    if (this.selectedSub === 'all') {
      return true;
    }
    return category === `${this.selectedMain}-${this.selectedSub}`;
  }

  formatPrice(price: number): string {
    if (price === 0) {
      return '0円';
    }
    return `${price.toLocaleString('ja-JP')}円`;
  }

  filterMainLabel(): string {
    return this.mainCategories.find((c) => c.id === this.selectedMain)?.label ?? this.selectedMain;
  }

  filterSubLabel(): string {
    const subs = this.subFilters[this.selectedMain] ?? [];
    return subs.find((s) => s.id === this.selectedSub)?.label ?? this.selectedSub;
  }

  categoryLabel(product: Product): string {
    const category = product.category ?? '';
    if (!category) {
      return '中古';
    }
    const parts = category.split('-');
    if (parts.length === 2) {
      return parts[1];
    }
    return category;
  }

  mainCategoryLabel(product: Product): string {
    const category = product.category ?? '';
    return category.split('-')[0] || '中古';
  }

  locationLabel(product: Product): string {
    return this.locations[product.id % this.locations.length];
  }

  postedDate(product: Product): string {
    if (product.createdAt) {
      const d = new Date(product.createdAt);
      const mm = String(d.getMonth() + 1).padStart(2, '0');
      const dd = String(d.getDate()).padStart(2, '0');
      return `${mm}/${dd}`;
    }
    const mm = String((product.id % 12) + 1).padStart(2, '0');
    const dd = String((product.id % 28) + 1).padStart(2, '0');
    return `${mm}/${dd}`;
  }

  isFavorite(id: number): boolean {
    return this.favorites.has(id);
  }

  toggleFavorite(id: number, event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    if (this.favorites.has(id)) {
      this.favorites.delete(id);
    } else {
      this.favorites.add(id);
    }
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

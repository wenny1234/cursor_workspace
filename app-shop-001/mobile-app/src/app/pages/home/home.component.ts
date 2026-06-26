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

interface CategoryItem {
  label: string;
  icon: string;
}

interface FeaturedTag {
  id: string;
  label: string;
  badge: string;
  badgeClass: string;
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
  selectedTag = 'popular';
  favorites = new Set<number>();

  readonly categories: CategoryItem[] = [
    { label: '売買', icon: '🛒' },
    { label: 'メンバー', icon: '👥' },
    { label: '中古車', icon: '🚗' },
    { label: '助け合い', icon: '🏠' },
    { label: 'イベント', icon: '🎈' },
    { label: 'バイト', icon: '👷' },
    { label: '正社員', icon: '👔' },
    { label: '教室', icon: '🎓' },
    { label: '不動産', icon: '🏢' },
    { label: 'お店', icon: '🏪' },
    { label: '里親', icon: '🐾' },
  ];

  readonly featuredTags: FeaturedTag[] = [
    { id: 'popular', label: '人気', badge: 'UP', badgeClass: 'badge-up' },
    { id: 'new', label: '新着', badge: 'NEW', badgeClass: 'badge-new' },
    { id: 'free', label: '0円でもらう', badge: '¥0', badgeClass: 'badge-free' },
    { id: 'daily', label: '日払いバイト', badge: '💴', badgeClass: 'badge-job' },
    { id: 'rent', label: '敷金礼金0物件', badge: '🏠', badgeClass: 'badge-rent' },
    { id: 'kids', label: '子供用品', badge: '👶', badgeClass: 'badge-kids' },
  ];

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

  selectTag(tagId: string): void {
    this.selectedTag = tagId;
  }

  get onlineProducts(): Product[] {
    return this.products.filter((p) => p.inStock !== false).slice(0, 10);
  }

  get feedProducts(): Product[] {
    let list = [...this.products];

    switch (this.selectedTag) {
      case 'new':
        list.sort((a, b) => b.id - a.id);
        break;
      case 'free':
        list = list.filter((p) => p.price === 0);
        break;
      case 'kids':
        list = list.filter((p) => (p.category ?? '').includes('子供') || (p.category ?? '').includes('ベビー'));
        break;
      default:
        list.sort((a, b) => (b.stock ?? 0) - (a.stock ?? 0));
        break;
    }

    return list;
  }

  formatPrice(price: number): string {
    if (price === 0) {
      return '0円';
    }
    return `${price.toLocaleString('ja-JP')}円`;
  }

  categoryLabel(product: Product): string {
    return product.category || '売買';
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

# wms-admin-shop-springboot-one

WMS・マスタ管理統合画面（Spring Boot 4 版）。React 版 `wms-shop-manage` は変更せず、本プロジェクトで同等＋拡張機能を提供します。

## 技術スタック

| 項目 | バージョン |
|------|-----------|
| Spring Boot | 4.0.7 |
| Java | 17+ |
| Thymeleaf | ページエンジン |
| Bootstrap | 5.3 |
| jQuery | 3.7 |
| jqGrid (free-jqgrid) | 4.15 |
| MyBatis + Oracle | 共有 DB |

## 機能

- **売上統計**: 売上総額・注文数・商品/ユーザー数
- **出荷処理**: 支払済注文の jqGrid 一覧、送り場所登録、出荷済み更新
- **商品業務**: 一覧検索、登録/更新（画像アップロード）、無効化
- **ユーザー業務**: 一覧検索、登録/更新（パスワード 8-12 桁 3 種混合）、無効化
- **最近の注文履歴**: 直近 50 件

UI は**ヘッダーメニュー**と**右側メニュー**を切り替えて利用できます（設定はブラウザに保存）。

## 起動

```powershell
cd d:\cursor_workspace\app-shop-001\wms-admin-shop-springboot-one
$env:SPRING_PROFILES_ACTIVE = "oracle"
mvn spring-boot:run
```

- URL: http://localhost:8082
- サンプル: `staff` / `staff123`（ADMIN / STAFF のみ）

## ポート

| アプリ | ポート |
|--------|--------|
| mobile-app-backend | 8080 |
| admin-shop-manage | 8081 |
| **wms-admin-shop-springboot-one** | **8082** |
| wms-shop-manage (React) | 3000 |

## 備考

- Oracle に直接接続。`mobile-app-backend` なしでも動作します。
- 画像は `../data/images` に保存、`/files/{filename}` で参照。

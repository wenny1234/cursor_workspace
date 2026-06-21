# CSV → Oracle インポート

## 通常運用（既定）

起動時の CSV インポートは **オフ** です。Oracle に既に入っているデータをそのまま使います。

```powershell
cd D:\cursor_workspace\app-shop-001\mobile-app-backend
$env:SPRING_PROFILES_ACTIVE='oracle'
mvn spring-boot:run
```

## CSV を再取り込みしたい場合のみ

`data/csv/users.csv` と `data/csv/products.csv` を読み込み、
Oracle の `users` / `products` テーブルへ再投入します（既存行は削除してから再投入）。

```powershell
cd D:\cursor_workspace\app-shop-001\mobile-app-backend
$env:SPRING_PROFILES_ACTIVE='oracle'
$env:ORACLE_IMPORT_CSV='true'
mvn spring-boot:run
```

環境変数:

| 変数 | 説明 | 既定値 |
|------|------|--------|
| `ORACLE_IMPORT_CSV` | 起動時に CSV を取り込む | `false` |
| `CSV_DATA_DIR` | CSV ディレクトリ | `../data/csv` |

## 取り込まれるデータ（現時点）

- **users**: 4 件（admin, staff, viewer, testuser）
- **products**: 9 件（ID 1〜9、最後は `New Test Product`）

CSV の **ID はそのまま保持** され、シーケンスも `MAX(id)+1` に合わせて再設定されます。

## 手動で再実行したい場合

1. CSV を編集: `D:\cursor_workspace\app-shop-001\data\csv\`
2. バックエンドを `oracle` プロファイルで再起動（`ORACLE_IMPORT_CSV=true`）

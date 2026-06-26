#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Oracle 用 INSERT SQL を products-furniture-appliances.json から生成する"""

import json
from datetime import datetime
from pathlib import Path

from product_image_utils import image_url

JSON_FILE = Path(__file__).resolve().parent / "products-furniture-appliances.json"
SQL_FILE = Path(__file__).resolve().parents[1] / "oracle" / "08_seed_furniture_appliances.sql"

TS = datetime.now().strftime("%Y-%m-%d %H:%M:%S")


def esc(s: str) -> str:
    return s.replace("'", "''")


def main():
    products = json.loads(JSON_FILE.read_text(encoding="utf-8"))
    lines = [
        "-- 中古家具・小家电サンプルデータ",
        "-- Run: wsl -e sh -c \"docker cp /mnt/d/cursor_workspace/app-shop-001/database/oracle/08_seed_furniture_appliances.sql oracle-db:/tmp/08_seed_furniture_appliances.sql && docker exec -i oracle-db sqlplus -s shop/ShopPassword123@XEPDB1 @/tmp/08_seed_furniture_appliances.sql\"",
        "",
        "DELETE FROM order_items;",
        "DELETE FROM orders;",
        "DELETE FROM products;",
        "COMMIT;",
        "",
    ]

    for i, p in enumerate(products, start=1):
        img = image_url(i)
        lines.append(
            "INSERT INTO products (id, name, description, price, stock, category, image_url, created_at, updated_at) VALUES "
            f"({i}, '{esc(p['name'])}', '{esc(p['desc'])}', {p['price']}, {p.get('stock', 1)}, "
            f"'{esc(p['category'])}', '{esc(img)}', "
            f"TIMESTAMP '{TS}', TIMESTAMP '{TS}');"
        )

    max_id = len(products)
    lines.extend([
        "",
        "COMMIT;",
        "",
        "BEGIN",
        "  EXECUTE IMMEDIATE 'DROP SEQUENCE products_seq';",
        "EXCEPTION WHEN OTHERS THEN NULL;",
        "END;",
        "/",
        f"CREATE SEQUENCE products_seq START WITH {max_id + 1} INCREMENT BY 1 NOCACHE NOCYCLE;",
        "",
        "SELECT category, COUNT(*) AS cnt FROM products GROUP BY category ORDER BY category;",
        "",
        "EXIT;",
        "",
    ])

    SQL_FILE.write_text("\n".join(lines), encoding="utf-8")
    print(f"Wrote {len(products)} products -> {SQL_FILE}")


if __name__ == "__main__":
    main()

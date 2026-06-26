#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Oracle: 商品 category を更新"""

import json
import sys
from datetime import datetime
from pathlib import Path

if sys.stdout.encoding != "utf-8":
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", errors="replace")

JSON_FILE = Path(__file__).resolve().parent / "products-furniture-appliances.json"
SQL_FILE = Path(__file__).resolve().parents[1] / "oracle" / "10_update_product_categories.sql"


def esc(s: str) -> str:
    return s.replace("'", "''")


def main():
    products = json.loads(JSON_FILE.read_text(encoding="utf-8"))
    ts = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    lines = [
        "-- 商品分类更新（家电 / 小家具 / 生活杂物）",
        f"-- Generated: {ts}",
        "",
    ]
    for i, product in enumerate(products, start=1):
        cat = esc(product["category"])
        lines.append(
            f"UPDATE products SET category = '{cat}', updated_at = TIMESTAMP '{ts}' WHERE id = {i};"
        )

    lines.extend(["", "COMMIT;", "", "SELECT category, COUNT(*) AS cnt FROM products GROUP BY category ORDER BY category;", "", "EXIT;", ""])
    SQL_FILE.write_text("\n".join(lines), encoding="utf-8")
    print(f"Wrote {len(products)} updates -> {SQL_FILE}")


if __name__ == "__main__":
    main()

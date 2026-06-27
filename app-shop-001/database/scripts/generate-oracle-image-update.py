#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Oracle: 商品 image_url のみ更新"""

import json
import sys
from datetime import datetime
from pathlib import Path

if sys.stdout.encoding != "utf-8":
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", errors="replace")

from product_image_utils import image_url

JSON_FILE = Path(__file__).resolve().parent / "products-furniture-appliances.json"
SQL_FILE = Path(__file__).resolve().parents[1] / "oracle" / "09_update_product_images.sql"


def esc(s: str) -> str:
    return s.replace("'", "''")


def main():
    products = json.loads(JSON_FILE.read_text(encoding="utf-8"))
    ts = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    lines = [
        "-- 商品画像 URL 更新（家具・家電 PNG 実写）",
        f"-- Generated: {ts}",
        "",
    ]
    for i, _product in enumerate(products, start=1):
        url = esc(image_url(i))
        lines.append(f"UPDATE products SET image_url = '{url}', updated_at = TIMESTAMP '{ts}' WHERE id = {i};")

    lines.extend(["", "COMMIT;", "", "SELECT id, name, image_url FROM products ORDER BY id;", "", "EXIT;", ""])
    SQL_FILE.write_text("\n".join(lines), encoding="utf-8")
    print(f"Wrote {len(products)} updates -> {SQL_FILE}")


if __name__ == "__main__":
    main()

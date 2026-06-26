#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""products-furniture-appliances.json から data/csv/products.csv を生成する"""

import csv
import json
from datetime import datetime
from pathlib import Path

from product_image_utils import image_url

ROOT = Path(__file__).resolve().parents[2]
JSON_FILE = Path(__file__).resolve().parent / "products-furniture-appliances.json"
CSV_FILE = ROOT / "data" / "csv" / "products.csv"

TS = datetime.now().replace(microsecond=0).isoformat()


def main():
    products = json.loads(JSON_FILE.read_text(encoding="utf-8"))
    rows = []
    for i, p in enumerate(products, start=1):
        rows.append({
            "ID": str(i),
            "NAME": p["name"],
            "DESCRIPTION": p["desc"],
            "PRICE": str(p["price"]),
            "STOCK": str(p.get("stock", 1)),
            "CATEGORY": p["category"],
            "IMAGEURL": image_url(i),
            "CREATEDAT": TS,
            "UPDATEDAT": TS,
        })

    fieldnames = ["CATEGORY", "CREATEDAT", "DESCRIPTION", "ID", "IMAGEURL", "NAME", "PRICE", "STOCK", "UPDATEDAT"]
    with CSV_FILE.open("w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames, quoting=csv.QUOTE_ALL)
        writer.writeheader()
        for row in rows:
            writer.writerow(row)

    furniture = sum(1 for p in products if p["category"] == "家具")
    appliances = sum(1 for p in products if p["category"] == "小家电")
    print(f"Generated {len(products)} products -> {CSV_FILE}")
    print(f"  furniture={furniture}  appliances={appliances}")


if __name__ == "__main__":
    main()

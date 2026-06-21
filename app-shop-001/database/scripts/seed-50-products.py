#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""50件のサンプル商品を admin-shop-manage API 経由で登録する"""

import json
import shutil
import sys
import io
from pathlib import Path

if sys.stdout.encoding != "utf-8":
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", errors="replace")

try:
    import requests
except ImportError:
    print("requests が必要です: pip install requests")
    sys.exit(1)

BASE_DIR = Path(__file__).resolve().parents[2]
IMAGES_DIR = BASE_DIR / "data" / "images"
DATA_FILE = Path(__file__).resolve().parent / "products-50.json"
BASE_URL = "http://localhost:8081"

SOURCE_IMAGES = [
    "product-1-smartphone.png",
    "product-2-laptop.png",
    "product-3-tshirt.png",
    "product-4-jeans.png",
    "product-5-coffee-maker.png",
    "product-6-earbuds.png",
    "product-7-sneakers.png",
    "product-8-backpack.png",
]


def main():
    products = json.loads(DATA_FILE.read_text(encoding="utf-8"))
    session = requests.Session()

    # ログイン
    session.get(f"{BASE_URL}/login")
    resp = session.post(
        f"{BASE_URL}/login",
        data={"username": "admin", "password": "admin123"},
        allow_redirects=False,
    )
    if resp.status_code not in (302, 200):
        print(f"ログイン失敗: HTTP {resp.status_code}")
        sys.exit(1)

    existing = session.get(f"{BASE_URL}/api/products").json()
    existing_images = {p.get("imageUrl", "") for p in existing}

    created = 0
    failed = 0
    skipped = 0

    for i, p in enumerate(products):
        num = i + 10
        dest_name = f"product-{num}-seed.png"
        image_url = f"/api/files/{dest_name}"

        if image_url in existing_images:
            skipped += 1
            continue

        src = SOURCE_IMAGES[i % len(SOURCE_IMAGES)]
        shutil.copy2(IMAGES_DIR / src, IMAGES_DIR / dest_name)

        payload = {
            "name": p["name"],
            "description": p["desc"],
            "price": p["price"],
            "stock": p["stock"],
            "category": p["category"],
            "imageUrl": image_url,
            "active": True,
        }

        r = session.post(f"{BASE_URL}/api/products", json=payload)
        if r.status_code == 200:
            created += 1
            print(f"  OK [{created}/{len(products) - skipped}] {p['name']}")
        else:
            failed += 1
            print(f"  NG {p['name']}: {r.status_code} {r.text[:200]}")

    total = session.get(f"{BASE_URL}/api/products").json()
    print(f"\n完了: {created} 件登録, {skipped} 件スキップ, {failed} 件失敗")
    print(f"商品総数: {len(total)} 件")


if __name__ == "__main__":
    main()

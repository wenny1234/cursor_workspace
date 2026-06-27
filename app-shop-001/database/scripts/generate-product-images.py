#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""家具・家電向け Pexels 実写をダウンロードし PNG として保存"""

import json
import sys
import urllib.error
import urllib.request
from io import BytesIO
from pathlib import Path

if sys.stdout.encoding != "utf-8":
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", errors="replace")

from PIL import Image

from product_image_utils import (
    IMAGES_DIR,
    image_filename,
    local_image_path,
    photo_ids_for_product,
    pexels_image_url,
)

JSON_FILE = Path(__file__).resolve().parent / "products-furniture-appliances.json"
USER_AGENT = "app-shop-001-product-images/1.0 (local dev; educational)"
OUTPUT_SIZE = 480


def download_bytes(url: str) -> bytes:
    req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    with urllib.request.urlopen(req, timeout=60) as resp:
        data = resp.read()
    if len(data) < 2048:
        raise ValueError(f"Download too small ({len(data)} bytes)")
    return data


def save_as_png(data: bytes, dest: Path) -> None:
    img = Image.open(BytesIO(data)).convert("RGB")
    if img.width != OUTPUT_SIZE or img.height != OUTPUT_SIZE:
        img = img.resize((OUTPUT_SIZE, OUTPUT_SIZE), Image.Resampling.LANCZOS)
    dest.parent.mkdir(parents=True, exist_ok=True)
    img.save(dest, format="PNG", optimize=True)


def download_product_image(product_id: int, name: str, category: str) -> str:
    last_error: Exception | None = None
    for photo_id in photo_ids_for_product(product_id, name, category):
        url = pexels_image_url(photo_id)
        try:
            save_as_png(download_bytes(url), local_image_path(product_id))
            return photo_id
        except (urllib.error.URLError, ValueError, OSError, TimeoutError) as ex:
            last_error = ex
    raise RuntimeError(f"All sources failed for product {product_id}: {last_error}")


def remove_legacy_svgs() -> int:
    removed = 0
    for svg in IMAGES_DIR.glob("product-fa-*.svg"):
        svg.unlink()
        removed += 1
    return removed


def write_product_images(products: list[dict]) -> None:
    IMAGES_DIR.mkdir(parents=True, exist_ok=True)
    ok = 0
    for i, product in enumerate(products, start=1):
        try:
            photo_id = download_product_image(i, product["name"], product["category"])
            ok += 1
            print(f"  [{i:02d}] {image_filename(i)} <- pexels:{photo_id}")
        except RuntimeError as ex:
            print(f"  [{i:02d}] FAILED: {ex}", file=sys.stderr)
    print(f"Downloaded {ok}/{len(products)} PNG -> {IMAGES_DIR}")


def main():
    products = json.loads(JSON_FILE.read_text(encoding="utf-8"))
    write_product_images(products)
    removed = remove_legacy_svgs()
    if removed:
        print(f"Removed {removed} legacy SVG files")


if __name__ == "__main__":
    main()

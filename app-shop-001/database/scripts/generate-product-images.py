#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""家具・小家电向け SVG 商品画像を生成する"""

import json
import sys
from pathlib import Path

if sys.stdout.encoding != "utf-8":
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", errors="replace")

from product_image_utils import IMAGES_DIR, write_product_images

JSON_FILE = Path(__file__).resolve().parent / "products-furniture-appliances.json"


def main():
    products = json.loads(JSON_FILE.read_text(encoding="utf-8"))
    write_product_images(products)
    print(f"Generated {len(products)} images -> {IMAGES_DIR}")


if __name__ == "__main__":
    main()

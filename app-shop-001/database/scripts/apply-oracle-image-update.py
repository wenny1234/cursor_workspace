#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""09_update_product_images.sql を Oracle に適用する"""

import os
import sys
from pathlib import Path

if sys.stdout.encoding != "utf-8":
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", errors="replace")

try:
    import oracledb
except ImportError:
    print("oracledb が未インストールです: pip install oracledb", file=sys.stderr)
    sys.exit(1)

SQL_FILE = Path(__file__).resolve().parents[1] / "oracle" / "09_update_product_images.sql"

USER = os.environ.get("ORACLE_USER", "shop")
PASSWORD = os.environ.get("ORACLE_PASSWORD", "ShopPassword123")
DSN = os.environ.get("ORACLE_DSN", "localhost:1521/XEPDB1")


def main() -> int:
    if not SQL_FILE.is_file():
        print(f"SQL file not found: {SQL_FILE}", file=sys.stderr)
        return 1

    lines = SQL_FILE.read_text(encoding="utf-8").splitlines()
    updates = [
        line.strip().rstrip(";")
        for line in lines
        if line.strip().upper().startswith("UPDATE ")
    ]

    if not updates:
        print("No UPDATE statements found.", file=sys.stderr)
        return 1

    conn = oracledb.connect(user=USER, password=PASSWORD, dsn=DSN)
    try:
        cur = conn.cursor()
        for stmt in updates:
            cur.execute(stmt)
        conn.commit()

        cur.execute("SELECT COUNT(*) FROM products WHERE image_url LIKE '%.png'")
        png_count = cur.fetchone()[0]
        cur.execute("SELECT COUNT(*) FROM products WHERE image_url LIKE '%.svg'")
        svg_count = cur.fetchone()[0]
        cur.execute(
            "SELECT id, image_url FROM products WHERE id <= 3 ORDER BY id"
        )
        sample = cur.fetchall()
        cur.close()
    finally:
        conn.close()

    print(f"Applied {len(updates)} UPDATE statements from {SQL_FILE.name}")
    print(f"PNG URLs: {png_count}, SVG URLs remaining: {svg_count}")
    for row in sample:
        print(f"  id={row[0]} -> {row[1]}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

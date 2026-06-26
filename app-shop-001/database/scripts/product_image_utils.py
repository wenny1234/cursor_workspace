# -*- coding: utf-8 -*-
"""商品名・カテゴリから画像ファイル名とアイコンを決定"""

from __future__ import annotations

import html
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
IMAGES_DIR = ROOT / "data" / "images"

ICON_RULES: list[tuple[str, str]] = [
    ("ソファベッド", "🛋️"),
    ("ソファ", "🛋️"),
    ("ダイニングセット", "🍽️"),
    ("ダイニングテーブル", "🪑"),
    ("ダイニングチェア", "🪑"),
    ("折りたたみテーブル", "🪑"),
    ("サイドテーブル", "🪵"),
    ("テーブル", "🪑"),
    ("ワークデスク", "🖥️"),
    ("学習机", "📝"),
    ("デスク", "🖥️"),
    ("オフィスチェア", "💺"),
    ("チェア", "💺"),
    ("マットレス", "🛏️"),
    ("すのこベッド", "🛏️"),
    ("ベッド", "🛏️"),
    ("本棚", "📚"),
    ("テレビ台", "📺"),
    ("ローボード", "🗄️"),
    ("チェスト", "🗃️"),
    ("こたつ布団", "🛌"),
    ("こたつ", "♨️"),
    ("衣装ケース", "👕"),
    ("カラーボックス", "📦"),
    ("ドレッサー", "🪞"),
    ("パーテーション", "🚪"),
    ("シューズラック", "👟"),
    ("電子レンジ", "📻"),
    ("オーブン", "🔥"),
    ("炊飯器", "🍚"),
    ("掃除機", "🧹"),
    ("空気清浄機", "🌬️"),
    ("ケトル", "☕"),
    ("ポット", "☕"),
    ("トースター", "🍞"),
    ("扇風機", "🌀"),
    ("加湿器", "💧"),
    ("除湿機", "💨"),
    ("アイロン", "👔"),
    ("ミキサー", "🥤"),
    ("コーヒー", "☕"),
    ("圧力鍋", "🍲"),
    ("IH", "🍳"),
    ("毛布", "🧣"),
    ("ドライヤー", "💇"),
    ("食器洗", "🍽️"),
    ("サンドメーカー", "🥪"),
    ("布団クリーナー", "🛏️"),
    ("冷蔵庫", "🧊"),
    ("洗濯機", "🫧"),
    ("エアコン", "❄️"),
]


def main_category(category: str) -> str:
    return category.split("-", 1)[0] if "-" in category else category


def category_style(category: str) -> tuple[str, str, str]:
    main = main_category(category)
    if main == "小家具":
        return "#fff3e0", "#ef6c00", "#ffcc80"
    if main == "生活雑貨":
        return "#f3e5f5", "#7b1fa2", "#ce93d8"
    return "#e3f2fd", "#1565c0", "#90caf9"


def pick_icon(name: str, category: str) -> str:
    for keyword, icon in ICON_RULES:
        if keyword in name:
            return icon
    main = main_category(category)
    if main == "小家具":
        return "🪑"
    if main == "生活雑貨":
        return "📦"
    return "🔌"


def image_filename(product_id: int) -> str:
    return f"product-fa-{product_id:02d}.svg"


def image_url(product_id: int) -> str:
    return f"/api/files/{image_filename(product_id)}"


def wrap_title(name: str, max_len: int = 14) -> list[str]:
    if len(name) <= max_len:
        return [name]
    parts: list[str] = []
    current = ""
    for ch in name:
        current += ch
        if len(current) >= max_len:
            parts.append(current)
            current = ""
    if current:
        parts.append(current)
    return parts[:2]


def build_svg(product_id: int, name: str, category: str) -> str:
    bg, accent, border = category_style(category)
    icon = pick_icon(name, category)
    lines = wrap_title(name)
    title_y = 300 if len(lines) == 1 else 285
    title_nodes = []
    for idx, line in enumerate(lines):
        y = title_y + idx * 26
        title_nodes.append(
            f'<text x="200" y="{y}" text-anchor="middle" fill="#333" '
            f'font-size="18" font-family="Segoe UI, Hiragino Sans, Meiryo, sans-serif">'
            f"{html.escape(line)}</text>"
        )

    return f"""<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400" viewBox="0 0 400 400">
  <rect width="400" height="400" fill="{bg}"/>
  <rect x="20" y="20" width="360" height="360" rx="16" fill="#fff" stroke="{border}" stroke-width="3"/>
  <rect x="28" y="28" width="80" height="28" rx="14" fill="{accent}"/>
  <text x="68" y="48" text-anchor="middle" fill="#fff" font-size="14" font-weight="700"
        font-family="Segoe UI, Hiragino Sans, Meiryo, sans-serif">{html.escape(category)}</text>
  <text x="200" y="185" text-anchor="middle" font-size="72">{icon}</text>
  {''.join(title_nodes)}
  <text x="200" y="360" text-anchor="middle" fill="#888" font-size="13"
        font-family="Segoe UI, Hiragino Sans, Meiryo, sans-serif">中古 #{product_id:02d}</text>
</svg>
"""


def write_product_images(products: list[dict]) -> None:
    IMAGES_DIR.mkdir(parents=True, exist_ok=True)
    for i, product in enumerate(products, start=1):
        filename = image_filename(i)
        svg = build_svg(i, product["name"], product["category"])
        (IMAGES_DIR / filename).write_text(svg, encoding="utf-8")

# -*- coding: utf-8 -*-
"""商品名・カテゴリから画像ファイル名と Pexels 写真 ID を決定"""

from __future__ import annotations

from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
IMAGES_DIR = ROOT / "data" / "images"

PHOTO_KEY_RULES: list[tuple[str, str]] = [
    ("ソファベッド", "sofa_bed"),
    ("ソファ", "sofa"),
    ("ダイニングセット", "dining_set"),
    ("ダイニングテーブル", "dining_table"),
    ("ダイニングチェア", "chair"),
    ("折りたたみテーブル", "table"),
    ("サイドテーブル", "side_table"),
    ("ワークデスク", "desk"),
    ("学習机", "desk"),
    ("デスク", "desk"),
    ("テーブル", "table"),
    ("オフィスチェア", "office_chair"),
    ("チェア", "chair"),
    ("マットレス", "mattress"),
    ("すのこベッド", "bed"),
    ("ベッド", "bed"),
    ("本棚", "bookshelf"),
    ("テレビ台", "tv_stand"),
    ("ローボード", "cabinet"),
    ("チェスト", "drawer"),
    ("こたつ布団", "kotatsu"),
    ("こたつ", "kotatsu"),
    ("衣装ケース", "storage"),
    ("カラーボックス", "shelf"),
    ("ドレッサー", "dresser"),
    ("パーテーション", "partition"),
    ("シューズラック", "shoe_rack"),
    ("電子レンジ", "microwave"),
    ("オーブン", "oven"),
    ("炊飯器", "rice_cooker"),
    ("掃除機", "vacuum"),
    ("空気清浄機", "air_purifier"),
    ("ケトル", "kettle"),
    ("ポット", "kettle"),
    ("トースター", "toaster"),
    ("扇風機", "fan"),
    ("加湿器", "humidifier"),
    ("除湿機", "dehumidifier"),
    ("アイロン", "iron"),
    ("ミキサー", "blender"),
    ("コーヒー", "coffee_maker"),
    ("圧力鍋", "pressure_cooker"),
    ("IH", "induction"),
    ("毛布", "blanket"),
    ("ドライヤー", "hair_dryer"),
    ("食器洗", "dishwasher"),
    ("サンドメーカー", "sandwich_maker"),
    ("布団クリーナー", "cleaner"),
    ("冷蔵庫", "refrigerator"),
    ("洗濯機", "washing_machine"),
    ("エアコン", "air_conditioner"),
]

# Pexels 実写 ID（家具・家電、Pexels License）
PHOTO_POOL: dict[str, list[str]] = {
    "sofa": ["271897", "1866149", "1350789", "1571468", "1665522"],
    "sofa_bed": ["1648768", "271743", "1454806", "1866149"],
    "dining_set": ["1571460", "1080721", "1571468", "1665522"],
    "dining_table": ["1571460", "1080721", "1571453", "1665522"],
    "table": ["1571460", "1080721", "1571453", "1665522"],
    "desk": ["1336496", "159711", "2452080", "1957478"],
    "side_table": ["1571453", "1080721", "1665522"],
    "office_chair": ["1957478", "1181293", "3771834", "1336496"],
    "chair": ["1181293", "1957478", "3771834", "1571460"],
    "bed": ["1648768", "271743", "1454806", "1866149"],
    "mattress": ["1648768", "271743", "1454806"],
    "bookshelf": ["159711", "1080721", "1571453", "6610109"],
    "tv_stand": ["1571468", "271897", "1665522", "1080721"],
    "cabinet": ["1571453", "6610109", "1080721", "159711"],
    "drawer": ["1571453", "6610109", "159711"],
    "kotatsu": ["1571460", "1080721", "1665522"],
    "storage": ["6610109", "1571453", "159711", "1080721"],
    "shelf": ["159711", "6610109", "1571453"],
    "dresser": ["6610109", "1571453", "1648768"],
    "partition": ["1571468", "1665522", "271897"],
    "shoe_rack": ["6610109", "1571453", "1080721"],
    "microwave": ["2724748", "42059", "699529", "302091"],
    "oven": ["42059", "2724748", "699529"],
    "rice_cooker": ["42059", "699529", "2724748", "302091"],
    "vacuum": ["4107117", "5709062", "5591518"],
    "air_purifier": ["5591518", "5709062", "4107117"],
    "kettle": ["6770525", "42059", "302091"],
    "toaster": ["42059", "2724748", "699529"],
    "fan": ["5591518", "5709062", "1631401"],
    "humidifier": ["5591518", "5709062", "4107117"],
    "dehumidifier": ["5709062", "5591518", "4107117"],
    "iron": ["5709062", "4107117", "5591518"],
    "blender": ["42059", "699529", "2724748"],
    "coffee_maker": ["302899", "302091", "42059"],
    "pressure_cooker": ["42059", "699529", "2724748"],
    "induction": ["42059", "2724748", "699529"],
    "blanket": ["1648768", "271743", "1454806"],
    "hair_dryer": ["5709062", "5591518", "6770525"],
    "dishwasher": ["42059", "699529", "2724748"],
    "sandwich_maker": ["42059", "2724748", "699529"],
    "cleaner": ["4107117", "5709062", "5591518"],
    "refrigerator": ["699529", "2343466", "42059"],
    "washing_machine": ["4107117", "5709062", "5591518"],
    "air_conditioner": ["5591518", "1631401", "5709062"],
    "furniture": ["271897", "1571460", "1336496", "1648768"],
    "appliance": ["42059", "699529", "4107117", "5591518"],
    "misc": ["6610109", "1571453", "1080721"],
}

FALLBACK_POOL = PHOTO_POOL["furniture"] + PHOTO_POOL["appliance"] + PHOTO_POOL["misc"]


def main_category(category: str) -> str:
    return category.split("-", 1)[0] if "-" in category else category


def pick_photo_key(name: str, category: str) -> str:
    for keyword, pool_key in PHOTO_KEY_RULES:
        if keyword in name:
            return pool_key
    main = main_category(category)
    if main == "小家具":
        return "furniture"
    if main == "生活雑貨":
        return "misc"
    if main == "家電":
        return "appliance"
    return "misc"


def photo_ids_for_product(product_id: int, name: str, category: str) -> list[str]:
    key = pick_photo_key(name, category)
    pool = PHOTO_POOL.get(key, FALLBACK_POOL)
    if not pool:
        return list(FALLBACK_POOL)
    start = (product_id - 1) % len(pool)
    ordered = pool[start:] + pool[:start]
    # 重複を除きつつフォールバック候補を広げる
    seen: set[str] = set()
    result: list[str] = []
    for pid in ordered + FALLBACK_POOL:
        if pid not in seen:
            seen.add(pid)
            result.append(pid)
    return result


def pexels_image_url(photo_id: str, size: int = 640) -> str:
    return (
        f"https://images.pexels.com/photos/{photo_id}/pexels-photo-{photo_id}.jpeg"
        f"?auto=compress&cs=tinysrgb&w={size}&h={size}&fit=crop"
    )


def image_filename(product_id: int) -> str:
    return f"product-fa-{product_id:02d}.png"


def image_url(product_id: int) -> str:
    return f"/api/files/{image_filename(product_id)}"


def local_image_path(product_id: int) -> Path:
    return IMAGES_DIR / image_filename(product_id)

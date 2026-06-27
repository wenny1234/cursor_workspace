package com.shop.wms.model;

public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPING,
    COMPLETED,
    CANCELLED;

    public String label() {
        return switch (this) {
            case PENDING -> "処理待ち";
            case PAID -> "支払済";
            case SHIPPING -> "配送中";
            case COMPLETED -> "完了";
            case CANCELLED -> "キャンセル";
        };
    }
}

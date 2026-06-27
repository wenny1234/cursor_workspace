package com.shop.wms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShipOrderRequest {
    @NotBlank(message = "送り場所を入力してください")
    private String shippingAddress;
}

package com.shop.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ShipOrderRequest {
    @NotBlank(message = "送り場所を入力してください")
    private String shippingAddress;
}

package com.shop.backend.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class CreateOrderRequest {
    @NotEmpty(message = "订单商品不能为空")
    @Valid
    private List<OrderItemRequest> items;
}

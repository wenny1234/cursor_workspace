package com.shop.backend.dto;

import com.shop.backend.model.OrderStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateOrderStatusRequest {
    @NotNull(message = "状态不能为空")
    private OrderStatus status;
}

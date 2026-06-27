package com.shop.wms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsSummary {
    private long totalOrders;
    private long completedOrders;
    private BigDecimal totalSalesAmount;
    private long activeProducts;
    private long activeUsers;
}

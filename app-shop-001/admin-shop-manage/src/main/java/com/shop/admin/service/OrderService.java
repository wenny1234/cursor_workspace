package com.shop.admin.service;

import com.shop.admin.dto.StatsSummary;
import com.shop.admin.mapper.OrderMapper;
import com.shop.admin.model.Order;
import com.shop.admin.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final ProductService productService;
    private final UserService userService;

    public List<Order> findRecent(int limit) {
        return orderMapper.findRecent(limit);
    }

    public Order getById(Long id) {
        return orderMapper.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("注文が見つかりません: " + id));
    }

    public StatsSummary getStatsSummary() {
        return StatsSummary.builder()
                .totalOrders(orderMapper.countAll())
                .completedOrders(orderMapper.countByStatus(OrderStatus.COMPLETED.name()))
                .totalSalesAmount(orderMapper.sumTotalAmount())
                .activeProducts(productService.countActive())
                .activeUsers(userService.countActive())
                .build();
    }
}

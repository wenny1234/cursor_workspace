package com.shop.wms.service;

import com.shop.wms.dto.StatsSummary;
import com.shop.wms.mapper.OrderItemMapper;
import com.shop.wms.mapper.OrderMapper;
import com.shop.wms.model.Order;
import com.shop.wms.model.OrderItem;
import com.shop.wms.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductService productService;
    private final UserService userService;

    public List<Order> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderMapper.findByStatus(status);
        orders.forEach(this::attachItems);
        return orders;
    }

    public List<Order> findRecent(int limit) {
        return orderMapper.findRecent(limit);
    }

    public Order getOrderById(Long id) {
        Order order = orderMapper.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("注文が見つかりません: " + id));
        attachItems(order);
        return order;
    }

    @Transactional
    public Order shipOrder(Long orderId, String shippingAddress) {
        if (shippingAddress == null || shippingAddress.isBlank()) {
            throw new IllegalArgumentException("送り場所を入力してください");
        }

        Order order = orderMapper.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("注文が見つかりません: " + orderId));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalArgumentException("支払済の注文のみ出荷できます");
        }

        order.setShippingAddress(shippingAddress.trim());
        order.setStatus(OrderStatus.SHIPPING);
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.update(order);
        attachItems(order);
        return order;
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

    public String formatItemsSummary(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            return "";
        }
        return order.getItems().stream()
                .map(item -> item.getProductName() + " × " + item.getQuantity())
                .collect(Collectors.joining(", "));
    }

    private void attachItems(Order order) {
        List<OrderItem> items = orderItemMapper.findByOrderId(order.getId());
        order.setItems(items);
    }
}

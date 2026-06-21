package com.shop.backend.service;

import com.shop.backend.dto.CreateOrderRequest;
import com.shop.backend.dto.OrderItemRequest;
import com.shop.backend.model.Order;
import com.shop.backend.model.OrderItem;
import com.shop.backend.model.OrderStatus;
import com.shop.backend.model.Product;
import com.shop.backend.repository.OrderRepository;
import com.shop.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(Long userId, CreateOrderRequest request) {
        LocalDateTime now = LocalDateTime.now();
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("商品不存在: " + itemRequest.getProductId()));

            if (!product.isInStock() || product.getStock() == null || product.getStock() < itemRequest.getQty()) {
                throw new IllegalArgumentException("商品库存不足: " + product.getName());
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQty()));
            total = total.add(lineTotal);

            orderItems.add(OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .unitPrice(unitPrice)
                    .quantity(itemRequest.getQty())
                    .lineTotal(lineTotal)
                    .build());

            product.setStock(product.getStock() - itemRequest.getQty());
            product.setUpdatedAt(now);
            productRepository.save(product);
        }

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .userId(userId)
                .totalAmount(total)
                .status(OrderStatus.PAID)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return orderRepository.save(order, orderItems);
    }

    public List<Order> getOrdersForUser(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        orders.forEach(this::attachItems);
        return orders;
    }

    public Order getOrderForUser(Long userId, Long orderId, boolean canManageAll) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        if (!canManageAll && !order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权访问该订单");
        }

        attachItems(order);
        return order;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        List<OrderItem> items = orderRepository.findItemsByOrderId(orderId);
        return orderRepository.save(order, items);
    }

    private void attachItems(Order order) {
        order.setItems(orderRepository.findItemsByOrderId(order.getId()));
    }

    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long suffix = System.currentTimeMillis() % 1_000_000L;
        return String.format(Locale.ROOT, "ORD-%s-%06d", datePart, suffix);
    }
}

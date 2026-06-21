package com.shop.backend.controller;

import com.shop.backend.dto.CreateOrderRequest;
import com.shop.backend.dto.UpdateOrderStatusRequest;
import com.shop.backend.model.Order;
import com.shop.backend.security.UserDetailsImpl;
import com.shop.backend.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        try {
            UserDetailsImpl user = getCurrentUser(authentication);
            Order order = orderService.createOrder(user.getId(), request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "订单创建成功");
            response.put("order", order);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getMyOrders(Authentication authentication) {
        UserDetailsImpl user = getCurrentUser(authentication);
        List<Order> orders = orderService.getOrdersForUser(user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orders);
        response.put("count", orders.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            UserDetailsImpl user = getCurrentUser(authentication);
            boolean canManageAll = hasManageRole(user);
            Order order = orderService.getOrderForUser(user.getId(), id, canManageAll);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        try {
            Order order = orderService.updateOrderStatus(id, request.getStatus());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "订单状态更新成功");
            response.put("order", order);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private UserDetailsImpl getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new IllegalArgumentException("未认证");
        }
        return (UserDetailsImpl) authentication.getPrincipal();
    }

    private boolean hasManageRole(UserDetailsImpl user) {
        return user.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_STAFF".equals(a.getAuthority()));
    }
}

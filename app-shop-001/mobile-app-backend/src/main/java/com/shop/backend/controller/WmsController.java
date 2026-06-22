package com.shop.backend.controller;

import com.shop.backend.dto.ShipOrderRequest;
import com.shop.backend.model.Order;
import com.shop.backend.model.OrderStatus;
import com.shop.backend.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wms")
@Slf4j
@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
public class WmsController {

    private final OrderService orderService;

    public WmsController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResponseEntity<?> listOrders(@RequestParam(defaultValue = "PAID") OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orders);
        response.put("count", orders.size());
        response.put("status", status.name());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders/{id}/ship")
    public ResponseEntity<?> shipOrder(
            @PathVariable Long id,
            @Valid @RequestBody ShipOrderRequest request) {
        try {
            Order order = orderService.shipOrder(id, request.getShippingAddress());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "出荷処理が完了しました");
            response.put("order", order);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

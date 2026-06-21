package com.shop.admin.controller.api;

import com.shop.admin.dto.StatsSummary;
import com.shop.admin.model.Order;
import com.shop.admin.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    @GetMapping("/recent")
    public List<Order> recent(@RequestParam(defaultValue = "20") int limit) {
        return orderService.findRecent(Math.min(limit, 100));
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping("/stats/summary")
    public StatsSummary summary() {
        return orderService.getStatsSummary();
    }
}

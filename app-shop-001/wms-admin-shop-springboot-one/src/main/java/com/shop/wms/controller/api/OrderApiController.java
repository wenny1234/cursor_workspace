package com.shop.wms.controller.api;

import com.shop.wms.dto.StatsSummary;
import com.shop.wms.model.Order;
import com.shop.wms.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    @GetMapping("/recent")
    public List<Order> recent(@RequestParam(defaultValue = "50") int limit) {
        return orderService.findRecent(Math.min(limit, 100));
    }

    @GetMapping("/stats/summary")
    public StatsSummary summary() {
        return orderService.getStatsSummary();
    }
}

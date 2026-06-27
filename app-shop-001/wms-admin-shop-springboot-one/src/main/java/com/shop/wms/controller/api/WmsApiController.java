package com.shop.wms.controller.api;

import com.shop.wms.dto.JqGridResponse;
import com.shop.wms.dto.ShipOrderRequest;
import com.shop.wms.model.Order;
import com.shop.wms.model.OrderStatus;
import com.shop.wms.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/wms")
@RequiredArgsConstructor
public class WmsApiController {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private static final NumberFormat YEN_FMT = NumberFormat.getCurrencyInstance(Locale.JAPAN);

    private final OrderService orderService;

    @GetMapping("/orders/grid")
    public JqGridResponse listOrdersGrid(
            @RequestParam(defaultValue = "PAID") OrderStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int rows) {

        List<Order> all = orderService.getOrdersByStatus(status);
        int totalRecords = all.size();
        int totalPages = totalRecords == 0 ? 0 : (int) Math.ceil((double) totalRecords / rows);
        int safePage = Math.max(1, Math.min(page, Math.max(totalPages, 1)));

        int from = (safePage - 1) * rows;
        int to = Math.min(from + rows, totalRecords);
        List<Order> pageOrders = from >= totalRecords ? List.of() : all.subList(from, to);

        List<JqGridResponse.JqGridRow> gridRows = new ArrayList<>();
        for (Order order : pageOrders) {
            gridRows.add(JqGridResponse.JqGridRow.builder()
                    .id(order.getId())
                    .cell(List.of(
                            order.getOrderNumber(),
                            formatYen(order.getTotalAmount()),
                            String.valueOf(order.getUserId()),
                            order.getStatus().label(),
                            order.getShippingAddress() != null ? order.getShippingAddress() : "",
                            orderService.formatItemsSummary(order),
                            order.getCreatedAt() != null ? order.getCreatedAt().format(DATE_FMT) : ""
                    ))
                    .build());
        }

        return JqGridResponse.builder()
                .page(safePage)
                .total(totalPages)
                .records(totalRecords)
                .rows(gridRows)
                .build();
    }

    @PostMapping("/orders/{id}/ship")
    public ResponseEntity<?> shipOrder(
            @PathVariable Long id,
            @Valid @RequestBody ShipOrderRequest request) {
        try {
            Order order = orderService.shipOrder(id, request.getShippingAddress());
            Map<String, Object> body = new HashMap<>();
            body.put("message", "出荷処理が完了しました");
            body.put("order", order);
            return ResponseEntity.ok(body);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private String formatYen(java.math.BigDecimal amount) {
        if (amount == null) {
            return YEN_FMT.format(0);
        }
        return YEN_FMT.format(amount.setScale(0, RoundingMode.HALF_UP));
    }
}

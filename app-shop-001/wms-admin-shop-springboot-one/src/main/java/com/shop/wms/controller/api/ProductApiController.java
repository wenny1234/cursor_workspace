package com.shop.wms.controller.api;

import com.shop.wms.model.Product;
import com.shop.wms.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductApiController {

    private final ProductService productService;

    @GetMapping
    public List<Product> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        return productService.search(keyword, includeInactive);
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping
    public Product create(@Valid @RequestBody Product product) {
        product.setId(null);
        return productService.save(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody Product product) {
        product.setId(id);
        return productService.save(product);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivate(@PathVariable Long id) {
        productService.deactivate(id);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "商品を無効化しました");
        body.put("id", id);
        return ResponseEntity.ok(body);
    }
}

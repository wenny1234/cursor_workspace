package com.shop.backend.controller;

import com.shop.backend.model.Product;
import com.shop.backend.model.User;
import com.shop.backend.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean inStock) {
        
        List<Product> products;
        
        if (category != null && !category.isEmpty()) {
            products = productRepository.findByCategory(category);
        } else if (search != null && !search.isEmpty()) {
            products = productRepository.findByNameContaining(search);
        } else if (inStock != null) {
            products = inStock ? productRepository.findInStock() : productRepository.findOutOfStock();
        } else {
            products = productRepository.findAll();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("count", products.size());
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> createProduct(@Valid @RequestBody Product product, Authentication authentication) {
        // 设置时间戳
        LocalDateTime now = LocalDateTime.now();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        
        // 设置默认库存为0
        if (product.getStock() == null) {
            product.setStock(0);
        }
        
        Product savedProduct = productRepository.save(product);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "商品创建成功");
        response.put("product", savedProduct);
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody Product productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setDescription(productDetails.getDescription());
                    product.setPrice(productDetails.getPrice());
                    product.setStock(productDetails.getStock());
                    product.setCategory(productDetails.getCategory());
                    product.setImageUrl(productDetails.getImageUrl());
                    product.setUpdatedAt(LocalDateTime.now());
                    
                    Product updatedProduct = productRepository.save(product);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "商品更新成功");
                    response.put("product", updatedProduct);
                    
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        productRepository.deleteById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "商品删除成功");
        response.put("id", id);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        List<Product> products = productRepository.findAll();
        List<String> categories = products.stream()
                .map(Product::getCategory)
                .distinct()
                .filter(category -> category != null && !category.isEmpty())
                .sorted()
                .toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        response.put("count", categories.size());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> getProductStats() {
        List<Product> allProducts = productRepository.findAll();
        List<Product> inStockProducts = productRepository.findInStock();
        List<Product> outOfStockProducts = productRepository.findOutOfStock();
        
        double totalValue = allProducts.stream()
                .filter(p -> p.getPrice() != null && p.getStock() != null)
                .mapToDouble(p -> p.getPrice().doubleValue() * p.getStock())
                .sum();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalProducts", allProducts.size());
        response.put("inStock", inStockProducts.size());
        response.put("outOfStock", outOfStockProducts.size());
        response.put("totalValue", totalValue);
        response.put("categories", allProducts.stream()
                .map(Product::getCategory)
                .distinct()
                .filter(category -> category != null && !category.isEmpty())
                .count());
        
        return ResponseEntity.ok(response);
    }
}
package com.shop.wms.service;

import com.shop.wms.mapper.ProductMapper;
import com.shop.wms.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    public List<Product> search(String keyword, boolean includeInactive) {
        return productMapper.search(keyword, includeInactive);
    }

    public Product getById(Long id) {
        return productMapper.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません: " + id));
    }

    @Transactional
    public Product save(Product product) {
        LocalDateTime now = LocalDateTime.now();
        if (product.getId() == null) {
            product.setActive(product.getActive() == null || product.getActive());
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
            productMapper.insert(product);
        } else {
            Product existing = getById(product.getId());
            product.setCreatedAt(existing.getCreatedAt());
            product.setActive(product.getActive() == null ? existing.getActive() : product.getActive());
            product.setUpdatedAt(now);
            productMapper.update(product);
        }
        return product;
    }

    @Transactional
    public void deactivate(Long id) {
        getById(id);
        productMapper.deactivate(id, LocalDateTime.now());
    }

    public long countActive() {
        return productMapper.countActive();
    }
}

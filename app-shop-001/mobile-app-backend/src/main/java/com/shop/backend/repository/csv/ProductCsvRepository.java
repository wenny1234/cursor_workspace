package com.shop.backend.repository.csv;

import com.shop.backend.model.Product;
import com.shop.backend.repository.ProductRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("csv")
public class ProductCsvRepository extends BaseCsvRepository<Product> implements ProductRepository {
    
    @Override
    protected Class<Product> getEntityClass() {
        return Product.class;
    }
    
    @Override
    protected String getCsvFileName() {
        return "products.csv";
    }
    
    @Override
    protected String[] getCsvHeaders() {
        return new String[]{"id", "name", "description", "price", "stock", "category", "image_url", "created_at", "updated_at"};
    }
    
    @Override
    protected Long getEntityId(Product entity) {
        return entity.getId();
    }
    
    @Override
    protected void setEntityId(Product entity, Long id) {
        entity.setId(id);
    }
    
    @Override
    public Optional<Product> findById(Long id) {
        return findAll().stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }
    
    @Override
    public List<Product> findAll() {
        return super.findAll();
    }
    
    @Override
    public List<Product> findByCategory(String category) {
        return findAll().stream()
                .filter(product -> category.equalsIgnoreCase(product.getCategory()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByNameContaining(String name) {
        String searchName = name.toLowerCase();
        return findAll().stream()
                .filter(product -> product.getName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
    }
    
    @Override
    public Product save(Product product) {
        return super.save(product);
    }
    
    @Override
    public void deleteById(Long id) {
        super.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return findAll().stream()
                .anyMatch(product -> product.getId().equals(id));
    }
    
    @Override
    public long count() {
        return findAll().size();
    }
    
    @Override
    public List<Product> findInStock() {
        return findAll().stream()
                .filter(Product::isInStock)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findOutOfStock() {
        return findAll().stream()
                .filter(product -> !product.isInStock())
                .collect(Collectors.toList());
    }
}
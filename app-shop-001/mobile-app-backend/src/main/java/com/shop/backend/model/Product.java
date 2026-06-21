package com.shop.backend.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.shop.backend.repository.csv.LocalDateTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @CsvBindByName(column = "ID")
    private Long id;
    
    @NotBlank(message = "商品名称不能为空")
    @Size(min = 1, max = 200, message = "商品名称长度必须在1到200个字符之间")
    @CsvBindByName(column = "NAME", required = true)
    private String name;
    
    @Size(max = 1000, message = "商品描述长度不能超过1000个字符")
    @CsvBindByName(column = "DESCRIPTION")
    private String description;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "价格必须大于0")
    @CsvBindByName(column = "PRICE")
    private BigDecimal price;
    
    @Min(value = 0, message = "库存不能为负数")
    @CsvBindByName(column = "STOCK")
    private Integer stock;
    
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    @CsvBindByName(column = "CATEGORY")
    private String category;
    
    @CsvBindByName(column = "IMAGEURL")
    private String imageUrl;
    
    @CsvCustomBindByName(column = "CREATEDAT", converter = LocalDateTimeConverter.class)
    private LocalDateTime createdAt;
    
    @CsvCustomBindByName(column = "UPDATEDAT", converter = LocalDateTimeConverter.class)
    private LocalDateTime updatedAt;
    
    // Helper method to check if product is in stock
    public boolean isInStock() {
        return stock != null && stock > 0;
    }
    
    // Helper method to get formatted price
    public String getFormattedPrice() {
        if (price == null) {
            return "0.00";
        }
        return String.format("¥%,.2f", price);
    }
}
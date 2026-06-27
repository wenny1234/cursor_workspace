package com.shop.wms.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;

    @NotBlank(message = "商品名は必須です")
    @Size(max = 200, message = "商品名は200文字以内で入力してください")
    private String name;

    @Size(max = 1000, message = "商品説明は1000文字以内で入力してください")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "価格は0より大きい値を入力してください")
    private BigDecimal price;

    @Min(value = 0, message = "在庫は0以上で入力してください")
    private Integer stock;

    @Size(max = 100, message = "カテゴリ名は100文字以内で入力してください")
    private String category;

    private String imageUrl;

    @Builder.Default
    private Boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

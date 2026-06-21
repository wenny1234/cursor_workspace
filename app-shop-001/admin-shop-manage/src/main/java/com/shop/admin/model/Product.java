package com.shop.admin.model;

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

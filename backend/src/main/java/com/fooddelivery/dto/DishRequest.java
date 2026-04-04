package com.fooddelivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DishRequest {
    private Long categoryId;
    
    @NotBlank(message = "菜品名称不能为空")
    private String name;
    
    private String image;
    
    private String description;
    
    @NotNull(message = "价格不能为空")
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private Integer status;
}

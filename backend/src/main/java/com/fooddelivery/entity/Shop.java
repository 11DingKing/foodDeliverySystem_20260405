package com.fooddelivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("shop")
public class Shop {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String name;
    
    private String category;
    
    private String logo;
    
    private String description;
    
    private String phone;
    
    private String province;
    
    private String city;
    
    private String district;
    
    private String address;
    
    private BigDecimal minPrice;
    
    private BigDecimal deliveryFee;
    
    private BigDecimal rating;
    
    private Integer monthlySales;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

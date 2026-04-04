package com.fooddelivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private Long shopId;
    
    private Long riderId;
    
    private BigDecimal totalAmount;
    
    private BigDecimal deliveryFee;
    
    private BigDecimal payAmount;
    
    private Integer status;
    
    private String contactName;
    
    private String contactPhone;
    
    private String deliveryAddress;
    
    private String remark;
    
    private LocalDateTime payTime;
    
    private LocalDateTime acceptTime;
    
    private LocalDateTime pickupTime;
    
    private LocalDateTime deliverTime;
    
    private LocalDateTime cancelTime;
    
    private String cancelReason;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    // 非数据库字段，用于展示
    @TableField(exist = false)
    private String shopName;
    
    @TableField(exist = false)
    private String shopAddress;
    
    @TableField(exist = false)
    private String shopPhone;
}

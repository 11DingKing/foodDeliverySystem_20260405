package com.fooddelivery.common;

public class Constants {
    
    // 用户角色
    public static final int ROLE_USER = 0;      // 普通用户
    public static final int ROLE_MERCHANT = 1;  // 商家
    public static final int ROLE_RIDER = 2;     // 骑手

    // 用户状态
    public static final int USER_STATUS_DISABLED = 0;
    public static final int USER_STATUS_NORMAL = 1;

    // 店铺状态
    public static final int SHOP_STATUS_PENDING = 0;   // 待审核
    public static final int SHOP_STATUS_OPEN = 1;      // 营业中
    public static final int SHOP_STATUS_REST = 2;      // 休息中
    public static final int SHOP_STATUS_CLOSED = 3;    // 已关闭

    // 菜品状态
    public static final int DISH_STATUS_OFF = 0;       // 下架
    public static final int DISH_STATUS_ON = 1;        // 上架

    // 订单状态
    public static final int ORDER_STATUS_UNPAID = 0;       // 待支付
    public static final int ORDER_STATUS_PENDING = 1;      // 待接单
    public static final int ORDER_STATUS_PREPARING = 2;    // 备餐中
    public static final int ORDER_STATUS_READY = 3;        // 待取餐
    public static final int ORDER_STATUS_DELIVERING = 4;   // 配送中
    public static final int ORDER_STATUS_COMPLETED = 5;    // 已送达
    public static final int ORDER_STATUS_CANCELLED = 6;    // 已取消

    // 申请状态
    public static final int APPLICATION_PENDING = 0;   // 待审核
    public static final int APPLICATION_APPROVED = 1;  // 通过
    public static final int APPLICATION_REJECTED = 2;  // 拒绝
}

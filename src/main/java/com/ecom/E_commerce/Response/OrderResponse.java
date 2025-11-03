package com.ecom.E_commerce.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private String orderStatus;
    private BigDecimal itemsTotal;
    private BigDecimal shippingCost;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private String shippingType;
    private String payment;
    private ShippingAddressResponse shippingAddress;
    private UserResponse user;
    private List<OrderItemResponse> items;
    private String paymentUrl;

}


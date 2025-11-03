package com.ecom.E_commerce.Request;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    private Long userId;
    private Long addressId;
    private List<CartItemRequest> cartItems;
    private String paymentMethod;
    private String shippingType;
}


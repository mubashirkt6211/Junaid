package com.ecom.E_commerce.Response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long itemId;
    private String itemName;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal totalPrice;
    private String imageUrl; 
}

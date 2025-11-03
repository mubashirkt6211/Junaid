package com.ecom.E_commerce.Response;

import com.ecom.E_commerce.Model.CartItem;
import com.ecom.E_commerce.Model.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private Long id;
    private int quantity;
    private BigDecimal totalPrice;
    private String color;
    private String size;
    private ProductResponse product;

    public CartItemResponse(CartItem cartItem) {
        this.id = cartItem.getId();
        this.quantity = cartItem.getQuantity();
        this.totalPrice = cartItem.getTotalPrice();
        if (cartItem.getItem() != null) {
            this.product = new ProductResponse(cartItem.getItem());
        }
    }
}

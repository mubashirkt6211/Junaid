package com.ecom.E_commerce.Request;

import com.ecom.E_commerce.Model.Item;
import lombok.Data;

@Data
public class CartItemRequest {

    private Long itemId;
    private int quantity;
    private String color;
    private String size;


}

package com.ecom.E_commerce.Request;

import com.ecom.E_commerce.Model.ShippingType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ItemRequest {

    private String name;
    private BigDecimal price;
    private String description;

    private Long categoryId;

    private String brand;
    private int quantity = 0;
    private BigDecimal discountPrice;
    private boolean onSale = false;
    private boolean inStock = true;
    private Double weight;

    private Set<ShippingType> shippingClasses = new HashSet<>();

    private List<String> colors = new ArrayList<>();
    private List<String> sizes = new ArrayList<>();
    private Integer reviewCount = 0;
    private Double averageRating = 0.0;
}

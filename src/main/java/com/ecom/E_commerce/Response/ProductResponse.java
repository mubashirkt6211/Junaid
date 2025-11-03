package com.ecom.E_commerce.Response;

import com.ecom.E_commerce.Model.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String brand;
    private List<String> size;
    private List<String> colors;
    private BigDecimal price;
    private List<String> imageUrls;

    public ProductResponse(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.brand = item.getBrand();
        this.size = item.getSizes();
        this.colors = item.getColors();
        this.price = item.getPrice();
        this.imageUrls = item.getImageUrls();
    }
}

package com.ecom.E_commerce.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private BigDecimal price;
    private String description;
    private int quantity;
    private String brand;
    private Double weight;

    @ElementCollection(targetClass = ShippingType.class)
    @CollectionTable(name = "item_shipping_classes", joinColumns = @JoinColumn(name = "item_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_class")
    private Set<ShippingType> shippingClasses = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "product_colors", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "color")
    private List<String> colors = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "product_sizes", joinColumns = @JoinColumn(name = "item_id"))
    private List<String> sizes = new ArrayList<>();

    private boolean inStock;

    private BigDecimal discountPrice;
    private boolean onSale;



    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"items"})
    private Category category;

    @ElementCollection
    @CollectionTable(name = "item_images", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    private Date creationdate;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    @JsonIgnore
    private User seller;

    @ManyToMany(mappedBy = "wishlist")
    @JsonIgnore
    private List<User> users = new ArrayList<>();



}

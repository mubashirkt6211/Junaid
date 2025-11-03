    package com.ecom.E_commerce.Request;

    import com.ecom.E_commerce.Model.Item;
    import jakarta.persistence.ElementCollection;
    import lombok.Data;

    import java.util.ArrayList;
    import java.util.List;

    @Data

    public class CategoryRequest {
        private String name;
        private List<Item> items = new ArrayList<>();
    }


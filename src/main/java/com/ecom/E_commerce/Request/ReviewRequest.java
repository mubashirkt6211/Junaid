package com.ecom.E_commerce.Request;

import lombok.Data;

@Data

public class ReviewRequest {
    private String content;
    private Long itemId;
}

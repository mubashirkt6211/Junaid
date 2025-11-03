package com.ecom.E_commerce.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShippingAddressResponse {
    private Long id;
    private String street;
    private String city;
    private String district;
    private String state;
    private String zipcode;
    private String country;
    private String phoneNumber;
}

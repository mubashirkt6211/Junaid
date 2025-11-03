package com.ecom.E_commerce.Request;

import com.ecom.E_commerce.Model.User;
import lombok.Data;

@Data
public class AddressRequest {
    private Long id;
    private String street;
    private String city;
    private String district;
    private String state;
    private String zipcode;
    private String country;
    private String phoneNumber;
    private User user;
}

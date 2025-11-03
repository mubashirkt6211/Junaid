package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Exception.AddressInUseException;
import com.ecom.E_commerce.Exception.ResourceNotFoundException;
import com.ecom.E_commerce.Model.ShippingAddress;
import com.ecom.E_commerce.Request.AddressRequest;

import java.util.List;

public interface AddressService {

    ShippingAddress createAddress(AddressRequest request) throws ResourceNotFoundException;

    ShippingAddress updateAddress(Long id, AddressRequest request) throws ResourceNotFoundException;

    void deleteAddress(Long id) throws AddressInUseException;

    ShippingAddress getAddressById(Long id) throws ResourceNotFoundException;

    List<ShippingAddress> getAddressesByUserId(Long userId);
}

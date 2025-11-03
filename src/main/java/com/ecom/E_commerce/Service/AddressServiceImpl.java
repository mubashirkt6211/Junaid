package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Exception.AddressInUseException;
import com.ecom.E_commerce.Exception.ItemNotFoundException;
import com.ecom.E_commerce.Model.ShippingAddress;
import com.ecom.E_commerce.Model.User;
import com.ecom.E_commerce.Repository.AddressRepository;
import com.ecom.E_commerce.Repository.OrderRepository;
import com.ecom.E_commerce.Repository.UserRepository;
import com.ecom.E_commerce.Request.AddressRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public ShippingAddress createAddress(AddressRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new ItemNotFoundException("User not found with id: " + request.getId()));
        ShippingAddress address = new ShippingAddress();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setDistrict(request.getDistrict());
        address.setZipcode(request.getZipcode());
        address.setCountry(request.getCountry());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setUser(user);

        return addressRepository.save(address);
    }

    @Override
    public ShippingAddress updateAddress(Long id, AddressRequest request) {
        ShippingAddress existing = addressRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Address not found with id: " + id));
        existing.setStreet(request.getStreet());
        existing.setCity(request.getCity());
        existing.setState(request.getState());
        existing.setDistrict(request.getDistrict());
        existing.setZipcode(request.getZipcode());
        existing.setCountry(request.getCountry());
        existing.setPhoneNumber(request.getPhoneNumber());
        if (request.getId() != null) {
            User user = userRepository.findById(request.getId())
                    .orElseThrow(() -> new ItemNotFoundException("User not found with id: " + request.getId()));
            existing.setUser(user);
        }
        return addressRepository.save(existing);
    }

    @Override
    public void deleteAddress(Long id) throws AddressInUseException {
        ShippingAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Address not found with id: " + id));
        boolean isInUse = orderRepository.existsByAddress(address);
        if (isInUse) {
            throw new AddressInUseException("This address is connected to an order. For security reasons, it cannot be deleted.");
        }
        addressRepository.delete(address);
    }


    @Override
    public ShippingAddress getAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Address not found with id: " + id));
    }

    @Override
    public List<ShippingAddress> getAddressesByUserId(Long userId) {
        List<ShippingAddress> addresses = addressRepository.findByUserId(userId);
        if (addresses.isEmpty()) {
            throw new ItemNotFoundException("No addresses found for user with id: " + userId);
        }
        return addresses;
    }
}

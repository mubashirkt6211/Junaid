package com.ecom.E_commerce.Controller;

import com.ecom.E_commerce.Exception.AddressInUseException;
import com.ecom.E_commerce.Exception.ResourceNotFoundException;
import com.ecom.E_commerce.Model.ShippingAddress;
import com.ecom.E_commerce.Model.User;
import com.ecom.E_commerce.Repository.UserRepository;
import com.ecom.E_commerce.Request.AddressRequest;
import com.ecom.E_commerce.Response.ApiResponse;
import com.ecom.E_commerce.Service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final UserRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ShippingAddress>> createAddress(
            Authentication authentication,
            @RequestBody AddressRequest request
    ) throws ResourceNotFoundException {

        User user = getAuthenticatedUser(authentication);
        request.setId(user.getId());
        ShippingAddress address = addressService.createAddress(request);

        return ResponseEntity.ok(
                new ApiResponse<>("Success", "Address created successfully", address)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ShippingAddress>> updateAddress(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody AddressRequest request
    ) throws ResourceNotFoundException {

        User user = getAuthenticatedUser(authentication);
        ShippingAddress existingAddress = addressService.getAddressById(id);

        checkOwnership(existingAddress, user);

        request.setId(user.getId());
        ShippingAddress updatedAddress = addressService.updateAddress(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>("Success", "Address updated successfully", updatedAddress)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAddress(
            Authentication authentication,
            @PathVariable Long id
    ) throws ResourceNotFoundException, AddressInUseException {
        User user = getAuthenticatedUser(authentication);
        ShippingAddress existingAddress = addressService.getAddressById(id);
        checkOwnership(existingAddress, user);
        addressService.deleteAddress(id);
        return ResponseEntity.ok(
                new ApiResponse<>("Success", "Address deleted successfully", null)
        );

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShippingAddress>> getAddressById(
            Authentication authentication,
            @PathVariable Long id
    ) throws ResourceNotFoundException {

        User user = getAuthenticatedUser(authentication);
        ShippingAddress address = addressService.getAddressById(id);

        checkOwnership(address, user);

        return ResponseEntity.ok(
                new ApiResponse<>("Success", "Address fetched successfully", address)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ShippingAddress>>> getAddressesByUserId(@PathVariable Long userId) {
        List<ShippingAddress> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(
                new ApiResponse<>("Success", "Address fetched successfully", addresses)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<ShippingAddress>>> getMyAddresses(Authentication authentication)
            throws ResourceNotFoundException {

        User user = getAuthenticatedUser(authentication);
        List<ShippingAddress> addresses = addressService.getAddressesByUserId(user.getId());

        return ResponseEntity.ok(
                new ApiResponse<>("Success", "Addresses fetched successfully", addresses)
        );
    }

    private User getAuthenticatedUser(Authentication authentication) throws ResourceNotFoundException {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private void checkOwnership(ShippingAddress address, User user) {
        if (!address.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to access this address.");
        }
    }
}

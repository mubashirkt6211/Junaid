package com.ecom.E_commerce.Controller;

import com.ecom.E_commerce.Exception.ResourceNotFoundException;
import com.ecom.E_commerce.Model.User;
import com.ecom.E_commerce.Request.CartItemRequest;
import com.ecom.E_commerce.Response.CartItemResponse;
import com.ecom.E_commerce.Response.CartResponse;
import com.ecom.E_commerce.Service.CartService;
import com.ecom.E_commerce.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(Authentication authentication) throws ResourceNotFoundException {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemResponse> addItemToCart(Authentication authentication,
                                                          @RequestBody CartItemRequest request) throws ResourceNotFoundException {
        User user = getAuthenticatedUser(authentication);
        CartItemResponse response = cartService.addItemToCart(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCartCount(Authentication authentication) throws ResourceNotFoundException {
        User user = getAuthenticatedUser(authentication);
        int count = cartService.cartCount(user.getId());
        return ResponseEntity.ok(count);
    }

    @GetMapping("/items")
    public ResponseEntity<CartResponse> getUserCart(Authentication authentication) throws ResourceNotFoundException {
        User user = getAuthenticatedUser(authentication);
        CartResponse cart = cartService.getUserCart(user.getId());
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/{cartItemId}/quantity")
    public ResponseEntity<CartItemResponse> updateCartItemQuantity(@PathVariable Long cartItemId,
                                                                   @RequestParam int quantity,
                                                                   Authentication authentication) throws ResourceNotFoundException {
        getAuthenticatedUser(authentication);
        CartItemResponse updated = cartService.updateCartItemQuantity(cartItemId, quantity);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<String> removeItemFromCart(@PathVariable Long cartItemId,
                                                     Authentication authentication) throws ResourceNotFoundException {
        User user = getAuthenticatedUser(authentication);
        cartService.removeItemFromCart(cartItemId, user.getId());
        return ResponseEntity.ok("Item removed from cart successfully.");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(Authentication authentication) throws ResourceNotFoundException {
        User user = getAuthenticatedUser(authentication);
        cartService.clearCart(user.getId());
        return ResponseEntity.ok("Cart cleared successfully.");
    }

    @PutMapping("/{cartItemId}/increment")
    public ResponseEntity<CartItemResponse> incrementCartItem(@PathVariable Long cartItemId,
                                                              Authentication authentication) throws ResourceNotFoundException {
        getAuthenticatedUser(authentication);
        CartItemResponse updated = cartService.incrementCartItem(cartItemId);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{cartItemId}/decrement")
    public ResponseEntity<?> decrementCartItem(@PathVariable Long cartItemId,
                                               Authentication authentication) throws ResourceNotFoundException {
        getAuthenticatedUser(authentication);
        CartItemResponse updated = cartService.decrementCartItem(cartItemId);
        if (updated == null) {
            return ResponseEntity.ok("Item removed from cart since quantity reached zero.");
        }
        return ResponseEntity.ok(updated);
    }
}

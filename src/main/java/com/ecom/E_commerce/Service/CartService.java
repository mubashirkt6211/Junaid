package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Exception.ResourceNotFoundException;
import com.ecom.E_commerce.Request.CartItemRequest;
import com.ecom.E_commerce.Response.CartItemResponse;
import com.ecom.E_commerce.Response.CartResponse;

import java.math.BigDecimal;

public interface CartService {

    CartItemResponse addItemToCart(Long userId, CartItemRequest request) throws ResourceNotFoundException;

    int cartCount(Long userId) throws ResourceNotFoundException;

    CartResponse getUserCart(Long userId) throws ResourceNotFoundException;

    CartItemResponse updateCartItemQuantity(Long cartItemId, int quantity) throws ResourceNotFoundException;

    void removeItemFromCart(Long cartItemId, Long userId) throws ResourceNotFoundException;

    BigDecimal calculateCartTotals(Long userId) throws ResourceNotFoundException;

    void clearCart(Long userId) throws ResourceNotFoundException;

    CartItemResponse incrementCartItem(Long cartItemId) throws ResourceNotFoundException;

    CartItemResponse decrementCartItem(Long cartItemId) throws ResourceNotFoundException;
}

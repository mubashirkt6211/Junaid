package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Exception.ResourceNotFoundException;
import com.ecom.E_commerce.Model.*;
import com.ecom.E_commerce.Repository.*;
import com.ecom.E_commerce.Request.CartItemRequest;
import com.ecom.E_commerce.Response.CartItemResponse;
import com.ecom.E_commerce.Response.CartResponse;
import com.ecom.E_commerce.Response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;


import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getQuantity(),
                cartItem.getTotalPrice(),
                cartItem.getColor(),
                cartItem.getSize(),
                cartItem.getItem() != null ? new ProductResponse(cartItem.getItem()) : null
        );
    }

    private CartResponse mapToCartResponse(Cart cart) {
        var items = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cart.getId(), items, total);
    }

    @Override
    public CartItemResponse addItemToCart(Long userId, CartItemRequest request) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        CartItem cartItem = cart.getItems().stream()
                .filter(ci -> ci.getItem().getId().equals(item.getId())
                        && Objects.equals(ci.getColor(), request.getColor())
                        && Objects.equals(ci.getSize(), request.getSize()))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItem.setTotalPrice(item.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .item(item)
                    .quantity(request.getQuantity())
                    .totalPrice(item.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                    .color(request.getColor())
                    .size(request.getSize())
                    .build();
            cart.getItems().add(cartItem);
        }

        cartRepository.save(cart);
        return mapToCartItemResponse(cartItem);
    }

    @Override
    public int cartCount(Long userId) throws ResourceNotFoundException {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));
        return cart.getItems().size();
    }

    @Override
    public CartResponse getUserCart(Long userId) throws ResourceNotFoundException {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));
        return mapToCartResponse(cart);
    }

    @Override
    public CartItemResponse updateCartItemQuantity(Long cartItemId, int quantity) throws ResourceNotFoundException {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(cartItem.getItem().getPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItemRepository.save(cartItem);

        return mapToCartItemResponse(cartItem);
    }

    @Override
    public void removeItemFromCart(Long cartItemId, Long userId) throws ResourceNotFoundException {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!cart.getItems().contains(cartItem))
            throw new ResourceNotFoundException("Item does not belong to the user's cart");
        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public BigDecimal calculateCartTotals(Long userId) throws ResourceNotFoundException {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));
        return cart.getItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void clearCart(Long userId) throws ResourceNotFoundException {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public CartItemResponse incrementCartItem(Long cartItemId) throws ResourceNotFoundException {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cartItem.setQuantity(cartItem.getQuantity() + 1);
        cartItem.setTotalPrice(cartItem.getItem().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        cartItemRepository.save(cartItem);
        return mapToCartItemResponse(cartItem);
    }

    @Override
    public CartItemResponse decrementCartItem(Long cartItemId) throws ResourceNotFoundException {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        int newQuantity = cartItem.getQuantity() - 1;
        if (newQuantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }
        cartItem.setQuantity(newQuantity);
        cartItem.setTotalPrice(cartItem.getItem().getPrice().multiply(BigDecimal.valueOf(newQuantity)));
        cartItemRepository.save(cartItem);
        return mapToCartItemResponse(cartItem);
    }
}

package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Model.Item;

import java.util.List;

public interface WishlistService {

    public void addToWishlist(Long userId,Long itemId);

    public void removeWishlist(Long userId,Long itemId);

    public int countOfWishlist(Long userId);

    public List<Item> getWishlist(Long userId);
}

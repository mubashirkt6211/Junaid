package com.ecom.E_commerce.Controller;

import com.ecom.E_commerce.Model.Item;
import com.ecom.E_commerce.Service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/add/{userId}/{itemId}")
    public ResponseEntity<String> addToWishlist(@PathVariable Long userId, @PathVariable Long itemId) {
        wishlistService.addToWishlist(userId, itemId);
        return ResponseEntity.ok("Item added to wishlist");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Item>> getWishlist(@PathVariable Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlist(userId));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromWishlist(
            @RequestParam Long userId,
            @RequestParam Long itemId
    ) {
        wishlistService.removeWishlist(userId, itemId);
        return ResponseEntity.ok("Item removed from wishlist");
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<Integer> getWishlistCount(@PathVariable Long userId) {
        int count = wishlistService.countOfWishlist(userId);
        return ResponseEntity.ok(count);
    }
}

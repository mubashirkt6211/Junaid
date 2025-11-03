package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Model.Item;
import com.ecom.E_commerce.Model.User;
import com.ecom.E_commerce.Repository.ItemRepository;
import com.ecom.E_commerce.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService{

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public void addToWishlist(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        if (!user.getWishlist().contains(item)) {
            user.getWishlist().add(item);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void removeWishlist(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        if (user.getWishlist().contains(item)) {
            user.getWishlist().remove(item);
            item.getUsers().remove(user);
        }
    }




    @Override
    public int countOfWishlist(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getWishlist().size();
    }

    @Override
    public List<Item> getWishlist(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getWishlist();
    }
}

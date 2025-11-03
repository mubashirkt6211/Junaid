package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Exception.ItemNotFoundException;
import com.ecom.E_commerce.Model.Category;
import com.ecom.E_commerce.Model.Item;
import com.ecom.E_commerce.Model.User;
import com.ecom.E_commerce.Repository.CategoryRepository;
import com.ecom.E_commerce.Repository.ItemRepository;
import com.ecom.E_commerce.Request.ItemRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Item createitem(ItemRequest request, MultipartFile[] images,User seller) throws Exception {
        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID must not be null");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException(
                        "Category not found with ID: " + request.getCategoryId()));
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setBrand(request.getBrand());
        item.setCategory(category);
        item.setCreationdate(new Date());
        item.setQuantity(request.getQuantity());
        item.setColors(request.getColors());
        item.setSizes(request.getSizes());
        item.setWeight(request.getWeight());
        item.setDiscountPrice(request.getDiscountPrice());
        item.setInStock(request.isInStock());
        item.setOnSale(request.isOnSale());
        item.setShippingClasses(request.getShippingClasses());
        item.setSeller(seller);
        List<String> uploadedImageUrls = new ArrayList<>();
        if (images != null && images.length > 0) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String fileUrl = fileStorageService.saveFile(file); // Save & return path
                    uploadedImageUrls.add(fileUrl);
                }
            }
        }
        item.setImageUrls(uploadedImageUrls);
        return itemRepository.save(item);
    }

    @Override
    public Page<Item> getItemsBySeller(User seller, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> itemsPage = itemRepository.findBySeller(seller, pageable);
        if (itemsPage.isEmpty()) {
            throw new ItemNotFoundException("No items found for seller: " + seller.getEmail());
        }
        return itemsPage;
    }


//    @Override
//    public List<Item> getItemsBySeller(User seller) {
//        List<Item> items = itemRepository.findBySeller(seller);
//        if (items.isEmpty()) {
//            throw new ItemNotFoundException("No items found for seller: " + seller.getEmail());
//        }
//        return items;
//    }

    @Override
    public Page<Item> getAllItems(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return itemRepository.findAll(pageable);
    }


    @Override
    @Transactional
    public void deleteItemById(Long id) throws ItemNotFoundException {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Cannot delete — item not found with ID: " + id));
        item.getUsers().forEach(user -> user.getWishlist().remove(item));
        itemRepository.delete(item);
    }


    @Override
    @Transactional
    public void updateItem(Item updatedItem, Long itemId, User seller) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Cannot update — item not found with ID: " + itemId));
        if (!existingItem.getSeller().getId().equals(seller.getId())) {
            throw new SecurityException("You are not authorized to update this item.");
        }
        if (updatedItem.getCategory() != null && updatedItem.getCategory().getId() != null) {
            Category category = categoryRepository.findById(updatedItem.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Category not found with ID: " + updatedItem.getCategory().getId()));
            existingItem.setCategory(category);
        }
        if (updatedItem.getName() != null) existingItem.setName(updatedItem.getName());
        if (updatedItem.getPrice() != null) existingItem.setPrice(updatedItem.getPrice());
        if (updatedItem.getBrand() != null) existingItem.setBrand(updatedItem.getBrand());
        if (updatedItem.getQuantity() > 0) existingItem.setQuantity(updatedItem.getQuantity());
        if (updatedItem.getWeight() != null) existingItem.setWeight(updatedItem.getWeight());
        if (updatedItem.getDiscountPrice() != null) existingItem.setDiscountPrice(updatedItem.getDiscountPrice());
        existingItem.setInStock(updatedItem.isInStock());
        existingItem.setOnSale(updatedItem.isOnSale());
    /*
    if (updatedItem.getColors() != null) {
        existingItem.getColors().clear();
        existingItem.getColors().addAll(updatedItem.getColors());
    }
    if (updatedItem.getSizes() != null) {
        existingItem.getSizes().clear();
        existingItem.getSizes().addAll(updatedItem.getSizes());
    }
    if (updatedItem.getShippingClasses() != null) {
        existingItem.setShippingClasses(updatedItem.getShippingClasses());
    }
    */
        itemRepository.save(existingItem);
    }



    @Override
    public Item findItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }




    @Override
    public Long countItemsByBrandAndName(String brand, String name) {
        return itemRepository.countItemByBrandAndName(brand, name);
    }

    @Override
    public List<Item> searchItems(String category, String brand, String name) {
        List<Item> items = itemRepository.searchItems(category, brand, name);
        if (items.isEmpty()) {
            throw new ItemNotFoundException(
                    "No items found matching filters — category: " + category +
                            ", brand: " + brand + ", name: " + name);
        }
        return items;
    }

    @Override
    public List<Item> relatedItems(Long itemId, String category, String brand) {
        List<Item> related = itemRepository.findRelatedItems(itemId, category, brand);
        if (related.isEmpty()) {
            return itemRepository.findRandomFeaturedItems(4);
        }
        return related;
    }

    @Override
    public List<Item> getFeaturedItems(int limit) {
        return itemRepository.findRandomFeaturedItems(limit);
    }
}


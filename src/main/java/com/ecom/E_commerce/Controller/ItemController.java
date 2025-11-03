package com.ecom.E_commerce.Controller;

import com.ecom.E_commerce.Model.Category;
import com.ecom.E_commerce.Model.Item;
import com.ecom.E_commerce.Model.User;
import com.ecom.E_commerce.Repository.UserRepository;
import com.ecom.E_commerce.Request.ItemRequest;
import com.ecom.E_commerce.Service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<Item> createItem(
            @RequestPart("item") ItemRequest request,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            Principal principal
    ) throws Exception {
        User seller = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        Item item = itemService.createitem(request, images, seller);
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Item>> getItem(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Item> items = itemService.getAllItems(page, size);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Item> GetById(@PathVariable Long id){
        Item item = itemService.findItemById(id);
        return new ResponseEntity<>(item,HttpStatus.OK);
    }
    public ResponseEntity<Item> findById(@PathVariable Long id){
        Item item = itemService.findItemById(id);
        return new ResponseEntity<>(item,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteItem(@PathVariable Long id) {
        Item item = itemService.findItemById(id);
        itemService.deleteItemById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Product deleted successfully");
        response.put("deletedItemName", item.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Map<String, Object>> updateItem(
            @PathVariable Long id,
            @RequestBody Item updatedItem,
            Principal principal
    ) {
        User seller = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        itemService.updateItem(updatedItem, id, seller);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Item updated successfully");
        response.put("item", itemService.findItemById(id));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/my-items")
    public ResponseEntity<Page<Item>> getSellerItems(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        System.out.println("JWT User: " + principal.getName());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authorities: " + auth.getAuthorities());
        User seller = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        Page<Item> items = itemService.getItemsBySeller(seller, page, size);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> searchItems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String name) {
        List<Item> items = itemService.searchItems(category, brand, name);
        return ResponseEntity.ok(items);
    }
    @GetMapping("/count")
    public Long getCountByBrandAndName(
            @RequestParam String brand,
            @RequestParam String name) {
        return itemService.countItemsByBrandAndName(brand, name);
    }

    @GetMapping("/{id}/related")
    public List<Item> getRelatedItems(@PathVariable Long id) {
        Item currentItem = itemService.findItemById(id);
        return itemService.relatedItems(
                currentItem.getId(),
                currentItem.getCategory().getName(),
                currentItem.getBrand()
        );
    }


    @GetMapping("/featured")
    public List<Item> getFeaturedItems(@RequestParam(defaultValue = "8") int limit) {
        return itemService.getFeaturedItems(limit);
    }



}

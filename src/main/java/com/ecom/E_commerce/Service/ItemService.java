package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Exception.ItemNotFoundException;
import com.ecom.E_commerce.Model.Item;
import com.ecom.E_commerce.Model.User;
import com.ecom.E_commerce.Request.ItemRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {

    public Item createitem(ItemRequest request, MultipartFile[] images,User seller) throws Exception;

    Page<Item> getItemsBySeller(User seller,int page,int size);

    Page<Item> getAllItems(int page, int size);

    void deleteItemById(Long id) throws ItemNotFoundException;

    void updateItem(Item item,Long itemId,User seller);

    Item findItemById(Long id);

    Long countItemsByBrandAndName(String brand,String name);

    List<Item> searchItems(String category, String brand, String name);

    List<Item> relatedItems(Long itemId, String category, String brand);

    List<Item> getFeaturedItems(int limit);


}

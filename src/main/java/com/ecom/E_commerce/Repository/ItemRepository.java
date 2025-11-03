package com.ecom.E_commerce.Repository;

import com.ecom.E_commerce.Model.Item;
import com.ecom.E_commerce.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {



    Page<Item> findBySeller(User seller, Pageable pageable);

    List<Item> findByCategory_Name(String categoryName);

    List<Item> findByBrand(String brand);

    List<Item> findByCategory_NameAndBrand(String categoryName, String brand);

    List<Item> findByName(String name);

    List<Item> findByCategory_NameAndName(String categoryName, String name);

    @Query("SELECT COUNT(i) FROM Item i WHERE TRIM(LOWER(i.brand)) = TRIM(LOWER(:brand)) AND TRIM(LOWER(i.name)) = TRIM(LOWER(:name))")
    Long countItemByBrandAndName(@Param("brand") String brand, @Param("name") String name);

    @Query("SELECT i FROM Item i " +
            "WHERE (:category IS NULL OR i.category.name = :category) " +
            "AND (:brand IS NULL OR i.brand = :brand) " +
            "AND (:name IS NULL OR i.name = :name)")
    List<Item> searchItems(@Param("category") String category,
                           @Param("brand") String brand,
                           @Param("name") String name);


    @Query("SELECT i FROM Item i WHERE i.category.name = :category AND i.brand = :brand AND i.id <> :itemId")
    List<Item> findRelatedItems(@Param("itemId") Long itemId,
                                @Param("category") String category,
                                @Param("brand") String brand);

    @Query(value = "SELECT * FROM item ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Item> findRandomFeaturedItems(@Param("limit") int limit);
}

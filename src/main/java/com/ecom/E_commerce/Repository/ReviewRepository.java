package com.ecom.E_commerce.Repository;

import com.ecom.E_commerce.Model.Item;
import com.ecom.E_commerce.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    List<Review> findByItem(Item item);
}

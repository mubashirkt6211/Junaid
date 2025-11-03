package com.ecom.E_commerce.Repository;

import com.ecom.E_commerce.Model.Cart;
import com.ecom.E_commerce.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUserId(Long userId);

    Cart findByUser(User user);
}

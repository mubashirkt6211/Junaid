package com.ecom.E_commerce.Repository;

import com.ecom.E_commerce.Model.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AddressRepository extends JpaRepository<ShippingAddress,Long> {

    List<ShippingAddress> findByUserId(Long userId);



}

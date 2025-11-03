package com.ecom.E_commerce.Repository;

import com.ecom.E_commerce.Model.Order;
import com.ecom.E_commerce.Model.OrderStatus;
import com.ecom.E_commerce.Model.ShippingAddress;
import com.ecom.E_commerce.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findByUser(User user);

    Optional<Order> findByIdAndUser(Long id, User user);

    boolean existsByAddress(ShippingAddress address);

    Long countByOrderStatus(OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE DATE(o.createdAt) = CURRENT_DATE")
    Long countTodayOrders();

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.orderStatus = :status")
    Double getTotalRevenueByStatus(@Param("status") OrderStatus status);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.orderStatus = :status AND MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year")
    Double getMonthlyRevenueByStatus(@Param("status") OrderStatus status,
                                     @Param("month") int month,
                                     @Param("year") int year);
}

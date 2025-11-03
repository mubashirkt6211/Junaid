package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Model.Order;
import com.ecom.E_commerce.Request.OrderRequest;
import com.ecom.E_commerce.Response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    List<OrderResponse> getAllOrders();

    void deleteOrder(Long orderId);

    void cancelOrder(Long orderId);

    OrderResponse updateOrderStatus(Long orderId, String status);

    List<OrderResponse> getUserOrders(Long userId);

    Long countUserOrders(Long userId);

    OrderResponse getUserOrderById(Long userId, Long orderId);

    Long countAllOrders();

    Long countOrdersByStatus(String status);

    Long countTodayOrders();

    public Double getTotalRevenue();

    public List<Double> getMonthlyRevenue(int year);

    Order findById(Long id);




}

package com.ecom.E_commerce.Controller;

import com.ecom.E_commerce.Model.Order;
import com.ecom.E_commerce.Response.ApiResponse;
import com.ecom.E_commerce.Response.OrderResponse;
import com.ecom.E_commerce.Request.OrderRequest;
import com.ecom.E_commerce.Response.PaymentResponse;
import com.ecom.E_commerce.Service.OrderService;
import com.ecom.E_commerce.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody OrderRequest request) {
        OrderResponse createdOrder = orderService.createOrder(request);
        return ResponseEntity.ok(new ApiResponse<>("success","Order created successfully", createdOrder));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> allOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(new ApiResponse<>("success","All orders fetched successfully", orders));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(@PathVariable Long userId) {
        List<OrderResponse> userOrders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(new ApiResponse<>("success","User orders fetched successfully", userOrders));
    }

    @GetMapping("/user/{userId}/order/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getUserOrderById(
            @PathVariable Long userId,
            @PathVariable Long orderId) {
        OrderResponse orderById = orderService.getUserOrderById(userId, orderId);
        return ResponseEntity.ok(new ApiResponse<>("success","Order fetched successfully", orderById));
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long orderId) {
        try {
            orderService.cancelOrder(orderId);
            return ResponseEntity.ok(new ApiResponse<>("success","Order cancelled successfully", null));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("error",e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(new ApiResponse<>("success","Order deleted successfully", null));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(new ApiResponse<>("success","Order status updated successfully", updatedOrder));
    }

    @GetMapping("/dashboard/stats")
    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalOrders", orderService.countAllOrders());
        stats.put("processingOrders", orderService.countOrdersByStatus("PROCESSING"));
        stats.put("outForDeliveryOrders", orderService.countOrdersByStatus("OUT_OF_DELIVERY"));
        stats.put("deliveredOrders", orderService.countOrdersByStatus("DELIVERED"));
        stats.put("cancelledOrders", orderService.countOrdersByStatus("CANCELLED"));
        stats.put("pendingOrders", orderService.countOrdersByStatus("PENDING"));
        stats.put("todayOrders", orderService.countTodayOrders());
        return stats;
    }

    @GetMapping("/dashboard/revenue")
    public Map<String, Object> getRevenueStats(@RequestParam(defaultValue = "2025") int year) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", orderService.getTotalRevenue());
        stats.put("monthlyRevenue", orderService.getMonthlyRevenue(year));
        return stats;
    }

}

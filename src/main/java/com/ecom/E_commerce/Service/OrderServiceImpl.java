package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Exception.ItemNotFoundException;
import com.ecom.E_commerce.Model.*;
import com.ecom.E_commerce.Repository.AddressRepository;
import com.ecom.E_commerce.Repository.ItemRepository;
import com.ecom.E_commerce.Repository.OrderRepository;
import com.ecom.E_commerce.Repository.UserRepository;
import com.ecom.E_commerce.Request.CartItemRequest;
import com.ecom.E_commerce.Request.OrderRequest;
import com.ecom.E_commerce.Response.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final AddressRepository addressRepository;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        ShippingAddress address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + request.getAddressId()));

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);

        order.setShippingType(ShippingType.valueOf(request.getShippingType().trim().toUpperCase()));
        Payment paymentMethod = Payment.valueOf(request.getPaymentMethod().trim().toUpperCase());
        order.setPayment(paymentMethod);
        if (request.getCartItems() == null || request.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart cannot be empty");
        }
        BigDecimal itemsTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItemRequest cartItem : request.getCartItems()) {
            Item item = itemRepository.findById(cartItem.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found with id: " + cartItem.getItemId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(item.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            itemsTotal = itemsTotal.add(orderItem.getTotalPrice());
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        order.setTotalItem(orderItems.size());

        BigDecimal shippingCost = BigDecimal.valueOf(order.getShippingType().getExtraCharge());
        order.setShippingCost(shippingCost);
        order.setTotalPrice(itemsTotal.add(shippingCost));
        Order savedOrder = orderRepository.save(order);

        PaymentResponse paymentResponse = null;

        if (!order.getPayment().name().equalsIgnoreCase("COD")) {
            paymentResponse = paymentService.createPaymentLink(savedOrder);
        }
        OrderResponse response = mapToOrderResponse(savedOrder);
        if (paymentResponse != null) {
            response.setPaymentUrl(paymentResponse.getPayment_url());
        }
        return response;

    }


    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUser(user).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long countUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return (long) orderRepository.findByUser(user).size();
    }

    @Override
    public OrderResponse getUserOrderById(Long userId, Long orderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new ItemNotFoundException(
                        "Order not found for user with id " + userId + " and orderId " + orderId
                ));

        return mapToOrderResponse(order);
    }

    @Override
    public Long countAllOrders() {
        return orderRepository.count();
    }

    @Override
    public Long countOrdersByStatus(String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            return orderRepository.countByOrderStatus(orderStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }
    }

    @Override
    public Long countTodayOrders() {
        return orderRepository.countTodayOrders();
    }

    @Override
    public Double getTotalRevenue() {
        return orderRepository.getTotalRevenueByStatus(OrderStatus.DELIVERED);
    }

    @Override
    public List<Double> getMonthlyRevenue(int year) {
        List<Double> monthlyRevenue = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Double revenue = orderRepository.getMonthlyRevenueByStatus(OrderStatus.DELIVERED, month, year);
            monthlyRevenue.add(revenue != null ? revenue : 0.0);
        }
        return monthlyRevenue;
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ItemNotFoundException("Order not found with id " + orderId));

        if (order.getOrderStatus() == OrderStatus.PENDING) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        } else {
            throw new IllegalStateException(
                    "Your order is already " + order.getOrderStatus() + " so it cannot be cancelled."
            );
        }
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ItemNotFoundException("Order not found with id: " + orderId));

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setOrderStatus(newStatus);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Invalid order status: " + status +
                    ". Allowed values: " + java.util.Arrays.toString(OrderStatus.values()));
        }

        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }


    @Override
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ItemNotFoundException("Order not found with id " + orderId));
        orderRepository.delete(order);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus().name())
                .itemsTotal(order.getOrderItems().stream()
                        .map(OrderItem::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .shippingCost(order.getShippingCost())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .shippingType(order.getShippingType().name())
                .payment(order.getPayment() != null ? order.getPayment().name() : "NOT_PAID")
                .shippingAddress(order.getAddress() != null ? ShippingAddressResponse.builder()
                        .id(order.getAddress().getId())
                        .street(order.getAddress().getStreet())
                        .city(order.getAddress().getCity())
                        .district(order.getAddress().getDistrict())
                        .state(order.getAddress().getState())
                        .zipcode(order.getAddress().getZipcode())
                        .country(order.getAddress().getCountry())
                        .phoneNumber(order.getAddress().getPhoneNumber())
                        .build() : null)
                .user(order.getUser() != null ? UserResponse.builder()
                        .id(order.getUser().getId())
                        .name(order.getUser().getFirstName())
                        .email(order.getUser().getEmail())
                        .build() : null)
                .items(order.getOrderItems().stream()
                        .map(oi -> OrderItemResponse.builder()
                                .itemId(oi.getItem().getId())
                                .itemName(oi.getItem().getName())
                                .unitPrice(oi.getItem().getPrice())
                                .quantity(oi.getQuantity())
                                .totalPrice(oi.getTotalPrice())
                                .imageUrl(
                                        oi.getItem().getImageUrls() != null && !oi.getItem().getImageUrls().isEmpty()
                                                ? oi.getItem().getImageUrls().get(0)
                                                : null
                                )
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Model.Order;
import com.ecom.E_commerce.Response.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Override
    public PaymentResponse createPaymentLink(Order order) {
        Stripe.apiKey = stripeSecretKey;

        BigDecimal totalAmount = order.getTotalPrice();

        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than zero.");
        }

        Long amountInCents = totalAmount.multiply(BigDecimal.valueOf(100)).longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4200/ordersuccess?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:4200/payment/fail")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("inr")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Make your Contactless Payment!")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("orderId", String.valueOf(order.getId()))
                .build();

        try {
            Session session = Session.create(params);

            PaymentResponse response = new PaymentResponse();
            response.setPayment_url(session.getUrl());
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error creating Stripe session", e);
        }
    }

}

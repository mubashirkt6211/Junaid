package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Model.Order;
import com.ecom.E_commerce.Response.PaymentResponse;

public interface PaymentService {

    public PaymentResponse createPaymentLink(Order order);
}

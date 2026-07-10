package com.resteflex.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @Value("${stripe.publishable-key}")
    private String stripePublishableKey;

    public Session createCheckoutSession(String bookingId, Double amount, String email) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://resteflex-conciergerie.fr/booking-success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("https://resteflex-conciergerie.fr/listings")
                .setCustomerEmail(email)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount((long) (amount * 100))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Réservation ResteFlex")
                                                                .build())
                                                .build())
                                .setQuantity(1L)
                                .build())
                .putMetadata("booking_id", bookingId)
                .build();

        return Session.create(params);
    }

    public String getPublishableKey() {
        return stripePublishableKey;
    }
}

package com.resteflex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    private String id;
    @JsonProperty("listing_id")
    private String listingId;
    private String email;
    @JsonProperty("check_in")
    private String checkIn;
    @JsonProperty("check_out")
    private String checkOut;
    private Integer guests;
    @JsonProperty("total_price")
    private Double totalPrice;
    @JsonProperty("stripe_payment_id")
    private String stripePaymentId;
    private String status;
    @JsonProperty("created_at")
    private String createdAt;
}

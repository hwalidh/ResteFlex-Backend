package com.resteflex.dto;

import com.resteflex.entity.Booking;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private String id;
    private String listingId;
    private String listingTitle;
    private String email;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guests;
    private Double totalPrice;
    private String stripePaymentId;
    private String status;
    private LocalDateTime createdAt;

    public static BookingResponse fromEntity(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .listingId(booking.getListing().getId())
                .listingTitle(booking.getListing().getTitle())
                .email(booking.getEmail())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .guests(booking.getGuests())
                .totalPrice(booking.getTotalPrice())
                .stripePaymentId(booking.getStripePaymentId())
                .status(booking.getStatus().toString())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}

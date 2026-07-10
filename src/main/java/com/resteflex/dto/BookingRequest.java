package com.resteflex.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {
    private String listingId;
    private String email;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guests;
}

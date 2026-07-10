package com.resteflex.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {
    private String listingId;
    private String email;
    private String checkIn;   // format: "2024-12-15"
    private String checkOut;  // format: "2024-12-20"
    private Integer guests;
}

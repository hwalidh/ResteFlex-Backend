package com.resteflex.controller;

import com.resteflex.dto.BookingRequest;
import com.resteflex.dto.BookingResponse;
import com.resteflex.service.BookingService;
import com.resteflex.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Gestion des réservations")
public class BookingController {

    private final BookingService bookingService;
    private final StripeService stripeService;

    @PostMapping
    @Operation(summary = "Créer une réservation")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BookingResponse.fromEntity(bookingService.createBooking(request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une réservation")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable String id) {
        return ResponseEntity.ok(BookingResponse.fromEntity(bookingService.getBookingById(id)));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Réservations par email")
    public ResponseEntity<List<BookingResponse>> getBookingsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(
                bookingService.getBookingsByEmail(email).stream()
                        .map(BookingResponse::fromEntity).toList());
    }

    @GetMapping("/listing/{listingId}")
    @Operation(summary = "Réservations par logement")
    public ResponseEntity<List<BookingResponse>> getBookingsByListing(@PathVariable String listingId) {
        return ResponseEntity.ok(
                bookingService.getBookingsByListing(listingId).stream()
                        .map(BookingResponse::fromEntity).toList());
    }

    @PostMapping("/{id}/checkout")
    @Operation(summary = "Créer session Stripe Checkout")
    public ResponseEntity<Map<String, String>> createCheckoutSession(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) throws StripeException {
        var booking = bookingService.getBookingById(id);
        Session session = stripeService.createCheckoutSession(id, booking.getTotalPrice(), payload.get("email"));
        return ResponseEntity.ok(Map.of(
                "sessionId", session.getId(),
                "url", session.getUrl()));
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirmer le paiement Stripe")
    public ResponseEntity<BookingResponse> confirmPayment(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(
                BookingResponse.fromEntity(
                        bookingService.confirmPayment(id, payload.get("stripePaymentId"))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Annuler une réservation")
    public ResponseEntity<Void> cancelBooking(@PathVariable String id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }
}

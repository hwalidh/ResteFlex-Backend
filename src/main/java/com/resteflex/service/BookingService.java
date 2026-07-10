package com.resteflex.service;

import com.resteflex.client.SupabaseClient;
import com.resteflex.dto.BookingRequest;
import com.resteflex.model.Booking;
import com.resteflex.model.Listing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final SupabaseClient supabaseClient;
    private final ICalService iCalService;

    public Booking createBooking(BookingRequest request) {
        // Récupérer le logement
        Listing listing = supabaseClient.getSingle("listings",
                "id=eq." + request.getListingId() + "&select=*", Listing.class);

        if (listing == null) {
            throw new NoSuchElementException("Listing not found: " + request.getListingId());
        }

        // Vérifier disponibilité
        checkAvailability(request.getListingId(), request.getCheckIn(), request.getCheckOut());

        // Calculer le prix
        long nights = ChronoUnit.DAYS.between(
                LocalDate.parse(request.getCheckIn()),
                LocalDate.parse(request.getCheckOut()));
        double totalPrice = listing.getPrice() * nights;

        // Créer la réservation
        Booking booking = Booking.builder()
                .listingId(request.getListingId())
                .email(request.getEmail())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .guests(request.getGuests())
                .totalPrice(totalPrice)
                .status("pending")
                .build();

        Booking saved = supabaseClient.insert("bookings", booking, Booking.class);

        // Sync iCal
        iCalService.addBookingToCalendar(saved, listing);

        return saved;
    }

    public Booking getBookingById(String id) {
        Booking booking = supabaseClient.getSingle("bookings",
                "id=eq." + id + "&select=*", Booking.class);
        if (booking == null) throw new NoSuchElementException("Booking not found: " + id);
        return booking;
    }

    public List<Booking> getBookingsByEmail(String email) {
        return supabaseClient.getList("bookings",
                "email=eq." + email + "&select=*&order=created_at.desc", Booking.class);
    }

    public List<Booking> getBookingsByListing(String listingId) {
        return supabaseClient.getList("bookings",
                "listing_id=eq." + listingId + "&select=*", Booking.class);
    }

    public Booking confirmPayment(String bookingId, String stripePaymentId) {
        Booking update = new Booking();
        update.setStripePaymentId(stripePaymentId);
        update.setStatus("paid");
        return supabaseClient.update("bookings", "id=eq." + bookingId, update, Booking.class);
    }

    public void cancelBooking(String bookingId) {
        Booking booking = getBookingById(bookingId);
        Booking update = new Booking();
        update.setStatus("cancelled");
        supabaseClient.update("bookings", "id=eq." + bookingId, update, Booking.class);
        iCalService.removeBookingFromCalendar(bookingId);
    }

    private void checkAvailability(String listingId, String checkIn, String checkOut) {
        List<Booking> existing = supabaseClient.getList("bookings",
                "listing_id=eq." + listingId +
                "&status=neq.cancelled" +
                "&select=check_in,check_out", Booking.class);

        LocalDate newIn = LocalDate.parse(checkIn);
        LocalDate newOut = LocalDate.parse(checkOut);

        boolean conflict = existing.stream().anyMatch(b -> {
            LocalDate bIn = LocalDate.parse(b.getCheckIn());
            LocalDate bOut = LocalDate.parse(b.getCheckOut());
            return !(newOut.isBefore(bIn) || newIn.isAfter(bOut));
        });

        if (conflict) throw new IllegalArgumentException("Ces dates ne sont pas disponibles");
    }
}

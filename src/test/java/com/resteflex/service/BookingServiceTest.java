package com.resteflex.service;

import com.resteflex.client.SupabaseClient;
import com.resteflex.dto.BookingRequest;
import com.resteflex.model.Booking;
import com.resteflex.model.Listing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private SupabaseClient supabaseClient;
    @Mock private ICalService iCalService;
    @InjectMocks private BookingService bookingService;

    private Listing testListing;
    private BookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        testListing = Listing.builder()
                .id("listing-1")
                .title("Penthouse Paris")
                .price(185.0)
                .location("Paris")
                .build();

        bookingRequest = BookingRequest.builder()
                .listingId("listing-1")
                .email("test@example.com")
                .checkIn("2024-12-15")
                .checkOut("2024-12-20")
                .guests(2)
                .build();
    }

    @Test
    void testCreateBooking_Success() {
        when(supabaseClient.getSingle(eq("listings"), anyString(), eq(Listing.class)))
                .thenReturn(testListing);
        when(supabaseClient.getList(eq("bookings"), anyString(), eq(Booking.class)))
                .thenReturn(List.of());

        Booking savedBooking = Booking.builder()
                .id("booking-1")
                .listingId("listing-1")
                .email("test@example.com")
                .checkIn("2024-12-15")
                .checkOut("2024-12-20")
                .guests(2)
                .totalPrice(925.0)
                .status("pending")
                .build();

        when(supabaseClient.insert(eq("bookings"), any(), eq(Booking.class)))
                .thenReturn(savedBooking);

        Booking result = bookingService.createBooking(bookingRequest);

        assertEquals("test@example.com", result.getEmail());
        assertEquals("pending", result.getStatus());
        assertEquals(925.0, result.getTotalPrice());
        verify(iCalService).addBookingToCalendar(any(), any());
    }

    @Test
    void testCreateBooking_ListingNotFound() {
        when(supabaseClient.getSingle(eq("listings"), anyString(), eq(Listing.class)))
                .thenReturn(null);
        assertThrows(NoSuchElementException.class, () -> bookingService.createBooking(bookingRequest));
    }

    @Test
    void testCreateBooking_DatesConflict() {
        when(supabaseClient.getSingle(eq("listings"), anyString(), eq(Listing.class)))
                .thenReturn(testListing);

        Booking existing = Booking.builder()
                .checkIn("2024-12-14")
                .checkOut("2024-12-18")
                .status("confirmed")
                .build();

        when(supabaseClient.getList(eq("bookings"), anyString(), eq(Booking.class)))
                .thenReturn(List.of(existing));

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(bookingRequest));
    }

    @Test
    void testCancelBooking() {
        Booking booking = Booking.builder().id("b-1").status("pending").build();
        when(supabaseClient.getSingle(eq("bookings"), anyString(), eq(Booking.class)))
                .thenReturn(booking);
        when(supabaseClient.update(anyString(), anyString(), any(), eq(Booking.class)))
                .thenReturn(booking);

        bookingService.cancelBooking("b-1");

        verify(iCalService).removeBookingFromCalendar("b-1");
    }

    @Test
    void testConfirmPayment() {
        Booking booking = Booking.builder().id("b-1").status("paid")
                .stripePaymentId("pi_123").build();
        when(supabaseClient.update(anyString(), anyString(), any(), eq(Booking.class)))
                .thenReturn(booking);

        Booking result = bookingService.confirmPayment("b-1", "pi_123");

        assertEquals("paid", result.getStatus());
        assertEquals("pi_123", result.getStripePaymentId());
    }
}

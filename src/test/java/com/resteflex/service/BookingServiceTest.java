package com.resteflex.service;

import com.resteflex.dto.BookingRequest;
import com.resteflex.entity.Booking;
import com.resteflex.entity.Listing;
import com.resteflex.repository.BookingRepository;
import com.resteflex.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private ListingRepository listingRepository;
    @Mock private StripeService stripeService;
    @Mock private ICalService iCalService;

    @InjectMocks
    private BookingService bookingService;

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
                .checkIn(LocalDate.now().plusDays(5))
                .checkOut(LocalDate.now().plusDays(10))
                .guests(2)
                .build();
    }

    @Test
    void testCreateBooking_Success() {
        when(listingRepository.findById("listing-1")).thenReturn(Optional.of(testListing));
        when(bookingRepository.findByListingIdAndStatusNot("listing-1", Booking.BookingStatus.CANCELLED))
                .thenReturn(List.of());

        Booking saved = Booking.builder()
                .id("booking-1")
                .listing(testListing)
                .email("test@example.com")
                .checkIn(bookingRequest.getCheckIn())
                .checkOut(bookingRequest.getCheckOut())
                .guests(2)
                .totalPrice(925.0)
                .status(Booking.BookingStatus.PENDING)
                .build();

        when(bookingRepository.save(any())).thenReturn(saved);

        Booking result = bookingService.createBooking(bookingRequest);

        assertEquals("test@example.com", result.getEmail());
        assertEquals(Booking.BookingStatus.PENDING, result.getStatus());
        assertEquals(925.0, result.getTotalPrice());
        verify(iCalService).addBookingToCalendar(any());
    }

    @Test
    void testCreateBooking_ListingNotFound() {
        when(listingRepository.findById("invalid")).thenReturn(Optional.empty());
        BookingRequest req = BookingRequest.builder().listingId("invalid").build();
        assertThrows(NoSuchElementException.class, () -> bookingService.createBooking(req));
    }

    @Test
    void testCreateBooking_DatesNotAvailable() {
        when(listingRepository.findById("listing-1")).thenReturn(Optional.of(testListing));

        Booking existing = Booking.builder()
                .checkIn(LocalDate.now().plusDays(4))
                .checkOut(LocalDate.now().plusDays(8))
                .status(Booking.BookingStatus.CONFIRMED)
                .build();

        when(bookingRepository.findByListingIdAndStatusNot("listing-1", Booking.BookingStatus.CANCELLED))
                .thenReturn(List.of(existing));

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(bookingRequest));
    }

    @Test
    void testGetBookingById_Found() {
        Booking booking = Booking.builder().id("booking-1").build();
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        assertEquals("booking-1", bookingService.getBookingById("booking-1").getId());
    }

    @Test
    void testGetBookingById_NotFound() {
        when(bookingRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> bookingService.getBookingById("x"));
    }

    @Test
    void testCancelBooking() {
        Booking booking = Booking.builder()
                .id("booking-1")
                .status(Booking.BookingStatus.PENDING)
                .build();
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        bookingService.cancelBooking("booking-1");

        assertEquals(Booking.BookingStatus.CANCELLED, booking.getStatus());
        verify(iCalService).removeBookingFromCalendar(booking);
    }

    @Test
    void testConfirmPayment() {
        Booking booking = Booking.builder()
                .id("booking-1")
                .status(Booking.BookingStatus.PENDING)
                .build();
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        bookingService.confirmPayment("booking-1", "pi_stripe_123");

        assertEquals(Booking.BookingStatus.PAID, booking.getStatus());
        assertEquals("pi_stripe_123", booking.getStripePaymentId());
    }
}

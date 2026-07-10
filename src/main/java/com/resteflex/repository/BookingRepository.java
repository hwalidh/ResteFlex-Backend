package com.resteflex.repository;

import com.resteflex.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByEmail(String email);
    List<Booking> findByListingId(String listingId);
    List<Booking> findByListingIdAndStatusNot(String listingId, Booking.BookingStatus status);
    List<Booking> findByCheckInBetween(LocalDate startDate, LocalDate endDate);
    List<Booking> findByStripePaymentId(String stripePaymentId);
}

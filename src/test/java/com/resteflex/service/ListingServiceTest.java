package com.resteflex.service;

import com.resteflex.entity.Listing;
import com.resteflex.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;

    @InjectMocks
    private ListingService listingService;

    private Listing testListing;

    @BeforeEach
    void setUp() {
        testListing = Listing.builder()
                .id("1")
                .title("Penthouse Paris")
                .description("Magnifique penthouse")
                .price(185.0)
                .location("Paris")
                .bedrooms(3)
                .bathrooms(2.0)
                .guests(6)
                .imageUrl("http://example.com/image.jpg")
                .amenities(new String[]{"WiFi", "Climatisation"})
                .build();
    }

    @Test
    void testGetAllListings() {
        when(listingRepository.findAll()).thenReturn(List.of(testListing));
        List<Listing> result = listingService.getAllListings();
        assertEquals(1, result.size());
        assertEquals("Penthouse Paris", result.get(0).getTitle());
    }

    @Test
    void testGetListingById_Found() {
        when(listingRepository.findById("1")).thenReturn(Optional.of(testListing));
        Listing result = listingService.getListingById("1");
        assertEquals("Penthouse Paris", result.getTitle());
    }

    @Test
    void testGetListingById_NotFound() {
        when(listingRepository.findById("999")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> listingService.getListingById("999"));
    }

    @Test
    void testCreateListing() {
        when(listingRepository.save(any())).thenReturn(testListing);
        Listing result = listingService.createListing(testListing);
        assertEquals("Penthouse Paris", result.getTitle());
        verify(listingRepository).save(testListing);
    }

    @Test
    void testDeleteListing() {
        listingService.deleteListing("1");
        verify(listingRepository).deleteById("1");
    }

    @Test
    void testSearchByLocation() {
        when(listingRepository.findByLocationContainingIgnoreCase("Paris")).thenReturn(List.of(testListing));
        List<Listing> result = listingService.searchByLocation("Paris");
        assertEquals(1, result.size());
    }
}

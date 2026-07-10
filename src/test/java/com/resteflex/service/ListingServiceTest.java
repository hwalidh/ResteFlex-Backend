package com.resteflex.service;

import com.resteflex.client.SupabaseClient;
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
class ListingServiceTest {

    @Mock private SupabaseClient supabaseClient;
    @InjectMocks private ListingService listingService;

    private Listing testListing;

    @BeforeEach
    void setUp() {
        testListing = Listing.builder()
                .id("1")
                .title("Penthouse Paris")
                .price(185.0)
                .location("Paris")
                .bedrooms(3)
                .guests(6)
                .build();
    }

    @Test
    void testGetAllListings() {
        when(supabaseClient.getList(eq("listings"), anyString(), eq(Listing.class)))
                .thenReturn(List.of(testListing));
        List<Listing> result = listingService.getAllListings();
        assertEquals(1, result.size());
        assertEquals("Penthouse Paris", result.get(0).getTitle());
    }

    @Test
    void testGetListingById() {
        when(supabaseClient.getSingle(eq("listings"), anyString(), eq(Listing.class)))
                .thenReturn(testListing);
        Listing result = listingService.getListingById("1");
        assertEquals("Penthouse Paris", result.getTitle());
    }

    @Test
    void testGetListingById_NotFound() {
        when(supabaseClient.getSingle(eq("listings"), anyString(), eq(Listing.class)))
                .thenReturn(null);
        assertThrows(NoSuchElementException.class, () -> listingService.getListingById("999"));
    }

    @Test
    void testCreateListing() {
        when(supabaseClient.insert(eq("listings"), any(), eq(Listing.class)))
                .thenReturn(testListing);
        Listing result = listingService.createListing(testListing);
        assertEquals("Penthouse Paris", result.getTitle());
    }

    @Test
    void testSearchByLocation() {
        when(supabaseClient.getList(eq("listings"), contains("Paris"), eq(Listing.class)))
                .thenReturn(List.of(testListing));
        List<Listing> result = listingService.searchByLocation("Paris");
        assertEquals(1, result.size());
    }
}

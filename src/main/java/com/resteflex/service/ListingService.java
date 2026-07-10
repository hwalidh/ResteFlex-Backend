package com.resteflex.service;

import com.resteflex.client.SupabaseClient;
import com.resteflex.model.Listing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final SupabaseClient supabaseClient;

    public List<Listing> getAllListings() {
        return supabaseClient.getList("listings", "select=*&order=created_at.asc", Listing.class);
    }

    public Listing getListingById(String id) {
        Listing listing = supabaseClient.getSingle("listings", "id=eq." + id + "&select=*", Listing.class);
        if (listing == null) throw new NoSuchElementException("Listing not found: " + id);
        return listing;
    }

    public List<Listing> searchByLocation(String location) {
        return supabaseClient.getList("listings", "location=ilike.*" + location + "*&select=*", Listing.class);
    }

    public List<Listing> searchByPriceRange(Double min, Double max) {
        return supabaseClient.getList("listings", "price=gte." + min + "&price=lte." + max + "&select=*", Listing.class);
    }

    public List<Listing> searchByGuests(Integer guests) {
        return supabaseClient.getList("listings", "guests=gte." + guests + "&select=*", Listing.class);
    }

    public Listing createListing(Listing listing) {
        return supabaseClient.insert("listings", listing, Listing.class);
    }

    public Listing updateListing(String id, Listing listing) {
        return supabaseClient.update("listings", "id=eq." + id, listing, Listing.class);
    }

    public void deleteListing(String id) {
        supabaseClient.delete("listings", "id=eq." + id);
    }
}

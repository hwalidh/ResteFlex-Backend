package com.resteflex.service;

import com.resteflex.entity.Listing;
import com.resteflex.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;

    public List<Listing> getAllListings() {
        return listingRepository.findAll();
    }

    public Listing getListingById(String id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Listing not found: " + id));
    }

    public List<Listing> searchByLocation(String location) {
        return listingRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<Listing> searchByPriceRange(Double minPrice, Double maxPrice) {
        return listingRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Listing> searchByGuests(Integer guests) {
        return listingRepository.findByGuestsGreaterThanEqual(guests);
    }

    public Listing createListing(Listing listing) {
        return listingRepository.save(listing);
    }

    public Listing updateListing(String id, Listing update) {
        Listing existing = getListingById(id);
        if (update.getTitle() != null)       existing.setTitle(update.getTitle());
        if (update.getDescription() != null) existing.setDescription(update.getDescription());
        if (update.getPrice() != null)       existing.setPrice(update.getPrice());
        if (update.getLocation() != null)    existing.setLocation(update.getLocation());
        if (update.getBedrooms() != null)    existing.setBedrooms(update.getBedrooms());
        if (update.getBathrooms() != null)   existing.setBathrooms(update.getBathrooms());
        if (update.getGuests() != null)      existing.setGuests(update.getGuests());
        if (update.getImageUrl() != null)    existing.setImageUrl(update.getImageUrl());
        if (update.getImages() != null)      existing.setImages(update.getImages());
        if (update.getAmenities() != null)   existing.setAmenities(update.getAmenities());
        return listingRepository.save(existing);
    }

    public void deleteListing(String id) {
        listingRepository.deleteById(id);
    }
}

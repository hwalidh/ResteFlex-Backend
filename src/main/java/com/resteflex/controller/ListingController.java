package com.resteflex.controller;

import com.resteflex.model.Listing;
import com.resteflex.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/listings")
@RequiredArgsConstructor
@Tag(name = "Listings", description = "Gestion des logements")
public class ListingController {

    private final ListingService listingService;

    @GetMapping
    @Operation(summary = "Tous les logements")
    public ResponseEntity<List<Listing>> getAllListings() {
        return ResponseEntity.ok(listingService.getAllListings());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Un logement par ID")
    public ResponseEntity<Listing> getListingById(@PathVariable String id) {
        return ResponseEntity.ok(listingService.getListingById(id));
    }

    @GetMapping("/search/location")
    @Operation(summary = "Rechercher par localisation")
    public ResponseEntity<List<Listing>> searchByLocation(@RequestParam String location) {
        return ResponseEntity.ok(listingService.searchByLocation(location));
    }

    @GetMapping("/search/price")
    @Operation(summary = "Rechercher par prix")
    public ResponseEntity<List<Listing>> searchByPrice(
            @RequestParam Double min, @RequestParam Double max) {
        return ResponseEntity.ok(listingService.searchByPriceRange(min, max));
    }

    @GetMapping("/search/guests")
    @Operation(summary = "Rechercher par nombre d'hôtes")
    public ResponseEntity<List<Listing>> searchByGuests(@RequestParam Integer guests) {
        return ResponseEntity.ok(listingService.searchByGuests(guests));
    }

    @PostMapping
    @Operation(summary = "Créer un logement")
    public ResponseEntity<Listing> createListing(@RequestBody Listing listing) {
        return ResponseEntity.status(HttpStatus.CREATED).body(listingService.createListing(listing));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un logement")
    public ResponseEntity<Listing> updateListing(@PathVariable String id, @RequestBody Listing listing) {
        return ResponseEntity.ok(listingService.updateListing(id, listing));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un logement")
    public ResponseEntity<Void> deleteListing(@PathVariable String id) {
        listingService.deleteListing(id);
        return ResponseEntity.noContent().build();
    }
}

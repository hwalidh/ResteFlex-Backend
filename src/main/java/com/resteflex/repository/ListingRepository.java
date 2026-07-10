package com.resteflex.repository;

import com.resteflex.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, String> {
    List<Listing> findByLocationContainingIgnoreCase(String location);
    List<Listing> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Listing> findByGuestsGreaterThanEqual(Integer guests);
}

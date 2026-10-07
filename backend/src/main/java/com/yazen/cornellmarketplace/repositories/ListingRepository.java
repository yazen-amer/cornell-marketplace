package com.yazen.cornellmarketplace.repositories;

import com.yazen.cornellmarketplace.entities.Users;
import java.util.List;
import com.yazen.cornellmarketplace.entities.Listing;
import org.springframework.data.repository.CrudRepository;

public interface ListingRepository extends CrudRepository<Listing, Long> {
    List<Listing> findByTitleContainingIgnoreCase(String title);
}

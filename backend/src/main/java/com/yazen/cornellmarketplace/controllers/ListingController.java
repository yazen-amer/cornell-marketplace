package com.yazen.cornellmarketplace.controllers;

import com.yazen.cornellmarketplace.dtos.ListingEditRequest;
import com.yazen.cornellmarketplace.entities.Listing;
import com.yazen.cornellmarketplace.entities.Users;
import com.yazen.cornellmarketplace.repositories.UserRepository;
import com.yazen.cornellmarketplace.services.S3Service;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.yazen.cornellmarketplace.repositories.ListingRepository;
import com.yazen.cornellmarketplace.requests.ListingRequest;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ListingController {

    private final ListingRepository listingRepository;

    private final UserRepository userRepository;

    private S3Service s3Service;

    public ListingController(ListingRepository listingRepository, UserRepository userRepository, S3Service s3Service) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }

    @GetMapping("/listings/{id}")
    @ResponseBody
    public Listing getListing(@PathVariable Long id) {
        return listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
    }

    @PostMapping("/listings")
    @ResponseBody
    public Listing createListing(
            @RequestPart ListingRequest listingRequest,
            @RequestPart MultipartFile imageUpload) {

        String imageUrl = s3Service.upload(imageUpload);

        Listing listing = new Listing(
                listingRequest.getTitle(),
                listingRequest.getDescription(),
                listingRequest.getPrice(),
                imageUrl
        );

        Listing saved = listingRepository.save(listing);

        return saved;
    }

    @PatchMapping("/listings/{id}")
    public ResponseEntity<String> editListing(
            @PathVariable Long id,
            Principal principal,
            @RequestPart("listingEditRequest") ListingEditRequest listingEditRequest,
            @RequestPart(value = "imageUpload", required = false) MultipartFile imageUpload
    ) {

        Listing listing = listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found."));
        String username = principal.getName();
        Users user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found."));
        Long userID = user.getId();

        if (!listing.getSeller().getId().equals(userID)) {
            throw new RuntimeException("This is not your listing.");
        } else {
            if (listingEditRequest.getTitle() != null) {
                listing.setTitle(listingEditRequest.getTitle());
            }
            if (listingEditRequest.getDescription() != null) {
                listing.setDescription(listingEditRequest.getDescription());
            }
            if (listingEditRequest.getPrice() != null) {
                listing.setPrice(listingEditRequest.getPrice());
            }
            if (imageUpload != null && !imageUpload.isEmpty()) {
                listing.setImageUrl(s3Service.upload(imageUpload));
            }

            Listing saved = listingRepository.save(listing);
            return ResponseEntity.ok("Listing updated");
        }
    }

    @GetMapping("/listings")
    @ResponseBody
    public List<Listing> getAllListings() {
        ArrayList<Listing> ret = new ArrayList<Listing>();
        for (Listing listing : listingRepository.findAll()) {
            ret.add(listing);
        }
        return ret;
    }

    @GetMapping("/listings/search")
    @ResponseBody
    public List<Listing> getListingByTitle(@RequestParam String title) {
        return listingRepository.findByTitleContainingIgnoreCase(title);
    }

    @DeleteMapping("/listings/{id}")
    @ResponseBody
    public Listing deleteListing(@PathVariable Long id, Principal principal) {
        Listing listing = listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found."));
        String username = principal.getName();
        Users user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found."));
        Long userID = user.getId();

        if (!listing.getSeller().getId().equals(userID)) {
            throw new RuntimeException("This is not your listing.");
        } else {
            listingRepository.delete(listing);
            return listing;
        }
    }

}
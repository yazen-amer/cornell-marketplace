package com.yazen.cornellmarketplace.configs;

import com.yazen.cornellmarketplace.entities.Listing;
import com.yazen.cornellmarketplace.entities.Users;
import com.yazen.cornellmarketplace.repositories.ListingRepository;
import com.yazen.cornellmarketplace.repositories.UserRepository;
import java.io.IOException;
import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

/** Fictional campus listings, loaded only when the explicit demo profile is active. */
@Component
@Profile("demo")
public class DemoDataSeeder implements CommandLineRunner {
    static final String EMAIL = "marketplace-demo-listings@cornell.edu";
    private final UserRepository users;
    private final ListingRepository listings;
    private final PasswordEncoder encoder;
    private final JsonMapper json;

    public DemoDataSeeder(UserRepository users, ListingRepository listings,
                          PasswordEncoder encoder, JsonMapper json) {
        this.users = users;
        this.listings = listings;
        this.encoder = encoder;
        this.json = json;
    }

    @Override
    @Transactional
    public void run(String... args) throws IOException {
        Users seller = users.findByEmail(EMAIL).orElseGet(() -> users.save(
            new Users("Campus demo seller", EMAIL, encoder.encode(UUID.randomUUID().toString()))));
        // No verification bypass or shared demo password. This seller cannot log in.
        try (var input = new ClassPathResource("demo/listings.json").getInputStream()) {
            var entries = json.readTree(input);
            var existing = listings.findAll();
            for (var entry : entries) {
                String title = entry.get("title").asText();
                boolean present = java.util.stream.StreamSupport.stream(existing.spliterator(), false).anyMatch(item ->
                    item.getSeller().getId().equals(seller.getId()) && item.getTitle().equals(title));
                if (present) continue;
                Listing item = new Listing();
                item.setTitle(title);
                item.setDescription(entry.get("description").asText());
                item.setPrice(entry.get("price").asDouble());
                item.setImageUrl(entry.get("imageUrl").asText());
                item.setPickupLocation(entry.get("pickupLocation").asText());
                item.setSeller(seller);
                listings.save(item);
            }
        }
    }
}

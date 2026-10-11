package com.yazen.cornellmarketplace;

import com.yazen.cornellmarketplace.configs.DemoDataSeeder;
import com.yazen.cornellmarketplace.repositories.ListingRepository;
import com.yazen.cornellmarketplace.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("demo")
class DemoDataSeederTests {
    @Autowired DemoDataSeeder seeder;
    @Autowired ListingRepository listings;
    @Autowired UserRepository users;

    @Test void seedsRealisticListingsWithoutBypassingAccountVerification() throws Exception {
        assertEquals(12, listings.count());
        assertEquals(1, users.count());
        var seller = users.findByEmail("marketplace-demo-listings@cornell.edu").orElseThrow();
        assertFalse(seller.isVerified());
        assertNull(seller.getVerificationCode());
        assertTrue(seller.getPassword().startsWith("$2"));
        assertEquals(12, java.util.stream.StreamSupport.stream(listings.findAll().spliterator(), false).map(item -> item.getTitle()).distinct().count());
        for (var item : listings.findAll()) {
            assertTrue(item.getPrice() > 0 && item.getPrice() <= 200);
            assertTrue(item.getDescription().startsWith("Demo listing (fictional):"));
            assertFalse(item.getPickupLocation().isBlank());
            assertTrue(item.getImageUrl().startsWith("/demo/"));
            assertEquals(seller.getId(), item.getSeller().getId());
        }
        assertEquals(2, listings.findByTitleContainingIgnoreCase("COMPUTER").size());
        seeder.run();
        assertEquals(12, listings.count());
        assertEquals(1, users.count());
    }
}

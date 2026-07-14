package com.yazen.cornellmarketplace.requests;

import jakarta.persistence.ElementCollection;
import java.util.List;

public class ListingRequest {
    private String title;

    private String description;

    private double price;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }
}

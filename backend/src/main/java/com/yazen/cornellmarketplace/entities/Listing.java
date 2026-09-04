package com.yazen.cornellmarketplace.entities;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;

@Entity
public class Listing {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String title;

    private String description;

    private double price;

    private String imageUrl;

    private String pickupLocation;

    @ManyToOne
    private Users seller;

    public Listing() {}

    public Listing(String title, String description, double price, String imageUrl, String pickupLocation) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.pickupLocation = pickupLocation;
        this.seller = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

//    public String toString() {
//        return String.format(
//                "Listing[id=%d, title='%s', description='%s', price='']",
//                id, title, description, price, imageUrls);
//    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setSeller(Users user) {
        this.seller = user;
    }

    public Users getSeller() {
        return seller;
    }

    public void setTitle(String t) {
        this.title = t;
    }

    public void setDescription(String d) {
        this.description = d;
    }

    public void setPrice(double p) {
        this.price = p;
    }

    public void setImageUrl(String i) {
        this.imageUrl = i;
    }

    public void setPickupLocation(String p) {
        this.pickupLocation = p;
    }
}

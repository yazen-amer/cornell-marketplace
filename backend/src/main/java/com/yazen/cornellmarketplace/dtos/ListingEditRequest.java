package com.yazen.cornellmarketplace.dtos;

public class ListingEditRequest {
    private String title;

    private String description;

    private Double price;

    private String imageUrl;

    private String pickupLocation;

    public ListingEditRequest(String t, String d, Double p, String i, String pl) {
        this.title = t;
        this.description = d;
        this.price = p;
        this.imageUrl = i;
        this.pickupLocation = pl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Double getPrice() {
        return price;
    }

    public String getPickupLocation() {
        return pickupLocation;
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

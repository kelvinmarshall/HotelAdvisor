package dev.marshall.hoteladvisor.model;

/**
 * Created by Marshall on 10/04/2018.
 */

public class HotelSearch {
    private String name;
    private String image;
    private String location;
    private String price;
    private String rating;
    private String objectID;

    public HotelSearch(String name, String image, String location, String price, String rating, String objectID) {
        this.name = name;
        this.image = image;
        this.location = location;
        this.price = price;
        this.rating = rating;
        this.objectID = objectID;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getRating() {
        return rating;
    }

    public String getObjectID() {
        return objectID;
    }
}

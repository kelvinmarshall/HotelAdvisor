package dev.marshall.hoteladvisor.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Marshall on 27/01/2018.
 */

public class Hotels {
    private String Phone;
    private String Name;
    private String Image;
    private String Location;
    private String Price;
    private String Description;
    private String Locationdetail;
    private String Checkin;
    private String Checkout;
    private String Extras;
    private Amenities Amenities;
    private String Url;
    private MoreInformation MoreInfo;
    private Double lat,lng;
    private  String Rating;

    public Hotels() {
    }

    public Hotels(String phone, String name, String image, String location, String price, String description, String locationdetail, String checkin, String checkout, String extras, dev.marshall.hoteladvisor.model.Amenities amenities, String url, MoreInformation moreInfo, Double lat, Double lng, String rating) {
        Phone = phone;
        Name = name;
        Image = image;
        Location = location;
        Price = price;
        Description = description;
        Locationdetail = locationdetail;
        Checkin = checkin;
        Checkout = checkout;
        Extras = extras;
        Amenities = amenities;
        Url = url;
        MoreInfo = moreInfo;
        this.lat = lat;
        this.lng = lng;
        Rating = rating;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLocationdetail() {
        return Locationdetail;
    }

    public void setLocationdetail(String locationdetail) {
        Locationdetail = locationdetail;
    }

    public String getCheckin() {
        return Checkin;
    }

    public void setCheckin(String checkin) {
        Checkin = checkin;
    }

    public String getCheckout() {
        return Checkout;
    }

    public void setCheckout(String checkout) {
        Checkout = checkout;
    }

    public String getExtras() {
        return Extras;
    }

    public void setExtras(String extras) {
        Extras = extras;
    }

    public dev.marshall.hoteladvisor.model.Amenities getAmenities() {
        return Amenities;
    }

    public void setAmenities(dev.marshall.hoteladvisor.model.Amenities amenities) {
        Amenities = amenities;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public MoreInformation getMoreInfo() {
        return MoreInfo;
    }

    public void setMoreInfo(MoreInformation moreInfo) {
        MoreInfo = moreInfo;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }
}
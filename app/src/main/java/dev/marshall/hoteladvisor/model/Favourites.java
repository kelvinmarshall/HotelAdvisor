package dev.marshall.hoteladvisor.model;

/**
 * Created by Marshall on 08/04/2018.
 */

public class Favourites {
    private String HotelId, UserPhone, HotelName, HotelImage, HotelLocation, HotelPrice;

    public Favourites() {
    }

    public Favourites(String hotelId, String userPhone, String hotelName, String hotelImage, String hotelLocation, String hotelPrice) {
        HotelId = hotelId;
        UserPhone = userPhone;
        HotelName = hotelName;
        HotelImage = hotelImage;
        HotelLocation = hotelLocation;
        HotelPrice = hotelPrice;
    }

    public String getHotelId() {
        return HotelId;
    }

    public void setHotelId(String hotelId) {
        HotelId = hotelId;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getHotelName() {
        return HotelName;
    }

    public void setHotelName(String hotelName) {
        HotelName = hotelName;
    }

    public String getHotelImage() {
        return HotelImage;
    }

    public void setHotelImage(String hotelImage) {
        HotelImage = hotelImage;
    }

    public String getHotelLocation() {
        return HotelLocation;
    }

    public void setHotelLocation(String hotelLocation) {
        HotelLocation = hotelLocation;
    }

    public String getHotelPrice() {
        return HotelPrice;
    }

    public void setHotelPrice(String hotelPrice) {
        HotelPrice = hotelPrice;
    }
}

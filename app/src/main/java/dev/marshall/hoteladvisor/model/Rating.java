package dev.marshall.hoteladvisor.model;

/**
 * Created by Marshall on 31/03/2018.
 */

public class Rating {
    private String HotelID;
    private String ValSecurity;
    private String ValStaff;
    private String ValAccessbility;
    private String ValAmenities;
    private String ValConfort;
    private String ValCleanliness;
    private String ValMoney;

    public Rating() {
    }

    public Rating(String hotelID, String valSecurity, String valStaff, String valAccessbility, String valAmenities, String valConfort, String valCleanliness, String valMoney) {
        HotelID = hotelID;
        ValSecurity = valSecurity;
        ValStaff = valStaff;
        ValAccessbility = valAccessbility;
        ValAmenities = valAmenities;
        ValConfort = valConfort;
        ValCleanliness = valCleanliness;
        ValMoney = valMoney;
    }

    public String getHotelID() {
        return HotelID;
    }

    public void setHotelID(String hotelID) {
        HotelID = hotelID;
    }

    public String getValSecurity() {
        return ValSecurity;
    }

    public void setValSecurity(String valSecurity) {
        ValSecurity = valSecurity;
    }

    public String getValStaff() {
        return ValStaff;
    }

    public void setValStaff(String valStaff) {
        ValStaff = valStaff;
    }

    public String getValAccessbility() {
        return ValAccessbility;
    }

    public void setValAccessbility(String valAccessbility) {
        ValAccessbility = valAccessbility;
    }

    public String getValAmenities() {
        return ValAmenities;
    }

    public void setValAmenities(String valAmenities) {
        ValAmenities = valAmenities;
    }

    public String getValConfort() {
        return ValConfort;
    }

    public void setValConfort(String valConfort) {
        ValConfort = valConfort;
    }

    public String getValCleanliness() {
        return ValCleanliness;
    }

    public void setValCleanliness(String valCleanliness) {
        ValCleanliness = valCleanliness;
    }

    public String getValMoney() {
        return ValMoney;
    }

    public void setValMoney(String valMoney) {
        ValMoney = valMoney;
    }
}

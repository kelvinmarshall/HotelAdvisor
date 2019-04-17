package dev.marshall.hoteladvisor.model;

/**
 * Created by Marshall on 02/04/2018.
 */

public class Rooms {
    private String Roomtype;
    private String Rooprice;
    private String Capacity;
    private String Image;
    private String lunch,dinner,breakfast,booknow,cancelation,hotshower,bathtub,tv;

    public Rooms() {
    }

    public Rooms(String roomtype, String rooprice, String capacity, String image, String lunch, String dinner, String breakfast, String booknow, String cancelation, String hotshower, String bathtub, String tv) {
        Roomtype = roomtype;
        Rooprice = rooprice;
        Capacity = capacity;
        Image = image;
        this.lunch = lunch;
        this.dinner = dinner;
        this.breakfast = breakfast;
        this.booknow = booknow;
        this.cancelation = cancelation;
        this.hotshower = hotshower;
        this.bathtub = bathtub;
        this.tv = tv;
    }

    public String getRoomtype() {
        return Roomtype;
    }

    public void setRoomtype(String roomtype) {
        Roomtype = roomtype;
    }

    public String getRooprice() {
        return Rooprice;
    }

    public void setRooprice(String rooprice) {
        Rooprice = rooprice;
    }

    public String getCapacity() {
        return Capacity;
    }

    public void setCapacity(String capacity) {
        Capacity = capacity;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getBooknow() {
        return booknow;
    }

    public void setBooknow(String booknow) {
        this.booknow = booknow;
    }

    public String getCancelation() {
        return cancelation;
    }

    public void setCancelation(String cancelation) {
        this.cancelation = cancelation;
    }

    public String getHotshower() {
        return hotshower;
    }

    public void setHotshower(String hotshower) {
        this.hotshower = hotshower;
    }

    public String getBathtub() {
        return bathtub;
    }

    public void setBathtub(String bathtub) {
        this.bathtub = bathtub;
    }

    public String getTv() {
        return tv;
    }

    public void setTv(String tv) {
        this.tv = tv;
    }
}

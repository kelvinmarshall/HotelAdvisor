package dev.marshall.hoteladvisor.model;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Marshall on 24/03/2018.
 */

public class Reviews {
    private String phone;
    private String username;
    private String review;
    private  String image;
    private  String Stayed;
    private  String As_a;


    public Reviews() {
    }

    public Reviews(String phone, String username, String review, String image, String stayed, String as_a) {
        this.phone = phone;
        this.username = username;
        this.review = review;
        this.image = image;
        Stayed = stayed;
        As_a = as_a;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getStayed() {
        return Stayed;
    }

    public void setStayed(String stayed) {
        Stayed = stayed;
    }

    public String getAs_a() {
        return As_a;
    }

    public void setAs_a(String as_a) {
        As_a = as_a;
    }
}

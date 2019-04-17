package dev.marshall.hoteladvisor.model;

/**
 * Created by Marshall on 25/01/2018.
 */

public class User {
    private String Name, Email, Password, Phone,Image, staff,Current_UserID;


    public User() {
    }

    public User(String name, String email, String password, String current_UserID) {
        Name = name;
        Email = email;
        Password = password;
        Image = "";
        this.staff = "false";
        Current_UserID = current_UserID;
    }

    public String getEmail() {
        return Email;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getCurrent_UserID() {
        return Current_UserID;
    }

    public void setCurrent_UserID(String current_UserID) {
        Current_UserID = current_UserID;
    }
}
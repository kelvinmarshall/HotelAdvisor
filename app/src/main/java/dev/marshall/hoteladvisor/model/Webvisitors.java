package dev.marshall.hoteladvisor.model;

/**
 * Created by Marshall on 03/04/2018.
 */

public class Webvisitors {
    private String Visitorname;
    private String VisitorImage;
    private String HotelVisited;

    public Webvisitors() {
    }

    public Webvisitors(String visitorname, String visitorImage, String hotelVisited) {
        Visitorname = visitorname;
        VisitorImage = visitorImage;
        HotelVisited = hotelVisited;
    }

    public String getVisitorname() {
        return Visitorname;
    }

    public void setVisitorname(String visitorname) {
        Visitorname = visitorname;
    }

    public String getVisitorImage() {
        return VisitorImage;
    }

    public void setVisitorImage(String visitorImage) {
        VisitorImage = visitorImage;
    }

    public String getHotelVisited() {
        return HotelVisited;
    }

    public void setHotelVisited(String hotelVisited) {
        HotelVisited = hotelVisited;
    }
}

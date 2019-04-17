package dev.marshall.hoteladvisor.model;

/**
 * Created by Marshall on 06/04/2018.
 */

public class Amenities {
    private String parking,food,security,internet ,laundry,golf,bar,beach,pool,gym,rservice,child,spa,casino,airportshuttle,airconditioning;

    public Amenities() {
    }

    public Amenities(String parking, String food, String security, String internet, String laundry, String golf, String bar, String beach, String pool, String gym, String rservice, String child, String spa, String casino, String airportshuttle, String airconditioning) {
        this.parking = parking;
        this.food = food;
        this.security = security;
        this.internet = internet;
        this.laundry = laundry;
        this.golf = golf;
        this.bar = bar;
        this.beach = beach;
        this.pool = pool;
        this.gym = gym;
        this.rservice = rservice;
        this.child = child;
        this.spa = spa;
        this.casino = casino;
        this.airportshuttle = airportshuttle;
        this.airconditioning = airconditioning;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getInternet() {
        return internet;
    }

    public void setInternet(String internet) {
        this.internet = internet;
    }

    public String getLaundry() {
        return laundry;
    }

    public void setLaundry(String laundry) {
        this.laundry = laundry;
    }

    public String getGolf() {
        return golf;
    }

    public void setGolf(String golf) {
        this.golf = golf;
    }

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public String getBeach() {
        return beach;
    }

    public void setBeach(String beach) {
        this.beach = beach;
    }

    public String getPool() {
        return pool;
    }

    public void setPool(String pool) {
        this.pool = pool;
    }

    public String getGym() {
        return gym;
    }

    public void setGym(String gym) {
        this.gym = gym;
    }

    public String getRservice() {
        return rservice;
    }

    public void setRservice(String rservice) {
        this.rservice = rservice;
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    public String getSpa() {
        return spa;
    }

    public void setSpa(String spa) {
        this.spa = spa;
    }

    public String getCasino() {
        return casino;
    }

    public void setCasino(String casino) {
        this.casino = casino;
    }

    public String getAirportshuttle() {
        return airportshuttle;
    }

    public void setAirportshuttle(String airportshuttle) {
        this.airportshuttle = airportshuttle;
    }

    public String getAirconditioning() {
        return airconditioning;
    }

    public void setAirconditioning(String airconditioning) {
        this.airconditioning = airconditioning;
    }
}

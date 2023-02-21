package com.app.nailappointmentapp;


public class Appointment {
    String date;
    String price;
    String service;
    String time;
    String id;

    public Appointment() {
    }

    public Appointment(String date, String price, String service, String time) {
        this.date = date;
        this.price = price;
        this.service = service;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

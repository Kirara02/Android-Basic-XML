package com.kirara.backgroundlocation;

public class User {
    private double lat;
    private double lon;

    public User(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    // Getter and Setter
    public double getLatitude() {
        return lat;
    }

    public void setLatitude(double lat) {
        this.lat = lat;
    }

    public double getLongitude() {
        return lon;
    }

    public void setLongitude(double lon) {
        this.lon = lon;
    }
}


package com.example.alzguardpage1;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class LocationInfo {
    public Double latitude = 0.0;
    public Double longitude = 0.0;

    public LocationInfo() {}

    public LocationInfo(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and setters for latitude and longitude
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}

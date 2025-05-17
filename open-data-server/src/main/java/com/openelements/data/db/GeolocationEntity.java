package com.openelements.data.db;

import com.openelements.data.data.Geolocation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class GeolocationEntity extends EntityWithId {


    @Column
    private double latitude;

    @Column
    private double longitude;

    public GeolocationEntity() {
    }

    public GeolocationEntity(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeolocationEntity(Geolocation geolocation) {
        this.latitude = geolocation.latitude();
        this.longitude = geolocation.longitude();
    }
    
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

package com.openelements.data.db;

import com.openelements.data.data.Geolocation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class GeolocationEntity {

    @Id
    @GeneratedValue
    private UUID id;

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

package com.openelements.data.api.types;

public record Geolocation(double latitude, double longitude) {

    public static Geolocation of(double latitude, double longitude) {
        return new Geolocation(latitude, longitude);
    }

    public static Geolocation of(String latitude, String longitude) {
        return new Geolocation(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }

    @Override
    public String toString() {
        return "Geolocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

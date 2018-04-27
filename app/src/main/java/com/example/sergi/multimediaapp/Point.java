package com.example.sergi.multimediaapp;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.Serializable;

class Point implements Serializable {
    private double lat;
    private double lon;
    private String foto;

    public Point(String foto, double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        this.foto = foto;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}

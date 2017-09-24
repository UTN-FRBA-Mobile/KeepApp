package com.utn.mobile.keepapp.domain;

/**
 * Created by norchow on 9/24/17.
 */

public class Gimnasio {
    private String firebaseId;

    private String nombre;
    private double latitud;
    private double longitud;
    public Gimnasio() {
        //Constructor vacio para que firebase lo pueda instanciar
    }

    public Gimnasio(String nombre, double latitud, double longitud) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}

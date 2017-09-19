package com.utn.mobile.keepapp.domain;

/**
 * Created by Joel on 10/9/2017.
 */

public class Ejercicio {
    private String nombre;
    private String fecha;
    private Double valor;
    private String unidad;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    //Se necesita un constructor vacio para que lo pueda instanciar Firebase directamente
    public Ejercicio (){}

    public Ejercicio(String nombre, String fecha, Double valor, String unidad) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.valor = valor;
        this.unidad = unidad;
    }
}

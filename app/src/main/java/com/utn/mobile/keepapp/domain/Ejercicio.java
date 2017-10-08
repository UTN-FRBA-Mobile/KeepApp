package com.utn.mobile.keepapp.domain;

/**
 * Created by Joel on 10/9/2017.
 */

public class Ejercicio implements Comparable<Ejercicio> {
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

    public boolean esMismoEjercicio(Ejercicio otro_ej){
        return (this.nombre.equals(otro_ej.getNombre()) && this.unidad.equals(otro_ej.getUnidad()));
    }

    public boolean esMejor(Ejercicio otro_ej){
        switch (unidad){
            case "min":
                if(this.valor < otro_ej.getValor())
                    return true;
                break;
            case "kg":
            case "mts":
                if(this.valor > otro_ej.getValor())
                    return true;
                break;
        }
        return false;
    }

    @Override
    public int compareTo(Ejercicio ej) {
        // Aca se pueden poner varios criterios para ordenar la coleccion
        // Por ahora solo pongo por orden alfabetico
        int comp = this.nombre.compareTo(ej.getNombre());
        return comp;
    }
}

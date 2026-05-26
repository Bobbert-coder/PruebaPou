package com.example.poupirata2;

public class Food {

    private String nombre;
    private int precio;
    private int imagen;
    private int cantidad;
    private int hambre;
    private int nivelRequerido;

    public Food(String nombre, int precio, int imagen, int cantidad, int hambre, int nivelRequerido) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        this.cantidad = cantidad;
        this.hambre = hambre;
        this.nivelRequerido = nivelRequerido;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrecio() {
        return precio;
    }
    public int getHambre() {
        return hambre;
    }
    public int getNivelRequerido() {
        return nivelRequerido;
    }

    public int getImagen() {
        return imagen;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
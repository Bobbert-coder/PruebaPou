package com.example.poupirata2;

public class Food {

    private String nombre;
    private int precio;
    private int imagen;
    private int cantidad;

    public Food(String nombre, int precio, int imagen, int cantidad) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrecio() {
        return precio;
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
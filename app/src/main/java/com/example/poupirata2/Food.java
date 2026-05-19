package com.example.poupirata2;

public class Food {

    private String nombre;
    private int precio;
    private int imagen;
    private int cantidad;
    private int hambre;

    public Food(String nombre, int precio, int imagen, int cantidad, int hambre) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.precio = precio;
        this.cantidad = cantidad;
        this.hambre = hambre;
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
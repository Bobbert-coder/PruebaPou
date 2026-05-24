package com.example.poupirata2;

public class Decoracion {

    private String id;
    private String nombre;
    private int precio;
    private int imagen;
    private String habitacion;
    private int x, y, ancho, alto;



    public Decoracion(String id, String nombre, int precio, int imagen, String habitacion,
                      int x, int y, int ancho, int alto) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        this.habitacion = habitacion;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }

    public String getId() {
        return id;
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

    public String getHabitacion() {
        return habitacion;
    }
}
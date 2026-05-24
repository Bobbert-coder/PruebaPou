package com.example.poupirata2;

public class Logro {

    private String id;
    private int progreso;
    private int meta;
    private String titulo;
    private String descripcion;
    private boolean desbloqueado;

    public Logro(String id, String titulo, String descripcion, boolean desbloqueado, int progreso, int meta) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.desbloqueado = desbloqueado;
        this.progreso = progreso;
        this.meta = meta;
    }

    public int getProgreso() {
        return progreso;
    }

    public int getMeta() {
        return meta;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isDesbloqueado() {
        return desbloqueado;
    }

    public void setDesbloqueado(boolean desbloqueado) {
        this.desbloqueado = desbloqueado;
    }
}
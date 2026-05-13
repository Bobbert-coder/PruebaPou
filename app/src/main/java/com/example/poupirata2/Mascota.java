package com.example.poupirata2;
public class Mascota {

    private int hambre = 50;
    private int energia = 50;
    private int felicidad = 50;

    public int getHambre() { return hambre; }
    public int getEnergia() { return energia; }
    public int getFelicidad() { return felicidad; }

    public void alimentar() {
        hambre = Math.min(hambre + 10, 100);
    }

    public void dormir() {
        energia = Math.min(energia + 10, 100);
        hambre = Math.max(hambre - 5, 0);
        felicidad = Math.max(felicidad - 2, 0);
    }

    public void reducirConTiempo() {
        hambre = Math.max(hambre - 5, 0);
        energia = Math.max(energia - 3, 0);
        felicidad = Math.max(felicidad - 2, 0);
    }

    public void aplicarDesgastePorTiempo(int segundos) {

        int ciclos = segundos / 5; // cada 5 segundos como tu loop

        hambre = Math.max(hambre - (ciclos * 5), 0);
        energia = Math.max(energia - (ciclos * 3), 0);
        felicidad = Math.max(felicidad - (ciclos * 2), 0);
    }

    public void jugar() {
        felicidad = Math.min(felicidad + 10, 100);
        energia = Math.max(energia - 5, 0);
        hambre = Math.max(hambre - 3, 0);
    }

    public void setHambre(int hambre) {
        this.hambre = hambre;
    }

    public void setEnergia(int energia) {
        this.energia = energia;
    }

    public void setFelicidad(int felicidad) {
        this.felicidad = felicidad;
    }
}

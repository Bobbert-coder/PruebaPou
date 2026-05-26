package com.example.poupirata2;

import java.util.ArrayList;

public class GameData {


    public static int monedas;
    public static int felicidadExtra = 0;
    public static int energiaExtra = 0;
    public static int nivel = 1;
    public static int experiencia = 0;

    public static ArrayList<Food> inventario = new ArrayList<>();

    public boolean comprar(int cantidad)
    {
        if(monedas >= cantidad){
            monedas = monedas - cantidad;
            return true;
        }
        else return false;
    }
}

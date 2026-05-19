package com.example.poupirata2;

public class GameData {


    public static int monedas;

    public static int manzanas = 0;
    public static int pizzas = 0;
    public static int hamburguesas = 0;

    public boolean comprar(int cantidad)
    {
        if(monedas >= cantidad){
            monedas = monedas - cantidad;
            return true;
        }
        else return false;
    }
}

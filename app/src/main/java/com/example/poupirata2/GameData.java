package com.example.poupirata2;

public class GameData {

    public static int monedas;

    public boolean comprar(int cantidad)
    {
        if(monedas >= cantidad){
            monedas = monedas - cantidad;
            return true;
        }
        else return false;
    }
}

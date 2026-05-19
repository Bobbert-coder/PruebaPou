package com.example.poupirata2;

import java.util.ArrayList;

public class GameData {


    public static int monedas;

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

package com.example.poupirata2;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EstadisticasActivity extends AppCompatActivity {

    TextView txtNivel, txtVecesComidas, txtMonedas, txtEnergia, txtHambre, txtFelicidad;

    BaseDatos baseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        baseDatos = new BaseDatos(this);

        txtVecesComidas = findViewById(R.id.txtVecesComidas);
        txtMonedas = findViewById(R.id.txtMonedasStats);
        txtEnergia = findViewById(R.id.txtEnergiaStats);
        txtHambre = findViewById(R.id.txtHambreStats);
        txtFelicidad = findViewById(R.id.txtFelicidadStats);
        txtNivel = findViewById(R.id.txtNivelStats);

        cargarEstadisticas();
    }

    private void cargarEstadisticas() {

        int vecesComidas = obtenerProgresoLogro(LogrosManager.COMELON);

        Cursor cursor = baseDatos.obtenerMascota();

        if (cursor.moveToFirst()) {
            int monedas = cursor.getInt(cursor.getColumnIndexOrThrow("monedas"));
            int energia = cursor.getInt(cursor.getColumnIndexOrThrow("energia"));
            int hambre = cursor.getInt(cursor.getColumnIndexOrThrow("hambre"));
            int felicidad = cursor.getInt(cursor.getColumnIndexOrThrow("felicidad"));
            int nivel = cursor.getInt(cursor.getColumnIndexOrThrow("nivel"));
            int experiencia = cursor.getInt(cursor.getColumnIndexOrThrow("experiencia"));

            txtVecesComidas.setText("🍔 Veces que ha comido: " + vecesComidas);
            txtMonedas.setText("💰 Monedas actuales: " + monedas);
            txtEnergia.setText("⚡ Energía: " + energia + "/1000");
            txtHambre.setText("🍗 Hambre: " + hambre + "/1000");
            txtFelicidad.setText("😊 Felicidad: " + felicidad + "/1000");


            int xpNecesaria = nivel * 100;
            txtNivel.setText(
                    "⭐ Nivel: " + nivel +
                            "\nXP: " + experiencia + "/" + xpNecesaria
            );
        }

        cursor.close();
    }

    private int obtenerProgresoLogro(String idLogro) {

        Cursor cursor = baseDatos.obtenerLogros();

        int progreso = 0;

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));

            if (id.equals(idLogro)) {
                progreso = cursor.getInt(cursor.getColumnIndexOrThrow("progreso"));
                break;
            }
        }

        cursor.close();

        return progreso;
    }
}
package com.example.poupirata2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mainLayout;
    ViewFlipper viewFlipper;
    ImageButton btnFlecha1, btnFlecha2;
    Handler handler = new Handler();
    Runnable runnable;
    boolean isRun = true;
    ProgressBar progressHambre, progressEnergia, progressFelicidad;
    Button btnAlimentar, btnDormir, btnJugar;
    private long finTiempoGracia = 0;

    Mascota mascota;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    View fillHambre, fillEnergia, fillFelicidad;
    FrameLayout boxHambre, boxEnergia, boxFelicidad;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Inicializar mascota
        mascota = new Mascota();

        prefs = getSharedPreferences("tamagotchi", MODE_PRIVATE);
        editor = prefs.edit();

        int hambre = prefs.getInt("hambre",50);
        int energia = prefs.getInt("energia", 50);
        int felicidad = prefs.getInt("felicidad", 50);

        finTiempoGracia = prefs.getLong("finTiempoGracia", 0);

        mascota.setHambre(hambre);
        mascota.setEnergia(energia);
        mascota.setFelicidad(felicidad);

        // Referencias UI
        fillHambre = findViewById(R.id.fillHambre);
        fillEnergia = findViewById(R.id.fillEnergia);
        fillFelicidad = findViewById(R.id.fillFelicidad);
        boxFelicidad = findViewById(R.id.boxFelicidad);
        boxHambre = findViewById(R.id.boxHambre);
        boxEnergia = findViewById(R.id.boxEnergia);
        //progressEnergia = findViewById(R.id.progressEnergia);
        //progressFelicidad = findViewById(R.id.progressFelicidad);

        btnAlimentar = findViewById(R.id.btnAlimentar);
        btnDormir = findViewById(R.id.btnDormir);
        btnJugar = findViewById(R.id.btnJugar);



        mainLayout = findViewById(R.id.layoutMain);
        btnFlecha1 = findViewById(R.id.btnFlecha1);
        btnFlecha2 = findViewById(R.id.btnFlecha2);
        viewFlipper = findViewById(R.id.viewFlipper);

        // Eventos
        btnAlimentar.setOnClickListener(v -> {
            activartiempodegracia();
            mascota.alimentar();
            actualizarUI();
        });

        btnDormir.setOnClickListener(v -> {
            activartiempodegracia();
            mascota.dormir();
            actualizarUI();
        });

        btnJugar.setOnClickListener(v -> {
            activartiempodegracia();
            mascota.jugar();
            actualizarUI();
        });

        btnFlecha1.setOnClickListener(v -> {
            viewFlipper.setInAnimation(this, R.anim.slide_in_left);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_right);

            viewFlipper.showPrevious();
        });

        btnFlecha2.setOnClickListener(v -> {
            viewFlipper.setInAnimation(this, R.anim.slide_in_right);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_left);

            viewFlipper.showNext();
        });
        actualizarUI();

        runnable = new Runnable() {
            @Override
            public void run() {

                long ahora = System.currentTimeMillis();
                if(ahora > finTiempoGracia) mascota.reducirConTiempo();

                //Actualizar UI
                actualizarUI();

                //Repetir cada 5 segundos
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 5000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        guardarDatos();
    }

    private void activartiempodegracia() {
        finTiempoGracia = System.currentTimeMillis() + 5000;
    }


    @Override
    protected void onPause()
    {
        isRun = false;
        super.onPause();
        handler.removeCallbacks(runnable);
        guardarDatos();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        aplicarTiempoFuera();
        actualizarUI();
        if (!isRun) handler.postDelayed(runnable, 5000);
    }

    private void guardarDatos() {

        long tiempoActual = System.currentTimeMillis();
        prefs.edit()
                .putLong("ultimoTiempo", tiempoActual)
                .apply();

        editor.putInt("hambre", mascota.getHambre());
        editor.putInt("energia", mascota.getEnergia());
        editor.putInt("felicidad", mascota.getFelicidad());
        editor.putLong("finTiempoGracia", finTiempoGracia);

        editor.apply(); // o commit() si quieres forzar guardado inmediato
    }

    private void actualizarUI() {
        actualizarStat(fillHambre, mascota.getHambre());
        actualizarStat(fillEnergia, mascota.getEnergia());
        actualizarStat(fillFelicidad, mascota.getFelicidad());
    }

    private void aplicarTiempoFuera() {
        long ultimoTiempo = prefs.getLong("ultimoTiempo", -1);
        if (ultimoTiempo != -1) {
            long tiempoActual = System.currentTimeMillis();

            long diferencia = tiempoActual - ultimoTiempo;
            // Convertir a segundos
            int segundos = (int) (diferencia / 1000);

            mascota.aplicarDesgastePorTiempo(segundos);
        }
    }

    private void actualizarStat(View fillView, int valor) {

        int alturaMax = 300; // mismo tamaño del cuadro
        int nuevaAltura = (alturaMax * valor) / 100;
        ViewGroup.LayoutParams params = fillView.getLayoutParams();
        params.height = nuevaAltura;
        fillView.setLayoutParams(params);
        // Cambiar color según valor

        if (valor > 60) {
            fillView.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if (valor > 30) {
            fillView.setBackgroundColor(Color.parseColor("#FFC107"));
        } else {
            fillView.setBackgroundColor(Color.parseColor("#F44336"));
        }
    }

}
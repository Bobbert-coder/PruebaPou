package com.example.poupirata2;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity {
    int comidaActual = 0;
    ImageView imgComida, imgMascota;
    private LinearLayout mainLayout;
    ViewFlipper viewFlipper;
    ImageButton btnFlecha1, btnFlecha2, btnManzana, btnPizza, btnHamburguesa;
    Handler handler = new Handler();
    Runnable runnable;
    boolean isRun = true;
    Button btnAlimentar, btnDormir, btnJugar, btnNevera;
    private long finTiempoGracia = 0;

    Mascota mascota;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    View fillHambre, fillEnergia, fillFelicidad;
    FrameLayout boxHambre, boxEnergia, boxFelicidad, menuNevera;

    @SuppressLint("ClickableViewAccessibility")
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
        imgComida = findViewById(R.id.imgComida);
        imgMascota = findViewById(R.id.imgMascota);

        fillHambre = findViewById(R.id.fillHambre);
        fillEnergia = findViewById(R.id.fillEnergia);
        fillFelicidad = findViewById(R.id.fillFelicidad);

        boxFelicidad = findViewById(R.id.boxFelicidad);
        boxHambre = findViewById(R.id.boxHambre);
        boxEnergia = findViewById(R.id.boxEnergia);
        //progressEnergia = findViewById(R.id.progressEnergia);
        //progressFelicidad = findViewById(R.id.progressFelicidad);

        menuNevera = findViewById(R.id.menuNevera);

        btnNevera = findViewById(R.id.btnNevera);
        btnManzana = findViewById(R.id.btnManzana);
        btnPizza = findViewById(R.id.btnPizza);
        btnHamburguesa = findViewById(R.id.btnHamburguesa);
        btnAlimentar = findViewById(R.id.btnAlimentar);
        btnDormir = findViewById(R.id.btnDormir);
        btnJugar = findViewById(R.id.btnJugar);


        final float[] comidaX = new float[1];
        final float[] comidaY = new float[1];

        //Posicion inicial de la comida
        imgComida.post(() -> {
            comidaX[0] = imgComida.getX();
            comidaY[0] = imgComida.getY();
        });


        mainLayout = findViewById(R.id.layoutMain);
        btnFlecha1 = findViewById(R.id.btnFlecha1);
        btnFlecha2 = findViewById(R.id.btnFlecha2);
        viewFlipper = findViewById(R.id.viewFlipper);

        // Eventos
        btnAlimentar.setOnClickListener(v -> {
            activartiempodegracia();
            mascota.alimentar(100);
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

        btnNevera.setOnClickListener(v -> {
            menuNevera.setVisibility(View.VISIBLE);
        });

        btnManzana.setOnClickListener(v -> {
            imgComida.setImageResource(R.drawable.ic_manzana);
            comidaActual = 1;
            menuNevera.setVisibility(View.GONE);
        });
        btnPizza.setOnClickListener(v -> {
            imgComida.setImageResource(R.drawable.ic_pizza);
            comidaActual = 2;
            menuNevera.setVisibility(View.GONE);
        });
        btnHamburguesa.setOnClickListener(v -> {
            imgComida.setImageResource(R.drawable.ic_comida);
            comidaActual = 0;
            menuNevera.setVisibility(View.GONE);
        });




        imgComida.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (colisionaConMascota(view, imgMascota)) {

                            switch (comidaActual) {
                                case 1:
                                    mascota.alimentar(100);
                                    break;
                                case 2:
                                    mascota.alimentar(500);
                                    break;
                                case 0:
                                    mascota.alimentar(250);
                                    break;
                            }
                            activartiempodegracia();
                            actualizarUI();
                            Toast.makeText(MainActivity.this,
                                    "Ñam ñam",
                                    Toast.LENGTH_SHORT).show();
                        }
                        view.animate()
                                .x(comidaX[0])
                                .y(comidaY[0])
                                .setDuration(300)
                                .start();
                        break;
                }
                return true;
            }
        });


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
        int alturaMax = 250; // mismo tamaño del cuadro
        int nuevaAltura = (alturaMax * valor) / 1000;
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

    private boolean colisionaConMascota(View comida, View mascota) {
        Rect rectComida = new Rect();
        comida.getHitRect(rectComida);
        Rect rectMascota = new Rect();
        mascota.getHitRect(rectMascota);
        return Rect.intersects(rectComida, rectMascota);
    }

}
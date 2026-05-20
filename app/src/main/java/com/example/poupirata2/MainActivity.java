package com.example.poupirata2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    int comidaActual = 0, habitacionActual=0;
    ImageView imgComida, imgMascota;
    private LinearLayout mainLayout;
    RecyclerView recyclerFoods;
    ArrayList<Food> foods = new ArrayList<>();
    Food comidaSeleccionada = null;
    MediaPlayer sonidoComer, sonidoLleno;

    ViewFlipper viewFlipper;
    ImageButton btnFlecha1, btnFlecha2, btnTienda;
    Handler handler = new Handler();
    Runnable runnable;
    boolean isRun = true;
    Button btnAlimentar, btnDormir, btnJugar, btnNevera, btnMinigame;
    private long finTiempoGracia = 0;

    Mascota mascota;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    View fillHambre, fillEnergia, fillFelicidad;
    FrameLayout boxHambre, boxEnergia, boxFelicidad;
    TextView txtMonedas, txtHabitacion;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(GameData.inventario.isEmpty()) {
            GameData.inventario.add(new Food("Manzana", 5, R.drawable.ic_manzana, 1, 100));
            GameData.inventario.add(new Food("Pizza", 10, R.drawable.ic_pizza, 1, 500));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //crear comidas
        crearcomidas();
        // Inicializar mascota
        mascota = new Mascota();

        prefs = getSharedPreferences("tamagotchi", MODE_PRIVATE);
        editor = prefs.edit();

        GameData.monedas = prefs.getInt("monedas",0);

        int hambre = prefs.getInt("hambre",50);
        int energia = prefs.getInt("energia", 50);
        int felicidad = prefs.getInt("felicidad", 50);

        finTiempoGracia = prefs.getLong("finTiempoGracia", 0);
        mascota.setHambre(hambre);
        mascota.setEnergia(energia);
        mascota.setFelicidad(felicidad);
        // Referencias UI
        sonidoComer = MediaPlayer.create(this, R.raw.comida_comer);
        sonidoLleno = MediaPlayer.create(this, R.raw.comida_llena);
        sonidoComer.setVolume(0.3f, 0.3f);
        sonidoLleno.setVolume(0.3f, 0.3f);
        txtMonedas = findViewById(R.id.txtMonedas);
        txtMonedas.setText(String.valueOf(GameData.monedas));
        txtHabitacion = findViewById(R.id.txtHabitacion);
        txtHabitacion.setText("Cocina");
        imgComida = findViewById(R.id.imgComida);
        imgMascota = findViewById(R.id.imgMascota);
        fillHambre = findViewById(R.id.fillHambre);
        fillEnergia = findViewById(R.id.fillEnergia);
        fillFelicidad = findViewById(R.id.fillFelicidad);
        boxFelicidad = findViewById(R.id.boxFelicidad);
        boxHambre = findViewById(R.id.boxHambre);
        boxEnergia = findViewById(R.id.boxEnergia);
        btnTienda = findViewById(R.id.btnTienda);
        btnNevera = findViewById(R.id.btnNevera);
        btnAlimentar = findViewById(R.id.btnAlimentar);
        btnDormir = findViewById(R.id.btnDormir);
        btnJugar = findViewById(R.id.btnJugar);
        btnMinigame = findViewById(R.id.btnMinigame);


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
            GameData.monedas = GameData.monedas + 100;
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
            if(habitacionActual>0) habitacionActual--;
            else habitacionActual=2;
            actualizarUI();
            viewFlipper.showPrevious();
        });

        btnFlecha2.setOnClickListener(v -> {
            viewFlipper.setInAnimation(this, R.anim.slide_in_right);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_left);
            if(habitacionActual<2) habitacionActual++;
            else habitacionActual=0;
            actualizarUI();
            viewFlipper.showNext();
        });
        actualizarUI();

        btnTienda.setOnClickListener(v -> {
            Dialog dialogCategorias = new Dialog(MainActivity.this);
            dialogCategorias.setContentView(R.layout.dialog_tienda_categorias);

            Button btnComida = dialogCategorias.findViewById(R.id.btnTiendaComida);

            btnComida.setOnClickListener(v2 -> {
                dialogCategorias.dismiss();
                abrirTiendaComida();
            });
            dialogCategorias.show();
            configurarDialog(dialogCategorias, 1f,1f);
        });

        btnNevera.setOnClickListener(v -> {

            Dialog dialognevera = new Dialog(MainActivity.this);

            dialognevera.setContentView(R.layout.dialog_nevera);
            RecyclerView recyclerNevera = dialognevera.findViewById(R.id.recyclerNevera);

            recyclerNevera.setLayoutManager(new LinearLayoutManager(this));

            // Lista SOLO con comidas disponibles
            ArrayList<Food> comidasDisponibles = new ArrayList<>();

            for(Food food : GameData.inventario) {
                if(food.getCantidad() > 0) {
                    comidasDisponibles.add(food);
                }
            }
            // Adapter
            FoodAdapterNevera adapter = new FoodAdapterNevera(this, comidasDisponibles, food -> {
                                imgComida.setImageResource(food.getImagen());
                                comidaSeleccionada = food;
                                Toast.makeText(this, "Seleccionaste " + food.getNombre(), Toast.LENGTH_SHORT).show();
                            });

            recyclerNevera.setAdapter(adapter);
            dialognevera.show();
            Window windownevera = dialognevera.getWindow();

            if(windownevera != null) {
                int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
                int height = (int)(getResources().getDisplayMetrics().heightPixels * 0.7);
                windownevera.setLayout(width, height);
                windownevera.setBackgroundDrawableResource(android.R.color.transparent);
            }
        });

        btnMinigame.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, MiniGameActivity.class);
            startActivity(intent);
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
                            if(comidaSeleccionada != null) {
                                if(mascota.getHambre() > 970) sonidoLleno.start();
                                else
                                {
                                    mascota.alimentar(comidaSeleccionada.getHambre());
                                    sonidoComer.start();
                                    comidaSeleccionada.setCantidad(comidaSeleccionada.getCantidad() - 1);
                                    if(comidaSeleccionada.getCantidad() <= 0) {
                                        comidaSeleccionada = null;
                                        imgComida.setImageResource(0);
                                    }
                                    activartiempodegracia();
                                    actualizarUI();
                                    Toast.makeText(MainActivity.this, "Ñam ñam", Toast.LENGTH_SHORT).show();
                                }
                            }
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

        editor.putInt("monedas", GameData.monedas);
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
        txtMonedas.setText(String.valueOf(GameData.monedas));

        switch (habitacionActual) {
            case 0:
                txtHabitacion.setText("Cocina");
                imgComida.setVisibility(View.VISIBLE);
                break;
            case 1:
                txtHabitacion.setText("Dormitorio");
                imgComida.setVisibility(View.INVISIBLE);
                break;
            case 2:
                txtHabitacion.setText("Sala de Juegos");
                imgComida.setVisibility(View.INVISIBLE);
                break;
        }

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
        int alturaMax = 140; // mismo tamaño del cuadro
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

    private void crearcomidas()
    {
        foods.add(new Food("Manzana", 5, R.drawable.ic_manzana, 0, 100));
        foods.add(new Food("Hamburguesa", 15, R.drawable.ic_comida, 0, 250));
        foods.add(new Food("Pizza", 50, R.drawable.ic_pizza, 0, 500));
        foods.add(new Food("Sushi", 100, R.drawable.ic_sushi, 0, 1000));
    }

    private void abrirTiendaComida() {

        Dialog dialogtienda = new Dialog(MainActivity.this);
        dialogtienda.setContentView(R.layout.dialog_tienda);
        RecyclerView recyclerFoods = dialogtienda.findViewById(R.id.recyclerFoods);

        recyclerFoods.setLayoutManager(new LinearLayoutManager(this));
        FoodAdapter adapter = new FoodAdapter(this, foods);
        recyclerFoods.setAdapter(adapter);
        dialogtienda.show();
        configurarDialog(dialogtienda, 1f,1f);
    }

    private void configurarDialog(Dialog dialog, float anchoPantalla, float altoPantalla) {

        Window window = dialog.getWindow();
        if(window != null) {

            int width = (int)(getResources().getDisplayMetrics().widthPixels * anchoPantalla);
            int height = (int)(getResources().getDisplayMetrics().heightPixels * altoPantalla);

            window.setLayout(width, height);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

}
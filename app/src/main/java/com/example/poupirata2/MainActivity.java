package com.example.poupirata2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int ENERGIA_MAXIMA = 1000;
    private static final int ENERGIA_POR_INTERVALO = 10;

    // Para pruebas usa 50.
    // Cuando ya funcione normal, cámbialo a 1000.
    private static final int INTERVALO_DORMIR_MS = 50;

    int comidaActual = 0, habitacionActual = 0;

    FrameLayout contenedorDecoracionesCocina,contenedorDecoracionesDormitorio, contenedorDecoracionesSala;
    ImageView imgComida, imgMascota, imgGorrito;
    private LinearLayout mainLayout;

    RecyclerView recyclerFoods;
    ArrayList<Food> foods = new ArrayList<>();
    Food comidaSeleccionada = null;
    BaseDatos baseDatos;

    MediaPlayer sonidoComer, sonidoLleno;

    ViewFlipper viewFlipper;
    ImageButton btnFlecha1, btnFlecha2, btnNevera, btnMinigame, btnTienda;

    Handler handler = new Handler();
    Runnable runnable;
    boolean isRun = true;

    Button btnAlimentar, btnDormir, btnJugar, btnLogros;

    private long finTiempoGracia = 0;

    Mascota mascota;

    View fillHambre, fillEnergia, fillFelicidad, capaNoche;
    Handler handlerDormir = new Handler();
    boolean durmiendo = false;

    FrameLayout boxHambre, boxEnergia, boxFelicidad;
    TextView txtMonedas, txtHabitacion;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        crearCanalNotificaciones();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
            }
        }

        crearcomidas();

        mascota = new Mascota();
        baseDatos = new BaseDatos(this);
        cargarDatosSQLite();
        cargarInventarioSQLite();
        aplicarTiempoFuera();

        sonidoComer = MediaPlayer.create(this, R.raw.comida_comer);
        sonidoLleno = MediaPlayer.create(this, R.raw.comida_llena);
        sonidoComer.setVolume(0.3f, 0.3f);
        sonidoLleno.setVolume(0.3f, 0.3f);

        txtMonedas = findViewById(R.id.txtMonedas);
        txtHabitacion = findViewById(R.id.txtHabitacion);

        contenedorDecoracionesCocina = findViewById(R.id.contenedorDecoracionesCocina);
        contenedorDecoracionesSala = findViewById(R.id.contenedorDecoracionesSala);
        contenedorDecoracionesDormitorio = findViewById(R.id.contenedorDecoracionesDormitorio);

        cargarDecoracionesCompradas();

        imgComida = findViewById(R.id.imgComida);
        imgMascota = findViewById(R.id.imgMascota);
        imgGorrito = findViewById(R.id.imgGorrito);

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
        btnLogros = findViewById(R.id.btnLogros);

        capaNoche = findViewById(R.id.capaNoche);

        mainLayout = findViewById(R.id.layoutMain);
        btnFlecha1 = findViewById(R.id.btnFlecha1);
        btnFlecha2 = findViewById(R.id.btnFlecha2);
        viewFlipper = findViewById(R.id.viewFlipper);

        final float[] comidaX = new float[1];
        final float[] comidaY = new float[1];

        imgComida.post(() -> {
            comidaX[0] = imgComida.getX();
            comidaY[0] = imgComida.getY();
        });

        txtMonedas.setText(String.valueOf(GameData.monedas));
        txtHabitacion.setText("Cocina");

        btnAlimentar.setOnClickListener(v -> {
            GameData.monedas = GameData.monedas + 100;
            actualizarUI();
        });

        btnDormir.setOnClickListener(v -> {
            if (!durmiendo) {
                empezarDormir();
            } else {
                despertar();
            }
        });

        btnJugar.setOnClickListener(v -> {
            activartiempodegracia();
            mascota.jugar();
            actualizarUI();
        });

        btnLogros.setOnClickListener(v -> {
            Intent intentlogros = new Intent(MainActivity.this, LogrosActivity.class);
            startActivity(intentlogros);
        });

        btnFlecha1.setOnClickListener(v -> {
            viewFlipper.setInAnimation(this, R.anim.slide_in_left);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_right);

            if (habitacionActual > 0) {
                habitacionActual--;
            } else {
                habitacionActual = 2;
            }

            actualizarUI();
            viewFlipper.showPrevious();
        });

        btnFlecha2.setOnClickListener(v -> {
            viewFlipper.setInAnimation(this, R.anim.slide_in_right);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_left);

            if (habitacionActual < 2) {
                habitacionActual++;
            } else {
                habitacionActual = 0;
            }

            actualizarUI();
            viewFlipper.showNext();
        });

        btnTienda.setOnClickListener(v -> {
            Dialog dialogCategorias = new Dialog(MainActivity.this);
            dialogCategorias.setContentView(R.layout.dialog_tienda_categorias);

            LinearLayout btnComida = dialogCategorias.findViewById(R.id.btnTiendaComida);
            LinearLayout btnDecoracion = dialogCategorias.findViewById(R.id.btnTiendaDecoracion);

            btnComida.setOnClickListener(v2 -> {
                dialogCategorias.dismiss();
                abrirTiendaComida();
            });

            btnDecoracion.setOnClickListener(v2 -> {
                dialogCategorias.dismiss();
                abrirMenuDecoraciones();
            });

            dialogCategorias.show();
            configurarDialog(dialogCategorias, 1f, 1f);
        });

        btnNevera.setOnClickListener(v -> {

            Dialog dialognevera = new Dialog(MainActivity.this);
            dialognevera.setContentView(R.layout.dialog_nevera);

            RecyclerView recyclerNevera = dialognevera.findViewById(R.id.recyclerNevera);
            recyclerNevera.setLayoutManager(new LinearLayoutManager(this));

            ArrayList<Food> comidasDisponibles = new ArrayList<>();

            for (Food food : GameData.inventario) {
                if (food.getCantidad() > 0) {
                    comidasDisponibles.add(food);
                }
            }

            FoodAdapterNevera adapter = new FoodAdapterNevera(this, comidasDisponibles, food -> {
                imgComida.setImageResource(food.getImagen());
                comidaSeleccionada = food;
                Toast.makeText(this, "Seleccionaste " + food.getNombre(), Toast.LENGTH_SHORT).show();
            });

            recyclerNevera.setAdapter(adapter);
            dialognevera.show();

            Window windownevera = dialognevera.getWindow();

            if (windownevera != null) {
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.7);
                windownevera.setLayout(width, height);
                windownevera.setBackgroundDrawableResource(android.R.color.transparent);
            }
        });

        btnMinigame.setOnClickListener(v -> {
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
                            if (comidaSeleccionada != null) {
                                if (mascota.getHambre() > 970) {
                                    sonidoLleno.start();
                                } else {
                                    mascota.alimentar(comidaSeleccionada.getHambre());
                                    LogrosManager.desbloquear(
                                            MainActivity.this,
                                            LogrosManager.PRIMER_ALIMENTO
                                    );
                                    LogrosManager.actualizarProgreso(
                                            MainActivity.this,
                                            LogrosManager.PRIMER_ALIMENTO,
                                            1
                                    );
                                    int vecesComidas = obtenerProgresoComelon() + 1;
                                    LogrosManager.actualizarProgreso(
                                            MainActivity.this,
                                            LogrosManager.COMELON,
                                            vecesComidas
                                    );
                                    if (vecesComidas >= 50) {
                                        LogrosManager.desbloquear(
                                                MainActivity.this,
                                                LogrosManager.COMELON
                                        );
                                    }
                                    sonidoComer.start();
                                    comidaSeleccionada.setCantidad(
                                            comidaSeleccionada.getCantidad() - 1
                                    );
                                    if (comidaSeleccionada.getCantidad() <= 0) {
                                        comidaSeleccionada = null;
                                        imgComida.setImageResource(0);
                                    }
                                    activartiempodegracia();
                                    guardarDatos();
                                    actualizarUI();
                                    Toast.makeText(
                                            MainActivity.this,
                                            "Ñam ñam",
                                            Toast.LENGTH_SHORT
                                    ).show();
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

                if (ahora > finTiempoGracia && !durmiendo) {
                    mascota.reducirConTiempo();
                }

                actualizarUI();

                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(runnable, 5000);

        revisarSiSigueDurmiendo();

        actualizarUI();
    }

    @Override
    protected void onPause() {
        isRun = false;
        super.onPause();

        handler.removeCallbacks(runnable);

        guardarDatos();
    }

    @Override
    protected void onStop() {
        super.onStop();

        guardarDatos();
    }

    @Override
    protected void onResume() {
        super.onResume();

        aplicarTiempoFuera();

        if (GameData.felicidadExtra > 0) {
            mascota.setFelicidad(mascota.getFelicidad() + GameData.felicidadExtra);
            Toast.makeText(this, "Tu mascota se divirtió 🎮", Toast.LENGTH_SHORT).show();
            GameData.felicidadExtra = 0;
        }

        revisarSiSigueDurmiendo();

        actualizarUI();

        if (!isRun) {
            isRun = true;
            handler.postDelayed(runnable, 5000);
        }
    }

    private void guardarDatos() {
        long ultimoTiempo = System.currentTimeMillis();
        // Mascota + monedas + dormir en SQLite
        baseDatos.guardarMascota(
                mascota.getHambre(),
                mascota.getEnergia(),
                mascota.getFelicidad(),
                GameData.monedas,
                ultimoTiempo,
                durmiendo,
                finTiempoGracia
        );
        // Inventario en SQLite
        baseDatos.guardarInventarioComida(GameData.inventario);

    }

    private void aplicarTiempoFuera() {

        Cursor cursor = baseDatos.obtenerMascota();

        if (!cursor.moveToFirst()) {
            cursor.close();
            guardarDatos();
            return;
        }
        long ultimoTiempo = cursor.getLong(
                cursor.getColumnIndexOrThrow("ultimoTiempo")
        );
        boolean estabaDurmiendo =
                cursor.getInt(cursor.getColumnIndexOrThrow("durmiendo")) == 1;
        cursor.close();
        if (ultimoTiempo <= 0) {
            guardarDatos();
            return;
        }
        long tiempoActual = System.currentTimeMillis();
        long diferencia = tiempoActual - ultimoTiempo;
        if (diferencia <= 0) {
            return;
        }

        if (estabaDurmiendo) {
            int intervalosDormidos =
                    (int) (diferencia / INTERVALO_DORMIR_MS);
            if (intervalosDormidos > 0) {
                int energiaGanada =
                        intervalosDormidos * ENERGIA_POR_INTERVALO;
                int nuevaEnergia =
                        mascota.getEnergia() + energiaGanada;
                if (nuevaEnergia >= ENERGIA_MAXIMA) {
                    nuevaEnergia = ENERGIA_MAXIMA;
                    mascota.setEnergia(nuevaEnergia);
                    durmiendo = false;
                    cancelarNotificacionEnergiaMaxima();
                    guardarDatos();
                    return;
                }
                mascota.setEnergia(nuevaEnergia);
            }
        } else {
            int segundosFuera = (int) (diferencia / 1000);
            if (segundosFuera > 0) {
                mascota.aplicarDesgastePorTiempo(segundosFuera);
            }
        }
        guardarDatos();
    }

    private void empezarDormir() {
        if (mascota.getEnergia() >= ENERGIA_MAXIMA) {
            Toast.makeText(
                    this,
                    "Tu mascota ya tiene la energía al máximo",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        durmiendo = true;
        guardarDatos();
        programarNotificacionEnergiaMaxima();
        capaNoche.setVisibility(View.VISIBLE);
        imgGorrito.setVisibility(View.VISIBLE);
        btnDormir.setText("☀️ Despertar");
        handlerDormir.removeCallbacks(subirEnergiaRunnable);
        handlerDormir.postDelayed(
                subirEnergiaRunnable,
                INTERVALO_DORMIR_MS
        );
    }

    private void despertar() {
        durmiendo = false;
        cancelarNotificacionEnergiaMaxima();
        guardarDatos();
        capaNoche.setVisibility(View.GONE);
        imgGorrito.setVisibility(View.GONE);
        btnDormir.setText("🌙 Dormir");
        handlerDormir.removeCallbacks(subirEnergiaRunnable);
        actualizarUI();
    }

    private final Runnable subirEnergiaRunnable = new Runnable() {

        @Override
        public void run() {
            if (!durmiendo) {
                return;
            }

            if (mascota.getEnergia() >= ENERGIA_MAXIMA) {
                cancelarNotificacionEnergiaMaxima();
                despertar();
                return;
            }
            int nuevaEnergia =
                    mascota.getEnergia() + ENERGIA_POR_INTERVALO;
            if (nuevaEnergia >= ENERGIA_MAXIMA) {
                nuevaEnergia = ENERGIA_MAXIMA;
                mascota.setEnergia(nuevaEnergia);
                cancelarNotificacionEnergiaMaxima();
                despertar();
                return;
            }
            mascota.setEnergia(nuevaEnergia);
            activartiempodegracia();
            guardarDatos();
            actualizarUI();
            handlerDormir.postDelayed(
                    this,
                    INTERVALO_DORMIR_MS
            );
        }
    };

    private void revisarSiSigueDurmiendo() {
        Cursor cursor = baseDatos.obtenerMascota();
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }
        boolean estabaDurmiendo =
                cursor.getInt(
                        cursor.getColumnIndexOrThrow("durmiendo")
                ) == 1;
        cursor.close();
        if (estabaDurmiendo &&
                mascota.getEnergia() < ENERGIA_MAXIMA) {
            durmiendo = true;
            capaNoche.setVisibility(View.VISIBLE);
            imgGorrito.setVisibility(View.VISIBLE);
            btnDormir.setText("☀️ Despertar");
            handlerDormir.removeCallbacks(subirEnergiaRunnable);
            handlerDormir.postDelayed(
                    subirEnergiaRunnable,
                    INTERVALO_DORMIR_MS
            );
        } else {
            durmiendo = false;
            capaNoche.setVisibility(View.GONE);
            imgGorrito.setVisibility(View.GONE);
            btnDormir.setText("🌙 Dormir");
            handlerDormir.removeCallbacks(subirEnergiaRunnable);
            if (mascota.getEnergia() >= ENERGIA_MAXIMA) {
                guardarDatos();
            }
        }
    }

    private void programarNotificacionEnergiaMaxima() {

        int energiaActual = mascota.getEnergia();

        if (energiaActual >= ENERGIA_MAXIMA) {
            return;
        }

        int energiaFaltante = ENERGIA_MAXIMA - energiaActual;

        int intervalosNecesarios =
                (int) Math.ceil((double) energiaFaltante / ENERGIA_POR_INTERVALO);

        long tiempoNotificacion =
                System.currentTimeMillis() + ((long) intervalosNecesarios * INTERVALO_DORMIR_MS);

        Intent intent = new Intent(this, EnergiaReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                200,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager =
                (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    tiempoNotificacion,
                    pendingIntent
            );
        }
    }

    private void cancelarNotificacionEnergiaMaxima() {

        Intent intent = new Intent(this, EnergiaReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                200,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager =
                (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void crearCanalNotificaciones() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel canal = new NotificationChannel(
                    "canal_energia",
                    "Energía",
                    NotificationManager.IMPORTANCE_HIGH
            );

            canal.setDescription("Notificaciones de energía de la mascota");

            NotificationManager manager = getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(canal);
            }
        }
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

        LogrosManager.verificarLogros(this, mascota);
    }

    private void actualizarStat(View fillView, int valor) {

        int alturaMax = 140;
        int nuevaAltura = (alturaMax * valor) / 1000;

        ViewGroup.LayoutParams params = fillView.getLayoutParams();
        params.height = nuevaAltura;
        fillView.setLayoutParams(params);

        if (valor > 600) {
            fillView.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if (valor > 250) {
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

    private void crearcomidas() {

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

        configurarDialog(dialogtienda, 1f, 1f);
    }

    private void configurarDialog(Dialog dialog, float anchoPantalla, float altoPantalla) {

        Window window = dialog.getWindow();

        if (window != null) {

            int width = (int) (getResources().getDisplayMetrics().widthPixels * anchoPantalla);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * altoPantalla);

            window.setLayout(width, height);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void activartiempodegracia() {
        finTiempoGracia = System.currentTimeMillis() + 5000;
    }

    private void abrirMenuDecoraciones() {

        Dialog dialogDecoraciones = new Dialog(MainActivity.this);
        dialogDecoraciones.setContentView(R.layout.dialog_decoracion_categorias);

        LinearLayout btnCocina = dialogDecoraciones.findViewById(R.id.btnDecoracionCocina);
        LinearLayout btnSala = dialogDecoraciones.findViewById(R.id.btnDecoracionSala);
        LinearLayout btnDormitorio = dialogDecoraciones.findViewById(R.id.btnDecoracionDormitorio);

        btnCocina.setOnClickListener(v -> {
            dialogDecoraciones.dismiss();
            abrirTiendaDecoraciones("cocina");
        });

        btnSala.setOnClickListener(v -> {
            dialogDecoraciones.dismiss();
            abrirTiendaDecoraciones("sala");
        });

        btnDormitorio.setOnClickListener(v -> {
            dialogDecoraciones.dismiss();
            abrirTiendaDecoraciones("dormitorio");
        });

        dialogDecoraciones.show();
        configurarDialog(dialogDecoraciones, 1f, 1f);
    }

    private void abrirTiendaDecoraciones(String habitacion) {

        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_tienda_decoraciones);

        TextView txtTitulo = dialog.findViewById(R.id.txtTituloDecoraciones);
        RecyclerView recyclerDecoraciones = dialog.findViewById(R.id.recyclerDecoraciones);

        recyclerDecoraciones.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Decoracion> lista = obtenerDecoracionesPorHabitacion(habitacion);

        if (habitacion.equals("cocina")) {
            txtTitulo.setText("🍳 Decoraciones de cocina");
        } else if (habitacion.equals("sala")) {
            txtTitulo.setText("🎮 Decoraciones de sala");
        } else {
            txtTitulo.setText("🛏️ Decoraciones de dormitorio");
        }

        DecoracionAdapter adapter = new DecoracionAdapter(this, lista, decoracion -> {
            guardarDecoracionComprada(decoracion);
            mostrarDecoracionEnHabitacion(decoracion);
            actualizarUI();
        });

        recyclerDecoraciones.setAdapter(adapter);

        dialog.show();
        configurarDialog(dialog, 1f, 1f);
    }

    private ArrayList<Decoracion> obtenerDecoracionesPorHabitacion(String habitacion) {

        ArrayList<Decoracion> lista = new ArrayList<>();

        if (habitacion.equals("cocina")) {
            lista.add(new Decoracion(
                    "planta_cocina",
                    "Planta",
                    100,
                    R.drawable.ic_planta,
                    "cocina",
                    870, 2000, 300, 300
            ));
            lista.add(new Decoracion(
                    "cuadro_cocina",
                    "Cuadro",
                    150,
                    R.drawable.ic_cuadro,
                    "cocina",
                    870, 600, 300, 450
            ));
        } else if (habitacion.equals("sala")) {
            lista.add(new Decoracion(
                    "consola_sala",
                    "Consola",
                    200,
                    R.drawable.ic_consola,
                    "sala",
                    400, 1800, 600, 600
            ));
            lista.add(new Decoracion(
                    "alfombra_sala",
                    "Alfombra",
                    180,
                    R.drawable.ic_alfombra,
                    "sala",
                    50, 1500, 1200, 400
            ));
        } else if (habitacion.equals("dormitorio")) {
            lista.add(new Decoracion(
                    "lampara_dormitorio",
                    "Lámpara",
                    120,
                    R.drawable.ic_lampara,
                    "dormitorio",
                    0, 700, 400, 700
            ));

            lista.add(new Decoracion(
                    "mesa_dormitorio",
                    "Mesa",
                    160,
                    R.drawable.ic_mesa,
                    "dormitorio",
                    650, 1750, 600, 600
            ));
        }
        return lista;
    }

    private void guardarDecoracionComprada(Decoracion decoracion) {
        baseDatos.guardarDecoracionComprada(decoracion);
    }

    private void mostrarDecoracionEnHabitacion(Decoracion decoracion) {

        FrameLayout contenedor;
        if (decoracion.getHabitacion().equals("cocina")) {
            contenedor = contenedorDecoracionesCocina;
        } else if (decoracion.getHabitacion().equals("sala")) {
            contenedor = contenedorDecoracionesSala;
        } else {
            contenedor = contenedorDecoracionesDormitorio;
        }
        if (contenedor == null) {
            return;
        }
        ImageView img = new ImageView(this);
        img.setImageResource(decoracion.getImagen());
        img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                decoracion.getAncho(),
                decoracion.getAlto()
        );
        params.leftMargin = decoracion.getX();
        params.topMargin = decoracion.getY();

        img.setLayoutParams(params);

        contenedor.addView(img);
    }

    private void cargarDecoracionesCompradas() {

        Cursor cursor = baseDatos.obtenerDecoracionesCompradas();
        while (cursor.moveToNext()) {
            Decoracion decoracion = new Decoracion(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    0,
                    cursor.getInt(cursor.getColumnIndexOrThrow("imagen")),
                    cursor.getString(cursor.getColumnIndexOrThrow("habitacion")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("x")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("y")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("ancho")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("alto"))
            );
            mostrarDecoracionEnHabitacion(decoracion);
        }
        cursor.close();
    }

    private void cargarDatosSQLite() {
        Cursor cursor = baseDatos.obtenerMascota();

        if (cursor.moveToFirst()) {
            int hambre = cursor.getInt(cursor.getColumnIndexOrThrow("hambre"));
            int energia = cursor.getInt(cursor.getColumnIndexOrThrow("energia"));
            int felicidad = cursor.getInt(cursor.getColumnIndexOrThrow("felicidad"));
            int monedas = cursor.getInt(cursor.getColumnIndexOrThrow("monedas"));
            finTiempoGracia = cursor.getLong(
                    cursor.getColumnIndexOrThrow("finTiempoGracia")
            );
            mascota.setHambre(hambre);
            mascota.setEnergia(energia);
            mascota.setFelicidad(felicidad);
            GameData.monedas = monedas;
        }

        cursor.close();
    }

    private void cargarInventarioSQLite() {
        GameData.inventario.clear();
        Cursor cursor = baseDatos.obtenerInventarioComida();
        while (cursor.moveToNext()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));
            if (nombre.equals("Manzana")) {
                GameData.inventario.add(new Food("Manzana", 5, R.drawable.ic_manzana, cantidad, 100));
            } else if (nombre.equals("Hamburguesa")) {
                GameData.inventario.add(new Food("Hamburguesa", 15, R.drawable.ic_comida, cantidad, 250));
            } else if (nombre.equals("Pizza")) {
                GameData.inventario.add(new Food("Pizza", 50, R.drawable.ic_pizza, cantidad, 500));
            } else if (nombre.equals("Sushi")) {
                GameData.inventario.add(new Food("Sushi", 100, R.drawable.ic_sushi, cantidad, 1000));
            }
        }
        cursor.close();
    }

    private int obtenerProgresoComelon() {
        Cursor cursor = baseDatos.obtenerLogros();
        int progreso = 0;
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            if (id.equals(LogrosManager.COMELON)) {
                progreso = cursor.getInt(cursor.getColumnIndexOrThrow("progreso"));
                break;
            }
        }
        cursor.close();
        return progreso;
    }

}
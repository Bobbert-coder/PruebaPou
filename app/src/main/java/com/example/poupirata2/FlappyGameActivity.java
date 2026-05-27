package com.example.poupirata2;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FlappyGameActivity extends AppCompatActivity {

    FrameLayout layoutFlappy;
    ImageView imgPlayer;
    View tuboArriba, tuboAbajo;
    TextView txtScore, txtFinalScore, txtMonedasGanadas;
    LinearLayout layoutGameOver;
    Button btnReiniciar;
    Handler handler = new Handler();

    int score = 0;
    int scoreTotal = 0;
    int velocidadTubo = 12;

    float gravedad = 2.2f;
    float velocidadY = 0;
    float salto = -28;

    int tuboX;
    int espacioTubo = 430;
    boolean gameOver = false;
    boolean puntoSumado = false;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (gameOver) {
                return;
            }

            moverJugador();
            moverTubos();
            verificarColision();

            handler.postDelayed(this, 20);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flappy_game);

        layoutFlappy = findViewById(R.id.layoutFlappy);
        imgPlayer = findViewById(R.id.imgPlayerFlappy);
        tuboArriba = findViewById(R.id.tuboArriba);
        tuboAbajo = findViewById(R.id.tuboAbajo);
        txtScore = findViewById(R.id.txtScoreFlappy);
        layoutGameOver = findViewById(R.id.layoutGameOver);
        txtFinalScore = findViewById(R.id.txtFinalScore);
        txtMonedasGanadas = findViewById(R.id.txtMonedasGanadas);
        btnReiniciar = findViewById(R.id.btnReiniciar);

        layoutFlappy.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !gameOver) {
                velocidadY = salto;
            }
            return true;
        });

        btnReiniciar.setOnClickListener(v -> {
            iniciarJuego();
        });

        layoutFlappy.post(() -> {
            mostrarInicioMinijuego(
                    "🐦 Flappy Fappy",
                    "Toca la pantalla para saltar.\nEvita los tubos y consigue puntos.",
                    () -> iniciarJuego()
            );
        });
    }

    private void iniciarJuego() {

        score = 0;
        velocidadY = 0;
        gameOver = false;
        puntoSumado = false;

        txtScore.setText("Puntos: 0");
        layoutGameOver.setVisibility(View.GONE);

        imgPlayer.setX(120);
        imgPlayer.setY(layoutFlappy.getHeight() / 2f);

        reiniciarTubos();

        handler.removeCallbacks(runnable);
        handler.post(runnable);
    }

    private void moverJugador() {

        velocidadY += gravedad;

        float nuevaY = imgPlayer.getY() + velocidadY;

        if (nuevaY < 0) {
            nuevaY = 0;
            velocidadY = 0;
        }

        if (nuevaY + imgPlayer.getHeight() > layoutFlappy.getHeight()) {
            terminarJuego();
            return;
        }

        imgPlayer.setY(nuevaY);
    }

    private void moverTubos() {

        tuboX -= velocidadTubo;

        tuboArriba.setX(tuboX);
        tuboAbajo.setX(tuboX);

        if (!puntoSumado && tuboX + tuboArriba.getWidth() < imgPlayer.getX()) {
            score++;
            puntoSumado = true;

            if (score % 5 == 0) {
                GameData.monedas += 1;
            }

            txtScore.setText("Puntos: " + score);
        }

        if (tuboX + tuboArriba.getWidth() < 0) {
            reiniciarTubos();
        }
    }

    private void reiniciarTubos() {

        tuboX = layoutFlappy.getWidth() + 100;
        puntoSumado = false;

        int alturaPantalla = layoutFlappy.getHeight();

        int minEspacioY = 180;
        int maxEspacioY = alturaPantalla - espacioTubo - 180;

        if (maxEspacioY < minEspacioY) {
            maxEspacioY = minEspacioY + 1;
        }

        int espacioY = minEspacioY + (int) (Math.random() * (maxEspacioY - minEspacioY));

        ViewGroup.LayoutParams paramsArriba = tuboArriba.getLayoutParams();
        paramsArriba.height = espacioY;
        tuboArriba.setLayoutParams(paramsArriba);

        ViewGroup.LayoutParams paramsAbajo = tuboAbajo.getLayoutParams();
        paramsAbajo.height = alturaPantalla - espacioY - espacioTubo;
        tuboAbajo.setLayoutParams(paramsAbajo);

        tuboArriba.setX(tuboX);
        tuboArriba.setY(0);

        tuboAbajo.setX(tuboX);
        tuboAbajo.setY(espacioY + espacioTubo);
    }

    private void verificarColision() {

        Rect jugador = new Rect();
        imgPlayer.getHitRect(jugador);

        Rect rectTuboArriba = new Rect();
        tuboArriba.getHitRect(rectTuboArriba);

        Rect rectTuboAbajo = new Rect();
        tuboAbajo.getHitRect(rectTuboAbajo);

        if (Rect.intersects(jugador, rectTuboArriba) ||
                Rect.intersects(jugador, rectTuboAbajo)) {
            terminarJuego();
        }
    }

    private void terminarJuego() {

        if (gameOver) {
            return;
        }

        gameOver = true;
        handler.removeCallbacks(runnable);

        scoreTotal += score;

        int monedasGanadas = score * 5;
        int xpGanada = score * 3;

        txtFinalScore.setText("Puntos: " + score);
        txtMonedasGanadas.setText(" " + monedasGanadas);
        TextView txtXpGanada = findViewById(R.id.txtXpGanada);
        txtXpGanada.setText("+" + xpGanada + " XP");

        GameData.felicidadExtra = score * 3;
        GameData.monedas = GameData.monedas + monedasGanadas;

        LogrosManager.desbloquear(this, LogrosManager.PRIMER_JUEGO);
        LogrosManager.actualizarProgreso(this, LogrosManager.PRIMER_JUEGO, 1);
        LogrosManager.verificarPuntaje(this, score);

        Intent resultado = new Intent();
        resultado.putExtra("score", scoreTotal);
        resultado.putExtra("xpGanada", scoreTotal * 3);
        setResult(RESULT_OK, resultado);

        layoutGameOver.setVisibility(View.VISIBLE);
    }

    private void mostrarInicioMinijuego(String titulo, String descripcion, Runnable onStart) {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_inicio_minijuego);

        TextView txtTitulo = dialog.findViewById(R.id.txtTituloInicioMinijuego);
        TextView txtDescripcion = dialog.findViewById(R.id.txtDescripcionInicioMinijuego);
        Button btnIniciar = dialog.findViewById(R.id.btnIniciarMinijuego);

        txtTitulo.setText(titulo);
        txtDescripcion.setText(descripcion);

        btnIniciar.setOnClickListener(v -> {
            dialog.dismiss();
            onStart.run();
        });

        dialog.setCancelable(false);
        dialog.show();

        Window window = dialog.getWindow();

        if (window != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(runnable);

        Intent resultado = new Intent();
        resultado.putExtra("score", scoreTotal);
        resultado.putExtra("xpGanada", scoreTotal * 3);
        setResult(RESULT_OK, resultado);
    }
}
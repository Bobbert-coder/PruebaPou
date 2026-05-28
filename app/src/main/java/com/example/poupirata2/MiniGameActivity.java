package com.example.poupirata2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Dialog;
import android.view.Window;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MiniGameActivity extends AppCompatActivity {
    ImageView imgFood, imgPlayer;
    TextView txtScore;
    int aumento = 20, multExp = 2;
    int scoreTotal = 0;
    Handler handler = new Handler();
    MediaPlayer musica;
    LinearLayout layoutInicio;
    Button btnIniciarJuego;
    boolean juegoIniciado = false;
    int foodX=0, foodY=0;
    int playerX;

    int score = 0, vidas = 3;
    LinearLayout layoutGameOver;
    RelativeLayout layoutJuego;
    TextView txtFinalScore, txtGanadas;
    Button btnReiniciar;


    boolean gameOver = false;

    Random random = new Random();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(gameOver) return;
            System.out.println(aumento);

            foodY += aumento;


            imgFood.setY(foodY);

            // Reiniciar comida
            if (foodY > layoutJuego.getHeight()+100) {
                gameOver = true;
                handler.removeCallbacks(runnable);

                txtFinalScore.setText("Puntos: " + score);
                scoreTotal += score;
                int ganancia = score/2;
                txtGanadas.setText(String.valueOf(ganancia));
                TextView txtXpGanada = findViewById(R.id.txtXpGanada);
                int xpGanada = score * multExp;
                txtXpGanada.setText("+" + xpGanada + " XP");

                layoutGameOver.setVisibility(View.VISIBLE);
                musica.pause();

                GameData.felicidadExtra = score * 4;
                LogrosManager.desbloquear(MiniGameActivity.this, LogrosManager.PRIMER_JUEGO);
                LogrosManager.actualizarProgreso(MiniGameActivity.this, LogrosManager.PRIMER_JUEGO, 1);
                LogrosManager.verificarPuntaje(MiniGameActivity.this, score);

                Intent resultado = new Intent();
                resultado.putExtra("score", scoreTotal);
                resultado.putExtra("xpGanada", scoreTotal * multExp);
                setResult(RESULT_OK, resultado);
            }

            // Colisión
            // Colisión
            if (imgFood.getX() < imgPlayer.getX() + imgPlayer.getWidth() &&
                    imgFood.getX() + imgFood.getWidth() > imgPlayer.getX() &&
                    imgFood.getY() < imgPlayer.getY() + imgPlayer.getHeight() &&
                    imgFood.getY() + imgFood.getHeight() > imgPlayer.getY()) {

                if (aumento < 130)
                    aumento += 10;
                score++;
                if(score%2 == 0)
                {
                    GameData.monedas = GameData.monedas+1;
                }
                txtScore.setText("Puntos: " + score);

                // Reiniciar comida COMPLETAMENTE
                foodY = -400;

                reiniciarposicion();
                imgFood.setX(foodX);
                imgFood.setY(foodY);
            }

            handler.postDelayed(this, 15);
        }
    };

    void reiniciarposicion(){
        int maxX = findViewById(R.id.activity_minigame).getWidth()
                - findViewById(R.id.imgFood).getWidth();

        foodX = random.nextInt(Math.max(maxX, 1));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame);


        musica = MediaPlayer.create(this, R.raw.splashing_around);
        musica.setVolume(0.2f, 0.2f);
        musica.setLooping(true);
        vidas = 3;
        layoutJuego = findViewById(R.id.activity_minigame);
        imgFood = findViewById(R.id.imgFood);
        reiniciarposicion();
        imgFood.setX(foodX);
        imgPlayer = findViewById(R.id.imgPlayer);
        txtScore = findViewById(R.id.txtScore);
        txtGanadas = findViewById(R.id.txtMonedasGanadas);
        layoutGameOver = findViewById(R.id.layoutGameOver);
        txtFinalScore = findViewById(R.id.txtFinalScore);
        btnReiniciar = findViewById(R.id.btnReiniciar);

        // Movimiento jugador
        imgPlayer.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                playerX = (int) event.getRawX();
                imgPlayer.setX(playerX - imgPlayer.getWidth() / 2);
            }
            return true;
        });

        btnReiniciar.setOnClickListener(v -> {
            iniciarJuego();
        });

        mostrarInicioMinijuego(
                "Atrapa Comida",
                "Arrastra a tu mascota\nhacia la comida que cae.\nMientras más atrapes, más puntos, monedas y XP ganarás.",
                () -> iniciarJuego()
        );

    }

    @Override
    protected void onDestroy() {
        Intent resultado = new Intent();
        resultado.putExtra("score", scoreTotal);
        resultado.putExtra("xpGanada", scoreTotal * multExp);
        musica.stop();
        super.onDestroy();
        handler.removeCallbacks(runnable);
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
            musica.start();
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

    private void iniciarJuego() {
        gameOver = false;
        score = 0;
        aumento = 20;
        foodY = 0;
        musica.start();
        txtScore.setText("Puntos: 0");
        layoutGameOver.setVisibility(View.GONE);
        reiniciarposicion();
        imgFood.setX(foodX);
        imgFood.setY(foodY);
        handler.removeCallbacks(runnable);
        handler.post(runnable);
    }
}
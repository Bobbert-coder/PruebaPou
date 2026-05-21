package com.example.poupirata2;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MiniGameActivity extends AppCompatActivity {
    ImageView imgFood, imgPlayer;
    TextView txtScore;
    int aumento = 20;

    Handler handler = new Handler();

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
                int ganancia = score/5;
                txtGanadas.setText(String.valueOf(ganancia));
                layoutGameOver.setVisibility(View.VISIBLE);

                GameData.felicidadExtra = score * 4;

            }

            // Colisión
            // Colisión
            if (imgFood.getX() < imgPlayer.getX() + imgPlayer.getWidth() &&
                    imgFood.getX() + imgFood.getWidth() > imgPlayer.getX() &&
                    imgFood.getY() < imgPlayer.getY() + imgPlayer.getHeight() &&
                    imgFood.getY() + imgFood.getHeight() > imgPlayer.getY()) {

                if (aumento < 100)
                    aumento += 10;

                score++;
                if(score%5 == 0)
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

        handler.post(runnable);

        // Movimiento jugador
        imgPlayer.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_MOVE) {

                playerX = (int) event.getRawX();

                imgPlayer.setX(playerX - imgPlayer.getWidth() / 2);
            }

            return true;
        });

        btnReiniciar.setOnClickListener(v -> {

            score = 0;
            aumento = 20;
            vidas = 3;

            txtScore.setText("Puntos: 0");

            foodY = 0;
            reiniciarposicion();

            imgFood.setX(foodX);

            gameOver = false;

            layoutGameOver.setVisibility(View.GONE);

            handler.removeCallbacks(runnable);
            handler.post(runnable);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
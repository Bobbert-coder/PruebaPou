package com.example.poupirata2;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MiniGameActivity extends AppCompatActivity {

    ImageView imgFood, imgPlayer;
    TextView txtScore;
    int aumento = 20;

    Handler handler = new Handler();

    int foodX, foodY;
    int playerX;

    int score = 0;

    Random random = new Random();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            foodY += aumento;

            imgFood.setY(foodY);

            // Reiniciar comida
            if (foodY > 2300) {
                foodY = 0;
                foodX = random.nextInt(800);
                aumento = Math.max(aumento - 40, 20);
                imgFood.setX(foodX);
            }

            // Colisión
            if (imgFood.getX() < imgPlayer.getX() + imgPlayer.getWidth() &&
                    imgFood.getX() + imgFood.getWidth() > imgPlayer.getX() &&
                    imgFood.getY() < imgPlayer.getY() + imgPlayer.getHeight() &&
                    imgFood.getY() + imgFood.getHeight() > imgPlayer.getY()) {

                if(aumento<150) aumento = aumento + 20;

                score++;
                txtScore.setText("Puntos: " + score);

                foodY = 0;
                foodX = random.nextInt(800);

                imgFood.setX(foodX);
            }

            handler.postDelayed(this, 30);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame);

        imgFood = findViewById(R.id.imgFood);
        imgPlayer = findViewById(R.id.imgPlayer);
        txtScore = findViewById(R.id.txtScore);

        foodX = random.nextInt(800);

        imgFood.setX(foodX);

        handler.post(runnable);

        // Movimiento jugador
        imgPlayer.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_MOVE) {

                playerX = (int) event.getRawX();

                imgPlayer.setX(playerX - imgPlayer.getWidth() / 2);
            }

            return true;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
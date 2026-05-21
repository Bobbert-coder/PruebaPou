package com.example.poupirata2;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LogrosActivity extends AppCompatActivity {

    LinearLayout contenedorLogros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logros);

        contenedorLogros = findViewById(R.id.contenedorLogros);

        cargarLogros();
    }

    private void cargarLogros() {
        contenedorLogros.removeAllViews();

        ArrayList<Logro> logros = LogrosManager.obtenerLogros(this);

        for (Logro logro : logros) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(30, 25, 30, 25);
            card.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams paramsCard = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            paramsCard.setMargins(0, 0, 0, 25);
            card.setLayoutParams(paramsCard);

            if (logro.isDesbloqueado()) {
                card.setBackgroundColor(Color.parseColor("#FFF3CD"));
            } else {
                card.setBackgroundColor(Color.parseColor("#E0E0E0"));
            }

            TextView txtTitulo = new TextView(this);
            txtTitulo.setText((logro.isDesbloqueado() ? "🏆 " : "🔒 ") + logro.getTitulo());
            txtTitulo.setTextSize(20);
            txtTitulo.setTypeface(null, Typeface.BOLD);
            txtTitulo.setTextColor(Color.parseColor("#222222"));

            TextView txtDescripcion = new TextView(this);
            txtDescripcion.setText(logro.getDescripcion());
            txtDescripcion.setTextSize(15);
            txtDescripcion.setTextColor(Color.parseColor("#555555"));
            txtDescripcion.setPadding(0, 8, 0, 0);

            TextView txtProgreso = new TextView(this);
            txtProgreso.setText(logro.getProgreso() + "/" + logro.getMeta());
            txtProgreso.setTextSize(14);
            txtProgreso.setTextColor(Color.parseColor("#333333"));
            txtProgreso.setPadding(0, 8, 0, 0);


            TextView txtEstado = new TextView(this);
            txtEstado.setText(logro.isDesbloqueado() ? "Desbloqueado" : "Bloqueado");
            txtEstado.setTextSize(14);
            txtEstado.setTypeface(null, Typeface.BOLD);
            txtEstado.setPadding(0, 10, 0, 0);

            if (logro.isDesbloqueado()) {
                txtEstado.setTextColor(Color.parseColor("#2E7D32"));
            } else {
                txtEstado.setTextColor(Color.parseColor("#B71C1C"));
            }

            card.addView(txtTitulo);
            card.addView(txtDescripcion);
            card.addView(txtProgreso);
            card.addView(txtEstado);

            contenedorLogros.addView(card);
        }
    }
}
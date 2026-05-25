package com.example.poupirata2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DecoracionAdapter extends RecyclerView.Adapter<DecoracionAdapter.ViewHolder> {

    Context context;
    ArrayList<Decoracion> decoraciones;
    OnDecoracionComprada listener;

    public interface OnDecoracionComprada {
        void onComprada(Decoracion decoracion);
    }

    public DecoracionAdapter(Context context, ArrayList<Decoracion> decoraciones, OnDecoracionComprada listener) {
        this.context = context;
        this.decoraciones = decoraciones;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_decoracion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Decoracion decoracion = decoraciones.get(position);

        holder.imgDecoracion.setImageResource(decoracion.getImagen());
        holder.txtNombre.setText(decoracion.getNombre());
        holder.txtPrecio.setText("💰 " + decoracion.getPrecio() + " monedas");

        BaseDatos baseDatos = new BaseDatos(context);
        boolean yaComprada = baseDatos.decoracionComprada(decoracion.getId());

        if (yaComprada) {
            holder.btnComprar.setText("Comprado");
            holder.btnComprar.setEnabled(false);
            holder.btnComprar.setAlpha(0.5f);
        } else {
            holder.btnComprar.setText("Comprar");
            holder.btnComprar.setEnabled(true);
            holder.btnComprar.setAlpha(1f);
        }
        holder.btnComprar.setOnClickListener(v -> {
            boolean compradoAhora =
                    baseDatos.decoracionComprada(decoracion.getId());
            if (compradoAhora) {
                Toast.makeText(
                        context,
                        "Ya compraste este objeto",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (GameData.monedas >= decoracion.getPrecio()) {
                GameData.monedas -= decoracion.getPrecio();
                baseDatos.guardarDecoracionComprada(decoracion);
                holder.btnComprar.setText("Comprado");
                holder.btnComprar.setEnabled(false);
                holder.btnComprar.setAlpha(0.5f);
                listener.onComprada(decoracion);
                Toast.makeText(
                        context,
                        "Compraste " + decoracion.getNombre(),
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(
                        context,
                        "No tienes suficientes monedas",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return decoraciones.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgDecoracion;
        TextView txtNombre, txtPrecio;
        Button btnComprar;

        public ViewHolder(View itemView) {
            super(itemView);

            imgDecoracion = itemView.findViewById(R.id.imgDecoracion);
            txtNombre = itemView.findViewById(R.id.txtNombreDecoracion);
            txtPrecio = itemView.findViewById(R.id.txtPrecioDecoracion);
            btnComprar = itemView.findViewById(R.id.btnComprarDecoracion);
        }
    }
}
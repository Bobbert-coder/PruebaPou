package com.example.poupirata2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FoodAdapterNevera
        extends RecyclerView.Adapter<FoodAdapterNevera.ViewHolder> {

    Context context;
    ArrayList<Food> foods;

    public interface OnFoodUseListener {
        void onFoodUsed(Food food);
    }

    OnFoodUseListener listener;

    public FoodAdapterNevera(Context context, ArrayList<Food> foods, OnFoodUseListener listener) {
        this.context = context;
        this.foods = foods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.nevera_comida, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foods.get(position);
        holder.txtNombre.setText(food.getNombre());
        holder.txtCantidad.setText("Cantidad: " + food.getCantidad());

        holder.imgFood.setImageResource(food.getImagen());

        holder.btnUsar.setOnClickListener(v -> {

            if(food.getCantidad() > 0) {
                //food.setCantidad(food.getCantidad() - 1);
                notifyItemChanged(position);
                listener.onFoodUsed(food);
                if(food.getCantidad() <= 0) {
                    foods.remove(position);
                    notifyItemRemoved(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgFood;
        TextView txtNombre;
        TextView txtCantidad;
        Button btnUsar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFood = itemView.findViewById(R.id.imgFood);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            btnUsar = itemView.findViewById(R.id.btnUsar);
        }
    }
}
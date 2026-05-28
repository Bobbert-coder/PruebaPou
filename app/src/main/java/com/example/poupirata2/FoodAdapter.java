package com.example.poupirata2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    Context context;
    List<Food> foods;

    public FoodAdapter(Context context, List<Food> foods) {
        this.context = context;
        this.foods = foods;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_comida, parent, false);

        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foods.get(position);
        holder.txtNombre.setText(food.getNombre());
        holder.txtPrecio.setText(
                " " + food.getPrecio() +
                        " | Lvl " + food.getNivelRequerido()
        );
        holder.imgFood.setImageResource(food.getImagen());
        if (GameData.nivel < food.getNivelRequerido()) {
            holder.btnComprar.setText("Bloqueado");
            holder.btnComprar.setAlpha(0.5f);
        } else {
            holder.btnComprar.setText("Comprar");
            holder.btnComprar.setAlpha(1f);
        }
        holder.btnComprar.setOnClickListener(v -> {
            if (GameData.nivel < food.getNivelRequerido()) {
                Toast.makeText(
                        context,
                        "Necesitas nivel " + food.getNivelRequerido(),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (GameData.monedas >= food.getPrecio()) {
                GameData.monedas -= food.getPrecio();
                boolean existe = false;
                for (Food inventarioFood : GameData.inventario) {
                    if (inventarioFood.getNombre().equals(food.getNombre())) {
                        inventarioFood.setCantidad(inventarioFood.getCantidad() + 1);
                        existe = true;
                        break;
                    }
                }
                if (!existe) {
                    GameData.inventario.add(
                            new Food(
                                    food.getNombre(),
                                    food.getPrecio(),
                                    food.getImagen(),
                                    1,
                                    food.getHambre(),
                                    food.getNivelRequerido()
                            )
                    );
                }
                Toast.makeText(context, "Compraste " + food.getNombre(), Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(context, "No tienes monedas suficientes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }
    public static class FoodViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFood;
        TextView txtNombre, txtPrecio;
        Button btnComprar;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFood = itemView.findViewById(R.id.imgFood);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            btnComprar = itemView.findViewById(R.id.btnComprar);
        }
    }
}
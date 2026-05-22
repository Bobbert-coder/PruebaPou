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
                " " + food.getPrecio()
        );
        holder.imgFood.setImageResource(food.getImagen());

        holder.btnComprar.setOnClickListener(v -> {
            if(GameData.monedas >= food.getPrecio()) {
                GameData.monedas -= food.getPrecio();
                boolean existe = false;
                // Buscar si ya existe en inventario
                for(Food inventarioFood : GameData.inventario) {
                    if(inventarioFood.getNombre().equals(food.getNombre())) {
                        inventarioFood.setCantidad(inventarioFood.getCantidad() + 1);
                        existe = true;
                        break;
                    }
                }
                // Si no existe, agregar nueva comida
                if(!existe) {
                    GameData.inventario.add(new Food(food.getNombre(), food.getPrecio(), food.getImagen(), 1, food.getHambre()));
                }
                Toast.makeText(context, "Compraste " + food.getNombre(), Toast.LENGTH_SHORT).show();
            }
            else {
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
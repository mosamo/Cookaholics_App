package com.mohamed_mosabeh.utils.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Cuisine;

import java.util.ArrayList;

public class CuisineRecipesRecyclerViewAdapter extends RecyclerView.Adapter<CuisineRecipesRecyclerViewAdapter.CuisineRecipesViewHolder> {
    
    ArrayList<Cuisine> cuisines;
    
    public CuisineRecipesRecyclerViewAdapter(ArrayList<Cuisine> cuisines) {
        this.cuisines = cuisines;
    }
    
    @NonNull
    @Override
    public CuisineRecipesRecyclerViewAdapter.CuisineRecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_recipes_flag_square, parent, false);
        CuisineRecipesRecyclerViewAdapter.CuisineRecipesViewHolder viewHolder = new CuisineRecipesRecyclerViewAdapter.CuisineRecipesViewHolder(view);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull CuisineRecipesRecyclerViewAdapter.CuisineRecipesViewHolder holder, int position) {
        Cuisine cuisine = cuisines.get(position);
        
        holder.cuisineName.setText(cuisine.getName() + "\nCuisine");
        holder.cuisineCountry.setText(cuisine.getCountry_code());
    }
    
    @Override
    public int getItemCount() {
        return cuisines.size();
    }
    
    public static class CuisineRecipesViewHolder extends RecyclerView.ViewHolder {
        
        TextView cuisineName;
        TextView cuisineCountry;
        
        
        public CuisineRecipesViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cuisineName = itemView.findViewById(R.id.scrip_scripText);
            cuisineCountry = itemView.findViewById(R.id.scrip_scripTextAbove);
        }
    }
    
    /*private void KillProgressBar(ProgressBar p) {
        p.setVisibility(View.GONE);
    }*/
    
}
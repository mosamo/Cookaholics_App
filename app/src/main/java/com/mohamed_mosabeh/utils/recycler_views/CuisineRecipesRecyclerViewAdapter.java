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

public class CuisineRecipesRecyclerViewAdapter extends RecyclerView.Adapter<CuisineRecipesRecyclerViewAdapter.CusineRecipesViewHolder> {
    
    ArrayList<Cuisine> cuisines;
    
    public CuisineRecipesRecyclerViewAdapter(ArrayList<Cuisine> cuisines) {
        this.cuisines = cuisines;
    }
    
    @NonNull
    @Override
    public CuisineRecipesRecyclerViewAdapter.CusineRecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_recipes_square_scrip, parent, false);
        CuisineRecipesRecyclerViewAdapter.CusineRecipesViewHolder viewHolder = new CuisineRecipesRecyclerViewAdapter.CusineRecipesViewHolder(view);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull CuisineRecipesRecyclerViewAdapter.CusineRecipesViewHolder holder, int position) {
        Cuisine cuisine = cuisines.get(position);
        
        holder.cuisineName.setText(cuisine.getName() + "\nCuisine");
        
    }
    
    @Override
    public int getItemCount() {
        return cuisines.size();
    }
    
    public static class CusineRecipesViewHolder extends RecyclerView.ViewHolder {
        
        TextView cuisineName;
        
        
        public CusineRecipesViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cuisineName = itemView.findViewById(R.id.scrip_scripText);
        }
    }
    
    /*private void KillProgressBar(ProgressBar p) {
        p.setVisibility(View.GONE);
    }*/
    
}
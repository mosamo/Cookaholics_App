package com.mohamed_mosabeh.utils.recycler_views;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerRecipeClickInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CardRecipesSmallRecyclerViewAdapter extends RecyclerView.Adapter<CardRecipesSmallRecyclerViewAdapter.CardRecipesSmallViewHolder> {
    
    private ArrayList<Recipe> recipes;
    private String topLabel;
    
    private final RecyclerRecipeClickInterface recyclerClickInterface;
    
    private Map<Recipe, CardRecipesSmallViewHolder> recipeHolderMap = new HashMap<>();
    
    public CardRecipesSmallRecyclerViewAdapter(ArrayList<Recipe> recipes, String topLabel, RecyclerRecipeClickInterface recyclerClickInterface) {
        this.recipes = recipes;
        this.topLabel = topLabel;
        this.recyclerClickInterface = recyclerClickInterface;
    }
    
    public Map<Recipe, CardRecipesSmallViewHolder> getRecipeHolderMap() {
        return recipeHolderMap;
    }
    
    public void KillProgressBar(ProgressBar p) {
        try {
            p.setVisibility(View.GONE);
        } catch (NullPointerException npe) {
            Log.i("View Not Found", "View not initiated yet!");
        }
    }
    
    @NonNull
    @Override
    public CardRecipesSmallRecyclerViewAdapter.CardRecipesSmallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_small_recipes_card, parent, false);
        CardRecipesSmallRecyclerViewAdapter.CardRecipesSmallViewHolder viewHolder = new CardRecipesSmallRecyclerViewAdapter.CardRecipesSmallViewHolder(view, recyclerClickInterface);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull CardRecipesSmallRecyclerViewAdapter.CardRecipesSmallViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        
        holder.cardTopLabel.setText(topLabel);
        
        holder.cardName.setText(recipe.getName());
        holder.cardLikes.setText(Integer.toString(recipe.getLikes()));
        holder.cardMinutes.setText(recipe.getDuration() + " Minutes");

        String servingsPlural = recipe.getServings() > 1 ? " Servings" : " Serving";
        holder.cardServings.setText(recipe.getServings() + servingsPlural);
    
        if (recipe.getIcon().equals("no-image")) {
            // If there is no Image: put placeholder
            holder.cardImage.setImageResource(R.drawable.placeholder);
            KillProgressBar(holder.cardProgress);
        } else if (recipe.getPicture() != null) {
            // If there is an Image: put it
            holder.cardImage.setImageBitmap(recipe.getPicture());
            KillProgressBar(holder.cardProgress);
        }
    
        recipeHolderMap.put(recipe, holder);
    }
    
    @Override
    public int getItemCount() {
        return recipes.size();
    }
    
    public static class CardRecipesSmallViewHolder extends RecyclerView.ViewHolder {
    
        TextView cardName;
        TextView cardLikes;
        
        TextView cardMinutes;
        TextView cardServings;
    
        TextView cardTopLabel;
        
        public ImageView cardImage;
        public ProgressBar cardProgress;
        
        
        public CardRecipesSmallViewHolder(@NonNull View itemView, RecyclerRecipeClickInterface mClickInterface) {
            super(itemView);
    
            cardLikes = itemView.findViewById(R.id.recipestxtSmall_Likes);
            cardName = itemView.findViewById(R.id.recipesCardSmallRecipeName);
            cardMinutes = itemView.findViewById(R.id.recipesCardSmallRecipeMinutes);
            cardServings = itemView.findViewById(R.id.recipesCardSmallRecipeServings);
            cardImage = itemView.findViewById(R.id.recipeCardSmall_imageView);
            cardProgress = itemView.findViewById(R.id.recipeCardSmall_hotprogressbar);
            cardTopLabel = itemView.findViewById(R.id.smallrec_upmostLabel);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickInterface != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mClickInterface.onItemRecipeClick(position);
                        }
                    }
                }
            });
        }
    }
    
}

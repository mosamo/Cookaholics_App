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

public class PopularCardRecipesRecyclerViewAdapter extends RecyclerView.Adapter<PopularCardRecipesRecyclerViewAdapter.PopularCardRecipesViewHolder> {

    private ArrayList<Recipe> recipes;

    private final RecyclerRecipeClickInterface recyclerClickInterface;

    private Map<Recipe, PopularCardRecipesViewHolder> recipeHolderMap = new HashMap<>();

    public PopularCardRecipesRecyclerViewAdapter(ArrayList<Recipe> recipes, RecyclerRecipeClickInterface recyclerClickInterface) {
        this.recipes = recipes;
        this.recyclerClickInterface = recyclerClickInterface;
    }

    public Map<Recipe, PopularCardRecipesViewHolder> getRecipeHolderMap() {
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
    public PopularCardRecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_popular_recipes_card, parent, false);
        PopularCardRecipesViewHolder viewHolder = new PopularCardRecipesViewHolder(view, recyclerClickInterface);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PopularCardRecipesViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

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

    public static class PopularCardRecipesViewHolder extends RecyclerView.ViewHolder {

        TextView cardName;
        TextView cardLikes;

        TextView cardMinutes;
        TextView cardServings;

        public ImageView cardImage;
        public ProgressBar cardProgress;


        public PopularCardRecipesViewHolder(@NonNull View itemView, RecyclerRecipeClickInterface mClickInterface) {
            super(itemView);

            cardLikes = itemView.findViewById(R.id.txtLikes);
            cardName = itemView.findViewById(R.id.recipeCard_name);
            cardMinutes = itemView.findViewById(R.id.recipeCard_minutes);
            cardServings = itemView.findViewById(R.id.recipeCard_servings);
            cardImage = itemView.findViewById(R.id.imageView_recipeCard);
            cardProgress = itemView.findViewById(R.id.recipeCard_progressBar);
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

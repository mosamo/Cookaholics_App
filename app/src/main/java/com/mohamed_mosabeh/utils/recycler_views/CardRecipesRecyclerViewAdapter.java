package com.mohamed_mosabeh.utils.recycler_views;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerRecipeClickInterface;

import java.util.ArrayList;

public class CardRecipesRecyclerViewAdapter extends RecyclerView.Adapter<com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter.CardRecipesViewHolder> {
    
    ArrayList<Recipe> recipes;
    ArrayList<CardRecipesViewHolder> holders = new ArrayList<>();
    private final RecyclerRecipeClickInterface recyclerClickInterface;
    
    public CardRecipesRecyclerViewAdapter(ArrayList<Recipe> recipes, RecyclerRecipeClickInterface recyclerClickInterface) {
        this.recipes = recipes;
        this.recyclerClickInterface = recyclerClickInterface;
    }
    
    public void bindImagesToHolders(ArrayList<Bitmap> bitmaps) {
        for (int i = 0; i < holders.size(); i++) {
            try {
                holders.get(i).cardImage.setImageBitmap(bitmaps.get(i));
                if (holders.get(i).cardProgress != null)
                    KillProgressBar(holders.get(i).cardProgress);
            } catch (IndexOutOfBoundsException e) {
                Log.i("CardRecipesRecycler", "Could not bind Image!");
            }
        }
    }
    
    public void reset() {
        holders.clear();
    }
    
    public void KillProgressBar(ProgressBar p) {
        p.setVisibility(View.GONE);
    }
    
    @NonNull
    @Override
    public CardRecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_main_recipes_card, parent, false);
        CardRecipesRecyclerViewAdapter.CardRecipesViewHolder viewHolder = new CardRecipesRecyclerViewAdapter.CardRecipesViewHolder(view, recyclerClickInterface);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter.CardRecipesViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holders.add(holder);
        
        holder.cardName.setText(recipe.getName());
        holder.cardLikes.setText(Integer.toString(recipe.getLikes()));
        holder.cardMinutes.setText(recipe.getDuration() + " Minutes");

        String servingsPlural = recipe.getServings() > 1 ? " Servings" : " Serving";
        holder.cardServings.setText(recipe.getServings() + servingsPlural);

        holder.cardCategory.setText(recipe.getCategory());
        
        holder.cardTagLists.setText(recipe.getTagsString().equals("No Tags") ? "" : recipe.getTagsString());

        holder.highlightedFrame.setVisibility(recipe.isHighlighted() ? View.GONE : View.VISIBLE);
        
        if (recipe.getIcon().equals("no-image")) {
            holder.cardImage.setImageResource(R.drawable.placeholder);
            KillProgressBar(holder.cardProgress);
        }
    }
    
    @Override
    public int getItemCount() {
        return recipes.size();
    }
    
    public static class CardRecipesViewHolder extends RecyclerView.ViewHolder {
    
        TextView cardName;
        TextView cardLikes;
        
        TextView cardMinutes;
        TextView cardServings;
        
        TextView cardCategory;
        TextView cardTagLists;
        
        ImageView cardImage;
        
        ProgressBar cardProgress;
        
        FrameLayout highlightedFrame;
        LinearLayout marginReducer;
        
        public CardRecipesViewHolder(@NonNull View itemView, RecyclerRecipeClickInterface recyclerClickInterface) {
            super(itemView);
    
            cardLikes = itemView.findViewById(R.id.recipestxt_Likes);
            cardName = itemView.findViewById(R.id.recipesCardRecipeName);
            cardMinutes = itemView.findViewById(R.id.recipesCardRecipeMinutes);
            cardServings = itemView.findViewById(R.id.recipesCardRecipeServings);
            cardCategory = itemView.findViewById(R.id.recipes_cardtxtCategory);
            cardTagLists = itemView.findViewById(R.id.recipes_cardTagList);
            highlightedFrame = itemView.findViewById(R.id.card_highlighted_frame);
            marginReducer = itemView.findViewById(R.id.linearlayoutMarginReducer);
            cardImage = itemView.findViewById(R.id.recipeCard_imageView);
            cardProgress = itemView.findViewById(R.id.recipeCard_hotprogressbar);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerClickInterface != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            recyclerClickInterface.onItemRecipeClick(position);
                        }
                    }
        
                }
            });
        }
    }
    
}

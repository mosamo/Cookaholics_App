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
import com.mohamed_mosabeh.data_objects.Category;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.ParserUtil;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerRecipeClickInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CardRecipesRecyclerViewAdapter extends RecyclerView.Adapter<com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter.CardRecipesViewHolder> {
    
    ArrayList<Recipe> recipes;
    private final RecyclerRecipeClickInterface recyclerClickInterface;
    private Map<Recipe, CardRecipesViewHolder> recipeHolderMap = new HashMap<>();
    
    public CardRecipesRecyclerViewAdapter(ArrayList<Recipe> recipes, RecyclerRecipeClickInterface recyclerClickInterface) {
        this.recipes = recipes;
        this.recyclerClickInterface = recyclerClickInterface;
    }
    
    public Map<Recipe, CardRecipesViewHolder> getRecipeHolderMap() {
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
    public CardRecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_main_recipes_card, parent, false);
        CardRecipesRecyclerViewAdapter.CardRecipesViewHolder viewHolder = new CardRecipesRecyclerViewAdapter.CardRecipesViewHolder(view, recyclerClickInterface);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter.CardRecipesViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        
        holder.cardName.setText(recipe.getName());
        holder.cardLikes.setText(Integer.toString(recipe.getLikes()));
        holder.cardMinutes.setText(recipe.getDuration() + " Minutes");

        String servingsPlural = recipe.getServings() > 1 ? " Servings" : " Serving";
        holder.cardServings.setText(recipe.getServings() + servingsPlural);

        holder.cardCategory.setText(recipe.getCategory());
        
        holder.cardTagLists.setText(ParserUtil.parseTags(recipe.getTags()).equals("No Tags") ? "" : ParserUtil.parseTags(recipe.getTags()));

        holder.highlightedFrame.setVisibility(recipe.isHighlighted() ? View.VISIBLE : View.GONE);
        
        if (recipe.getIcon().equals("no-image")) {
            // If there is no Image: put placeholder
            holder.cardImage.setImageResource(R.drawable.placeholder);
            KillProgressBar(holder.cardProgress);
        } else if (recipe.getPicture() != null) {
            // If there is an Image: put it
            holder.cardImage.setImageBitmap(recipe.getPicture());
            KillProgressBar(holder.cardProgress);
        }
        // else means: an Image is coming soon: wait for it to be auto-bound
        
        recipeHolderMap.put(recipe, holder);
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
        
        public ImageView cardImage;
        public ProgressBar cardProgress;
        
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

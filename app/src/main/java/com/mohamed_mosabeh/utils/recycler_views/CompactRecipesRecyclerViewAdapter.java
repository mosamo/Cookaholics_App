package com.mohamed_mosabeh.utils.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.ParserUtil;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerRecipeClickInterface;

import java.util.ArrayList;

public class CompactRecipesRecyclerViewAdapter extends RecyclerView.Adapter<CompactRecipesRecyclerViewAdapter.CompactRecipesViewHolder> {
    
    private ArrayList<Recipe> recipes;
    private final RecyclerRecipeClickInterface mClickInterface;
    
    private String labelOnTop;
    
    public CompactRecipesRecyclerViewAdapter(ArrayList<Recipe> recipes, RecyclerRecipeClickInterface clickInterface, String label) {
        this.recipes = recipes;
        this.mClickInterface = clickInterface;
        this.labelOnTop = label;
    }
    
    
    @NonNull
    @Override
    public CompactRecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_compact_recipes_card, parent, false);
        CompactRecipesRecyclerViewAdapter.CompactRecipesViewHolder viewHolder = new CompactRecipesRecyclerViewAdapter.CompactRecipesViewHolder(view, mClickInterface);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull CompactRecipesRecyclerViewAdapter.CompactRecipesViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
    
        holder.cardName.setText(recipe.getName());
        holder.cardLikes.setText(" " + recipe.getLikes());
        holder.cardMinutes.setText(recipe.getDuration() + " Minutes");
    
        String servingsPlural = recipe.getServings() > 1 ? " Servings" : " Serving";
        holder.cardServings.setText(recipe.getServings() + servingsPlural);
        String stepsPlural = recipe.getSteps().size() > 1 ? " Steps" : " Step";
        holder.cardSteps.setText(recipe.getSteps().size() + stepsPlural);
    
        holder.cardCategory.setText(recipe.getCategory());
        holder.labelTop.setText(labelOnTop);
    
        holder.cardTagLists.setText(ParserUtil.parseTags(recipe.getTags()).equals("No Tags") ? "" : ParserUtil.parseTags(recipe.getTags()));
    
        holder.highlightedFrame.setVisibility(recipe.isHighlighted() ? View.VISIBLE : View.GONE);
    }
    
    @Override
    public int getItemCount() {
        return recipes.size();
    }
    
    public static class CompactRecipesViewHolder extends RecyclerView.ViewHolder {
    
        TextView cardName;
        TextView cardLikes;
        TextView cardSteps;
    
        TextView cardMinutes;
        TextView cardServings;
    
        TextView cardCategory;
        TextView cardTagLists;
    
        FrameLayout highlightedFrame;
        LinearLayout marginReducer;
        
        TextView labelTop;
        
        public CompactRecipesViewHolder(@NonNull View itemView, RecyclerRecipeClickInterface clickInterface) {
            super(itemView);
    
            cardLikes = itemView.findViewById(R.id.recomp_textLikes);
            cardSteps = itemView.findViewById(R.id.recomp_textSteps);
            cardName = itemView.findViewById(R.id.recomp_recipeName);
            cardMinutes = itemView.findViewById(R.id.recomp_recipeDuration);
            cardServings = itemView.findViewById(R.id.recomp_recipeServings);
            cardCategory = itemView.findViewById(R.id.recomp_recipeCategory);
            cardTagLists = itemView.findViewById(R.id.recomp_tagsList);
            highlightedFrame = itemView.findViewById(R.id.recomp_card_highlighted_frame);
            marginReducer = itemView.findViewById(R.id.recomp_MarginReducer);
            labelTop = itemView.findViewById(R.id.recomp_labelOnTop);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickInterface != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            clickInterface.onItemRecipeClick(position);
                        }
                    }
                }
            });
        }
    }
}

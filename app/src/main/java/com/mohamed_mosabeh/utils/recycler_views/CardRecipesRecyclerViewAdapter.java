package com.mohamed_mosabeh.utils.recycler_views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Recipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CardRecipesRecyclerViewAdapter extends RecyclerView.Adapter<com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter.CardRecipesViewHolder> {
    
    ArrayList<Recipe> recipes;
    FirebaseStorage storage;
    
    public CardRecipesRecyclerViewAdapter(ArrayList<Recipe> recipes, FirebaseStorage storage) {
        this.recipes = recipes;
        this.storage = storage;
    }
    
    @NonNull
    @Override
    public com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter.CardRecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_main_recipes_card, parent, false);
        com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter.CardRecipesViewHolder viewHolder = new com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter.CardRecipesViewHolder(view);
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
        holder.cardTagLists.setText(recipe.getTagsString());


        if (!recipe.isHighlighted()) {
            holder.highlightedFrame.setVisibility(View.GONE);

            // You can also set bottom margin to 0dp if it is GONE
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT
//            );
//
//            params.setMargins(0, 25, 0, 0);
//            holder.marginReducer.setLayoutParams(params);

        } else {
            holder.highlightedFrame.setVisibility(View.VISIBLE);
        }
    
        if (!recipe.getIcon().equals("no-image")) {
            try {
                final File tempfile = File.createTempFile(recipes.get(position).getId()+"_icon", "png");
                final StorageReference storageRef = storage.getReference().child(recipe.getIcon());
                storageRef.getFile(tempfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(tempfile.getAbsolutePath());
                        holder.cardImage.setImageBitmap(bitmap);
                        
                        ProgressBar p = holder.cardProgress;
                        ((ViewGroup) p.getParent()).removeView(p);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase Storage", "Couldn't Fetch File: " + e.getMessage());
                        
                        // Kill Progress
                        ProgressBar p = holder.cardProgress;
                        ((ViewGroup) p.getParent()).removeView(p);
                        holder.cardImage.setImageResource(R.drawable.placeholder);
                    }
                });
                
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Kill Progress
            ProgressBar p = holder.cardProgress;
            ((ViewGroup) p.getParent()).removeView(p);
            holder.cardImage.setImageResource(R.drawable.placeholder);
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
        
        public CardRecipesViewHolder(@NonNull View itemView) {
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
        }
    }
    
}

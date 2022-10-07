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

public class CardRecipesSmallRecyclerViewAdapter extends RecyclerView.Adapter<CardRecipesSmallRecyclerViewAdapter.CardRecipesSmallViewHolder> {
    
    ArrayList<Recipe> recipes;
    FirebaseStorage storage;
    String topLabel;
    
    public CardRecipesSmallRecyclerViewAdapter(ArrayList<Recipe> recipes, FirebaseStorage storage, String topLabel) {
        this.recipes = recipes;
        this.storage = storage;
        this.topLabel = topLabel;
    }
    
    @NonNull
    @Override
    public CardRecipesSmallRecyclerViewAdapter.CardRecipesSmallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_small_recipes_card, parent, false);
        CardRecipesSmallRecyclerViewAdapter.CardRecipesSmallViewHolder viewHolder = new CardRecipesSmallRecyclerViewAdapter.CardRecipesSmallViewHolder(view);
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
    
    public static class CardRecipesSmallViewHolder extends RecyclerView.ViewHolder {
    
        TextView cardName;
        TextView cardLikes;
        
        TextView cardMinutes;
        TextView cardServings;
    
        TextView cardTopLabel;
        
        ImageView cardImage;
        
        ProgressBar cardProgress;
        
        
        public CardRecipesSmallViewHolder(@NonNull View itemView) {
            super(itemView);
    
            cardLikes = itemView.findViewById(R.id.recipestxtSmall_Likes);
            cardName = itemView.findViewById(R.id.recipesCardSmallRecipeName);
            cardMinutes = itemView.findViewById(R.id.recipesCardSmallRecipeMinutes);
            cardServings = itemView.findViewById(R.id.recipesCardSmallRecipeServings);
            cardImage = itemView.findViewById(R.id.recipeCardSmall_imageView);
            cardProgress = itemView.findViewById(R.id.recipeCardSmall_hotprogressbar);
            cardTopLabel = itemView.findViewById(R.id.smallrec_upmostLabel);
        }
    }
    
}

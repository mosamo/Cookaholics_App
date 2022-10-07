package com.mohamed_mosabeh.utils.recycler_views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class PopularCardRecipesRecyclerViewAdapter extends RecyclerView.Adapter<PopularCardRecipesRecyclerViewAdapter.CardRecipesPopularViewHolder> {

    ArrayList<Recipe> recipes;
    FirebaseStorage storage;

    public PopularCardRecipesRecyclerViewAdapter(ArrayList<Recipe> recipes, FirebaseStorage storage) {
        this.recipes = recipes;
        this.storage = storage;
    }
    
    @NonNull
    @Override
    public CardRecipesPopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_popular_recipes_card, parent, false);
        CardRecipesPopularViewHolder viewHolder = new CardRecipesPopularViewHolder(view);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull CardRecipesPopularViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        
        
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
    
    public static class CardRecipesPopularViewHolder extends RecyclerView.ViewHolder {
    
        TextView cardName;
        TextView cardLikes;
        
        TextView cardMinutes;
        TextView cardServings;
        
        ImageView cardImage;
        
        ProgressBar cardProgress;
        
        
        public CardRecipesPopularViewHolder(@NonNull View itemView) {
            super(itemView);
    
            cardLikes = itemView.findViewById(R.id.txtLikes);
            cardName = itemView.findViewById(R.id.recipesCard_name);
            cardMinutes = itemView.findViewById(R.id.recipeCard_minutes);
            cardServings = itemView.findViewById(R.id.recipeCard_servings);
            cardImage = itemView.findViewById(R.id.imageView_recipeCard);
            cardProgress = itemView.findViewById(R.id.recipeCard_progressBar);
        }
    }
    
}

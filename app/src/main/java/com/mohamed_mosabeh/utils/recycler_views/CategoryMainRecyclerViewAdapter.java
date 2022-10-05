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
import com.mohamed_mosabeh.data_objects.Category;
import com.mohamed_mosabeh.data_objects.Recipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CategoryMainRecyclerViewAdapter extends RecyclerView.Adapter<CategoryMainRecyclerViewAdapter.CategoriesCardsViewHolder> {
    
    ArrayList<Category> categories;
    FirebaseStorage storage;
    
    public CategoryMainRecyclerViewAdapter(ArrayList<Category> categories, FirebaseStorage storage) {
        this.categories = categories;
        this.storage = storage;
    }
    
    @NonNull
    @Override
    public CategoryMainRecyclerViewAdapter.CategoriesCardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_main_categories_card, parent, false);
        CategoryMainRecyclerViewAdapter.CategoriesCardsViewHolder viewHolder = new CategoryMainRecyclerViewAdapter.CategoriesCardsViewHolder(view);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryMainRecyclerViewAdapter.CategoriesCardsViewHolder holder, int position) {
        Category category = categories.get(position);
        
        holder.categoryName.setText(category.getName());

    
        if (!category.getImage().equals("no-image")) {
            try {
                final File tempfile = File.createTempFile(category.getName() + "_icon", "png");
                final StorageReference storageRef = storage.getReference().child(category.getImage());
                storageRef.getFile(tempfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(tempfile.getAbsolutePath());
                        holder.categoryImage.setImageBitmap(bitmap);
                        
                        // Kill Progress
                        KillProgressBar(holder.categoryProgress);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase Storage", "Couldn't Fetch File: " + e.getMessage());
                        
                        // Kill Progress
                        KillProgressBar(holder.categoryProgress);
                        holder.categoryImage.setImageResource(R.drawable.ic_full_star);
                    }
                });
                
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Kill Progress
            KillProgressBar(holder.categoryProgress);
            holder.categoryImage.setImageResource(R.drawable.ic_full_star);
        }
    }
    
    @Override
    public int getItemCount() {
        return categories.size();
    }
    
    public static class CategoriesCardsViewHolder extends RecyclerView.ViewHolder {
    
        TextView categoryName;
        ImageView categoryImage;
        ProgressBar categoryProgress;
        
        
        public CategoriesCardsViewHolder(@NonNull View itemView) {
            super(itemView);
    
            categoryName = itemView.findViewById(R.id.categorycard_categoryTxt);
            categoryImage = itemView.findViewById(R.id.categorycard_categoryImage);
            categoryProgress = itemView.findViewById(R.id.categorycard_progressbar);
        }
    }
    
    private void KillProgressBar(ProgressBar p) {
        p.setVisibility(View.GONE);
    }
    
}

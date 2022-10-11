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
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerCategoryClickInterface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CategoryMainRecyclerViewAdapter extends RecyclerView.Adapter<CategoryMainRecyclerViewAdapter.CategoriesCardsViewHolder> {
    
    ArrayList<Category> categories;
    ArrayList<CategoriesCardsViewHolder> holders = new ArrayList<>();
    private final RecyclerCategoryClickInterface mClickInterface;
    
    public void bindImagesToHolders(ArrayList<Bitmap> bitmaps) {
        
        for (int i = 0; i < holders.size(); i++) {
            try {
                holders.get(i).categoryImage.setImageBitmap(bitmaps.get(i));
                if (holders.get(i).categoryProgress != null)
                    KillProgressBar(holders.get(i).categoryProgress);
            } catch (IndexOutOfBoundsException e) {
                Log.i("CategoryRecyclerView", "Could not bind Image!");
            }
        }
    }
    
    
    public void KillProgressBar(ProgressBar p) {
        p.setVisibility(View.GONE);
    }
    
    public void reset() {
        holders.clear();
    }
    
    public CategoryMainRecyclerViewAdapter(ArrayList<Category> categories, RecyclerCategoryClickInterface clickInterface) {
        this.categories = categories;
        this.mClickInterface = clickInterface;
    }
    
    
    
    @NonNull
    @Override
    public CategoriesCardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_main_categories_card, parent, false);
        CategoryMainRecyclerViewAdapter.CategoriesCardsViewHolder viewHolder = new CategoryMainRecyclerViewAdapter.CategoriesCardsViewHolder(view, mClickInterface);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryMainRecyclerViewAdapter.CategoriesCardsViewHolder holder, int position) {
        Category category = categories.get(position);
        holders.add(holder);
        
        holder.categoryName.setText(category.getName());
    
        if (category.getImage().equals("no-image")) {
            holder.categoryImage.setImageResource(R.drawable.ic_full_star);
            KillProgressBar(holder.categoryProgress);
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
        
        
        public CategoriesCardsViewHolder(@NonNull View itemView, RecyclerCategoryClickInterface clickInterface) {
            super(itemView);
    
            categoryName = itemView.findViewById(R.id.categorycard_categoryTxt);
            categoryImage = itemView.findViewById(R.id.categorycard_categoryImage);
            categoryProgress = itemView.findViewById(R.id.categorycard_progressbar);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickInterface != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            clickInterface.onItemCategoryClick(position);
                        }
                    }
                }
            });
        }
    }
    
}

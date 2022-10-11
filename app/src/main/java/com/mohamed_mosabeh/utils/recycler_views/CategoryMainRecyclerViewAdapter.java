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
import com.mohamed_mosabeh.data_objects.Category;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerCategoryClickInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryMainRecyclerViewAdapter extends RecyclerView.Adapter<CategoryMainRecyclerViewAdapter.CategoriesCardsViewHolder> {
    
    private ArrayList<Category> categories;
    private final RecyclerCategoryClickInterface mClickInterface;
    public Map<Category, ProgressBar> progressBarMap = new HashMap<>();
    
    public void KillProgressBar(ProgressBar p) {
        try {
            p.setVisibility(View.GONE);
        } catch (NullPointerException npe) {
            Log.i("View Not Found", "View not initiated yet!");
        }
    }
    
    public Map<Category, ProgressBar> getProgressBarMap() {
        return progressBarMap;
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
        
        holder.categoryName.setText(category.getName());
    
        if (category.getImage().equals("no-image")) {
            // If there is no Image: put star
            holder.categoryImage.setImageResource(R.drawable.ic_full_star);
            KillProgressBar(holder.categoryProgress);
        } else if (category.getPicture() != null) {
            // If there is an Image: put it
            holder.categoryImage.setImageBitmap(category.getPicture());
            KillProgressBar(holder.categoryProgress);
        }
            // else then an Image is coming soon: wait for it to be auto-bound
        
        progressBarMap.put(category, holder.categoryProgress);
    }
    
    @Override
    public int getItemCount() {
        return categories.size();
    }
    
    public static class CategoriesCardsViewHolder extends RecyclerView.ViewHolder {
    
        private TextView categoryName;
        private ImageView categoryImage;
        private ProgressBar categoryProgress;
        
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

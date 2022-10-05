package com.mohamed_mosabeh.utils.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Cuisine;
import com.mohamed_mosabeh.data_objects.Tag;

import java.util.ArrayList;

public class TagsRecipesRecyclerViewAdapter extends RecyclerView.Adapter<TagsRecipesRecyclerViewAdapter.TagsRecipesViewHolder> {
    
    ArrayList<Tag> tags;
    
    public TagsRecipesRecyclerViewAdapter(ArrayList<Tag> tags) {
        this.tags = tags;
    }
    
    @NonNull
    @Override
    public TagsRecipesRecyclerViewAdapter.TagsRecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_recipes_tc_scrip, parent, false);
        TagsRecipesRecyclerViewAdapter.TagsRecipesViewHolder viewHolder = new TagsRecipesRecyclerViewAdapter.TagsRecipesViewHolder(view);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull TagsRecipesRecyclerViewAdapter.TagsRecipesViewHolder holder, int position) {
        Tag tag = tags.get(position);
        
        holder.textContent.setText("#" + tag.getName());
        
    }
    
    @Override
    public int getItemCount() {
        return tags.size();
    }
    
    public static class TagsRecipesViewHolder extends RecyclerView.ViewHolder {
        
        TextView textContent;
        
        public TagsRecipesViewHolder(@NonNull View itemView) {
            super(itemView);
            textContent = itemView.findViewById(R.id.scrip_scripText);
        }
    }
}
package com.mohamed_mosabeh.utils.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Tag;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerTagClickInterface;

import java.util.ArrayList;

public class TagsRecipesRecyclerViewAdapter extends RecyclerView.Adapter<TagsRecipesRecyclerViewAdapter.TagsRecipesViewHolder> {
    
    ArrayList<Tag> tags;
    
    private final RecyclerTagClickInterface mClickInterface;
    
    public TagsRecipesRecyclerViewAdapter(ArrayList<Tag> tags, RecyclerTagClickInterface mClickInterface) {
        this.tags = tags;
        this.mClickInterface = mClickInterface;
    }
    
    @NonNull
    @Override
    public TagsRecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_recipes_tag_scrip, parent, false);
        TagsRecipesRecyclerViewAdapter.TagsRecipesViewHolder viewHolder = new TagsRecipesRecyclerViewAdapter.TagsRecipesViewHolder(view, mClickInterface);
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
        
        public TagsRecipesViewHolder(@NonNull View itemView, RecyclerTagClickInterface mClickInterface) {
            super(itemView);
            textContent = itemView.findViewById(R.id.scrip_scripText);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            mClickInterface.onItemTagClick(pos);
                        }
                    }
                }
            });
        }
    }
}
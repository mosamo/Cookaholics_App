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

public class TagsDetailsRecyclerViewAdapter extends RecyclerView.Adapter<TagsDetailsRecyclerViewAdapter.TagDetailsViewHolder> {
    
    ArrayList<Tag> tags;
    private final RecyclerTagClickInterface mClickInterface;
    
    public TagsDetailsRecyclerViewAdapter(ArrayList<Tag> tags, RecyclerTagClickInterface mClickInterface) {
        this.tags = tags;
        this.mClickInterface = mClickInterface;
    }
    
    @NonNull
    @Override
    public TagDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_recipes_tag_scrip_two, parent, false);
        TagsDetailsRecyclerViewAdapter.TagDetailsViewHolder viewHolder = new TagsDetailsRecyclerViewAdapter.TagDetailsViewHolder(view, mClickInterface);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull TagsDetailsRecyclerViewAdapter.TagDetailsViewHolder holder, int position) {
        Tag tag = tags.get(position);
        
        holder.textContent.setText("#" + tag.getName());
        holder.textHits.setText(tag.getHits() +"\nhits");
        holder.textRecipes.setText(tag.getRecipes_count()+"\nrecipes");
    }
    
    @Override
    public int getItemCount() {
        return tags.size();
    }
    
    public static class TagDetailsViewHolder extends RecyclerView.ViewHolder {
        
        TextView textContent;
        TextView textHits;
        TextView textRecipes;
        
        public TagDetailsViewHolder(@NonNull View itemView, RecyclerTagClickInterface mClickInterface) {
            super(itemView);
            textContent = itemView.findViewById(R.id.scrip_scripText);
            textHits = itemView.findViewById(R.id.scrip_scripHits);
            textRecipes = itemView.findViewById(R.id.scrip_scripRecipes);
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
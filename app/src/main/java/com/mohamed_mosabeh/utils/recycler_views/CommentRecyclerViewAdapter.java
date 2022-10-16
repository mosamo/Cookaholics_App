package com.mohamed_mosabeh.utils.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Comment;

import java.util.ArrayList;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentCardViewHolder> {
    
    private ArrayList<Comment> comments;
    
    public CommentRecyclerViewAdapter(ArrayList<Comment>  comments) {
        this.comments = comments;
    }
    
    @NonNull
    @Override
    public CommentCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_comment_container_card, parent, false);
        CommentRecyclerViewAdapter.CommentCardViewHolder viewHolder = new CommentRecyclerViewAdapter.CommentCardViewHolder(view);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull CommentRecyclerViewAdapter.CommentCardViewHolder holder, int position) {
        Comment comment = comments.get(position);
        
        holder.commentContent.setText(comment.getContent());
        holder.commentUsername.setText(comment.getDisplay_name());
    }
    
    @Override
    public int getItemCount() {
        return comments.size();
    }
    
    public static class CommentCardViewHolder extends RecyclerView.ViewHolder {
    
        private TextView commentContent;
        private TextView commentUsername;
        
        public CommentCardViewHolder(@NonNull View itemView) {
            super(itemView);
            commentContent = itemView.findViewById(R.id.ccd_commentContent);
            commentUsername = itemView.findViewById(R.id.ccd_username);
        }
    }
}

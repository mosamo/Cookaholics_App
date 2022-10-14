package com.mohamed_mosabeh.data_objects;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.SearchActivity;

public class SearchResultAdapter extends FirebaseRecyclerAdapter<SearchResultModel, SearchResultAdapter.searchResultViewHolder>
{
   public SearchResultAdapter(@NonNull FirebaseRecyclerOptions<SearchResultModel> options)
   {
      super(options);
   }

   @Override
   protected void onBindViewHolder(@NonNull searchResultViewHolder holder, int position, @NonNull SearchResultModel model)
   {
      holder.srvhTitle.setText(model.srvRecipeTitle);
      holder.srvhDescription.setText(model.srvRecipeDescription);
      holder.srvhDisplayName.setText(model.srvRecipeDisplayName);
   }

   @NonNull
   @Override
   public searchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
   {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_layout,parent,false);
      return new searchResultViewHolder(view);
   }

   class searchResultViewHolder extends RecyclerView.ViewHolder
   {
      TextView srvhTitle, srvhDescription, srvhDisplayName;
      public searchResultViewHolder(@NonNull View itemView)
      {
         super(itemView);
         srvhTitle = itemView.findViewById(R.id.tvRecipeTitle);
         srvhDescription = itemView.findViewById(R.id.tvDescription);
         srvhDisplayName = itemView.findViewById(R.id.tvDisplayName);
      }

   }

}

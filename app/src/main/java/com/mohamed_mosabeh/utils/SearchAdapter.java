package com.mohamed_mosabeh.utils;

import static com.mohamed_mosabeh.utils.SearchUtils.searchString;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.RecipeStepsActivity;
import com.mohamed_mosabeh.data_objects.Recipe;

import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context c;
    public List<Recipe> recipes;

    interface ItemClickListener {
        void onItemClick(int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private final TextView nameTxt, descriptionTxt, tagsTxt;
        private ItemClickListener itemClickListener;

        ViewHolder(View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            descriptionTxt = itemView.findViewById(R.id.descriptionTxt);
            tagsTxt = itemView.findViewById(R.id.tagsTxt);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            this.itemClickListener.onItemClick(this.getLayoutPosition());
        }

        void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }

    public SearchAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.c = parent.getContext();
        View view = LayoutInflater.from(c).inflate(R.layout.model_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get current recipe
        final Recipe r = recipes.get(position);

        //bind data to widgets
        holder.nameTxt.setText(r.getName());
        holder.descriptionTxt.setText(r.getDescription());
        holder.tagsTxt.setText(ParserUtils.parseTags(r.getTags()));

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#d3d3d3"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        //get name
        String name = r.getName();

        //highlight name text while searching
        if (name.toLowerCase().startsWith(searchString.toLowerCase()) && !(searchString.isEmpty())) {
            int startPos = name.toLowerCase(Locale.ROOT).indexOf(searchString.toLowerCase());
            int endPos = startPos + searchString.length();

            Spannable spanString = Spannable.Factory.getInstance().
                    newSpannable(holder.nameTxt.getText());
            spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#cd5b45")), startPos, endPos,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.nameTxt.setText(spanString);
        }
        /* else {
        *    //Utils.show(ctx, "Search string empty");
        }*/

        holder.setItemClickListener(pos -> SearchUtils.sendRecipeToActivity(c, r,
                RecipeStepsActivity.class));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

}
package com.mohamed_mosabeh.cookaholics_capstone.recipe_steps_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.RecipeStepsActivity;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.ParserUtil;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class RecipeStepsCommentsFragment extends Fragment {
    
    private RecipeStepsActivity parent;
    
    private TextView recipeName;
    private TextView recipeCategory;
    private TextView recipeCuisine;
    private TextView recipeDescription;
    private TextView recipeServings;
    private TextView recipeDuration;
    private TextView recipeTags;
    private TextView recipeDate;
    private TextView recipeUsername;
    private TextView recipeLikes;
    
    public RecipeStepsCommentsFragment() {
        // Required empty public constructor
    }
    public RecipeStepsCommentsFragment(RecipeStepsActivity parent) {
        this.parent = parent;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_recipe_steps_comments, container, false);
    
        SetUpViews(view);
        
        return view;
    }
    
    private void SetUpViews(View view) {
        Button btnBackRecipeSteps = view.findViewById(R.id.rcms_backToRecipeSteps);
        btnBackRecipeSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.toggleComments(false);
            }
        });
        
        recipeName = view.findViewById(R.id.rcsm_rcName);
        recipeCategory = view.findViewById(R.id.rcsm_rcCategory);
        recipeCuisine = view.findViewById(R.id.rcsm_rcCuisine);
        recipeDescription = view.findViewById(R.id.rcsm_rcDescription);
        recipeServings = view.findViewById(R.id.rcsm_rcServing);
        recipeDuration = view.findViewById(R.id.rcsm_rcDuration);
        recipeTags = view.findViewById(R.id.rcsm_rcTags);
        recipeDate = view.findViewById(R.id.rcsm_rcDate);
        recipeUsername = view.findViewById(R.id.rcsm_rcUser);
        recipeLikes = view.findViewById(R.id.rscm_LikesCount);
        
        if (parent.getRecipe() != null)
            setRecipeDetails(parent.getRecipe());
    }
    
    public void setRecipeDetails(Recipe r) {
        if (r != null) {
            try {
                // Setting Details
                recipeName.setText(r.getName());
                recipeCategory.setText(r.getCategory());
                recipeCuisine.setText(r.getCuisine());
                recipeDescription.setText(r.getDescription());
                recipeServings.setText(r.getServings() > 1 ? r.getServings() + " Servings" : r.getServings() + " Serving");
                recipeDuration.setText(r.getDuration() > 1 ? r.getDuration() + " Minutes" : r.getDuration() + " Minute");
                recipeTags.setText(ParserUtil.parseTags(r.getTags()));
                recipeUsername.setText(r.getDisplay_name());
                recipeLikes.setText("Likes: " + r.getLikes());
                
                // Timestamp Parser
                Long time = r.getTimestamp();
    
                SimpleDateFormat dataFormat = new SimpleDateFormat("dd MMMM yyyy");
                String timeString = dataFormat.format(new Date(time));
    
                recipeDate.setText(timeString);
                
            } catch (NullPointerException npe) {
                Log.i("Recipe Comment Details", "Acquired Data too quickly! setting it when the fragment is initialized");
            }
        }
    }
}
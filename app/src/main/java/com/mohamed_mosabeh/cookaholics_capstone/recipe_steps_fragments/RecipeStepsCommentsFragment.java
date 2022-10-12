package com.mohamed_mosabeh.cookaholics_capstone.recipe_steps_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.RecipeStepsActivity;

public class RecipeStepsCommentsFragment extends Fragment {
    
    private RecipeStepsActivity parent;
    
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
    }
}
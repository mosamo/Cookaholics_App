package com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.RecipeStepsActivity;
import com.mohamed_mosabeh.cookaholics_capstone.SubmitActivity;

public class RecipeComfirmationFragment extends Fragment {
    
    private SubmitActivity parent;
    
    private ProgressBar rcomfProgress;
    
    private TextView textStatus;
    
    private ImageView successImage;
    
    public RecipeComfirmationFragment() {
    
    }
    
    public RecipeComfirmationFragment(SubmitActivity parent) {
        this.parent = parent;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_comfirmation, container, false);
        
        SetupViews(view);
        
        return view;
    }
    
    private void SetupViews(View view) {
        rcomfProgress = view.findViewById(R.id.rcomf_progressBar);
        textStatus = view.findViewById(R.id.rcomf_textStatus);
        successImage = view.findViewById(R.id.rcomf_successImage);
    }
    
    public void setLoadingText(String str) {
        if (textStatus != null) {
            textStatus.setText(str);
        }
    }
    
    public void DisplaySuccessUI (String id) {
        
        Button btnViewRecipe = parent.findViewById(R.id.rcomf_viewRecipe);
        
        rcomfProgress.setVisibility(View.GONE);
        successImage.setVisibility(View.VISIBLE);
        btnViewRecipe.setVisibility(View.VISIBLE);
        btnViewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent, RecipeStepsActivity.class);
                intent.putExtra("recipe_id", id);
                
                // Start Recipe
                if (id != null && !id.trim().isEmpty())
                    startActivity(intent);
                else {
                    textStatus.setText("Uh oh! Something Broke!");
                    btnViewRecipe.setVisibility(View.GONE);
                }
                parent.finish();
            }
        });
        
        textStatus.setText("Upload Completed");
    }
}
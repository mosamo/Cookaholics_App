package com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mohamed_mosabeh.cookaholics_capstone.R;

public class RecipeFormStepFragment extends Fragment {
    
    private int STEP_NUMBER;
    
    private TextView stepNumberLabel;
    
    public RecipeFormStepFragment() {
    }
    
    public RecipeFormStepFragment(int num) {
        this.STEP_NUMBER = num;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_recipe_form_step, container, false);
        
        SetupView(view);
        
        return view;
    }
    
    private void SetupView(View view) {
        stepNumberLabel = view.findViewById(R.id.rfsf_stepNumber);
        stepNumberLabel.setText("Step " + STEP_NUMBER);
    }
}
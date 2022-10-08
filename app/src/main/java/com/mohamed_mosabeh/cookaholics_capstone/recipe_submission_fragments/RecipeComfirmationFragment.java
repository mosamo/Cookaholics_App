package com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.SubmitActivity;

public class RecipeComfirmationFragment extends Fragment {
    
    private RecipeFormFragment recipeFormFragment;
    
    public RecipeComfirmationFragment() {
    
    }
    
    public RecipeComfirmationFragment(RecipeFormFragment rff) {
        this.recipeFormFragment = rff;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_comfirmation, container, false);
        
        SetupViews(view);
        
        return view;
    }
    
    private void SetupViews(View view) {
        Button switchFragment = view.findViewById(R.id.rcomf_fragmentBack);
        switchFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitActivity submitActivity = (SubmitActivity) getActivity();
                submitActivity.goBackToForm();
            }
        });
        
        Button finishFragment = view.findViewById(R.id.rcomf_fragmentFinish);
        finishFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }
}
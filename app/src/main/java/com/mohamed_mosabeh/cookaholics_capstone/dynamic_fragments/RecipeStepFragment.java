package com.mohamed_mosabeh.cookaholics_capstone.dynamic_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.RecipeStep;

public class RecipeStepFragment extends Fragment {
    
    ImageView stepImage;
    TextView stepHeader;
    TextView stepContent;
    
    RecipeStep vRecipeStep;
    
    public RecipeStepFragment(RecipeStep instructions) {
        vRecipeStep = instructions;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_step, container, false);
        
        stepImage = view.findViewById(R.id.rsfImage);
        stepHeader = view.findViewById(R.id.rsfHeader);
        stepContent = view.findViewById(R.id.rsfContent);
        
        // adding scroll bar
        stepContent.setMovementMethod(new ScrollingMovementMethod());
        
        // TODO: Implement Image Parsing
        //if (vRecipeStep.getImage_ref() != "no-image")
           // something = vRecipeStep.getImage_ref()
        
        stepHeader.setText(vRecipeStep.getHeader());
        stepContent.setText(vRecipeStep.getContent());
        
        return view;
    }
}
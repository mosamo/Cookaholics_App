package com.mohamed_mosabeh.cookaholics_capstone.recipe_steps_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.RecipeStepsActivity;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.RecipeInstructionsSwipeAdapter;

public class RecipeStepsContainerFragment extends Fragment {
    
    private FirebaseStorage storage;
    
    private TextView txtRecipeName;
    private TextView txtStepsIndicator;
    
    private RecipeStepsActivity parent;
    private ViewPager2 viewPager;
    
    private Recipe recipe;
    private RecipeInstructionsSwipeAdapter adapter;
    
    public RecipeStepsContainerFragment() {
    }
    
    public RecipeStepsContainerFragment(RecipeStepsActivity parent, FirebaseStorage storage) {
        this.parent = parent;
        this.storage = storage;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    
        View view = inflater.inflate(R.layout.fragment_recipe_steps_container, container, false);
        
        SetupViews(view);
        
        return view;
    }
    
    private void SetupViews(View view) {
    
        txtRecipeName = view.findViewById(R.id.rs_txtRecipeName);
        txtStepsIndicator = view.findViewById(R.id.rs_recipeStepIndicator);
        viewPager = view.findViewById(R.id.recipePager);
        
        Button btnDiscuss = view.findViewById(R.id.rsf_discussButton);
        btnDiscuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.toggleComments(true);
            }
        });
        
        setAvailableData();
        checkReceivedData();
    }
    
    public void setAvailableData() {
        adapter = parent.getOrCreateAdapter();
        recipe = parent.getRecipe();
    }
    public void checkReceivedData() {
        if (recipe != null) {
            txtRecipeName.setText(recipe.getName());
            txtStepsIndicator.setText("Step 1: " + recipe.getSteps().get(0).getHeader());
            
            if (adapter != null) {
                ViewPagerSetup(recipe, adapter);
            }
        }
    }
    
    private void ViewPagerSetup(Recipe recipe, RecipeInstructionsSwipeAdapter adapter) {
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
    
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                String header = "";
                if (recipe.getSteps().size() > 0)
                    header += " " + recipe.getSteps().get(position).getHeader();
                txtStepsIndicator.setText("Step " + (position + 1) + ":" + header);
            }
    
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }
}
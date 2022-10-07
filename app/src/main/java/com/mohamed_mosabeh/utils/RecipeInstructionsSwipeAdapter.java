package com.mohamed_mosabeh.utils;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.firebase.storage.FirebaseStorage;
import com.mohamed_mosabeh.cookaholics_capstone.dynamic_fragments.RecipeStepFragment;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.data_objects.RecipeStep;

import java.util.ArrayList;

public class RecipeInstructionsSwipeAdapter extends FragmentStateAdapter {
    
    private ArrayList<RecipeStepFragment> steps = new ArrayList<>();
    
    public RecipeInstructionsSwipeAdapter(@NonNull FragmentActivity fragmentActivity, @NonNull Recipe recipe, @NonNull FirebaseStorage storage) {
        super(fragmentActivity);
        for (RecipeStep step : recipe.getSteps()) {
            steps.add(new RecipeStepFragment(step, storage));
        }
    }
    
    
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position < steps.size())
            return steps.get(position);
        return null;
    }
    
    @Override
    public int getItemCount() {
        return steps.size();
    }
}
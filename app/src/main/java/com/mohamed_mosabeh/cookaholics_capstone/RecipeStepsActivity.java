package com.mohamed_mosabeh.cookaholics_capstone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mohamed_mosabeh.cookaholics_capstone.other_fragments.EmptyFragment;
import com.mohamed_mosabeh.cookaholics_capstone.recipe_steps_fragments.RecipeStepsCommentsFragment;
import com.mohamed_mosabeh.cookaholics_capstone.recipe_steps_fragments.RecipeStepsContainerFragment;
import com.mohamed_mosabeh.data_objects.HighlightedRecipe;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.RecipeInstructionsSwipeAdapter;

public class RecipeStepsActivity extends AppCompatActivity {
    
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    
    private RecipeStepsContainerFragment bigContainerFragment;
    private RecipeStepsCommentsFragment bigCommentFragment;
    private EmptyFragment emptyFragment;
    
    private RecipeInstructionsSwipeAdapter adapter;
    
    
    private Recipe recipe;
    private String recipe_id;
    private HighlightedRecipe highlightDetails;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);
        
        // Getting Recipe Details
        Intent intent = getIntent();
        recipe_id = intent.getStringExtra("recipe_id");
        
        // Firebase Variables
        database = FirebaseDatabase.getInstance(getString(R.string.asia_database));
        storage= FirebaseStorage.getInstance(getString(R.string.firebase_storage));
        mAuth = FirebaseAuth.getInstance();
        
        // View Pager Adapter
        
        // Initiate Fragments
        bigContainerFragment = new RecipeStepsContainerFragment(this, database, storage, recipe_id);
        bigCommentFragment = new RecipeStepsCommentsFragment(this, database, recipe_id);
        emptyFragment = new EmptyFragment();
        
        // Loading Data
        LoadRecipe();
        
        // Set Steps Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ars_bigFrameMain, bigContainerFragment).commit();
    }
    
    
    private void LoadRecipe() {
        
        reference = database.getReference("recipes").child(recipe_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    
                        try {
                            recipe = snapshot.getValue(Recipe.class);
                            recipe.setId(recipe_id);
    
                            bigContainerFragment.setAvailableData();
                            bigContainerFragment.checkReceivedData();
    
                            bigCommentFragment.setRecipeDetails(recipe);
    
                            if (recipe.isHighlighted()) {
                                reference = database.getReference("highlighted-recipes").child(recipe_id);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot != null)
                                            highlightDetails = snapshot.getValue(HighlightedRecipe.class);
                                    }
            
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                
                                    }
                                });
                            }
                        } catch (NullPointerException npe) {
                            Log.w("Recipe Steps", "Recipe Terminated");
                        }
                    
                } else {
                    Log.w("Recipe Steps Data", "No Data Found");
                }
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Recipe Steps Data", error.getMessage());
            }
        });
    }
    
    public void toggleComments(boolean status) {
        if (status) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_from_right,  // enter
                            R.anim.slide_out_right,    // exit
                            R.anim.slide_from_right,  // pop enter
                            R.anim.slide_out_right)    // pop exit
                    .replace(R.id.ars_commentsWindow, bigCommentFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_from_right,  // enter
                            R.anim.slide_out_right,    // exit
                            R.anim.slide_from_right,  // pop enter
                            R.anim.slide_out_right)    // pop exit
                    .replace(R.id.ars_commentsWindow, emptyFragment).commit();
        }
    }
    
    public RecipeInstructionsSwipeAdapter getOrCreateAdapter() {
        if (adapter == null && recipe != null) {
            adapter = new RecipeInstructionsSwipeAdapter(this, recipe, storage);
        }
        else
            if (adapter == null)
                return null;
        return adapter;
    }
    
    public Recipe getRecipe() {
        return recipe;
    }
    
    public HighlightedRecipe getHighlightDetails() {
        return highlightDetails;
    }
}
package com.mohamed_mosabeh.cookaholics_capstone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments.RecipeComfirmationFragment;
import com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments.RecipeFormFragment;
import com.mohamed_mosabeh.data_objects.Category;
import com.mohamed_mosabeh.data_objects.Cuisine;

import java.util.ArrayList;

public class SubmitActivity extends AppCompatActivity {
    
    private FirebaseDatabase database;
    
    private RecipeFormFragment recipeFormFragment = new RecipeFormFragment();
    private RecipeComfirmationFragment recipeComfirmationFragment = new RecipeComfirmationFragment();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
    
    
        database = FirebaseDatabase.getInstance(getString(R.string.asia_database));
        
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rsub_fragment, recipeFormFragment).commit();
        
        fetchFormSetupData();
    }
    
    public void goBackToForm() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_from_right,  // enter
                        R.anim.slide_out_left,    // exit
                        R.anim.slide_from_right,  // pop enter
                        R.anim.slide_out_left)    // pop exit
                .replace(R.id.rsub_fragment, recipeFormFragment).commit();
    }
    
    private void fetchFormSetupData() {
        // Acquiring Cuisines Data
        DatabaseReference referenceCuisine = database.getReference("cuisines");
        Query CuisinesQuery = referenceCuisine.limitToFirst(10);
    
        CuisinesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                ArrayList<Cuisine> cuisines = new ArrayList<>();
            
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Cuisine cuisine = snapshot.getValue(Cuisine.class);
                    cuisine.setName(snapshot.getKey());
                
                    cuisines.add(cuisine);
                }
    
                recipeFormFragment.addCuisinesDropdownData(cuisines);
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to get Cuisines.", error.toException());
            }
        });
    
        // Acquiring Categories Data
        DatabaseReference referenceCategories = database.getReference("categories");
        Query CategoryQuery = referenceCategories.limitToFirst(10);
    
        CategoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                ArrayList<Category> categories = new ArrayList<>();
            
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Category category = snapshot.getValue(Category.class);
                    category.setName(snapshot.getKey());
    
                    categories.add(category);
                }
            
                recipeFormFragment.addCategoriesDropdownData(categories);
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to get Categories.", error.toException());
            }
        });
    }
    
    public void mSubmitRecipe(View view) {
        boolean success = true;
        
        if (success) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_from_right,  // enter
                            R.anim.slide_out_left,    // exit
                            R.anim.slide_from_right,  // pop enter
                            R.anim.slide_out_left)    // pop exit
                    .replace(R.id.rsub_fragment, recipeComfirmationFragment).commit();
        }
    }
}
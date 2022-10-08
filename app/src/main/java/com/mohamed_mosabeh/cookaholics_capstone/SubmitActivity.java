package com.mohamed_mosabeh.cookaholics_capstone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments.RecipeComfirmationFragment;
import com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments.RecipeFormFragment;
import com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments.RecipeFormStepFragment;
import com.mohamed_mosabeh.data_objects.Category;
import com.mohamed_mosabeh.data_objects.Cuisine;

import org.objectweb.asm.tree.analysis.Frame;

import java.util.ArrayList;

public class SubmitActivity extends AppCompatActivity {
    
    private FirebaseDatabase database;
    
    private RecipeFormFragment recipeFormFragment = new RecipeFormFragment(this);
    private RecipeComfirmationFragment recipeComfirmationFragment = new RecipeComfirmationFragment(this);
    
    private final int STEPS_LOWER_LIMIT = 2;
    private final int STEPS_UPPER_LIMIT = 10;
    
    private Button btnDelStep;
    private Button btnAddStep;
    
    private LinearLayout bottomLinear;
    private ArrayList<FrameLayout> listFragments = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        
        database = FirebaseDatabase.getInstance(getString(R.string.asia_database));
        
        // Views
        bottomLinear = findViewById(R.id.rsub_bottomLL);
        btnDelStep = findViewById(R.id.rsub_removeStepButton);
        btnDelStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeStep();
            }
        });
        btnAddStep = findViewById(R.id.rsub_addStepButton);
        btnAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStep();
            }
        });
        // Each Recipe should have at least 2 steps
        for (int i = 0; i < STEPS_LOWER_LIMIT; i++)
            addStep();
        
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rsub_fragment, recipeFormFragment).commit();
        
        fetchFormSetupData();
    }
    
    
    public void mReturnToForm() {
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
    
    private void addStep() {
        if (listFragments.size() < STEPS_UPPER_LIMIT) {
            // add new FrameLayout to ArrayList
            listFragments.add(createFrame());
            // index of last element
            int last = listFragments.size() - 1;
            // add FrameLayout to LinearLayout
            bottomLinear.addView(listFragments.get(last));
        } else {
            Toast.makeText(this, "Cannot have more than " + STEPS_UPPER_LIMIT + " Steps!", Toast.LENGTH_SHORT).show();
            Button b = findViewById(R.id.rsub_addStepButton);
            b.setEnabled(false);
        }
    }
    
    private void removeStep() {
        if (listFragments.size() > STEPS_LOWER_LIMIT) {
            // index of last element
            int last = listFragments.size() - 1;
            // remove FrameLayout from LinearLayout
            bottomLinear.removeView(listFragments.get(last));
            // remove last FrameLayout from ArrayList
            listFragments.remove(last);
        } else {
            Toast.makeText(this, "Cannot have less than " + STEPS_LOWER_LIMIT + " Steps!", Toast.LENGTH_SHORT).show();
        }
        
    }
    
    private FrameLayout createFrame() {
        FrameLayout frame = new FrameLayout(getApplicationContext());
    
        int generatedId = View.generateViewId();
        
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,16);
        frame.setLayoutParams(params);
        frame.setId(generatedId);
        
        int number = listFragments.size() + 1;
        
        getSupportFragmentManager().beginTransaction()
                .replace(generatedId, new RecipeFormStepFragment(number)).commit();
        
        return frame;
    }
}
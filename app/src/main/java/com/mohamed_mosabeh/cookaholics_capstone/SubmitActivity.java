package com.mohamed_mosabeh.cookaholics_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments.RecipeComfirmationFragment;
import com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments.RecipeFormFragment;
import com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments.RecipeFormStepFragment;
import com.mohamed_mosabeh.data_objects.Category;
import com.mohamed_mosabeh.data_objects.Cuisine;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.data_objects.RecipeStep;
import com.mohamed_mosabeh.utils.ImageManipulation;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class SubmitActivity extends AppCompatActivity {
    
    private FirebaseDatabase database;
    
    private ImageManipulation controlImages = new ImageManipulation(this);
    private ArrayList<ImageView> listImageViews = new ArrayList<ImageView>();
    
    private RecipeFormFragment recipeFormFragment = new RecipeFormFragment(this);
    
    // this can be used for older devices that use request codes
    // but we are not using deprecated APIs in our app:
    public final int IMAGE_RESULT_STARTING_CODE = 600;
    
    private final int STEPS_LOWER_LIMIT = 2;
    private final int STEPS_UPPER_LIMIT = 10;
    
    private Button btnDelStep;
    private Button btnAddStep;
    private Button btnSubmit;
    
    private LinearLayout bottomLinear;
    private ArrayList<FrameLayout> listFragments = new ArrayList<>();
    private ArrayList<RecipeFormStepFragment> stepFragments = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        
        database = FirebaseDatabase.getInstance(getString(R.string.asia_database));
        
        // Views
        bottomLinear = findViewById(R.id.rsub_bottomLL);
        btnSubmit = findViewById(R.id.rsub_SubmitButton);
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
        
        
    
        // Create Recipe Form Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rsub_fragment, recipeFormFragment).commit();
        
        // Create Recipe Steps Form Fragment
        // Each Recipe should have at least 2 steps (2 Fragments created)
        for (int i = 0; i < STEPS_LOWER_LIMIT; i++)
            addStep();
    
        mControlButtonStatus(btnDelStep, false);
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
    
    // called by SubmitButton OnClick
    public void mSubmitRecipe(View view) {
        
        btnSubmit.setEnabled(false);
        
        ArrayList<Boolean> validationResults = new ArrayList<>();
        
        validationResults.add(recipeFormFragment.validateMainForm());
        
        for (RecipeFormStepFragment fragment : stepFragments ) {
            validationResults.add(fragment.validateStepForm());
        }
        
        // check if all is valid!
        for (Boolean valid : validationResults) {
            if (valid == false) {
                btnSubmit.setEnabled(true);
                return;
            }
        }
        
        mSuccessfulSubmission();
    }
    
    public void mSuccessfulSubmission() {
        
        // Change Views
        final FrameLayout blueArea = findViewById(R.id.rsub_ContainerSteps);
        
        blueArea.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);
        
        // create Recipe Object
        Recipe recipe = recipeFormFragment.getGeneratedRecipe();
        // add Steps to Recipe Object
        ArrayList<RecipeStep> steps = new ArrayList<>();
        for (RecipeFormStepFragment step : stepFragments) {
            steps.add(step.getGeneratedStep());
        }
        recipe.setSteps(steps);
    
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_from_right,  // enter
                        R.anim.slide_out_left,    // exit
                        R.anim.slide_from_right,  // pop enter
                        R.anim.slide_out_left)    // pop exit
                .replace(R.id.rsub_fragment, new RecipeComfirmationFragment(this)).commit();
        
        // Submit to database;
        DatabaseReference reference = database.getReference("recipes");
//        String id = reference.push().getKey();
        reference.push().setValue(recipe).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Database Error", e.getMessage());
            }
        });
    }
    
    private void addStep() {
        if (listFragments.size() < STEPS_UPPER_LIMIT) {
            // add new FrameLayout to ArrayList
            listFragments.add(createFrame());
            // index of last element
            int last = listFragments.size() - 1;
            // add FrameLayout to LinearLayout
            bottomLinear.addView(listFragments.get(last));
    
            if (listFragments.size() == STEPS_UPPER_LIMIT) {
                mControlButtonStatus(btnAddStep, false);
            } else {
                mControlButtonStatus(btnDelStep, true);
            }
        }
    }
    
    private void removeStep() {
        if (listFragments.size() > STEPS_LOWER_LIMIT) {
            // index of last element
            int lastFragmentIndex = listFragments.size() - 1;
            // remove FrameLayout from LinearLayout
            bottomLinear.removeView(listFragments.get(lastFragmentIndex));
            // remove last Fragment and FrameLayout from ArrayList
            stepFragments.remove(lastFragmentIndex);
            listFragments.remove(lastFragmentIndex);
            
            // remove last image; (this uses a different index due to there being more images than steps (1 more)
            int lastImageIndex = listImageViews.size() - 1;
            listImageViews.remove(lastImageIndex);
    
            if (listFragments.size() == STEPS_LOWER_LIMIT) {
                mControlButtonStatus(btnDelStep, false);
            } else {
                mControlButtonStatus(btnAddStep, true);
            }
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
        
        RecipeFormStepFragment rfsf = new RecipeFormStepFragment(this, number);
        stepFragments.add(rfsf);
        
        getSupportFragmentManager().beginTransaction().replace(generatedId, rfsf).commit();
        
        return frame;
    }
    
    private void mControlButtonStatus(Button button, boolean status) {
        if (status) {
            button.setEnabled(true);
            button.setBackgroundColor(getResources().getColor(R.color.comfort_blue));
        } else {
            button.setEnabled(false);
            button.setBackgroundColor(getResources().getColor(R.color.comfort_grey));
        }
    }
    
    public void registerNewImage(ImageView image) {
        listImageViews.add(image);
    }
    
    public ImageManipulation getActivityImageManipulator() {
        return controlImages;
    }
}
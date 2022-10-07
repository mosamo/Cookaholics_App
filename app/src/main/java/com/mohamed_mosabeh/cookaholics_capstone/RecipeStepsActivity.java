package com.mohamed_mosabeh.cookaholics_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mohamed_mosabeh.auth.AnonymousAuth;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.RecipeInstructionsSwipeAdapter;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class RecipeStepsActivity extends AppCompatActivity {
    
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    
    // Views
    private TextView txtRecipeName;
    private TextView txtStepsIndicator;
    
    // Pager
    private ViewPager2 viewPager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);
        
        // Firebase Variables
        database = FirebaseDatabase.getInstance(getString(R.string.asia_database));
        storage= FirebaseStorage.getInstance(getString(R.string.firebase_storage));
        mAuth = FirebaseAuth.getInstance();
        
        // Signing in
        AnonymousAuth.signIn(this, mAuth);
        
        // Setting Views
        txtRecipeName = findViewById(R.id.txtRecipeName);
        txtStepsIndicator = findViewById(R.id.rs_recipeStepIndicator);
        
        // Loading Data
        LoadRecipe();
    }
    
    private void LoadRecipe() {
        
        reference = database.getReference("recipes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
    
    
                    Recipe recipe = snapshot.child("Ls9xSAkd9020Dkds").getValue(Recipe.class);
                    
                    // Value Setting
                    txtRecipeName.setText(recipe.getName());
                    if (recipe.getSteps().size() > 0)
                        txtStepsIndicator.setText("Step 1: " + recipe.getSteps().get(0).getHeader());
                    
                    // Timestamp Importing and Parsing
//                    Long time = recipe.getTimestamp();
//
//                    SimpleDateFormat dataFormat = new SimpleDateFormat("dd MMM yyyy");
//                    String timeString = dataFormat.format(new Date(time));
//
//                    txtRecipeTimestamp.setText(timeString);
                    
                    // Pager Set up
                    ViewPagerSetup(recipe);
    
                    // Hide progress Bar
//                    final ProgressBar loading = findViewById(R.id.recipeLoadingProgress);
//                    loading.setVisibility(View.GONE);
                    
                } else {
                    Toast.makeText(getApplicationContext(), "No Database Found", Toast.LENGTH_SHORT).show();
                }
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
        
            }
        });
    }
    
    private void ViewPagerSetup(Recipe recipe) {
        RecipeInstructionsSwipeAdapter adapter = new RecipeInstructionsSwipeAdapter(RecipeStepsActivity.this, recipe, storage);
        viewPager = findViewById(R.id.recipePager);
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
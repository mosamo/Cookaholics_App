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
import com.mohamed_mosabeh.auth.AnonymousAuth;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.RecipeInstructionsSwipeAdapter;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class RecipeActivity extends AppCompatActivity {
    
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    
    // Views
    private TextView txtRecipeName;
    private TextView txtRecipeUsername;
    private TextView txtRecipeCategory;
    private TextView txtRecipeDescription;
    private TextView txtRecipeTimestamp;
    
    // Pager
    private ViewPager2 viewPager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        
        // Firebase Variables
        database = FirebaseDatabase.getInstance(getString(R.string.asia_database));
        mAuth = FirebaseAuth.getInstance();
        
        // Signing in
        AnonymousAuth.signIn(this, mAuth);
        
        // Setting Views
        txtRecipeName = findViewById(R.id.txtRecipeName);
        txtRecipeUsername = findViewById(R.id.txtRecipeUsername);
        txtRecipeCategory = findViewById(R.id.txtRecipeCategory);
        txtRecipeTimestamp = findViewById(R.id.txtRecipeTimestamp);
        txtRecipeDescription = findViewById(R.id.txtRecipeDescription);
        
        // Loading Data
        LoadRecipe();
    }
    
    private void LoadRecipe() {
        Toast.makeText(this, "Please Wait..", Toast.LENGTH_SHORT).show();
        
        reference = database.getReference("recipes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
    
    
                    Recipe recipe = snapshot.child("Ls9xSAkd9020Dkds").getValue(Recipe.class);
                    // Value Importing
                    String recipe_name = snapshot.child("Ls9xSAkd9020Dkds").child("name").getValue(String.class);
                    String recipe_username = snapshot.child("Ls9xSAkd9020Dkds").child("username").getValue(String.class);
                    String recipe_category = snapshot.child("Ls9xSAkd9020Dkds").child("category").getValue(String.class);
                    String recipe_description = snapshot.child("Ls9xSAkd9020Dkds").child("description").getValue(String.class);
                    
                    txtRecipeName.setText(recipe_name);
                    txtRecipeCategory.setText(recipe_category);
                    txtRecipeDescription.setText(recipe_description);
                    txtRecipeUsername.setText("By " + recipe_username);
                    
                    // Timestamp Importing and Parsing
                    Long time = snapshot.child("Ls9xSAkd9020Dkds").child("timestamp").getValue(Long.class);
    
                    SimpleDateFormat dataFormat = new SimpleDateFormat("dd MMM yyyy");
                    String timeString = dataFormat.format(new Date(time));
                    
                    txtRecipeTimestamp.setText(timeString);
                    
                    // Pager Set up
                    RecipeInstructionsSwipeAdapter adapter = new RecipeInstructionsSwipeAdapter(RecipeActivity.this, recipe);
                    viewPager = findViewById(R.id.recipePager);
                    viewPager.setAdapter(adapter);
                    
                    // Hide progress Bar
                    final ProgressBar loading = findViewById(R.id.recipeLoadingProgress);
                    loading.setVisibility(View.GONE);
                    
                } else {
                    Toast.makeText(getApplicationContext(), "No Database Found", Toast.LENGTH_SHORT).show();
                }
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
        
            }
        });
    }
}
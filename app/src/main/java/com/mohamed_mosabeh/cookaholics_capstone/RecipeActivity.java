package com.mohamed_mosabeh.cookaholics_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohamed_mosabeh.auth.AnonymousAuth;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class RecipeActivity extends AppCompatActivity {
    
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    
    // Views
    private TextView txtRecipeName;
    private TextView txtRecipeCategory;
    private TextView txtRecipeTimestamp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        
        // Firebase Variables
        database = FirebaseDatabase.getInstance("https://cookaholics-capstone-d4931-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();
        
        // Signing in
        AnonymousAuth.signIn(this, mAuth);
        
        // Setting Views
        txtRecipeName = findViewById(R.id.txtRecipeName);
        txtRecipeCategory = findViewById(R.id.txtRecipeCategory);
        txtRecipeTimestamp = findViewById(R.id.txtRecipeTimestamp);
    }
    
    public void buttonClick(View view) {
        LoadRecipe();
    }
    
    private void LoadRecipe() {
        Toast.makeText(this, "Please Wait..", Toast.LENGTH_SHORT).show();
        
        reference = database.getReference("recipes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    
                    // Value Importing
                    String recipe_name = snapshot.child("Ls9xSAkd9020Dkds").child("name").getValue(String.class);
                    String recipe_category = snapshot.child("Ls9xSAkd9020Dkds").child("category").getValue(String.class);
                    
                    txtRecipeName.setText(recipe_name);
                    txtRecipeCategory.setText(recipe_category);
                    
                    // Timestamp Importing and Parsing
                    Long time = snapshot.child("Ls9xSAkd9020Dkds").child("timestamp").getValue(Long.class);
    
                    SimpleDateFormat dataFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String timeString = dataFormat.format(new Date(time));
                    
                    txtRecipeTimestamp.setText(timeString);
                    
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
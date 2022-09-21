package com.mohamed_mosabeh.cookaholics_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohamed_mosabeh.auth.AnonymousAuth;
import com.mohamed_mosabeh.data_objects.Recipe;


public class TestActivity extends AppCompatActivity {
    
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    
    
        database = FirebaseDatabase.getInstance("https://cookaholics-capstone-d4931-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = database.getReference("recipes");
    
        mAuth = FirebaseAuth.getInstance();
        AnonymousAuth.signIn(this, mAuth);
    }
    
    public void mainButtonClick(View view) {
    
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final TextView testView = findViewById(R.id.txtTestView);
                
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Recipe recipe = dataSnapshot.child("Ls9xSAkd9020Dkds").getValue(Recipe.class);
                testView.setText(recipe.toString());
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
}
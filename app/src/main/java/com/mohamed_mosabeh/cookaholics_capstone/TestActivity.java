package com.mohamed_mosabeh.cookaholics_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.data_objects.Tag;
import com.mohamed_mosabeh.utils.ViewUtils;

import org.checkerframework.checker.units.qual.A;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;


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
    }
    
    public void mainButtonClick(View view) {
    
        DatabaseReference reference = database.getReference("recipes");
    
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
    
                int count = 0;
                
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    Recipe recipe = s.getValue(Recipe.class);
                    boolean x = recipe.getTags().contains("keto");
                    if (x)
                        count++;
                }
                
                ViewUtils.getSnackBar(TestActivity.this, "Recipes with tag:" + count);
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
}
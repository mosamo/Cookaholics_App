package com.mohamed_mosabeh.cookaholics_capstone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohamed_mosabeh.data_objects.Recipe;

public class CategoriesFragment extends Fragment {
    
    private FirebaseDatabase database;
    private DatabaseReference reference;
    
    private TextView testView;
    private String mSecretString;
    
    public CategoriesFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    
    
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        testView = view.findViewById(R.id.txtTestView1);
        return view;
        
        
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_categories, container, false);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (database == null) {
            database = FirebaseDatabase.getInstance("https://cookaholics-capstone-d4931-default-rtdb.asia-southeast1.firebasedatabase.app/");
            reference = database.getReference("recipes");
            fetchRecipe();
        } else {
            testView.setText(mSecretString);
        }
    }
    
    public void fetchRecipe() {
        
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Recipe recipe = dataSnapshot.child("Ls9xSAkd9020Dkds").getValue(Recipe.class);
                testView.setText(recipe.toString());
                mSecretString = recipe.toString();
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
}
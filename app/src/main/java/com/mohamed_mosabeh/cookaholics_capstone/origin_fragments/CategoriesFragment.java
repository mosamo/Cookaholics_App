package com.mohamed_mosabeh.cookaholics_capstone.origin_fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment {
    
    private FirebaseDatabase database;
    private DatabaseReference reference;
    
    private TextView testView;
    private String mSecretString;
    
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;

    // should be an array list
    private Recipe recipe;
    
    public CategoriesFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    
    
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        testView = view.findViewById(R.id.txtTestView1);
        recycler = view.findViewById(R.id.highlightedRecycler);
        
        
        return view;
        
        
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_categories, container, false);
    }
    
    private void recyclerSetUp() {
        //recycler.setHasFixedSize(true);
        // not sure if correct context
        recycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        ArrayList<Recipe> recipes = new ArrayList<>();
        recipes.add(recipe);
        recipes.add(recipe);
        recipes.add(recipe);
        recipes.add(recipe);
        
        adapter = new CardRecipesRecyclerViewAdapter(recipes, FirebaseStorage.getInstance());
        recycler.setAdapter(adapter);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (database == null) {
            database = FirebaseDatabase.getInstance(getString(R.string.asia_database));
            reference = database.getReference("recipes");
            
            // fetch highlighted
            // fetch tags
            // fetch categories
            fetchRecipe();
        } else {
            testView.setText(mSecretString);
            recyclerSetUp();
        }
    }
    
    public void fetchRecipe() {
        
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                recipe = dataSnapshot.child("Ls9xSAkd9020Dkds").getValue(Recipe.class);
                testView.setText(recipe.toString());
                mSecretString = recipe.toString();
    
    
                recyclerSetUp();
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
}
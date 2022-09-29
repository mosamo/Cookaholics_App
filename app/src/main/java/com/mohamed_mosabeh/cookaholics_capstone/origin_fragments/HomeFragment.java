package com.mohamed_mosabeh.cookaholics_capstone.origin_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseStorage storage;
    
    private RecyclerView WeeklyRecycler;
    private RecyclerView.Adapter adapter;
    
    private ArrayList<Recipe> recipes = new ArrayList<>();
    
    private TextView SeeAllWeeklyHottestTextView;
    
    private ProgressBar WeeklyHottestProgress;
    
    public HomeFragment() {
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        SetUpViews(view);
        
        return view;
    }
    
    private void SetUpViews(View parent) {
        WeeklyRecycler = parent.findViewById(R.id.home_weeklyRecyclerView);
        SeeAllWeeklyHottestTextView = parent.findViewById(R.id.home_recipeSeeAllWeeklyHottest);
        WeeklyHottestProgress = parent.findViewById(R.id.home_weeklyHottestProgress);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        // if there is no internet don't do anything
        if (database == null) {
            database = FirebaseDatabase.getInstance(getString(R.string.asia_database));
            storage = FirebaseStorage.getInstance("gs://cookaholics-capstone-d4931.appspot.com/");
            getData();
        } else {
            WeeklyRecyclerSetUp(recipes);
            killProgressBars();
        }
    }
    
    private void killProgressBars() {
        WeeklyHottestProgress.setVisibility(View.GONE);
    }
    
    private void getData() {
    
        recipes.clear();
        
        reference = database.getReference("recipes");
        Query latestTenRecipes = reference.orderByChild("timestamp").limitToLast(10);
    
        latestTenRecipes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
    
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    recipes.add(recipe);
                }
            
                WeeklyRecyclerSetUp(recipes);
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
    
    private void WeeklyRecyclerSetUp(ArrayList<Recipe> recipes) {
        //recycler.setHasFixedSize(true);
        // not sure if correct context
        WeeklyRecycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        
        adapter = new CardRecipesRecyclerViewAdapter(recipes, storage);
        WeeklyRecycler.setAdapter(adapter);
    
        killProgressBars();
    }
}
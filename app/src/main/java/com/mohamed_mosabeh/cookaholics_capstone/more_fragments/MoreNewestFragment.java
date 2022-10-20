package com.mohamed_mosabeh.cookaholics_capstone.more_fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohamed_mosabeh.cookaholics_capstone.OriginActivity;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.RecipeStepsActivity;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerRecipeClickInterface;
import com.mohamed_mosabeh.utils.recycler_views.CompactRecipesRecyclerViewAdapter;

import java.util.ArrayList;

public class MoreNewestFragment extends Fragment implements RecyclerRecipeClickInterface, MoreRecipesBaseInterface {
    
    private OriginActivity parent;
    private FirebaseDatabase database;
    
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private RecyclerView recipeRecycler;
    private RecyclerView.Adapter recipeAdapter = new CompactRecipesRecyclerViewAdapter(recipes, this, "New Recipe");
    
    public MoreNewestFragment() {
        // Required empty public constructor
    }
    
    public MoreNewestFragment(OriginActivity parent, FirebaseDatabase database) {
        this.parent = parent;
        this.database = database;
        getData();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_more_newest, container, false);
        
        SetupViews(v);
        RecyclerSetup();
        
        return v;
    }
    
    @Override
    public void getData() {
        DatabaseReference reference = database.getReference("recipes");
        Query newestThirtySix = reference.orderByChild("timestamp").limitToLast(36);
    
        newestThirtySix.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                recipes.clear();
            
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    recipe.setId(snapshot.getKey());
                    recipes.add(recipe);
                }
                
                RecyclerSetup();
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
    
    @Override
    public void SetupViews(View view) {
        recipeRecycler = view.findViewById(R.id.fmnr_Recycler);
        Button btnBack = view.findViewById(R.id.fmnr_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.alternativeFragments("recipes");
            }
        });
    }
    
    @Override
    public void RecyclerSetup() {
        if (recipeRecycler != null) {
            try {
                recipeRecycler.setLayoutManager(new GridLayoutManager(parent.getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false));
                recipeRecycler.setAdapter(recipeAdapter);
                recipeRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        outRect.set(6, 0, 6, 16);
                    }
                });
            
            } catch (Exception e) {
                Log.w("Recycler Exception", e.getMessage());
            }
        }
    }
    
    @Override
    public void onItemRecipeClick(int position) {
        Intent intent = new Intent(getActivity(), RecipeStepsActivity.class);
        intent.putExtra("recipe_id", recipes.get(position).getId());
        startActivity(intent);
    }
}
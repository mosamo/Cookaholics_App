package com.mohamed_mosabeh.cookaholics_capstone.origin_fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.RecipeStepsActivity;
import com.mohamed_mosabeh.data_objects.Cuisine;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.data_objects.Tag;
import com.mohamed_mosabeh.utils.RecyclerRecipeClickInterface;
import com.mohamed_mosabeh.utils.recycler_views.CardRecipesSmallRecyclerViewAdapter;
import com.mohamed_mosabeh.utils.recycler_views.CuisineRecipesRecyclerViewAdapter;
import com.mohamed_mosabeh.utils.recycler_views.TagsRecipesRecyclerViewAdapter;

import java.util.ArrayList;

public class RecipesFragment extends Fragment implements RecyclerRecipeClickInterface{
    
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    
    private ArrayList<Cuisine> cuisines = new ArrayList<>();
    private ArrayList<Tag> tags = new ArrayList<>();
    private ArrayList<Recipe> new_recipes = new ArrayList<>();
    
    private RecyclerView CuisineRecycler;
    private RecyclerView TagRecycler;
    private RecyclerView NewRecipesRecycler;
    
    private RecyclerView.Adapter CuisineAdapter;
    private RecyclerView.Adapter TagAdapter;
    private RecyclerView.Adapter NewRecipesAdapter;
    
    private ProgressBar CuisineProgressBar;
    private ProgressBar TagProgressBar;
    private ProgressBar NewRecipesProgressBar;
    
    public RecipesFragment() {
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    
    
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);
    
        SetUpViews(view);
        
        return view;
    }
    
    private void SetUpViews(View parent) {
        
        CuisineRecycler = parent.findViewById(R.id.rec_cusineRecycler);
        CuisineProgressBar = parent.findViewById(R.id.rec_cusineProgress);
        
        TagRecycler = parent.findViewById(R.id.rec_tagsRecycler);
        TagProgressBar = parent.findViewById(R.id.rec_tagsProgress);
        
        NewRecipesRecycler = parent.findViewById(R.id.rec_newRecipesRecycler);
        NewRecipesProgressBar = parent.findViewById(R.id.rec_newRecipesProgress);
    
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (database == null) {
            database = FirebaseDatabase.getInstance(getString(R.string.asia_database));
            storage = FirebaseStorage.getInstance(getString(R.string.firebase_storage));
            getData();
        } else {
            SetUpRecyclers();
        }
    }
    
    private void SetUpRecyclers() {
        CuisineRecyclerSetUp(cuisines);
        TagsRecyclerSetUp(tags);
        NewRecipesRecyclerSetUp(new_recipes);
    }
    

    
    private void getData() {
        getCuisinesData();
        getTagsData();
        getNewestRecipesData();
    }
    
    private void getNewestRecipesData() {
    
        DatabaseReference reference = database.getReference("recipes");
        Query newestTenRecipes = reference.orderByChild("timestamp").limitToLast(10);
    
        newestTenRecipes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                new_recipes.clear();
            
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    recipe.setId(snapshot.getKey());
                    new_recipes.add(recipe);
                }
            
                NewRecipesRecyclerSetUp(new_recipes);
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
    
    private void getTagsData() {
        DatabaseReference reference = database.getReference("tags");
        Query topTenTags = reference.orderByChild("hits").limitToLast(10);
    
        topTenTags.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                tags.clear();
            
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Tag tag = snapshot.getValue(Tag.class);
                    tag.setName(snapshot.getKey());
                
                    tags.add(tag);
                }
            
                TagsRecyclerSetUp(tags);
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
    
    private void getCuisinesData() {
        DatabaseReference reference = database.getReference("cuisines");
        Query topSixCuisines = reference.orderByChild("hits").limitToLast(6);
    
        topSixCuisines.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                cuisines.clear();
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Cuisine cuisine = snapshot.getValue(Cuisine.class);
                    cuisine.setName(snapshot.getKey());
                
                    cuisines.add(cuisine);
                }
            
                CuisineRecyclerSetUp(cuisines);
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
    
    private void NewRecipesRecyclerSetUp(ArrayList<Recipe> new_recipes) {
        NewRecipesRecycler.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false));
        
        NewRecipesAdapter = new CardRecipesSmallRecyclerViewAdapter(new_recipes, storage, this, "New Recipe");
        NewRecipesRecycler.setAdapter(NewRecipesAdapter);
        
        NewRecipesProgressBar.setVisibility(View.GONE);
    }
    
    private void CuisineRecyclerSetUp(ArrayList<Cuisine> cuisines) {
        CuisineRecycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    
        CuisineAdapter = new CuisineRecipesRecyclerViewAdapter(cuisines);
    
    
        CuisineRecycler.setAdapter(CuisineAdapter);
        
        CuisineProgressBar.setVisibility(View.GONE);
    }
    
    private void TagsRecyclerSetUp(ArrayList<Tag> tags) {
        TagRecycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    
        TagAdapter = new TagsRecipesRecyclerViewAdapter(tags);
        TagRecycler.setAdapter(TagAdapter);
        
        TagProgressBar.setVisibility(View.GONE);
    }
    
    @Override
    public void onItemRecipeClick(int position) {
        if (new_recipes.size() > 0) {
            // this might be better
            // recipes.size() - 1 <= position
            Intent intent = new Intent(getActivity(), RecipeStepsActivity.class);
            intent.putExtra("recipe_id", new_recipes.get(position).getId());
            startActivity(intent);
        }
    }
}
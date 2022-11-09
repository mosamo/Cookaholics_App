package com.mohamed_mosabeh.cookaholics_capstone.origin_fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mohamed_mosabeh.cookaholics_capstone.OriginActivity;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.RecipeStepsActivity;
import com.mohamed_mosabeh.cookaholics_capstone.SubmitActivity;
import com.mohamed_mosabeh.data_objects.Cuisine;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.data_objects.Tag;
import com.mohamed_mosabeh.utils.ViewUtils;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerCuisineClickInterface;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerRecipeClickInterface;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerTagClickInterface;
import com.mohamed_mosabeh.utils.recycler_views.CardRecipesSmallRecyclerViewAdapter;
import com.mohamed_mosabeh.utils.recycler_views.CuisineRecipesRecyclerViewAdapter;
import com.mohamed_mosabeh.utils.recycler_views.TagsRecipesRecyclerViewAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class RecipesFragment extends Fragment implements RecyclerRecipeClickInterface, RecyclerCuisineClickInterface, RecyclerTagClickInterface {
    
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    
    private ArrayList<Cuisine> cuisines = new ArrayList<>();
    private ArrayList<Tag> tags = new ArrayList<>();
    private ArrayList<Recipe> new_recipes = new ArrayList<>();
    
    private RecyclerView CuisineRecycler;
    private RecyclerView TagRecycler;
    private RecyclerView NewRecipesRecycler;
    
    private RecyclerView.Adapter NewRecipesAdapter = new CardRecipesSmallRecyclerViewAdapter(new_recipes, "New Recipe", this);
    private CardRecipesSmallRecyclerViewAdapter mNewRecipeAdapter = (CardRecipesSmallRecyclerViewAdapter) NewRecipesAdapter;
    private RecyclerView.Adapter TagAdapter = new TagsRecipesRecyclerViewAdapter(tags, this);
    private RecyclerView.Adapter CuisineAdapter = new CuisineRecipesRecyclerViewAdapter(cuisines, this);
    
    private ProgressBar CuisineProgressBar;
    private ProgressBar TagProgressBar;
    private ProgressBar NewRecipesProgressBar;
    
    private Button btnRecipeSubmission;
    
    private Button seeAllCuisines;
    private Button seeAllNewest;
    private Button seeAllTags;
    
    private OriginActivity parent;
    
    public RecipesFragment() {
    }
    
    public RecipesFragment(OriginActivity parent, FirebaseDatabase database, FirebaseStorage storage) {
        this.parent = parent;
        this.database = database;
        this.storage = storage;
        getData();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);
    
        SetUpViews(view);
        
        return view;
    }
    
    @Override
    public void onPause() {
        super.onPause();
        mNewRecipeAdapter.getRecipeHolderMap();
    }
    
    private void SetUpViews(View viewParent) {
        
        CuisineRecycler = viewParent.findViewById(R.id.rec_cusineRecycler);
        CuisineProgressBar = viewParent.findViewById(R.id.rec_cusineProgress);
        
        TagRecycler = viewParent.findViewById(R.id.rec_tagsRecycler);
        TagProgressBar = viewParent.findViewById(R.id.rec_tagsProgress);
        
        NewRecipesRecycler = viewParent.findViewById(R.id.rec_newRecipesRecycler);
        NewRecipesProgressBar = viewParent.findViewById(R.id.rec_newRecipesProgress);
    
        btnRecipeSubmission = viewParent.findViewById(R.id.rec_RecipeSubmission);
        btnRecipeSubmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SubmitActivity.class);
                startActivity(intent);
            }
        });
        
        seeAllCuisines = viewParent.findViewById(R.id.rec_SeeAllCuisine);
        seeAllCuisines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.alternativeFragments("more_cuisines");
            }
        });
        seeAllNewest = viewParent.findViewById(R.id.rec_SeeAllNewRecipes);
        seeAllNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.alternativeFragments("more_newest");
            }
        });
        seeAllTags = viewParent.findViewById(R.id.rec_SeeAllTags);
        seeAllTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.alternativeFragments("more_tags");
            }
        });
        
        ViewUtils.IfDataExistsHideProgressBar(new_recipes.size(), NewRecipesProgressBar);
        ViewUtils.IfDataExistsHideProgressBar(tags.size(), TagProgressBar);
        ViewUtils.IfDataExistsHideProgressBar(cuisines.size(), CuisineProgressBar);
    
        SetUpRecyclers();
    }
    
    
    private void SetUpRecyclers() {
        NewRecipesRecyclerSetUp();
        CuisineRecyclerSetUp();
        TagsRecyclerSetUp();
    }
    
    private void getData() {
        getNewestRecipesData();
        getCuisinesData();
        getTagsData();
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

                Collections.reverse(new_recipes);

                ViewUtils.IfDataExistsHideProgressBar(new_recipes.size(), NewRecipesProgressBar);
            
                fetchNewRecipeImages(new_recipes);
                NewRecipesRecyclerSetUp();
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
    
    private void fetchNewRecipeImages(ArrayList<Recipe> rs) {
        for (Recipe r : rs) {
        
            if (!r.getIcon().equals("no-image")) {
                try {
                    final File tempfile = File.createTempFile(r.getId() + "_icon", "png");
                    final StorageReference storageRef = storage.getReference().child(r.getIcon());
                    storageRef.getFile(tempfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(tempfile.getAbsolutePath());
                            r.setPicture(bitmap);
                            try {
                                mNewRecipeAdapter.getRecipeHolderMap().get(r).cardImage.setImageBitmap(bitmap);
                                mNewRecipeAdapter.KillProgressBar(mNewRecipeAdapter.getRecipeHolderMap().get(r).cardProgress);
                            } catch (NullPointerException es) {
                                Log.i("New Recipes Recycler", "Couldn't bind Recipes");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                            r.setPicture(bitmap);
                            try {
                                mNewRecipeAdapter.getRecipeHolderMap().get(r).cardImage.setImageBitmap(bitmap);
                                mNewRecipeAdapter.KillProgressBar(mNewRecipeAdapter.getRecipeHolderMap().get(r).cardProgress);
                            } catch (NullPointerException es) {
                                Log.i("New Recipes Recycler", "Couldn't bind Recipes");
                            }
                            Log.w("Firebase Storage", "New Recipes Storage: Couldn't Fetch File: " + e.getMessage());
                        }
                    });
                } catch (IOException e) {
                    Log.w("File Download Issue", e.getMessage());
                } catch (Exception e) {
                    Log.w("File Download Issue", e.getMessage());
                }
            }
        }
    }
    
    private void getTagsData() {
        DatabaseReference reference = database.getReference("tags");
        Query topTenTags = reference.orderByChild("recipes_count").limitToLast(10);
    
        topTenTags.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                tags.clear();
            
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Tag tag = snapshot.getValue(Tag.class);
                    tag.setName(snapshot.getKey());
                
                    tags.add(tag);
                }
    
                ViewUtils.IfDataExistsHideProgressBar(tags.size(), TagProgressBar);
            
                TagsRecyclerSetUp();
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
    
        topSixCuisines.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                cuisines.clear();
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Cuisine cuisine = snapshot.getValue(Cuisine.class);
                    cuisine.setName(snapshot.getKey());
                
                    cuisines.add(cuisine);
                }
    
    
                ViewUtils.IfDataExistsHideProgressBar(cuisines.size(), CuisineProgressBar);
            
                CuisineRecyclerSetUp();
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
    
    private void NewRecipesRecyclerSetUp() {
        if (NewRecipesRecycler != null) {
            try {
                NewRecipesRecycler.setLayoutManager(new GridLayoutManager(parent.getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false));
                NewRecipesRecycler.setAdapter(NewRecipesAdapter);
            } catch (Exception e) {
                Log.w("Recycler Exception", e.getMessage());
            }
        }
    }
    
    
    
    private void TagsRecyclerSetUp() {
        if (TagRecycler != null) {
            try {
                TagRecycler.setLayoutManager(new LinearLayoutManager(parent.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                TagRecycler.setAdapter(TagAdapter);
            } catch (Exception e) {
                Log.w("Recycler Exception", e.getMessage());
            }
        }
    }
    
    private void CuisineRecyclerSetUp() {
        if (CuisineRecycler != null) {
            try {
                CuisineRecycler.setLayoutManager(new LinearLayoutManager(parent.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                CuisineRecycler.setAdapter(CuisineAdapter);
            } catch (Exception e) {
                Log.w("Recycler Exception", e.getMessage());
            }
        }
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
    
    @Override
    public void onItemCuisineClick(int position) {
        String value = cuisines.get(position).getName();
        parent.setFilteredFragmentParameter("cuisine", value, "recipes");
        parent.alternativeFragments("filtered_by");
    }
    
    @Override
    public void onItemTagClick(int position) {
        String value = tags.get(position).getName();
        parent.setFilteredFragmentTag(value);
        parent.alternativeFragments("filtered_by_tag");
    }
}
package com.mohamed_mosabeh.cookaholics_capstone.origin_fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Category;
import com.mohamed_mosabeh.data_objects.HighlightedRecipe;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter;
import com.mohamed_mosabeh.utils.recycler_views.CategoryMainRecyclerViewAdapter;

import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class HomeFragment extends Fragment {
    
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    
    private RecyclerView WeeklyRecycler;
    private RecyclerView CategoryRecycler;
    
    private RecyclerView.Adapter recipeAdapter;
    private RecyclerView.Adapter categoryAdapter;
    
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private ArrayList<Category> categories = new ArrayList<>();
    
    private TextView SeeAllWeeklyHottestTextView;
    private TextView featuredRecipeComment;
    private TextView featuredRecipeCurator;
    private TextView featuredRecipeNameLabel;
    
    private ProgressBar WeeklyHottestProgress;
    private ProgressBar CategoriesProgress;
    private ProgressBar FeaturedProgressBar;
    private ProgressBar FeaturedCommentProgressBar;
    
    private ImageView FeaturedRecipeImageView;
    
    private HighlightedRecipe featuredRecipe;
    
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
        CategoryRecycler = parent.findViewById(R.id.home_categoriesRecyclerView);
        SeeAllWeeklyHottestTextView = parent.findViewById(R.id.home_recipeSeeAllWeeklyHottest);
        WeeklyHottestProgress = parent.findViewById(R.id.home_weeklyHottestProgress);
        CategoriesProgress = parent.findViewById(R.id.home_categoriesProgress);
        FeaturedProgressBar = parent.findViewById(R.id.home_featuredProgressBar);
        featuredRecipeComment = parent.findViewById(R.id.home_featuredRecipeComment);
        featuredRecipeCurator = parent.findViewById(R.id.home_featuredRecipeCurator);
        FeaturedCommentProgressBar = parent.findViewById(R.id.home_featuredRecipeCommentProgressBar);
        FeaturedRecipeImageView = parent.findViewById(R.id.home_featuredRecipeImage);
        featuredRecipeNameLabel = parent.findViewById(R.id.home_FeaturedRecipeNameLabel);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        // if there is no internet don't do anything
        if (database == null) {
            database = FirebaseDatabase.getInstance(getString(R.string.asia_database));
            storage = FirebaseStorage.getInstance(getString(R.string.firebase_storage));
            getData();
        } else {
            SetUpRecyclers();
            killProgressBars();
        }
    }
    
    private void SetUpRecyclers() {
        WeeklyRecyclerSetUp(recipes);
        CategoryRecyclerSetUp(categories);
        SetUpFeaturedContainer(featuredRecipe);
    }
    
    
    
    private void killProgressBars() {
        WeeklyHottestProgress.setVisibility(View.GONE);
        CategoriesProgress.setVisibility(View.GONE);
        FeaturedCommentProgressBar.setVisibility(View.GONE);
    }
    
    private void getData() {
        getFeaturedRecipeData();
        getWeeklyRecipesData();
        getCategoriesData();
    }
    
    private void getFeaturedRecipeData() {
    
        DatabaseReference reference = database.getReference("highlighted-recipes");
        Query LastHighlightedRecipe = reference.orderByChild("timestamp").limitToLast(1);

        LastHighlightedRecipe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
    
                HighlightedRecipe hlRecipe = new HighlightedRecipe();
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    hlRecipe = snapshot.getValue(HighlightedRecipe.class);
                    hlRecipe.setId(snapshot.getKey());
                }
    
                featuredRecipe = hlRecipe;
    
                FeaturedCommentProgressBar.setVisibility(View.GONE);
                SetUpFeaturedContainer(hlRecipe);
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
    
            }
        });
    }
    
    private void SetUpFeaturedContainer(HighlightedRecipe hlRecipe) {
        featuredRecipeComment.setText("\"" + hlRecipe.getCurator_comment() + "\"");
        featuredRecipeCurator.setText("--" + hlRecipe.getCurator_name());
        featuredRecipeNameLabel.setText(hlRecipe.getName());
    
    
        if (!hlRecipe.getIcon().equals("no-image")) {
            try {
                final File tempfile = File.createTempFile(hlRecipe.getId()+"_icon", "png");
                final StorageReference storageRef = storage.getReference().child(hlRecipe.getIcon());
                storageRef.getFile(tempfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(tempfile.getAbsolutePath());
                        FeaturedRecipeImageView.setImageBitmap(bitmap);
    
                        FeaturedProgressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase Storage", "Couldn't Fetch File: " + e.getMessage());
                    
                        // Kill Progress
                        FeaturedProgressBar.setVisibility(View.GONE);
                        FeaturedRecipeImageView.setImageResource(R.drawable.placeholder);
                    }
                });
            
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
//            // Kill Progress
//            if (FeaturedProgressBar != null) {
//                ((ViewGroup) FeaturedProgressBar.getParent()).removeView(FeaturedProgressBar);
//            }
            FeaturedProgressBar.setVisibility(View.GONE);
            FeaturedRecipeImageView.setImageResource(R.drawable.placeholder);
        }
    }
    
    
    private void getWeeklyRecipesData() {
    
        DatabaseReference reference = database.getReference("recipes");
        Query latestEightRecipes = reference.orderByChild("timestamp").limitToLast(8);
        
        latestEightRecipes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
    
                recipes.clear();
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    recipe.setId(snapshot.getKey());
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
    
    private void getCategoriesData() {
    
        DatabaseReference reference = database.getReference("categories");
        Query firstTwentyCategories = reference.limitToFirst(8);
    
        firstTwentyCategories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
    
                categories.clear();
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Category category = snapshot.getValue(Category.class);
                    category.setName(snapshot.getKey());
                    
                    categories.add(category);
                }
            
                CategoryRecyclerSetUp(categories);
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
        
        recipeAdapter = new CardRecipesRecyclerViewAdapter(recipes, storage);
        WeeklyRecycler.setAdapter(recipeAdapter);
    
        WeeklyHottestProgress.setVisibility(View.GONE);
    }
    
    private void CategoryRecyclerSetUp(ArrayList<Category> categories) {
        CategoryRecycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    
        categoryAdapter = new CategoryMainRecyclerViewAdapter(categories, storage);
        CategoryRecycler.setAdapter(categoryAdapter);
    
        CategoriesProgress.setVisibility(View.GONE);
    }
    
}
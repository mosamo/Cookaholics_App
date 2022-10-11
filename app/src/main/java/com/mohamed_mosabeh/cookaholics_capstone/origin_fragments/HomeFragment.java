package com.mohamed_mosabeh.cookaholics_capstone.origin_fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.mohamed_mosabeh.cookaholics_capstone.OriginActivity;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.RecipeStepsActivity;
import com.mohamed_mosabeh.data_objects.Category;
import com.mohamed_mosabeh.data_objects.HighlightedRecipe;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerCategoryClickInterface;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerRecipeClickInterface;
import com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter;
import com.mohamed_mosabeh.utils.recycler_views.CategoryMainRecyclerViewAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class HomeFragment extends Fragment implements RecyclerRecipeClickInterface, RecyclerCategoryClickInterface {
    
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    
    private RecyclerView WeeklyRecycler;
    private RecyclerView CategoryRecycler;
    
    
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private ArrayList<Category> categories = new ArrayList<>();
    
    
    private RecyclerView.Adapter recipeAdapter = new CardRecipesRecyclerViewAdapter(recipes, this);
    private CardRecipesRecyclerViewAdapter mRecipeAdapter = (CardRecipesRecyclerViewAdapter) recipeAdapter;
    private RecyclerView.Adapter categoryAdapter = new CategoryMainRecyclerViewAdapter(categories, this);
    private CategoryMainRecyclerViewAdapter mCategoryAdapter = (CategoryMainRecyclerViewAdapter) categoryAdapter;
    
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
    private Bitmap featuredImage;
    
    private OriginActivity parent;
    
    public HomeFragment() {
    }
    
    public HomeFragment(OriginActivity parent, FirebaseDatabase database, FirebaseStorage storage) {
        this.parent = parent;
        this.database = database;
        this.storage = storage;
        getData();
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
        WeeklyHottestProgress.setVisibility(View.VISIBLE);
        CategoriesProgress = parent.findViewById(R.id.home_categoriesProgress);
        CategoriesProgress.setVisibility(View.VISIBLE);
        CardView featuredMainContainer = parent.findViewById(R.id.home_CardRecipeOfTheWeek);
        featuredMainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (featuredRecipe != null) {
                    Intent intent = new Intent(getActivity(), RecipeStepsActivity.class);
                    intent.putExtra("recipe_id", featuredRecipe.getId());
                    startActivity(intent);
                }
            }
        });
        featuredRecipeComment = parent.findViewById(R.id.home_featuredRecipeComment);
        featuredRecipeCurator = parent.findViewById(R.id.home_featuredRecipeCurator);
        FeaturedProgressBar = parent.findViewById(R.id.home_featuredProgressBar);
        FeaturedProgressBar.setVisibility(View.VISIBLE);
        FeaturedCommentProgressBar = parent.findViewById(R.id.home_featuredRecipeCommentProgressBar);
        
        FeaturedRecipeImageView = parent.findViewById(R.id.home_featuredRecipeImage);
        if (featuredImage != null) {
            FeaturedRecipeImageView.setImageBitmap(featuredImage);
        }
        
        featuredRecipeNameLabel = parent.findViewById(R.id.home_FeaturedRecipeNameLabel);
        
        SetUpRecyclers();
        SetUpBindListeners();
    }
    
    private void SetUpBindListeners() {
        WeeklyRecycler
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                WeeklyRecycler
                                        .getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                                WeeklyHottestProgress.setVisibility(View.GONE);
                                
                                // If length is zero display Text
                            }
                        });
        
        CategoryRecycler
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                CategoryRecycler
                                        .getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                                CategoriesProgress.setVisibility(View.GONE);
                            }
                        });
    }
    
    @Override
    public void onPause() {
        super.onPause();
        mRecipeAdapter.getProgressBarMap().clear();
        mCategoryAdapter.getProgressBarMap().clear();
    }
    
    private void SetUpRecyclers() {
        WeeklyRecyclerSetUp();
        CategoryRecyclerSetUp();
        SetUpFeaturedContainer();
    }
    
    private void getData() {
        getWeeklyRecipesData();
        getCategoriesData();
        getFeaturedRecipeData();
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
                SetUpFeaturedContainer();
                fetchFeaturedImage();
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
    
            }
        });
    }
    
    private void fetchFeaturedImage() {
        try {
            final File tempfile = File.createTempFile(featuredRecipe.getId() + "_icon", "png");
            final StorageReference storageRef = storage.getReference().child(featuredRecipe.getIcon());
            storageRef.getFile(tempfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    featuredImage = BitmapFactory.decodeFile(tempfile.getAbsolutePath());
                    try {
                        FeaturedRecipeImageView.setImageBitmap(featuredImage);
                        FeaturedProgressBar.setVisibility(View.GONE);
                    } catch (NullPointerException npe) {
                        Log.w("Home Featured Image", npe.getMessage());
                    } catch (Exception e) {
                        Log.w("Home Featured Image", e.getMessage());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    featuredImage = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                    Log.w("Firebase Storage", "Featured Picture: Couldn't Fetch File: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            Log.w("File Download Issue", e.getMessage());
        } catch (Exception e) {
            Log.w("File Download Issue", e.getMessage());
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
                
                fetchRecipesImages(recipes);
                WeeklyRecyclerSetUp();
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
    
    private void fetchRecipesImages(ArrayList<Recipe> rs) {
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
                            mRecipeAdapter.KillProgressBar(mRecipeAdapter.getProgressBarMap().get(r));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                            r.setPicture(bitmap);
                            mRecipeAdapter.KillProgressBar(mRecipeAdapter.getProgressBarMap().get(r));
                            Log.w("Firebase Storage", "Weekly Recipe Images: Couldn't Fetch File: " + e.getMessage());
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
                
                fetchCategoriesImages(categories);
                CategoryRecyclerSetUp();
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
    
    private void fetchCategoriesImages(ArrayList<Category> cs) {
        for (Category c : cs) {
            if (!c.getImage().equals("no-image")) {
                try {
                    final File tempfile = File.createTempFile(c.getName() + "_icon", "png");
                    final StorageReference storageRef = storage.getReference().child(c.getImage());
                    storageRef.getFile(tempfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(tempfile.getAbsolutePath());
                            c.setPicture(bitmap);
                            mCategoryAdapter.KillProgressBar(mCategoryAdapter.getProgressBarMap().get(c));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                            c.setPicture(bitmap);
                            mCategoryAdapter.KillProgressBar(mCategoryAdapter.getProgressBarMap().get(c));
                            Log.w("Firebase Storage", "Category Images: Couldn't Fetch File: " + e.getMessage());
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
    
    private void WeeklyRecyclerSetUp() {
        if (WeeklyRecycler != null) {
            try {
                WeeklyRecycler.setLayoutManager(new LinearLayoutManager(parent.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                WeeklyRecycler.setAdapter(recipeAdapter);
            } catch (Exception e) {
                Log.w("Recycler Exception", e.getMessage());
            }
        }
    }
    
    private void CategoryRecyclerSetUp() {
        if (CategoryRecycler != null) {
            try {
                CategoryRecycler.setLayoutManager(new LinearLayoutManager(parent.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                CategoryRecycler.setAdapter(categoryAdapter);
            } catch (Exception e) {
                Log.w("Recycler Exception", e.getMessage());
            }
        }
    }
    
    private void SetUpFeaturedContainer() {
        try {
            if (featuredRecipe != null) {
                featuredRecipeComment.setText("\"" + featuredRecipe.getCurator_comment() + "\"");
                featuredRecipeCurator.setText("--" + featuredRecipe.getCurator_name());
                featuredRecipeNameLabel.setText(featuredRecipe.getName());
                FeaturedCommentProgressBar.setVisibility(View.GONE);
        
                if (featuredRecipe.getIcon().equals("no-image")) {
                    FeaturedRecipeImageView.setImageResource(R.drawable.placeholder);
                    FeaturedProgressBar.setVisibility(View.GONE);
                } else if (featuredImage != null) {
                    FeaturedRecipeImageView.setImageBitmap(featuredImage);
                    FeaturedProgressBar.setVisibility(View.GONE);
                }
            }
        } catch (NullPointerException npe) {
            Log.v("Home Fragment", "Views did not initialize yet");
        }
    }
    
    @Override
    public void onItemRecipeClick(int position) {
        if (recipes.size() > 0) {
            // this might be better
            // recipes.size() - 1 <= position
            Intent intent = new Intent(getActivity(), RecipeStepsActivity.class);
            intent.putExtra("recipe_id", recipes.get(position).getId());
            startActivity(intent);
        }
    }
    
    @Override
    public void onItemCategoryClick(int position) {
        if (categories.size() > 0) {
            Toast.makeText(parent, categories.get(position).getName(), Toast.LENGTH_SHORT).show();;
        }
    }
}
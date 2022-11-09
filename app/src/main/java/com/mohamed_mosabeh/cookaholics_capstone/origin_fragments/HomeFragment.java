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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
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
import com.mohamed_mosabeh.data_objects.Category;
import com.mohamed_mosabeh.data_objects.HighlightedRecipe;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.ParserUtils;
import com.mohamed_mosabeh.utils.SortByLikesComparator;
import com.mohamed_mosabeh.utils.ViewUtils;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerCategoryClickInterface;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerRecipeClickInterface;
import com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter;
import com.mohamed_mosabeh.utils.recycler_views.CategoryMainRecyclerViewAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


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
    
    private Button btnSeeAllCategories;
    
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
    
        //ViewUtils.getSnackBar(parent, String.valueOf(ParserUtils.getLastWeekTimestamp()));
        
        return view;
    }
    
    private void SetUpViews(View viewParent) {
        WeeklyRecycler = viewParent.findViewById(R.id.home_weeklyRecyclerView);
        CategoryRecycler = viewParent.findViewById(R.id.home_categoriesRecyclerView);
        WeeklyHottestProgress = viewParent.findViewById(R.id.home_weeklyHottestProgress);
        CategoriesProgress = viewParent.findViewById(R.id.home_categoriesProgress);
        CardView featuredMainContainer = viewParent.findViewById(R.id.home_CardRecipeOfTheWeek);
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
        featuredRecipeComment = viewParent.findViewById(R.id.home_featuredRecipeComment);
        featuredRecipeCurator = viewParent.findViewById(R.id.home_featuredRecipeCurator);
        FeaturedProgressBar = viewParent.findViewById(R.id.home_featuredProgressBar);
        FeaturedCommentProgressBar = viewParent.findViewById(R.id.home_featuredRecipeCommentProgressBar);
        
        FeaturedRecipeImageView = viewParent.findViewById(R.id.home_featuredRecipeImage);
        if (featuredImage != null) {
            FeaturedRecipeImageView.setImageBitmap(featuredImage);
        }
        
        featuredRecipeNameLabel = viewParent.findViewById(R.id.home_FeaturedRecipeNameLabel);
        btnSeeAllCategories = viewParent.findViewById(R.id.home_recipeSeeAllCategories);
        btnSeeAllCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.alternativeFragments("more_categories");
            }
        });
        
        ViewUtils.IfDataExistsHideProgressBar(recipes.size(), WeeklyHottestProgress);
        ViewUtils.IfDataExistsHideProgressBar(categories.size(), CategoriesProgress);
        
        SetUpRecyclers();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        mRecipeAdapter.getRecipeHolderMap().clear();
        mCategoryAdapter.getCategoryHolderMap().clear();
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
        Query allWeeklyRecipesQuery = reference.orderByChild("timestamp").startAt(ParserUtils.getLastWeekTimestamp());
        allWeeklyRecipesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
    
                recipes.clear();
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    recipe.setId(snapshot.getKey());
                    recipes.add(recipe);
                }
    
                // Sort Recipe by Likes using Comparator (reverse fixes order)
                Collections.sort(recipes, new SortByLikesComparator());
                Collections.reverse(recipes);
                
                // Keep first 8 Recipes only
                try {
                    recipes = (ArrayList<Recipe>) recipes.subList(0, 8);
                } catch (IndexOutOfBoundsException exception) {
                    Log.i("Home Fragment", "Recipe List Cropping: less than 8 recipes found!");
                }
                
                ViewUtils.IfDataExistsHideProgressBar(recipes.size(), WeeklyHottestProgress);
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
                            try {
                                mRecipeAdapter.getRecipeHolderMap().get(r).cardImage.setImageBitmap(bitmap);
                                mRecipeAdapter.KillProgressBar(mRecipeAdapter.getRecipeHolderMap().get(r).cardProgress);
                            } catch (NullPointerException es) {
                                Log.i("Recipes Recycler", "Cannot bind Recipes");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                            try {
                                mRecipeAdapter.getRecipeHolderMap().get(r).cardImage.setImageBitmap(bitmap);
                                mRecipeAdapter.KillProgressBar(mRecipeAdapter.getRecipeHolderMap().get(r).cardProgress);
                            } catch (NullPointerException ef) {
                                Log.i("Recipes Recycler", "Cannot bind Recipes");
                            }
                            Log.w("Firebase Storage", "Weekly Recipe Images: Couldn't Fetch File: " + e.getMessage() + " " + r.getIcon());
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
    
        firstTwentyCategories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
    
                categories.clear();
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Category category = snapshot.getValue(Category.class);
                    category.setName(snapshot.getKey());
                    
                    categories.add(category);
                }
    
    
                ViewUtils.IfDataExistsHideProgressBar(categories.size(), CategoriesProgress);
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
                            try {
                                mCategoryAdapter.getCategoryHolderMap().get(c).categoryImage.setImageBitmap(bitmap);
                                mCategoryAdapter.KillProgressBar(mCategoryAdapter.getCategoryHolderMap().get(c).categoryProgress);
                            } catch (NullPointerException e) {
                                Log.i("Category Recycler", "Cannot bind Category");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                            c.setPicture(bitmap);
                            try {
                                mCategoryAdapter.getCategoryHolderMap().get(c).categoryImage.setImageBitmap(bitmap);
                                mCategoryAdapter.KillProgressBar(mCategoryAdapter.getCategoryHolderMap().get(c).categoryProgress);
                            } catch (NullPointerException ef) {
                                Log.i("Category Recycler", "Cannot bind Category");
                            }
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
            String value = categories.get(position).getName();
            parent.setFilteredFragmentParameter("category", value, "home");
            parent.alternativeFragments("filtered_by");
        }
    }
}
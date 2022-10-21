package com.mohamed_mosabeh.cookaholics_capstone.origin_fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
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
import com.mohamed_mosabeh.cookaholics_capstone.RecipeStepsActivity;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.ParserUtil;
import com.mohamed_mosabeh.utils.recycler_views.PopularCardRecipesRecyclerViewAdapter;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class HottestFragment extends Fragment {

    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private ArrayList<Recipe> HottestTagRecipes = new ArrayList<>();
    private Recipe HottestTagRecipe;
    private TextView HT_Name;
    private TextView HT_Text;
    private MaterialCardView HT_CardView;
    private ImageView HT_ImageView;
    private ProgressBar HT_ImageProgress;
    private ProgressBar HT_ProgressBar;
    private String HT_String;
    private Bitmap HT_Image;

    private ArrayList<Recipe> HottestRecipes = new ArrayList<>();
    private RecyclerView HottestRecipesRecycler;
    private RecyclerView.Adapter HottestRecipesAdapter;
    private ProgressBar HottestRecipesProgressBar;

    private Recipe WeekHottestRecipe;
    private TextView WHR_Label;
    private TextView WHR_Name;
    private TextView WHR_Text;
    private TextView WHR_Tags;
    private MaterialCardView WHR_CardView;
    private ImageView WHR_ImageView;
    private ProgressBar WHR_ImageProgress;
    private ProgressBar WHR_ProgressBar;
    private DataSnapshot WHR_DataSnapshot;
    private Bitmap WHR_Image;

    public HottestFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hottest, container, false);

        SetUpViews(view);

        return view;
    }

    private void SetUpViews(View parent) {
        HT_Name = parent.findViewById(R.id.wideCard2_name);
        HT_Text = parent.findViewById(R.id.wideCard2_text);
        HT_ImageView = parent.findViewById(R.id.imageView_wideCard2);
        if (HT_Image != null) {
            HT_ImageView.setImageBitmap(HT_Image);
        }
        HT_CardView = parent.findViewById(R.id.wideCard2);
        HT_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HottestTagRecipe != null) {
                    Intent intent = new Intent(getActivity(), RecipeStepsActivity.class);
                    intent.putExtra("recipe_id", HottestTagRecipe.getId());
                    startActivity(intent);
                }
            }
        });
        HT_ProgressBar = parent.findViewById(R.id.wideCard2_progressBar);
        HT_ImageProgress = parent.findViewById(R.id.progressBar_imageView);
        WHR_Label = parent.findViewById(R.id.rec_lblRecipe);
        WHR_Tags = parent.findViewById(R.id.wideCard_tags);
        WHR_Name = parent.findViewById(R.id.wideCard_name);
        WHR_Text = parent.findViewById(R.id.wideCard_text);
        WHR_ImageView = parent.findViewById(R.id.imageView_wideCard);
        if (WHR_Image != null) {
            WHR_ImageView.setImageBitmap(WHR_Image);
        }
        WHR_CardView = parent.findViewById(R.id.wideCard);
        WHR_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WeekHottestRecipe != null) {
                    Intent intent = new Intent(getActivity(), RecipeStepsActivity.class);
                    intent.putExtra("recipe_id", WeekHottestRecipe.getId());
                    startActivity(intent);
                }
            }
        });
        WHR_ProgressBar = parent.findViewById(R.id.wideCard_progressBar);
        WHR_ImageProgress = parent.findViewById(R.id.imageView_progressBar);
        HottestRecipesRecycler = parent.findViewById(R.id.rec_HottestRecipesRecycler);
        HottestRecipesProgressBar = parent.findViewById(R.id.rec_HottestRecipesProgress);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (database == null) {
            WHR_CardView.setVisibility(View.GONE);
            HT_CardView.setVisibility(View.GONE);
            database = FirebaseDatabase.getInstance(getString(R.string.asia_database));
            storage = FirebaseStorage.getInstance(getString(R.string.firebase_storage));
            getData();
        } else {
            HT_ProgressBar.setVisibility(View.GONE);
            HT_CardView.setVisibility(View.VISIBLE);
            WHR_ProgressBar.setVisibility(View.GONE);
            WHR_CardView.setVisibility(View.VISIBLE);
            SetUpRecyclers();
        }
    }

    private void SetUpRecyclers() {
        SetUpWideCard(WeekHottestRecipe);
        SetUpWideCard2(HottestTagRecipe);
        HottestRecipesRecyclerSetUp(HottestRecipes);
    }

    private void HottestRecipesRecyclerSetUp(ArrayList<Recipe> hottest_recipes) {
        if (getActivity() != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            HottestRecipesRecycler.setLayoutManager(layoutManager);
            HottestRecipesAdapter = new PopularCardRecipesRecyclerViewAdapter(hottest_recipes, storage);
            HottestRecipesRecycler.setAdapter(HottestRecipesAdapter);
            HottestRecipesProgressBar.setVisibility(View.GONE);
        }
    }

    private void getData() {
        getHottestRecipesData();
        getWeekHottestRecipeData();
        getHottestTagData();
    }

    private void getHottestRecipesData() {

        DatabaseReference reference = database.getReference("recipes");
        Query hottestTenRecipes = reference.orderByChild("likes").limitToLast(10);

        hottestTenRecipes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HottestRecipes.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    recipe.setId(snapshot.getKey());
                    HottestRecipes.add(recipe);
                }

                HottestRecipesRecyclerSetUp(HottestRecipes);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }

    private void getHottestTagData() {

        DatabaseReference reference = database.getReference("tags");
        Query hottestTag = reference.orderByChild("hits").limitToLast(1);

        hottestTag.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HottestTagRecipes.clear();
                DataSnapshot hottestTagSnapshot = dataSnapshot;
                DatabaseReference reference2 = database.getReference("recipes");
                Query hottestRecipes = reference2.orderByChild("likes");
                hottestRecipes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        ArrayList<Recipe> MostLikedRecipes = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Recipe recipe = snapshot.getValue(Recipe.class);
                            recipe.setId(snapshot.getKey());
                            MostLikedRecipes.add(recipe);
                        }

                        for (int i = 0; i < MostLikedRecipes.size(); i++) {
                            Recipe recipe = MostLikedRecipes.get(i);
                            ArrayList<String> tags = recipe.getTags();
                            if (tags != null && !tags.isEmpty())
                                for (int j = 0; j < tags.size(); j++) {
                                    System.out.println(tags.get(j));
                                    HT_String = hottestTagSnapshot.getValue().toString();
                                    HT_String = HT_String.substring(1, HT_String.indexOf("="));
                                    if (tags.get(j).equals(HT_String)) {
                                        HottestTagRecipes.add(recipe);
                                    }
                                }
                        }
                        if (HottestTagRecipes.isEmpty()) {
                            int last = MostLikedRecipes.size() - 1;
                            HottestTagRecipe = MostLikedRecipes.get(last);
                        } else {
                            int last = HottestTagRecipes.size() - 1;
                            HottestTagRecipe = HottestTagRecipes.get(last);
                        }
                        HT_ProgressBar.setVisibility(View.GONE);
                        HT_CardView.setVisibility(View.VISIBLE);
                        SetUpWideCard2(HottestTagRecipe);
                        fetchHottestTagImage();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w("W", "Failed to read value.", error.toException());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }

    private void getWeekHottestRecipeData() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        cal.add(Calendar.DAY_OF_WEEK, -7);

        DatabaseReference reference = database.getReference("recipes");
        Query weekRecipes = reference.orderByChild("timestamp").startAt(cal.getTimeInMillis());

        weekRecipes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                WHR_DataSnapshot = dataSnapshot;
                if (WHR_DataSnapshot.exists()) {
                    ArrayList<Recipe> week_recipes = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Recipe recipe = snapshot.getValue(Recipe.class);
                        recipe.setId(snapshot.getKey());
                        week_recipes.add(recipe);
                    }

                    Collections.sort(week_recipes, new SortByLikes());

                    int last = week_recipes.size() - 1;

                    WeekHottestRecipe = week_recipes.get(last);

                    WHR_ProgressBar.setVisibility(View.GONE);
                    WHR_CardView.setVisibility(View.VISIBLE);
                    SetUpWideCard(WeekHottestRecipe);
                    fetchWeekHottestRecipeImage();
                } else {
                    Query hottestRecipe = reference.orderByChild("likes").limitToLast(1);
                    hottestRecipe.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Recipe recipe = new Recipe();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                recipe = snapshot.getValue(Recipe.class);
                                recipe.setId(snapshot.getKey());
                            }

                            WeekHottestRecipe = recipe;

                            WHR_Label.setText("Most Liked Recipe");
                            WHR_ProgressBar.setVisibility(View.GONE);
                            WHR_CardView.setVisibility(View.VISIBLE);
                            SetUpWideCard(WeekHottestRecipe);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Failed to read value
                            Log.w("W", "Failed to read value.", error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }

    private void SetUpWideCard(Recipe recipe) {
        if (recipe != null) {
            if (!WHR_DataSnapshot.exists()) {
                WHR_Label.setText("Most Liked Recipe");
            } else {
                WHR_Label.setText("Recipe of the Week");
            }
            WHR_Tags.setText(ParserUtil.parseTags(recipe.getTags()));
            WHR_Text.setText("This " + recipe.getCuisine() + " " + recipe.getCategory() + " recipe is liked by many!");
            WHR_Name.setText(recipe.getName());
            if (recipe.getIcon().equals("no-image")) {
                WHR_ImageView.setImageResource(R.drawable.placeholder);
                WHR_ImageProgress.setVisibility(View.GONE);
            } else if (WHR_Image != null) {
                WHR_ImageView.setImageBitmap(WHR_Image);
                WHR_ImageProgress.setVisibility(View.GONE);
            }
        }
    }

    private void SetUpWideCard2(Recipe recipe) {
        if (recipe != null) {
            HT_Name.setText("#" + HT_String);
            if (!HottestTagRecipes.isEmpty())
                HT_Text.setText(recipe.getName() + " is a popular " + recipe.getCuisine() + " " + recipe.getCategory() + " recipe using this tag!");
            else
                HT_Text.setText("Something went wrong... Existing recipes don't have this tag.");
            if (recipe.getIcon().equals("no-image")) {
                HT_ImageView.setImageResource(R.drawable.placeholder);
                HT_ImageProgress.setVisibility(View.GONE);
            } else if (HT_Image != null) {
                HT_ImageView.setImageBitmap(HT_Image);
                HT_ImageProgress.setVisibility(View.GONE);
            }
        }
    }

    private void fetchHottestTagImage() {
        try {
            final File tempfile = File.createTempFile(HottestTagRecipe.getId() + "_icon", "png");
            final StorageReference storageRef = storage.getReference().child(HottestTagRecipe.getIcon());
            storageRef.getFile(tempfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    HT_Image = BitmapFactory.decodeFile(tempfile.getAbsolutePath());
                    try {
                        HT_ImageView.setImageBitmap(HT_Image);
                        HT_ImageProgress.setVisibility(View.GONE);
                    } catch (NullPointerException npe) {
                        Log.w("HottestTagImage", npe.getMessage());
                    } catch (Exception e) {
                        Log.w("HottestTagImage", e.getMessage());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    HT_Image = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                    Log.w("Firebase Storage", "Couldn't Fetch File: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            Log.w("File Download Issue", e.getMessage());
        } catch (Exception e) {
            Log.w("File Download Issue", e.getMessage());
        }
    }

    private void fetchWeekHottestRecipeImage() {
        try {
            final File tempfile = File.createTempFile(WeekHottestRecipe.getId() + "_icon", "png");
            final StorageReference storageRef = storage.getReference().child(WeekHottestRecipe.getIcon());
            storageRef.getFile(tempfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    WHR_Image = BitmapFactory.decodeFile(tempfile.getAbsolutePath());
                    try {
                        WHR_ImageView.setImageBitmap(WHR_Image);
                        WHR_ImageProgress.setVisibility(View.GONE);
                    } catch (NullPointerException npe) {
                        Log.w("WeekHottestRecipeImage", npe.getMessage());
                    } catch (Exception e) {
                        Log.w("WeekHottestRecipeImage", e.getMessage());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    WHR_Image = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                    Log.w("Firebase Storage", "Couldn't Fetch File: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            Log.w("File Download Issue", e.getMessage());
        } catch (Exception e) {
            Log.w("File Download Issue", e.getMessage());
        }
    }

}

class SortByLikes implements Comparator<Recipe> {
    // Method
    // Sorting in ascending order
    public int compare(Recipe a, Recipe b) {
        return a.getLikes() - b.getLikes();
    }
}
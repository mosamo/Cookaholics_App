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
import com.mohamed_mosabeh.data_objects.Recipe;
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

    private ArrayList<Recipe> hottest_recipes = new ArrayList<>();
    private RecyclerView HottestRecipesRecycler;
    private RecyclerView.Adapter HottestRecipesAdapter;

    private ProgressBar HottestRecipesProgressBar;

    private Recipe WeekHottestRecipe;
    private TextView WHR_Tags;
    private TextView WHR_Name;
    private TextView WHR_Text;
    private ImageView WHR_ImageView;
    private MaterialCardView WHR_CardView;
    private ProgressBar WHR_ImageProgress;
    private ProgressBar WHR_ProgressBar;

    public HottestFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hottest, container, false);

        SetUpViews(view);

        return view;
    }

    private void SetUpViews(View parent) {
        WHR_Tags = parent.findViewById(R.id.wideCard_tags);
        WHR_Name = parent.findViewById(R.id.wideCard_name);
        WHR_Text = parent.findViewById(R.id.wideCard_text);
        WHR_ImageView = parent.findViewById(R.id.imageView_wideCard);
        WHR_CardView = parent.findViewById(R.id.wideCard);
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
            database = FirebaseDatabase.getInstance(getString(R.string.asia_database));
            storage = FirebaseStorage.getInstance(getString(R.string.firebase_storage));
            getData();
        } else {
            WHR_ProgressBar.setVisibility(View.GONE);
            WHR_CardView.setVisibility(View.VISIBLE);
            SetUpRecyclers();
        }
    }

    private void SetUpRecyclers() {
        SetUpWideCard(WeekHottestRecipe);
        HottestRecipesRecyclerSetUp(hottest_recipes);
    }

    private void HottestRecipesRecyclerSetUp(ArrayList<Recipe> hottest_recipes) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        HottestRecipesRecycler.setLayoutManager(layoutManager);
        HottestRecipesAdapter = new PopularCardRecipesRecyclerViewAdapter(hottest_recipes, storage);
        HottestRecipesRecycler.setAdapter(HottestRecipesAdapter);
        HottestRecipesProgressBar.setVisibility(View.GONE);
    }

    private void getData() {
        getHottestRecipesData();
        getWeekHottestRecipeData();
    }

    private void getHottestRecipesData() {

        DatabaseReference reference = database.getReference("recipes");
        Query hottestTenRecipes = reference.orderByChild("likes").limitToLast(10);

        hottestTenRecipes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                hottest_recipes.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    recipe.setId(snapshot.getKey());
                    hottest_recipes.add(recipe);
                }

                HottestRecipesRecyclerSetUp(hottest_recipes);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }

    private void getWeekHottestRecipeData()
    {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        System.out.println(timestamp);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        cal.add(Calendar.DAY_OF_WEEK, -7);

        DatabaseReference reference = database.getReference("recipes");

        Query weekRecipes = reference.orderByChild("timestamp").startAt(cal.getTimeInMillis());

        weekRecipes.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            ArrayList<Recipe> week_recipes = new ArrayList<>();

            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
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

        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w("W", "Failed to read value.", error.toException());
        }
    });
    }

    private void SetUpWideCard(Recipe recipe) {
        WHR_Tags.setText(recipe.getTagsString());
        WHR_Text.setText("This " + recipe.getCuisine() + " " + recipe.getCategory() + " recipe is liked by many!");
        WHR_Name.setText(recipe.getName());

        if (recipe.getIcon().equals("no-image")) {
            try {
                final File tempfile = File.createTempFile(recipe.getId()+"_icon", "png");
                final StorageReference storageRef = storage.getReference().child(recipe.getIcon());
                storageRef.getFile(tempfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(tempfile.getAbsolutePath());
                        WHR_ImageProgress.setVisibility(View.GONE);
                        WHR_ImageView.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase Storage", "Couldn't Fetch File: " + e.getMessage());

                        // Kill Progress
                        WHR_ImageProgress.setVisibility(View.GONE);
                        WHR_ImageView.setImageResource(R.drawable.placeholder);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            WHR_ImageProgress.setVisibility(View.GONE);
            WHR_ImageView.setImageResource(R.drawable.placeholder);
        }
    }

}

class SortByLikes implements Comparator<Recipe> {
    // Method
    // Sorting in ascending order
    public int compare(Recipe a, Recipe b)
    {
        return a.getLikes() - b.getLikes();
    }
}
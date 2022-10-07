package com.mohamed_mosabeh.cookaholics_capstone.origin_fragments;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.recycler_views.PopularCardRecipesRecyclerViewAdapter;

import java.util.ArrayList;

public class HottestFragment extends Fragment {

    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private ArrayList<Recipe> hottest_recipes = new ArrayList<>();

    private RecyclerView HottestRecipesRecycler;

    private RecyclerView.Adapter HottestRecipesAdapter;

    private ProgressBar TagProgressBar;
    private ProgressBar RecipeProgressBar;
    private ProgressBar HottestRecipesProgressBar;

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

        RecipeProgressBar = parent.findViewById(R.id.rec_RecipeProgress);

        TagProgressBar = parent.findViewById(R.id.rec_TagProgress);

        HottestRecipesRecycler = parent.findViewById(R.id.rec_HottestRecipesRecycler);
        HottestRecipesProgressBar = parent.findViewById(R.id.rec_HottestRecipesProgress);

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
}
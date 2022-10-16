package com.mohamed_mosabeh.cookaholics_capstone.more_fragments.filtered;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohamed_mosabeh.cookaholics_capstone.OriginActivity;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.TestActivity;
import com.mohamed_mosabeh.cookaholics_capstone.more_fragments.MoreRecipesBaseInterface;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.ViewUtils;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerRecipeClickInterface;
import com.mohamed_mosabeh.utils.recycler_views.CompactRecipesRecyclerViewAdapter;

import java.util.ArrayList;

public class FilteredByTagFragment extends Fragment implements MoreRecipesBaseInterface, RecyclerRecipeClickInterface {
    
    private OriginActivity parent;
    private FirebaseDatabase database;
    
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private RecyclerView recipeRecycler;
    private RecyclerView.Adapter recipeAdapter = new CompactRecipesRecyclerViewAdapter(recipes, this, "Tag Found");
    
    private String mWantedTag;
    private TextView loadingTextview;
    
    public void setTagSearch(String tag) {
        mWantedTag = tag;
    }
    
    public FilteredByTagFragment() {
        // Required empty public constructor
    }
    
    public FilteredByTagFragment(OriginActivity parent, FirebaseDatabase database) {
        this.parent = parent;
        this.database = database;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_filtered_by_tag, container, false);
        
        SetupViews(view);
        getRuntimeData();
        
        return view;
    }
    
    private void getRuntimeData() {
        DatabaseReference reference = database.getReference("recipes");
        Query mHundredRecipes = reference.limitToLast(100);
        mHundredRecipes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                recipes.clear();
            
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe.getTags().contains(mWantedTag))
                        recipes.add(recipe);
                }
    
                if (loadingTextview != null) {
                    if (recipes.size() == 0) {
                        loadingTextview.setText("No Recipes Found");
                    }else {
                        loadingTextview.setText("");
            
                    }
                }
    
                RecyclerSetup();
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
                if (loadingTextview != null) {
                    if (recipes.size() == 0) {
                        loadingTextview.setText("No Recipes Found");
                    }else {
                        loadingTextview.setText("");
            
                    }
                }
            }
        });
    }
    
    @Override
    public void getData() {
    
    }
    
    @Override
    public void SetupViews(View view) {
        recipeRecycler = view.findViewById(R.id.ffbt_Recycler);
        Button btnBack = view.findViewById(R.id.ffbt_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.alternativeFragments("recipes");
            }
        });
        loadingTextview = view.findViewById(R.id.ffbt_resultTextView);
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
    
                if (loadingTextview != null) {
                    if (recipes.size() > 1) {
                        loadingTextview.setText("");
                    }
                }
            
            } catch (Exception e) {
                Log.w("Recycler Exception", e.getMessage());
            }
        }
    }
    
    @Override
    public void onItemRecipeClick(int position) {
    
    }
}
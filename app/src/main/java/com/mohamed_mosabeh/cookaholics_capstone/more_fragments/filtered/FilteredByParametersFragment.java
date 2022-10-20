package com.mohamed_mosabeh.cookaholics_capstone.more_fragments.filtered;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.mohamed_mosabeh.cookaholics_capstone.more_fragments.MoreRecipesBaseInterface;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerRecipeClickInterface;
import com.mohamed_mosabeh.utils.recycler_views.CompactRecipesRecyclerViewAdapter;

import java.util.ArrayList;

public class FilteredByParametersFragment extends Fragment implements RecyclerRecipeClickInterface, MoreRecipesBaseInterface {
    
    private OriginActivity parent;
    private FirebaseDatabase database;
    
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private RecyclerView recipesRecycler;
    private String qParameter_type = "?";
    private String qParameter_value = "?";
    private String return_to_fragment;
    private RecyclerView.Adapter adapter = new CompactRecipesRecyclerViewAdapter(recipes, this, "Filtered Recipe");
    
    private TextView loadingTextview;
    private TextView searchLabel;
    
    public FilteredByParametersFragment() {
        // Required empty public constructor
    }
    
    public FilteredByParametersFragment(OriginActivity parent, FirebaseDatabase database) {
        this.parent = parent;
        this.database = database;
    }
    
    public void setQueryParameters(String param_type, String param_value, String returnsTo) {
        qParameter_type = param_type;
        qParameter_value = param_value;
        return_to_fragment = returnsTo;
        getRuntimeData();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filtered_by_parameters, container, false);
        
        SetupViews(view);
        RecyclerSetup();
        
        return view;
    }
    
    private void getRuntimeData() {
        recipes.clear();
        
        DatabaseReference reference = database.getReference("recipes");
        Query commentsQuery = reference.orderByChild(qParameter_type).equalTo(qParameter_value).limitToFirst(12);
    
        commentsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Recipe r = snapshot.getValue(Recipe.class);
                    recipes.add(r);
                    r.setId(snapshot.getKey());
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
                Log.w("W", "Filtered By Fragment", error.toException());
                if (loadingTextview != null) {
                    if (recipes.size() == 0) {
                        loadingTextview.setText("No Recipes Found.\n (Try a different Query)");
                    }else {
                        loadingTextview.setText("");
            
                    }
                }
            }
        });
    }
    
    @Override
    public void getData() {
        // For Filtered Fragments, Data should be acquired in runtime
        // ..So this will not be used
        // I'm keeping it here for consistency/informational Value
    }
    
    @Override
    public void SetupViews(View view) {
        recipesRecycler = view.findViewById(R.id.filteredb_recycler);
        Button btnBack = view.findViewById(R.id.filteredb_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.alternativeFragments(return_to_fragment);
            }
        });
        
        loadingTextview = view.findViewById(R.id.filtered_by_resultTextView);
        searchLabel = view.findViewById(R.id.filteredb_searchLabel);
    }
    
    @Override
    public void RecyclerSetup() {
        if (recipesRecycler != null) {
            try {
                recipesRecycler.setLayoutManager(new GridLayoutManager(parent.getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false));
                recipesRecycler.setAdapter(adapter);
                recipesRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
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
                
                if (searchLabel != null) {
                    searchLabel.setText("Search: Recipes with " + qParameter_type.toLowerCase() + " : " + qParameter_value.toLowerCase());
                }
                
            } catch (Exception e) {
                Log.w("Recycler Exception", e.getMessage());
            }
        }
    }
    
    @Override
    public void onItemRecipeClick(int position) {
        if (recipes.size() > 0) {
            Intent intent = new Intent(getActivity(), RecipeStepsActivity.class);
            intent.putExtra("recipe_id", recipes.get(position).getId());
            startActivity(intent);
        }
    }
}
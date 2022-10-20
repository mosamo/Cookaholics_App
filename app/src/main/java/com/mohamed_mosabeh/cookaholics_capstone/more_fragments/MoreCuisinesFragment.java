package com.mohamed_mosabeh.cookaholics_capstone.more_fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.mohamed_mosabeh.data_objects.Cuisine;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerCuisineClickInterface;
import com.mohamed_mosabeh.utils.recycler_views.CuisineRecipesRecyclerViewAdapter;

import java.util.ArrayList;

public class MoreCuisinesFragment extends Fragment implements RecyclerCuisineClickInterface {
    
    private OriginActivity parent;
    private FirebaseDatabase database;
    
    private ArrayList<Cuisine> cuisines = new ArrayList<>();
    private RecyclerView CuisineRecycler;
    private RecyclerView.Adapter cuisineAdapter = new CuisineRecipesRecyclerViewAdapter(cuisines, this);
    
    public MoreCuisinesFragment() {
        // Required empty public constructor
    }
    
    public MoreCuisinesFragment(OriginActivity parent, FirebaseDatabase database) {
        this.parent = parent;
        this.database = database;
        getData();
    }
    
    public void getData() {
        DatabaseReference reference = database.getReference("cuisines");
        Query firstTwentyFourCuisines = reference.limitToFirst(24);
    
        firstTwentyFourCuisines.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                
                    cuisines.clear();
                
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    
                        Cuisine cuisine = snapshot.getValue(Cuisine.class);
                        cuisine.setName(snapshot.getKey());
                    
                        cuisines.add(cuisine);
                    
                    }
                
                    CuisineRecyclerViewSetup();
                }
            
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("W", "Failed to read value.", error.toException());
                }
            });
    }
    
    private void CuisineRecyclerViewSetup() {
            if (CuisineRecycler != null) {
                try {
                    CuisineRecycler.setLayoutManager(new GridLayoutManager(parent.getApplicationContext(), 3, GridLayoutManager.VERTICAL, false));
                    CuisineRecycler.setAdapter(cuisineAdapter);
                    CuisineRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
                        @Override
                        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                            outRect.set(6, 6, 6, 8);
                        }
                    });
                
                } catch (Exception e) {
                    Log.w("Recycler Exception", e.getMessage());
                }
            }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_more_cuisines, container, false);
        
        SetupViews(view);
        CuisineRecyclerViewSetup();
        
        return view;
    }
    
    private void SetupViews(View view) {
        CuisineRecycler = view.findViewById(R.id.fmcz_Recycler);
        Button btnBack = view.findViewById(R.id.fmcz_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.alternativeFragments("recipes");
            }
        });
    }
    
    @Override
    public void onItemCuisineClick(int position) {
        String value = cuisines.get(position).getName();
        parent.setFilteredFragmentParameter("cuisine", value, "recipes");
        parent.alternativeFragments("filtered_by");
    }
}
package com.mohamed_mosabeh.cookaholics_capstone.more_fragments;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohamed_mosabeh.cookaholics_capstone.OriginActivity;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Category;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerCategoryClickInterface;
import com.mohamed_mosabeh.utils.recycler_views.CategoryMainRecyclerViewAdapter;

import java.util.ArrayList;

public class MoreCategoriesFragment extends Fragment implements RecyclerCategoryClickInterface {
    
    private OriginActivity parent;
    private FirebaseDatabase database;
    
    private ArrayList<Category> categories = new ArrayList<>();
    private RecyclerView CategoryRecycler;
    private RecyclerView.Adapter categoryAdapter = new CategoryMainRecyclerViewAdapter(categories, this);
    
    public MoreCategoriesFragment() {
    }
    
    public MoreCategoriesFragment(OriginActivity parent, FirebaseDatabase database) {
        this.parent = parent;
        this.database = database;
        getData();
    }
    
    public void getData() {
        DatabaseReference reference = database.getReference("categories");
        Query firstTwentyFourCategories = reference.limitToFirst(24);
    
        firstTwentyFourCategories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                categories.clear();
            
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    
                    Category category = snapshot.getValue(Category.class);
                    category.setName(snapshot.getKey());
                
                    categories.add(category);
                    
                }
                
                CategoryRecyclerSetUp();
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
    
    private void CategoryRecyclerSetUp() {
        if (CategoryRecycler != null) {
            try {
                CategoryRecycler.setLayoutManager(new GridLayoutManager(parent.getApplicationContext(), 3, GridLayoutManager.VERTICAL, false));
                CategoryRecycler.setAdapter(categoryAdapter);
                CategoryRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        outRect.set(16, 16, 16, 16);
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
        // Inflate the layout for this fragment
        
        View view = inflater.inflate(R.layout.fragment_more_categories, container, false);
        
        SetupView(view);
        
        CategoryRecyclerSetUp();
        
        return view;
    }
    
    private void SetupView(View view) {
        CategoryRecycler = view.findViewById(R.id.fmc_Recycler);
        
        Button btnBack = view.findViewById(R.id.fmc_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.alternativeFragments("home");
            }
        });
    }
    
    @Override
    public void onItemCategoryClick(int position) {
        String value = categories.get(position).getName();
        parent.setFilteredFragmentParameter("category", value, "home");
        parent.alternativeFragments("filtered_by");
    }
}
package com.mohamed_mosabeh.cookaholics_capstone.origin_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Category;
import com.mohamed_mosabeh.data_objects.Cuisine;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.data_objects.Tag;
import com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter;
import com.mohamed_mosabeh.utils.recycler_views.CuisineRecipesRecyclerViewAdapter;
import com.mohamed_mosabeh.utils.recycler_views.TagsRecipesRecyclerViewAdapter;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class RecipesFragment extends Fragment {
    
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    
    private ArrayList<Cuisine> cuisines = new ArrayList<>();
    private ArrayList<Tag> tags = new ArrayList<>();
    
    private RecyclerView CuisineRecycler;
    private RecyclerView TagRecycler;
    
    private RecyclerView.Adapter CuisineAdapter;
    private RecyclerView.Adapter TagAdapter;
    
    private ProgressBar CusineProgressBar;
    private ProgressBar TagProgressBar;
    
    public RecipesFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    
    
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);
    
        SetUpViews(view);
        
        return view;
    }
    
    private void SetUpViews(View parent) {
        CuisineRecycler = parent.findViewById(R.id.rec_cusineRecycler);
        CusineProgressBar = parent.findViewById(R.id.rec_cusineProgress);
        TagRecycler = parent.findViewById(R.id.rec_tagsRecycler);
        TagProgressBar = parent.findViewById(R.id.rec_tagsProgress);
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
            //killProgressBars();
        }
    }
    
    private void SetUpRecyclers() {
        CuisineRecyclerSetUp(cuisines);
        TagsRecyclerSetUp(tags);
    }
    
    private void getData() {
        getCuisinesData();
        getTagsData();
    }
    
    private void getTagsData() {
        DatabaseReference reference = database.getReference("tags");
        Query topTenTags = reference.orderByChild("hits").limitToLast(10);
    
        topTenTags.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                tags.clear();
            
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Tag tag = snapshot.getValue(Tag.class);
                    tag.setName(snapshot.getKey());
                
                    tags.add(tag);
                }
            
                TagsRecyclerSetUp(tags);
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
    
    private void getCuisinesData() {
        DatabaseReference reference = database.getReference("cuisines");
        Query topSixCuisines = reference.orderByChild("hits").limitToLast(6);
    
        topSixCuisines.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                cuisines.clear();
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Cuisine cuisine = snapshot.getValue(Cuisine.class);
                    cuisine.setName(snapshot.getKey());
                
                    cuisines.add(cuisine);
                }
            
                CuisineRecyclerSetUp(cuisines);
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
    
    private void CuisineRecyclerSetUp(ArrayList<Cuisine> cuisines) {
        CuisineRecycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    
        CuisineAdapter = new CuisineRecipesRecyclerViewAdapter(cuisines);
        CuisineRecycler.setAdapter(CuisineAdapter);
        
        CusineProgressBar.setVisibility(View.GONE);
    }
    
    private void TagsRecyclerSetUp(ArrayList<Tag> tags) {
        TagRecycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        
        TagAdapter = new TagsRecipesRecyclerViewAdapter(tags);
        TagRecycler.setAdapter(TagAdapter);
        
        TagProgressBar.setVisibility(View.GONE);
    }
    
}
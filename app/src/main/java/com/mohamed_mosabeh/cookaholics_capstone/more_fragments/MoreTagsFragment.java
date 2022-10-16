package com.mohamed_mosabeh.cookaholics_capstone.more_fragments;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.mohamed_mosabeh.data_objects.Tag;
import com.mohamed_mosabeh.utils.click_interfaces.RecyclerTagClickInterface;
import com.mohamed_mosabeh.utils.recycler_views.CategoryMainRecyclerViewAdapter;
import com.mohamed_mosabeh.utils.recycler_views.TagsDetailsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Set;

public class MoreTagsFragment extends Fragment implements MoreRecipesBaseInterface, RecyclerTagClickInterface {
    
    private OriginActivity parent;
    private FirebaseDatabase database;
    
    private ArrayList<Tag> tags = new ArrayList<>();
    private RecyclerView tagsRecycler;
    private RecyclerView.Adapter tagAdapter = new TagsDetailsRecyclerViewAdapter(tags, this);
    
    public MoreTagsFragment() {
        // Required empty public constructor
    }
    
    public MoreTagsFragment(OriginActivity parent, FirebaseDatabase database) {
        this.parent = parent;
        this.database = database;
        getData();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_more_tags, container, false);
        
        SetupViews(v);
        
        RecyclerSetup();
        
        return v;
    }
    
    @Override
    public void getData() {
        DatabaseReference reference = database.getReference("tags");
        Query topThirtySixTags = reference.orderByChild("recipes_count").limitToLast(36);
    
        // maybe value event listener is not the best choice
        // but I want stuff to show up
        topThirtySixTags.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                tags.clear();
            
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                
                    Tag t = snapshot.getValue(Tag.class);
                    t.setName(snapshot.getKey());
                
                    tags.add(t);
                
                }
            
                RecyclerSetup();
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to read value.", error.toException());
            }
        });
    }
    
    @Override
    public void SetupViews(View view) {
        tagsRecycler = view.findViewById(R.id.mtf_Recycler);
        Button btnBack = view.findViewById(R.id.mtf_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.alternativeFragments("recipes");
            }
        });
    }
    
    @Override
    public void RecyclerSetup() {
        if (tagsRecycler != null) {
            try {
                tagsRecycler.setLayoutManager(new LinearLayoutManager(parent.getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                tagsRecycler.setAdapter(tagAdapter);
                /*tagsRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        outRect.set(16, 16, 16, 16);
                    }
                });*/
            
            } catch (Exception e) {
                Log.w("Recycler Exception", e.getMessage());
            }
        }
    }
    
    @Override
    public void onItemTagClick(int position) {
    
    }
}
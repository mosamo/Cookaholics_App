package com.mohamed_mosabeh.cookaholics_capstone.recipe_steps_fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.RecipeStepsActivity;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.RecipeInstructionsSwipeAdapter;

public class RecipeStepsContainerFragment extends Fragment {
    
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    
    private TextView txtRecipeName;
    private TextView txtStepsIndicator;
    
    private RecipeStepsActivity parent;
    private ViewPager2 viewPager;
    
    private Recipe recipe;
    private String id;
    private RecipeInstructionsSwipeAdapter adapter;
    
    private Button btnLike;
    private Button btnReport;
    
    private boolean userDidNotLike = false;
    
    private int REPORTS_REQUIRED_THRESHOLD = 25;
    
    public RecipeStepsContainerFragment() {
    }
    
    public RecipeStepsContainerFragment(RecipeStepsActivity parent, FirebaseDatabase database, FirebaseStorage storage, String id) {
        this.parent = parent;
        this.storage = storage;
        this.database = database;
        this.id = id;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    
        View view = inflater.inflate(R.layout.fragment_recipe_steps_container, container, false);
        
        SetupViews(view);
        
        return view;
    }
    
    private void SetupViews(View view) {
    
        txtRecipeName = view.findViewById(R.id.rs_txtRecipeName);
        txtStepsIndicator = view.findViewById(R.id.rs_recipeStepIndicator);
        viewPager = view.findViewById(R.id.recipePager);
        
        Button btnDiscuss = view.findViewById(R.id.rsf_discussButton);
        btnDiscuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.toggleComments(true);
            }
        });
        
        btnLike = view.findViewById(R.id.rs_likeButton);
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeButtonClick();
            }
        });
        btnReport = view.findViewById(R.id.rs_reportButton);
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportButtonClick();
            }
        });
        
        mStatusLiking(1);
        mStatusReporting(1);
        
        mCheckLikesAndReports();
        
        setAvailableData();
        checkReceivedData();
    }
    
    public void mCheckLikesAndReports() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference likesReference = database.getReference("users").child(user.getUid()).child("likes");
            likesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (snap.getValue(String.class).equals(id)) {
                            mStatusLiking(3);
                            return;
                        }
                    }
                    mStatusLiking(2);
                }
    
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
        
                }
            });
            DatabaseReference reportsReference = database.getReference("users").child(user.getUid()).child("reports");
            reportsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (snap.getValue(String.class).equals(id)) {
                            mStatusReporting(3);
                            return;
                        }
                    }
                    mStatusReporting(2);
                }
        
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
            
                }
            });
        }
    }
    
    private void reportButtonClick() {
        Button b = btnReport;
        DatabaseReference reference = database.getReference().child("recipes").child(id).child("reports");
        if (reference != null) {
            if (b.getTag().toString().equals("fresh")) {
                incrementField(reference, "reportMethods");
                modifyUserDocument(id, "reports", true);
                mStatusReporting(1);
            } else if (b.getTag().toString().equals("stale")) {
                decrementField(reference, "reportMethods");
                modifyUserDocument(id, "reports", false);
                mStatusReporting(1);
            }
        }
    }
    
    public void likeButtonClick() {
        Button b = btnLike;
        DatabaseReference reference = database.getReference().child("recipes").child(id).child("likes");
        if (reference != null) {
            if (b.getTag().toString().equals("fresh")) {
                incrementField(reference, "likeMethods");
                modifyUserDocument(id, "likes", true);
                mStatusLiking(1);
            } else if (b.getTag().toString().equals("stale")) {
                decrementField(reference, "likeMethods");
                modifyUserDocument(id, "likes", false);
                mStatusLiking(1);
            }
        }
    }
    
    private void modifyUserDocument(String id, String field, boolean add) {
        // boolean true: adds to field
        // boolean false: removes from field
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            try {
                String user_id = "no-id";
                if (user.getUid() != null)
                    user_id = user.getUid();
    
                DatabaseReference reference = database.getReference().child("users").child(user_id).child(field);
                if (add) {
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int count = 0;
                            for (DataSnapshot s : snapshot.getChildren()) {
                                count++;
                            }
                            reference.child(String.valueOf(count)).setValue(id);
                        }
    
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
        
                        }
                    });
                } else if (add == false) { // remove
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                if (snap.getValue().equals(id)) {
                                    reference.child(snap.getKey()).setValue(null);
                                }
                            }
                            
                            reference.removeEventListener(this);
                        }
    
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
        
                        }
                    });
                }
            } catch (NullPointerException npe) {
                Log.w(field + " attempt", npe.getMessage());
            } catch (Exception e) {
                Log.w(field + " attempt", e.getMessage());
            }
        }
    }
    
    
    public void setAvailableData() {
        adapter = parent.getOrCreateAdapter();
        recipe = parent.getRecipe();
    }
    
    public void checkReceivedData() {
        if (recipe != null) {
            txtRecipeName.setText(recipe.getName());
            txtStepsIndicator.setText("Step 1: " + recipe.getSteps().get(0).getHeader());
            
            if (adapter != null) {
                ViewPagerSetup(recipe, adapter);
            }
        }
    }
    
    private void ViewPagerSetup(Recipe recipe, RecipeInstructionsSwipeAdapter adapter) {
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
    
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                String header = "";
                if (recipe.getSteps().size() > 0)
                    header += " " + recipe.getSteps().get(position).getHeader();
                txtStepsIndicator.setText("Step " + (position + 1) + ":" + header);
            }
    
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }
    
    private void mStatusLiking(int status) {
        Button btn = btnLike;
        switch (status) {
            case (1): // loading
                btn.setEnabled(false);
                btn.setText("Loading");
                btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                btn.setBackgroundColor(getResources().getColor(R.color.comfort_grey));
                break;
            case (2): // turn on
                btn.setEnabled(true);
                btn.setTag("fresh");
                btn.setText("Like");
                userDidNotLike = true;
                btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_icon, 0, 0);
                btn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                break;
            case (3): // turn on undo
                btn.setEnabled(true);
                btn.setTag("stale");
                btn.setText("Undo");
                userDidNotLike = false;
                btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_icon, 0, 0);
                btn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                break;
        }
    }
    
    private void mStatusReporting(int status) {
        Button btn = btnReport;
        switch (status) {
            case (1): // loading
                btn.setEnabled(false);
                btn.setText("Loading");
                btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                btn.setBackgroundColor(getResources().getColor(R.color.comfort_grey));
                break;
            case (2): // turn on
                btn.setEnabled(true);
                btn.setText("report");
                btn.setTag("fresh");
                btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_report_flag, 0, 0);
                btn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                break;
            case (3): // turn on undo
                btn.setEnabled(true);
                btn.setTag("stale");
                btn.setText("Undo");
                btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_report_flag, 0, 0);
                btn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                break;
        }
    }
    
    private void incrementField(DatabaseReference reference, String methodsTypes) {
        reference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long count = mutableData.getValue(Long.class);
                if (count == null) {
                    mutableData.setValue(1);
                }
                else {
                    mutableData.setValue(count + 1);
                }
                
                if (methodsTypes.equals("reportMethods") && count + 1 >= REPORTS_REQUIRED_THRESHOLD) {
                    recipeExodus();
                }
            
                return Transaction.success(mutableData);
            }
        
            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d("RecipeSteps transaction", "result: " +  databaseError);
                if (methodsTypes.equals("likeMethods")) {
                    if (databaseError == null) {
                        mStatusLiking(3);
                    } else {
                        mStatusLiking(2);
                    }
                } else if (methodsTypes.equals("reportMethods")) {
                    if (databaseError == null) {
                        mStatusReporting(3);
                    } else {
                        mStatusReporting(2);
                    }
                }
            }
        });
    }
    
    private void recipeExodus() {
        DatabaseReference reference = database.getReference("recipes").child(id);
        reference.setValue(null);
        parent.finish();
    }
    
    private void decrementField(DatabaseReference reference, String methodsTypes) {
        reference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long likes = mutableData.getValue(Long.class);
                if (likes == null || likes == 0) {
                    mutableData.setValue(0);
                }
                else {
                    mutableData.setValue(likes - 1);
                }
            
                return Transaction.success(mutableData);
            }
        
            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d("RecipeSteps transaction", "result: " +  databaseError);
                if (methodsTypes.equals("likeMethods")) {
                    if (databaseError == null) {
                        mStatusLiking(2);
                    } else {
                        mStatusLiking(3);
                    }
                } else if (methodsTypes.equals("reportMethods")) {
                    if (databaseError == null) {
                        mStatusReporting(2);
                    } else {
                        mStatusReporting(3);
                    }
                }
            }
        });
    }
}
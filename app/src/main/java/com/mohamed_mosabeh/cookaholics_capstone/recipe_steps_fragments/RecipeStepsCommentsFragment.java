package com.mohamed_mosabeh.cookaholics_capstone.recipe_steps_fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.RecipeStepsActivity;
import com.mohamed_mosabeh.data_objects.Comment;
import com.mohamed_mosabeh.data_objects.HighlightedRecipe;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.data_objects.Tag;
import com.mohamed_mosabeh.utils.ParserUtil;
import com.mohamed_mosabeh.utils.ViewUtil;
import com.mohamed_mosabeh.utils.recycler_views.CardRecipesRecyclerViewAdapter;
import com.mohamed_mosabeh.utils.recycler_views.CommentRecyclerViewAdapter;
import com.mohamed_mosabeh.utils.recycler_views.TagsRecipesRecyclerViewAdapter;

import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Text;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RecipeStepsCommentsFragment extends Fragment {
    
    private RecipeStepsActivity parent;
    
    
    private String id;
    
    // Basic Recipe Details
    private TextView recipeName;
    private TextView recipeCategory;
    private TextView recipeCuisine;
    private TextView recipeDescription;
    private TextView recipeServings;
    private TextView recipeDuration;
    private TextView recipeTags;
    private TextView recipeDate;
    private TextView recipeUsername;
    private TextView recipeLikes;
    
    private TextView loadingIndicator;
    
    // Highlighted Recipe Details
    private View hlHorizontal;
    private CardView hlFrame;
    private TextView curatorName;
    private TextView curatorComment;
    private TextView curatorRating;
    
    private FirebaseDatabase database;
    
    private RecyclerView CommentRecycler;
    
    private ArrayList<Comment> comments = new ArrayList<>();
    private RecyclerView.Adapter CommentsAdapter = new CommentRecyclerViewAdapter(comments);
    
    private TextInputLayout textInputLayout;
    private TextView commentPreventerLoading;
    private Button btnSubmit;
    private boolean mPermissionToComment = false;
    private String permissionToCommentStatus = "Loading..";
    
    private boolean DEBUG_CHATROOM_MODE = true;
    
    public RecipeStepsCommentsFragment() {
        // Required empty public constructor
    }
    public RecipeStepsCommentsFragment(RecipeStepsActivity parent, FirebaseDatabase database, String id) {
        this.parent = parent;
        this.database = database;
        this.id = id;
        getData();
    }
    
    private void getData() {
        if (DEBUG_CHATROOM_MODE)
            mPermissionToComment = true;
        getCommentsData();
        getPermissionToComment();
    }
    
    private void getPermissionToComment() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    
            if (user != null && !DEBUG_CHATROOM_MODE) {
                DatabaseReference reference = database.getReference("users").child(user.getUid()).child("comments");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            // if you found that the user already commented to this recipe..
                            if (snapshot.child("recipe_id").getValue(String.class).equals(id)) {
                                reference.removeEventListener(this);
                                mPermissionToComment = false;
                                permissionToCommentStatus = "(Max 1 Comment / per User)";
                                changePermissionToComment();
                                return;
                            }
                        }
    
                        // if the user didn't comment. he can comment
    
                        // do we need to remove event listener on success?
                        mPermissionToComment = true;
                        changePermissionToComment();
                        return;
                    }
            
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                
                    }
                });
            }
        
    }
    
    private void getCommentsData() {
        DatabaseReference reference = database.getReference("comments");
        Query commentsQuery = reference.orderByChild("recipe_id").equalTo(id);
    
        commentsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                comments.clear();
            
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    comments.add(comment);
                }
                
                if (!DEBUG_CHATROOM_MODE)
                    reference.removeEventListener(this);
            
                CommentRecyclerSetup();
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Comment Data.", error.toException());
            }
        });
    }
    
    private void CommentRecyclerSetup() {
        if (CommentRecycler != null) {
            try {
                CommentRecycler.setLayoutManager(new LinearLayoutManager(parent.getApplicationContext(), LinearLayoutManager.VERTICAL, true));
                CommentRecycler.setAdapter(CommentsAdapter);
            } catch (Exception e) {
                Log.w("Recycler Exception", e.getMessage());
            }
        }
        if (loadingIndicator != null) {
            if (comments.size() > 0)
                loadingIndicator.setText("");
            else
                loadingIndicator.setText("No Comments");
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_recipe_steps_comments, container, false);
    
        SetUpViews(view);
        
        return view;
    }
    
    private void SetUpViews(View view) {
        Button btnBackRecipeSteps = view.findViewById(R.id.rcms_backToRecipeSteps);
        btnBackRecipeSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.toggleComments(false);
            }
        });
        
        loadingIndicator = view.findViewById(R.id.rcom_LoadingIndicator);
        
        CommentRecycler = view.findViewById(R.id.rcom_commentRecycler);
        
        // Basic Recipe Views
        recipeName = view.findViewById(R.id.rcsm_rcName);
        recipeCategory = view.findViewById(R.id.rcsm_rcCategory);
        recipeCuisine = view.findViewById(R.id.rcsm_rcCuisine);
        recipeDescription = view.findViewById(R.id.rcsm_rcDescription);
        recipeServings = view.findViewById(R.id.rcsm_rcServing);
        recipeDuration = view.findViewById(R.id.rcsm_rcDuration);
        recipeTags = view.findViewById(R.id.rcsm_rcTags);
        recipeDate = view.findViewById(R.id.rcsm_rcDate);
        recipeUsername = view.findViewById(R.id.rcsm_rcUser);
        recipeLikes = view.findViewById(R.id.rscm_LikesCount);
        
        // Highlighted Recipe Views
        hlHorizontal = view.findViewById(R.id.rcom_curatorHorizontal);
        hlFrame = view.findViewById(R.id.rcom_curatorFrame);
        curatorComment = view.findViewById(R.id.rcom_curatorComment);
        curatorName = view.findViewById(R.id.rcom_curatorName);
        curatorRating = view.findViewById(R.id.rcom_curatorRating);
        
        // comment Views
        textInputLayout = view.findViewById(R.id.rcom_textInputComment);
        
        commentPreventerLoading = view.findViewById(R.id.rcom_commentPreventer);
        commentPreventerLoading.setText(permissionToCommentStatus);
        
        btnSubmit = view.findViewById(R.id.rcom_commentSubmit);
        
        
        if (parent.getRecipe() != null)
            setRecipeDetails(parent.getRecipe());
        
        // Submit Button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitComment();
            }
        });
    
        changePermissionToComment();
        CommentRecyclerSetup();
    }
    
    private void submitComment() {
        String str = textInputLayout.getEditText().getText().toString().trim();
        if (!str.isEmpty()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    
            if (user != null) {
                try {
                    String displayName = "Anonymous";
                    if (user.getDisplayName() != null && !user.getDisplayName().isEmpty())
                        displayName = user.getDisplayName();
    
                    String user_id = "no-id";
                    if (user.getUid() != null)
                        user_id = user.getUid();
    
                    DatabaseReference reference = database.getReference().child("comments");
                    DatabaseReference userReference = database.getReference().child("users").child(user.getUid()).child("comments");
                    
    
                    Comment comment = new Comment();
                    comment.setDisplay_name(displayName);
                    comment.setUser_id(user_id);
                    comment.setRecipe_id(parent.getRecipe().getId());
                    comment.setContent(str);
    
                    reference.push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            if (!DEBUG_CHATROOM_MODE) {
                                mPermissionToComment = false;
                                permissionToCommentStatus = "(Max 1 Comment / per User)";
                            }
                            changePermissionToComment();
                            textInputLayout.getEditText().setText("");
                            userReference.runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                    int childCount = (int) currentData.getChildrenCount();
                                    Comment small = new Comment();
                                    small.setContent(str);
                                    small.setRecipe_id(parent.getRecipe().getId());
                                    userReference.child(String.valueOf(childCount)).setValue(small);
                                    return null;
                                }
        
                                @Override
                                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
            
                                }
                            });
                            ViewUtil.getSnackBar(parent, "Comment Submitted!");
                        }
                    });
                    
                    
                } catch (NullPointerException npe) {
                    Log.w("Comment Submission", npe.getMessage());
                } catch (Exception e) {
                    Log.w("Comment Submission", e.getMessage());
                }
            }
        }
    }
    
    public void changePermissionToComment() {
        try {
            
            if (mPermissionToComment) {
                btnSubmit.setVisibility(View.VISIBLE);
                textInputLayout.setVisibility(View.VISIBLE);
                commentPreventerLoading.setVisibility(View.GONE);
                commentPreventerLoading.setText(permissionToCommentStatus);
            } else {
                btnSubmit.setVisibility(View.GONE);
                textInputLayout.setVisibility(View.GONE);
                commentPreventerLoading.setVisibility(View.VISIBLE);
                commentPreventerLoading.setText(permissionToCommentStatus);
            }
            
        } catch (NullPointerException npe) {
            Log.w("Comments Fragment", npe.getMessage());
        }
    }
    
    public void setRecipeDetails(Recipe r) {
        if (r != null) {
            try {
                // Setting Details
                recipeName.setText(r.getName());
                recipeCategory.setText(r.getCategory());
                recipeCuisine.setText(r.getCuisine());
                recipeDescription.setText(r.getDescription());
                recipeServings.setText(r.getServings() > 1 ? r.getServings() + " Servings" : r.getServings() + " Serving");
                recipeDuration.setText(r.getDuration() > 1 ? r.getDuration() + " Minutes" : r.getDuration() + " Minute");
                recipeTags.setText(ParserUtil.parseTags(r.getTags()));
                recipeUsername.setText(r.getDisplay_name());
                recipeLikes.setText("Likes: " + r.getLikes());
                
                // Timestamp Parser
                Long time = r.getTimestamp();
    
                SimpleDateFormat dataFormat = new SimpleDateFormat("dd MMMM yyyy");
                String timeString = dataFormat.format(new Date(time));
    
                recipeDate.setText(timeString);
                
                if (parent.getHighlightDetails() != null) {
                    HighlightedRecipe hr = parent.getHighlightDetails();
                    hlHorizontal.setVisibility(View.VISIBLE);
                    hlFrame.setVisibility(View.VISIBLE);
                    curatorComment.setText("\"" +hr.getCurator_comment() + "\"");
                    curatorName.setText("Curator " + hr.getCurator_name() + " Says:");
                    curatorRating.setText("Rating: " + hr.getCurator_rating() + "/10");
                }
                
            } catch (NullPointerException npe) {
                Log.i("Recipe Comment Details", "Acquired Data too quickly! setting it when the fragment is initialized");
            }
        }
    }
}
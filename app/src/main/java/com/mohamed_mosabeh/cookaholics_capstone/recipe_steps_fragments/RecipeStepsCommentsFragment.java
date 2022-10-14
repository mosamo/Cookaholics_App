package com.mohamed_mosabeh.cookaholics_capstone.recipe_steps_fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
        getCommentsData();
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
                
                // Todo consider this
                // TODO CONSIDER THIS
                //ViewUtil.IfDataExistsHideProgressBar(tags.size(), TagProgressBar);
            
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
                CommentRecycler.setLayoutManager(new LinearLayoutManager(parent.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                CommentRecycler.setAdapter(CommentsAdapter);
            } catch (Exception e) {
                Log.w("Recycler Exception", e.getMessage());
            }
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
        
        if (parent.getRecipe() != null)
            setRecipeDetails(parent.getRecipe());
    
        CommentRecyclerSetup();
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
package com.mohamed_mosabeh.cookaholics_capstone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments.RecipeComfirmationFragment;
import com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments.RecipeFormFragment;
import com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments.RecipeFormStepFragment;
import com.mohamed_mosabeh.data_objects.Category;
import com.mohamed_mosabeh.data_objects.Cuisine;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.data_objects.RecipeStep;
import com.mohamed_mosabeh.data_objects.Tag;
import com.mohamed_mosabeh.utils.ImageManipulation;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SubmitActivity extends AppCompatActivity {
    
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    DatabaseReference imageReference;
    StorageReference imageStorageRef;
    
    private ImageManipulation controlImages = new ImageManipulation(this);
    private ArrayList<ImageView> listImageViews = new ArrayList<ImageView>();
    
    private RecipeFormFragment recipeFormFragment = new RecipeFormFragment(this);
    
    // this can be used for older devices that use request codes
    // but we are not using deprecated APIs in our app:
    public final int IMAGE_RESULT_STARTING_CODE = 600;
    
    private final int STEPS_LOWER_LIMIT = 2;
    private final int STEPS_UPPER_LIMIT = 10;
    
    private String uploadedRecipeId;
    
    private Button btnDelStep;
    private Button btnAddStep;
    private Button btnSubmit;
    
    private LinearLayout bottomLinear;
    private ArrayList<FrameLayout> listFragments = new ArrayList<>();
    private ArrayList<RecipeFormStepFragment> stepFragments = new ArrayList<>();
    
    private RecipeComfirmationFragment comfirmationUIFragment;
    private int resultsNeededToShowComfirmationUI;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        
        database = FirebaseDatabase.getInstance(getString(R.string.asia_database));
        storage = FirebaseStorage.getInstance(getString(R.string.firebase_storage));
        
        comfirmationUIFragment = new RecipeComfirmationFragment(this);
    
        firebaseAuth = FirebaseAuth.getInstance();
    
        // Views
        bottomLinear = findViewById(R.id.rsub_bottomLL);
        btnSubmit = findViewById(R.id.rsub_SubmitButton);
        btnDelStep = findViewById(R.id.rsub_removeStepButton);
        btnDelStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeStep();
            }
        });
        btnAddStep = findViewById(R.id.rsub_addStepButton);
        btnAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStep();
            }
        });
        
        
    
        // Create Recipe Form Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rsub_fragment, recipeFormFragment).commit();
        
        // Create Recipe Steps Form Fragment
        // Each Recipe should have at least 2 steps (2 Fragments created)
        for (int i = 0; i < STEPS_LOWER_LIMIT; i++)
            addStep();
    
        mControlButtonStatus(btnDelStep, false);
        fetchFormSetupData();
    }
    
    private void fetchFormSetupData() {
        // Acquiring Cuisines Data
        DatabaseReference referenceCuisine = database.getReference("cuisines");
        Query CuisinesQuery = referenceCuisine.limitToFirst(10);
    
        CuisinesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                ArrayList<Cuisine> cuisines = new ArrayList<>();
            
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Cuisine cuisine = snapshot.getValue(Cuisine.class);
                    cuisine.setName(snapshot.getKey());
                
                    cuisines.add(cuisine);
                }
    
                recipeFormFragment.addCuisinesDropdownData(cuisines);
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to get Cuisines.", error.toException());
            }
        });
    
        // Acquiring Categories Data
        DatabaseReference referenceCategories = database.getReference("categories");
        Query CategoryQuery = referenceCategories.limitToFirst(10);
    
        CategoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            
                ArrayList<Category> categories = new ArrayList<>();
            
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Category category = snapshot.getValue(Category.class);
                    category.setName(snapshot.getKey());
    
                    categories.add(category);
                }
            
                recipeFormFragment.addCategoriesDropdownData(categories);
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("W", "Failed to get Categories.", error.toException());
            }
        });
    }
    
    // called by SubmitButton OnClick
    public void mSubmitRecipe(View view) {
        
        btnSubmit.setEnabled(false);
        
        ArrayList<Boolean> validationResults = new ArrayList<>();
        
        validationResults.add(recipeFormFragment.validateMainForm());
        
        for (RecipeFormStepFragment fragment : stepFragments ) {
            validationResults.add(fragment.validateStepForm());
        }
        
        // check if validation returns true
        for (Boolean valid : validationResults) {
            if (!valid) {
                btnSubmit.setEnabled(true);
                return;
            }
        }
        
        mSuccessfulSubmission();
    }
    
    public void mSuccessfulSubmission() {
        // Hide Keyboard
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    
        // Change Views
        final FrameLayout blueArea = findViewById(R.id.rsub_ContainerSteps);
        
        blueArea.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);
        
        // create Recipe Object
        Recipe recipe = recipeFormFragment.getGeneratedRecipe();
        // add Steps to Recipe Object
        ArrayList<RecipeStep> steps = new ArrayList<>();
        for (RecipeFormStepFragment step : stepFragments) {
            steps.add(step.getGeneratedStep());
        }
        recipe.setSteps(steps);
        
        
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_from_right,  // enter
                        R.anim.slide_out_left,    // exit
                        R.anim.slide_from_right,  // pop enter
                        R.anim.slide_out_left)    // pop exit
                .replace(R.id.rsub_fragment, comfirmationUIFragment).commit();

        comfirmationUIFragment.setLoadingText("Uploading Recipe Data..");

        // Submit to database;
        DatabaseReference reference = database.getReference("recipes");
        String id = reference.push().getKey();
        uploadedRecipeId = id;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String displayName = "Anonymous";
        try {
            if (user.getDisplayName() != null && !user.getDisplayName().isEmpty())
                displayName = user.getDisplayName();
        } catch (NullPointerException npe) {
            Log.i("Submit Activity", "No user name");
        }

        String user_id = "no-id";
        try {
            user_id = user.getUid();
        } catch (Exception e) {
            Log.w("Submit Activity", "Couldn't acquire uid");
        }

        recipe.setDisplay_name(displayName);
        recipe.setUser_id(user_id);
        
        reference.child(id).setValue(recipe).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                reference.child(id).child("timestamp").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                
                    @Override
                    public void onSuccess(Void unused) {
                        try {
                            addOrIncrementTags(recipe.getTags());
                        } catch (NullPointerException npe) {
                            Log.i("Npe Submit Activity", "no tags!");
                        }
                        if (listImageViews.get(0).getTag() == null)
                            comfirmationUIFragment.setLoadingText("Uploading Recipe Image..");
                        submitUploadImages();
                    }
                });
    
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finish();
                Log.w("Database Error", e.getMessage());
            }
        });
    }
    
    private void addOrIncrementTags(ArrayList<String> tags) {
            DatabaseReference reference = database.getReference("tags");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        for (String tag : tags) {
                            if (snapshot.hasChild(tag)) {
                                DatabaseReference ref = reference.child(tag).child("recipes_count");
                                ref.runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                        Long recipeCount = currentData.getValue(Long.class);
                                        if (recipeCount == null || recipeCount == 0) {
                                            currentData.setValue(1);
                                        } else {
                                            currentData.setValue(recipeCount + 1);
                                        }
                                        return Transaction.success(currentData);
                                    }
                
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                    
                                    }
                                });
                            } else {
                                Tag t = new Tag();
                                t.setHits(0);
                                t.setRecipes_count(1);
                                t.setTrending(false);
                                reference.child(tag).setValue(t);
                            }
                        }
                    } catch (NullPointerException npe) {
                        Log.i("Npe: Submit tags", "no tags");
                    }
            
                    reference.removeEventListener(this);
                }
        
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
            
                }
            });
    }
    
    private void submitUploadImages() {
    
        // Q: How many files do we need to upload?
        // A: as many Images as tagged
        resultsNeededToShowComfirmationUI = 0;
        
        for (ImageView image : listImageViews) {
            if (image.getTag() != null && image.getTag().toString().equals("filled"))
                resultsNeededToShowComfirmationUI++;
        }
        
        imageReference = database.getReference("recipes").child(uploadedRecipeId).child("icon");
        imageStorageRef = storage.getReference().child("recipes/" + uploadedRecipeId + "/" + "icon");
        
        ImageView image = listImageViews.get(0);
        if (image.getTag() != null && image.getTag().toString().equals("filled")) {
            
            Bitmap bitmap = getBitmapFromImageView(image);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();
    
            Log.i("Upload", "uploading icon to " + imageStorageRef.getPath());
            
            UploadTask uploadTask = imageStorageRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String path = "/recipes/" + uploadedRecipeId + "/icon";
                    imageReference.setValue(path);
                    
                    Log.i("Submitted Image", "Icon");
                    
                    listImageViews.remove(0);
    
                    NotifyResultRecieved(true);
    
    
                    comfirmationUIFragment.setLoadingText("Uploading Images..\n(" + resultsNeededToShowComfirmationUI + " Images Remaining)");
                    simpleSuccessCheck();
                    for (ImageView im : listImageViews) {
                        submitUploadImageStep(im);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Uploading Image Failed", e.getMessage());
                    
                    listImageViews.remove(0);
    
                    NotifyResultRecieved(false);
                    for (ImageView im : listImageViews) {
                        submitUploadImageStep(im);
                    }
                }
            });
        } else {
            // else: main image does not need to be submitted
            // submitting the rest
            listImageViews.remove(0);
            for (ImageView im : listImageViews) {
                submitUploadImageStep(im);
            }
        }
    
        simpleSuccessCheck();
    }
    
    private void submitUploadImageStep(ImageView image) {
        int i = listImageViews.indexOf(image);
        String path = "/recipes/" + uploadedRecipeId + "/" + i;
        
        imageReference = database.getReference("recipes").child(uploadedRecipeId).child("steps").child(String.valueOf(i)).child("image_ref");
        imageStorageRef = storage.getReference().child("recipes/" + uploadedRecipeId + "/" + i);
    
        if (image.getTag() != null && image.getTag().toString().equals("filled")) {
            
            Bitmap bitmap = getBitmapFromImageView(image);
        
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();
        
            Log.i("Upload", "uploading image step " + i + " to " + imageStorageRef.getPath());
    
            imageReference.setValue(path).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.i("Updated Path in Steps", path);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Updated Path Failed", e.getMessage());
                }
            });
            
            UploadTask uploadTask = imageStorageRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    comfirmationUIFragment.setLoadingText("Uploading Images..\n(" + (resultsNeededToShowComfirmationUI-1) + " Images Remaining)");
                    simpleSuccessCheck();
                    NotifyResultRecieved(true);
                    Log.i("Submitted Image", "Step" + i);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    NotifyResultRecieved(false);
                    Log.e("Uploading Image Failed", "Image Step " + i + e.getMessage());
                }
            });
        }
    }
    
    private void addStep() {
        if (listFragments.size() < STEPS_UPPER_LIMIT) {
            // add new FrameLayout to ArrayList
            listFragments.add(createFrame());
            // index of last element
            int last = listFragments.size() - 1;
            // add FrameLayout to LinearLayout
            bottomLinear.addView(listFragments.get(last));
    
            if (listFragments.size() == STEPS_UPPER_LIMIT) {
                mControlButtonStatus(btnAddStep, false);
            } else {
                mControlButtonStatus(btnDelStep, true);
            }
        }
    }
    
    private void removeStep() {
        if (listFragments.size() > STEPS_LOWER_LIMIT) {
            // index of last element
            int lastFragmentIndex = listFragments.size() - 1;
            // remove FrameLayout from LinearLayout
            bottomLinear.removeView(listFragments.get(lastFragmentIndex));
            // remove last Fragment and FrameLayout from ArrayList
            stepFragments.remove(lastFragmentIndex);
            listFragments.remove(lastFragmentIndex);
            
            // remove last image; (this uses a different index due to there being more images than steps (1 more)
            int lastImageIndex = listImageViews.size() - 1;
            listImageViews.remove(lastImageIndex);
    
            if (listFragments.size() == STEPS_LOWER_LIMIT) {
                mControlButtonStatus(btnDelStep, false);
            } else {
                mControlButtonStatus(btnAddStep, true);
            }
        }
    }
    
    private FrameLayout createFrame() {
        FrameLayout frame = new FrameLayout(getApplicationContext());
    
        int generatedId = View.generateViewId();
        
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,16);
        frame.setLayoutParams(params);
        frame.setId(generatedId);
        
        int number = listFragments.size() + 1;
        
        RecipeFormStepFragment rfsf = new RecipeFormStepFragment(this, number);
        stepFragments.add(rfsf);
        
        getSupportFragmentManager().beginTransaction().replace(generatedId, rfsf).commit();
        
        return frame;
    }
    
    private void mControlButtonStatus(Button button, boolean status) {
        if (status) {
            button.setEnabled(true);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.comfort_blue));
        } else {
            button.setEnabled(false);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.comfort_grey));
        }
    }
    
    public void registerNewImage(ImageView image) {
        listImageViews.add(image);
    }
    
    public ImageManipulation getActivityImageManipulator() {
        return controlImages;
    }
    
    private Bitmap getBitmapFromImageView(ImageView imageView) {
        return ((BitmapDrawable)imageView.getDrawable()).getBitmap();
    }
    
    public void NotifyResultRecieved(boolean status) {
        resultsNeededToShowComfirmationUI--;
        Log.i("Upload Finished", "Result:" + (status ? "Success!" : "Fail!") + " Results Needed: " + resultsNeededToShowComfirmationUI);
        
        // if all results have been received. display Success UI;
        if (resultsNeededToShowComfirmationUI == 0) {
            comfirmationUIFragment.DisplaySuccessUI(uploadedRecipeId);
        }
    }
    
    public void simpleSuccessCheck() {
        if (resultsNeededToShowComfirmationUI == 0) {
            comfirmationUIFragment.DisplaySuccessUI(uploadedRecipeId);
            
        }
    }
}
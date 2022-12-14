package com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.SubmitActivity;
import com.mohamed_mosabeh.data_objects.Category;
import com.mohamed_mosabeh.data_objects.Cuisine;
import com.mohamed_mosabeh.data_objects.Recipe;
import com.mohamed_mosabeh.utils.ImageManipulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RecipeFormFragment extends Fragment {
    
    private SubmitActivity parent;
    
    // Regex pattern (for tags): letters and middle hyphens ([comma] letters and middle hyphen)+
    private final Pattern regex = Pattern.compile("^([a-zA-Z]{1,}([\\-]{1}([a-zA-Z]){1,}){0,}){1,}([,]{1}([a-zA-Z]{1,}([\\-]{1}([a-zA-Z]){1,}){0,}){1,}){0,}$");
    private final long DEFAULT_TIME = 978292800000L;
    
    private Spinner SpinnerCuisines;
    private List<String> cuisineSpinnerItems =  new ArrayList<String>();
    private List<Cuisine> cuisinesValues = new ArrayList<>();
    
    private Spinner SpinnerCategories;
    private List<String> categorySpinnerItems =  new ArrayList<String>();
    
    private ImageButton imageButton;
    
    private EditText recipeNameEdit;
    private EditText recipeDurationEdit;
    private EditText recipeServingsEdit;
    private EditText recipeTagsEdit;
    private EditText recipeDescriptionEdit;
    
    private final String DEFAULT_VALUE = "None";
    
    public RecipeFormFragment() {
    }
    
    public RecipeFormFragment(SubmitActivity parent) {
        this.parent = parent;
        cuisineSpinnerItems.add(DEFAULT_VALUE);
        categorySpinnerItems.add(DEFAULT_VALUE);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    
        View view = inflater.inflate(R.layout.fragment_recipe_form, container, false);
    
        SetUpViews(view);
        
        return view;
    }
    
    private void SetUpViews(View view) {
        
        
        recipeNameEdit = view.findViewById(R.id.rsub_recipeName);
        recipeDurationEdit = view.findViewById(R.id.rsub_duration);
        recipeServingsEdit = view.findViewById(R.id.rsub_servings);
        recipeTagsEdit = view.findViewById(R.id.rsub_recipeTags);
        recipeDescriptionEdit = view.findViewById(R.id.rsub_recipeDescription);
        
        // Cuisine Spinner Setup
        SpinnerCuisines = view.findViewById(R.id.rsub_cuisinesSpinner);
        ArrayAdapter<String> cuisinesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cuisineSpinnerItems);
        cuisinesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerCuisines.setAdapter(cuisinesAdapter);
        
        // Category Spinner Setup
        SpinnerCategories = view.findViewById(R.id.rsub_categorySpinner);
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categorySpinnerItems);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerCategories.setAdapter(categoriesAdapter);
    
        
        
        // imageButton
        imageButton = view.findViewById(R.id.rsub_RecipeUpload);
        parent.registerNewImage(imageButton);
    
        // Result launcher
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            try {
                                ImageManipulation im = parent.getActivityImageManipulator();
                                Bitmap bitmap = im.UriToBitmap(uri);
                                Bitmap small = ImageManipulation.scaleSizeSquare(bitmap, ImageManipulation.VERY_SMALL_BITMAP_SIZE, false);
                                imageButton.setTag("filled");
                                imageButton.setImageBitmap(small);
                            } catch (IOException e) {
                                Log.w("ImageManipulator:", e.getMessage());
                            } catch (Exception e) {
                                Log.w("Image Error", e.getMessage());
                            }
                        }
                    }
                });
        
        // Image linking
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parent.getActivityImageManipulator().checkStoragePermission())
                    mGetContent.launch("image/*");
            }
        });
    }
    
    private boolean validateRecipeName() {
        if (recipeNameEdit.getText().toString().trim().isEmpty()) {
            recipeNameEdit.setError("Cannot leave recipe name empty!");
            recipeNameEdit.requestFocus();
            return false;
        } else
            return true;
    }
    
    private boolean validateMoreThanZero(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError("Cannot be Empty!");
            editText.requestFocus();
            return false;
        }
        
        int number = Integer.parseInt(editText.getText().toString());
        if (number < 1) {
            editText.setError("Cannot be less than 1");
            editText.requestFocus();
            return false;
        }
        return true;
    }
    
    private boolean validateTags() {
        String str = recipeTagsEdit.getText().toString();
        
        if (!str.isEmpty()) {
            Matcher matcher = regex.matcher(str);
            
            if (matcher.matches()) {
                return true;
            } else {
                recipeTagsEdit.setError("Tags Improperly formatted!\nExample: tag,tag-b,tag-c");
                recipeTagsEdit.requestFocus();
                return false;
            }
        }
        
        //trim
        return true;
    }
    
    
    
    public void addCuisinesDropdownData(ArrayList<Cuisine> cuisines) {
        cuisineSpinnerItems.clear();
        cuisineSpinnerItems.add(DEFAULT_VALUE);
        for (Cuisine c : cuisines) {
            cuisineSpinnerItems.add(c.getCountry_code());
            cuisinesValues.add(c);
        }
        
        // Notify Spinner Data Changed
        ArrayAdapter<String> cuisinesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cuisineSpinnerItems);
        cuisinesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cuisinesAdapter.notifyDataSetChanged();
        SpinnerCuisines.setAdapter(cuisinesAdapter);
    }
    
    public void addCategoriesDropdownData(ArrayList<Category> categories) {
        categorySpinnerItems.clear();
        categorySpinnerItems.add(DEFAULT_VALUE);
        for (Category c : categories) {
            categorySpinnerItems.add(c.getName());
        }
    
        // Notify Spinner Data Changed
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categorySpinnerItems);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesAdapter.notifyDataSetChanged();
        SpinnerCategories.setAdapter(categoriesAdapter);
    }
    
    public boolean validateMainForm() {
        
        boolean [] validate = {validateRecipeName(), validateMoreThanZero(recipeDurationEdit), validateMoreThanZero(recipeServingsEdit), validateTags()};
        
        // checks if all of them are true
        // if not the method returns false
        for (boolean b : validate)
            if (!b)
                return false;
        // we cannot use the "return x() && y() && z()" notation due to the fact
        // that it stops upon finding first false
        return true;
    }
    
    public Recipe getGeneratedRecipe() {
        Recipe recipe = new Recipe();
        // Required Values
        recipe.setName(recipeNameEdit.getText().toString().trim());
        recipe.setDuration(Integer.parseInt(recipeDurationEdit.getText().toString()));
        recipe.setServings(Integer.parseInt(recipeServingsEdit.getText().toString()));
        recipe.setCategory(SpinnerCategories.getSelectedItem().toString());
        
        recipe.setCuisine(SpinnerCuisines.getSelectedItem().toString());
        // if it is not default. get cuisine name (value) instead of cuisine flag (label)
        if (SpinnerCuisines.getSelectedItem().toString() != DEFAULT_VALUE)
            recipe.setCuisine(cuisinesValues.get(SpinnerCuisines.getSelectedItemPosition() - 1).getName());
        
        // Optional Values
        if (recipeDescriptionEdit.getText().toString().trim().isEmpty())
            recipe.setDescription("- - -");
        else
            recipe.setDescription(recipeDescriptionEdit.getText().toString().trim());
        
        // Tags Saving
        if (!recipeTagsEdit.getText().toString().trim().isEmpty()) {
            String [] tagsArray = recipeTagsEdit.getText().toString().split(",");
            ArrayList <String> tagsList = new ArrayList<>();
            for (String tag : tagsArray) {
                tagsList.add(tag);
            }
            recipe.setTags(tagsList);
        }
        recipe.setIcon("no-image");
        return recipe;
    }
}
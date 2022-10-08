package com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.Category;
import com.mohamed_mosabeh.data_objects.Cuisine;

import java.util.ArrayList;
import java.util.List;


public class RecipeFormFragment extends Fragment {
    
    
    private final long DEFAULT_TIME = 978292800000L;
    
    private Spinner SpinnerCuisines;
    private List<String> cuisineSpinnerItems =  new ArrayList<String>();
    
    private Spinner SpinnerCategories;
    private List<String> categorySpinnerItems =  new ArrayList<String>();
    
    private ImageButton imageButton;
    
    public RecipeFormFragment() {
        cuisineSpinnerItems.add("None");
        categorySpinnerItems.add("None");
    }
    
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecipeFormFragment.
     */
//    // TODO: Rename and change types and number of parameters
//    public static RecipeFormFragment newInstance(String param1, String param2) {
//        RecipeFormFragment fragment = new RecipeFormFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    
        View view = inflater.inflate(R.layout.fragment_recipe_form, container, false);
    
        SetUpViews(view);
        
        return view;
    }
    
    private void SetUpViews(View view) {
        
        imageButton = view.findViewById(R.id.rsub_RecipeUpload);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButton.setImageResource(R.drawable.placeholder);
                Toast.makeText(getActivity(), "Toasty!", Toast.LENGTH_SHORT).show();
            }
        });
        
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
    }
    
    public void addCuisinesDropdownData(ArrayList<Cuisine> cuisines) {
        cuisineSpinnerItems.clear();
        cuisineSpinnerItems.add("None");
        for (Cuisine c : cuisines) {
            cuisineSpinnerItems.add(c.getCountry_code());
        }
        
        // Notify Spinner Data Changed
        ArrayAdapter<String> cuisinesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cuisineSpinnerItems);
        cuisinesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cuisinesAdapter.notifyDataSetChanged();
        SpinnerCuisines.setAdapter(cuisinesAdapter);
    }
    
    public void addCategoriesDropdownData(ArrayList<Category> categories) {
        categorySpinnerItems.clear();
        categorySpinnerItems.add("None");
        for (Category c : categories) {
            categorySpinnerItems.add(c.getName());
        }
    
        // Notify Spinner Data Changed
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categorySpinnerItems);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesAdapter.notifyDataSetChanged();
        SpinnerCategories.setAdapter(categoriesAdapter);
    }
}
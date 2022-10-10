package com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.SubmitActivity;
import com.mohamed_mosabeh.utils.ImageManipulation;

import java.io.IOException;

public class RecipeFormStepFragment extends Fragment {
    
    private int STEP_NUMBER;
    
    private SubmitActivity parent;
    
    private TextView stepNumberLabel;
    
    private ImageView stepImage;
    
    public RecipeFormStepFragment() {
    }
    
    public RecipeFormStepFragment(SubmitActivity parent, int num) {
        this.parent = parent;
        this.STEP_NUMBER = num;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_recipe_form_step, container, false);
        
        SetupView(view);
        
        return view;
    }
    
    private void SetupView(View view) {
        stepNumberLabel = view.findViewById(R.id.rfsf_stepNumber);
        stepNumberLabel.setText("Step " + STEP_NUMBER);
        
        stepImage = view.findViewById(R.id.rfsf_uploadImage);
        parent.registerNewImage(stepImage);
    
        // Result launcher
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        try {
                            ImageManipulation im = parent.getActivityImageManipulator();
                            Bitmap bitmap = im.UriToBitmap(uri);
                            Bitmap small = ImageManipulation.scaleSizeSquare(bitmap, ImageManipulation.MEDIUM_BITMAP_SIZE, false);
                            stepImage.setImageBitmap(small);
                        } catch (IOException e) {
                            Log.w("ImageManipulator:", e.getMessage());
                        }
                    }
                });
        stepImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parent.getActivityImageManipulator().checkStoragePermission())
                    mGetContent.launch("image/*");
            }
        });
    }
}
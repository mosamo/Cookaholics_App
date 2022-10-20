package com.mohamed_mosabeh.cookaholics_capstone.recipe_submission_fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.cookaholics_capstone.SubmitActivity;
import com.mohamed_mosabeh.data_objects.RecipeStep;
import com.mohamed_mosabeh.utils.ImageManipulation;

import java.io.IOException;

public class RecipeFormStepFragment extends Fragment {
    
    private int STEP_NUMBER;
    
    private SubmitActivity parent;
    
    private TextView stepNumberLabel;
    
    private ImageView stepImage;
    
    private EditText stepHeader;
    private EditText stepContent;
    
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
                        if (uri != null) {
                            try {
                                ImageManipulation im = parent.getActivityImageManipulator();
                                Bitmap bitmap = im.UriToBitmap(uri);
                                Bitmap small = ImageManipulation.scaleSizeSquare(bitmap, ImageManipulation.MEDIUM_BITMAP_SIZE, false);
                                stepImage.setTag("filled");
                                stepImage.setImageBitmap(small);
                            } catch (IOException e) {
                                Log.w("ImageManipulator:", e.getMessage());
                            } catch (Exception e) {
                                Log.w("Image Error:", e.getMessage());
                            }
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
        
        // EditTexts
        stepHeader = view.findViewById(R.id.rfsf_header);
        stepContent = view.findViewById(R.id.rfsf_content);
    }
    
    // we can simply reuse the same method instead of having two different ones
    // but I'm leaving them as seperate incase we want to change that in the future
    
    private boolean validateStepHeader() {
        if (stepHeader.getText().toString().trim().isEmpty()) {
            stepHeader.setError("Cannot leave Step Header empty!");
            stepHeader.requestFocus();
            return false;
        } else
            return true;
    }
    
    private boolean validateStepContent() {
        if (stepContent.getText().toString().trim().isEmpty()) {
            stepContent.setError("Cannot leave Instructions empty!");
            stepContent.requestFocus();
            return false;
        } else
            return true;
    }
    
    public boolean validateStepForm() {
        boolean [] validate = {validateStepHeader(), validateStepContent()};
        
        for (boolean b : validate)
            if (!b)
                return false;
        // we cannot use the "return x() && y() && z()" notation due to the fact
        // that it stops upon finding first false
        return true;
    }
    
    public RecipeStep getGeneratedStep() {
        RecipeStep step = new RecipeStep();
        
        step.setHeader(stepHeader.getText().toString().trim());
        step.setContent(stepContent.getText().toString().trim());
        step.setImage_ref("no-image");
        
        return step;
    }
}
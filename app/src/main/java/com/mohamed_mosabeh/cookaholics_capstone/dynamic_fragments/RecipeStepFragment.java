package com.mohamed_mosabeh.cookaholics_capstone.dynamic_fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.mohamed_mosabeh.data_objects.RecipeStep;

import java.io.File;
import java.io.IOException;

public class RecipeStepFragment extends Fragment {
    
    ImageView stepImage;
    TextView stepContent;
    ProgressBar progressBarRSF;
    
    RecipeStep vRecipeStep;
    
    FirebaseStorage storage;
    
    Bitmap bitmap = null;
    
    public RecipeStepFragment(RecipeStep instructions, FirebaseStorage storage) {
        this.vRecipeStep = instructions;
        this.storage = storage;
        downloadStepImage();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_step, container, false);
        
        stepImage = view.findViewById(R.id.rsfImage);
        stepContent = view.findViewById(R.id.rsfContent);
        progressBarRSF = view.findViewById(R.id.rsf_progress);
        
        
        if (bitmap != null) {
            stepImage.setImageBitmap(bitmap);
            ((ViewGroup) progressBarRSF.getParent()).removeView(progressBarRSF);
        }
        
        if (vRecipeStep.getImage_ref().equals("no-image"))
            ((ViewGroup) progressBarRSF.getParent()).removeView(progressBarRSF);
        
        stepContent.setMovementMethod(new ScrollingMovementMethod());
        stepContent.setText(vRecipeStep.getContent());
        
        return view;
    }
    
    public void downloadStepImage() {
        if (vRecipeStep.getImage_ref().equals("no-image"))
            return;
        
        try {
            final File tempfile = File.createTempFile("step", "png");
            final StorageReference storageRef = storage.getReference().child(vRecipeStep.getImage_ref());
            storageRef.getFile(tempfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    bitmap = BitmapFactory.decodeFile(tempfile.getAbsolutePath());
                    
                    if (stepImage != null)
                        stepImage.setImageBitmap(bitmap);
    
                    // Kill Progress
                    if (progressBarRSF != null)
                        ((ViewGroup) progressBarRSF.getParent()).removeView(progressBarRSF);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Firebase Storage", "Couldn't Fetch File: " + e.getMessage());
                
                    // Kill Progress
                    if (progressBarRSF != null)
                        ((ViewGroup) progressBarRSF.getParent()).removeView(progressBarRSF);
                    
                    stepImage.setImageResource(R.drawable.placeholder);
                }
            });
        
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
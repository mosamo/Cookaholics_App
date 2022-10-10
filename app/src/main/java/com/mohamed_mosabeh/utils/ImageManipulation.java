package com.mohamed_mosabeh.utils;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class ImageManipulation {
    
    private AppCompatActivity activity;
    private ActivityResultLauncher<String> requestStoragePermissionLauncher;
    
    public static final int VERY_SMALL_BITMAP_SIZE = 128;
    public static final int SMALL_BITMAP_SIZE = 256;
    public static final int MEDIUM_BITMAP_SIZE = 512;
    
    public ImageManipulation (AppCompatActivity activity) {
        this.activity = activity;
        requestStoragePermissionLauncher =
                activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    final ViewGroup root = (ViewGroup) ((ViewGroup) activity
                            .findViewById(android.R.id.content)).getChildAt(0);
                    if (isGranted) {
                        // Notify the user the App has permission to access storage
                        Snackbar snackbar = Snackbar
                                .make(root, "Storage Permission Granted!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        // Notify the user he didn't grant permission to access storage
                        Snackbar snackbar = Snackbar
                                .make(root, "Storage Permission Denied!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
    }
    
    public boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return false;
        }
    }
    
    public Bitmap UriToBitmap(Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getApplicationContext().getContentResolver(), uri);
        return bitmap;
    }
    
    public static Bitmap scaleSize(Bitmap original, float maxImageSize,boolean filter) {
        float ratio = Math.min((float) maxImageSize / original.getWidth(), (float) maxImageSize / original.getHeight());
        int width = Math.round((float) ratio * original.getWidth());
        int height = Math.round((float) ratio * original.getHeight());
        
        Bitmap newBitmap = Bitmap.createScaledBitmap(original, width, height, filter);
        return newBitmap;
    }
    
    public static Bitmap scaleSizeSquare(Bitmap original, float maxImageSize,boolean filter) {
        float ratio = Math.min((float) maxImageSize / original.getWidth(), (float) maxImageSize / original.getHeight());
        int width = Math.round((float) ratio * original.getWidth());
        int height = Math.round((float) ratio * original.getHeight());
        int crop = Math.max(width, height);
        
        Bitmap newBitmap = Bitmap.createScaledBitmap(original, crop, crop, filter);
        return newBitmap;
    }
}

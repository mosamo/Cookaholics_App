package com.mohamed_mosabeh.cookaholics_capstone;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageUploadActivity extends AppCompatActivity {

    private File directory;
    private ActivityResultLauncher<Intent> launcher;
    final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private final OkHttpClient client = new OkHttpClient();
    private final String IMGUR_CLIENT_ID = "792737373c5c477";
    private final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

    public ImageUploadActivity() {
    }

    void imageChooser() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launcher.launch(i);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        directory = new File(getCacheDir() + "/imgur/");
        if (!directory.exists() && !directory.isDirectory()) {
            directory.mkdirs();
        }
        launcher = registerForActivityResult(
                new ActivityResultContracts
                        .StartActivityForResult(),
                result -> {
                    if (result.getResultCode()
                            == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            Uri uri = result.getData().getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        this.getContentResolver(),
                                        uri);
                                mDatabase.child("temp").child(auth.getUid()).child("base64").setValue(bitmapToString(bitmap));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        imageChooser();

        mDatabase.child("temp").child(auth.getUid()).child("base64").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String base64 = snapshot.getValue().toString();
                    Bitmap bitmap = stringToBitmap(base64);
                    File file = new File(directory, "image.jpeg");
                    bitmapToFile(bitmap, file);
                } else {
                    mDatabase.child("temp").child(auth.getUid()).child("base64").removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "title")
                .addFormDataPart("image", "image",
                        RequestBody.create(FileUtils.getFile(directory + "/image.jpeg"), MEDIA_TYPE_JPEG))
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .url("https://api.imgur.com/3/image")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String json = response.body().string();
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
            String link = JsonPath.read(document, "$.data.link");
            mDatabase.child("users").child(auth.getUid()).child("imageUrl").setValue(link);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap stringToBitmap(String string) {
        byte[] bytes = Base64.decode(string, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public void bitmapToFile(Bitmap bitmap, File file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
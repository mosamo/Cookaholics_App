package com.mohamed_mosabeh.cookaholics_capstone.origin_fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohamed_mosabeh.cookaholics_capstone.R;

public class AccountFragment extends Fragment {

    View inflatedView = null;
    FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.fragment_account, container, false);

        TextView name_text = inflatedView.findViewById(R.id.name);
        TextView email_text = inflatedView.findViewById(R.id.email);
        EditText notes_text = inflatedView.findViewById(R.id.notes);
        ImageButton saveNotes = inflatedView.findViewById(R.id.saveNotes);
        ImageButton profileImage = inflatedView.findViewById(R.id.profileImage);

        name_text.setText(auth.getDisplayName());
        email_text.setText(auth.getEmail());

        mDatabase.child("users").child(auth.getUid()).child("notes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (String.valueOf(task.getResult().getValue()) == "null") {
                    notes_text.setText("");
                } else
                    notes_text.setText(String.valueOf(task.getResult().getValue()));
            }
        });

        mDatabase.child("users").child(auth.getUid()).child("imageUrl").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Glide.with(getContext()).load(String.valueOf(task.getResult().getValue())).centerCrop().placeholder(com.facebook.R.drawable.com_facebook_profile_picture_blank_square).into(profileImage);
            }
        });

        saveNotes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
                String text = notes_text.getText().toString();
                new MaterialAlertDialogBuilder(getContext()).
                        setMessage("Would you like to save your notes?").
                        setPositiveButton("Yes", (dialog, which) ->
                                {
                                    mDatabase.child("users").child(auth.getUid()).child("notes").setValue(text);
                                    Toast.makeText(getContext(), "Your notes have been saved", Toast.LENGTH_SHORT).show();
                                }
                        ).setNegativeButton("No", null).show();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
                EditText input = new EditText(getContext());
                new MaterialAlertDialogBuilder(getContext()).
                        setTitle("Submit an image link to change your profile image").
                        setView(input).
                        setPositiveButton("Submit", (dialog, which) ->
                                {
                                    Glide.with(getContext()).load(input.getText().toString()).centerCrop().error(com.facebook.R.drawable.com_facebook_profile_picture_blank_square).listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            Toast.makeText(getContext(), "Input is not valid... Please try again", Toast.LENGTH_SHORT).show();
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            mDatabase.child("users").child(auth.getUid()).child("imageUrl").setValue(input.getText().toString());
                                            return false;
                                        }
                                    }).into(profileImage);
                                }
                        ).setNegativeButton("Cancel", null).show();
            }
        });

        return inflatedView;
    }
}
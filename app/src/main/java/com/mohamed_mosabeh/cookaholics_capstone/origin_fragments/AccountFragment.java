package com.mohamed_mosabeh.cookaholics_capstone.origin_fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohamed_mosabeh.cookaholics_capstone.R;
import com.squareup.picasso.Picasso;

public class AccountFragment extends Fragment {

    View inflatedView = null;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.fragment_account, container, false);
        ImageView pfp_image = inflatedView.findViewById(R.id.profilePicture);
        TextView uid_text = inflatedView.findViewById(R.id.user);
        TextView name_text = inflatedView.findViewById(R.id.name);
        TextView email_text = inflatedView.findViewById(R.id.email);
        EditText notes_text = inflatedView.findViewById(R.id.notes);
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        Picasso.get().load(auth.getPhotoUrl()).into(pfp_image);
        uid_text.setText(auth.getUid());
        name_text.setText(auth.getDisplayName());
        email_text.setText(auth.getEmail());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(auth.getUid()).child("notes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                notes_text.setText(String.valueOf(task.getResult().getValue()));
            }
        });
        ImageButton saveNotes = inflatedView.findViewById(R.id.saveNotes);
        saveNotes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
                String text = notes_text.getText().toString();
                System.out.println(text);
                mDatabase.child("users").child(auth.getUid()).child("notes").setValue(text);
            }
        });
        return inflatedView;
    }
}
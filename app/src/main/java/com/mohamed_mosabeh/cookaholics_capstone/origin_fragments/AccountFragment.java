package com.mohamed_mosabeh.cookaholics_capstone.origin_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mohamed_mosabeh.cookaholics_capstone.R;

public class AccountFragment extends Fragment {

    View inflatedView = null;

    public AccountFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.fragment_account, container, false);
        TextView uid_text = inflatedView.findViewById(R.id.user);
        TextView email_text = inflatedView.findViewById(R.id.email);
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        uid_text.setText(auth.getUid());
        email_text.setText(auth.getEmail());
        return inflatedView;
    }
}
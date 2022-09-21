package com.mohamed_mosabeh.cookaholics_capstone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohamed_mosabeh.auth.AnonymousAuth;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class OriginActivity extends AppCompatActivity {
    
    BottomNavigationView bottomNavigationView;
    
    private FirebaseAuth mAuth;
    
    private AccountFragment accountFragment = new AccountFragment();
    private SearchFragment searchFragment = new SearchFragment();
    private DefaultFragment defaultFragment = new DefaultFragment();
    private CategoriesFragment categoriesFragment = new CategoriesFragment();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_origin);
        
        // Signing in
        mAuth = FirebaseAuth.getInstance();
        AnonymousAuth.signIn(this, mAuth);
        
        // Navigation View Set up
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        
        // Setting Selected Item before the Listener:
        // ..we don't want it to execute selection just yet!
        bottomNavigationView.setSelectedItemId(R.id.home);
        
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.account:
                    switchFragment(accountFragment);
                    return true;
                case R.id.browse:
                    switchFragment(searchFragment);
                    return true;
                case R.id.recipes:
                    switchFragment(categoriesFragment);
                    return true;
                case R.id.home:
                case R.id.starred:
                    switchFragment(defaultFragment);
                    return true;
            }
            return false;
        });
    }
    
    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_from_right,  // enter
                        R.anim.slide_out_left,    // exit
                        R.anim.slide_from_right,  // pop enter
                        R.anim.slide_out_left)    // pop exit
                .replace(R.id.flFragment, fragment).commit();
    }
}
package com.mohamed_mosabeh.cookaholics_capstone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.mohamed_mosabeh.cookaholics_capstone.origin_fragments.AccountFragment;
import com.mohamed_mosabeh.cookaholics_capstone.origin_fragments.DefaultFragment;
import com.mohamed_mosabeh.cookaholics_capstone.origin_fragments.HomeFragment;
import com.mohamed_mosabeh.cookaholics_capstone.origin_fragments.HottestFragment;
import com.mohamed_mosabeh.cookaholics_capstone.origin_fragments.RecipesFragment;
import com.mohamed_mosabeh.cookaholics_capstone.origin_fragments.SearchFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OriginActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private HomeFragment homeFragment;
    private RecipesFragment recipesFragment;
    private SearchFragment searchFragment = new SearchFragment();
    private AccountFragment accountFragment = new AccountFragment();
    private DefaultFragment defaultFragment = new DefaultFragment();
    private HottestFragment hottestFragment = new HottestFragment();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_origin);

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if (auth == null){
            startActivity(new Intent(OriginActivity.this, PortalActivity.class));
            finish();
        }
        
        FirebaseStorage storage = FirebaseStorage.getInstance(getString(R.string.firebase_storage));
        FirebaseDatabase database = FirebaseDatabase.getInstance(getString(R.string.asia_database));

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
                    if (recipesFragment != null) {
                        switchFragment(recipesFragment);
                    } else {
                        recipesFragment = new RecipesFragment(this, database, storage);
                        switchFragment(recipesFragment);
                    }
                    return true;
                case R.id.home:
                    if (homeFragment != null) {
                        switchFragment(homeFragment);
                    } else {
                        homeFragment = new HomeFragment(this, database, storage);
                        switchFragment(homeFragment);
                    }
                    return true;
                case R.id.hottest:
                    switchFragment(hottestFragment);
                    return true;
            }
            return false;
        });
//
//        // default fragment
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.flFragment, homeFragment).commit();
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

    public void signOut(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser().isAnonymous()) {
            auth.getCurrentUser().delete();
        }
        auth.signOut();
        startActivity(new Intent(OriginActivity.this, PortalActivity.class));
        finish();
    }
}
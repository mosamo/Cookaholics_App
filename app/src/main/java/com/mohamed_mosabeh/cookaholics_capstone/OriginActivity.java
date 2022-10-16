package com.mohamed_mosabeh.cookaholics_capstone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.mohamed_mosabeh.cookaholics_capstone.more_fragments.MoreCategoriesFragment;
import com.mohamed_mosabeh.cookaholics_capstone.more_fragments.MoreCuisinesFragment;
import com.mohamed_mosabeh.cookaholics_capstone.more_fragments.MoreNewestFragment;
import com.mohamed_mosabeh.cookaholics_capstone.more_fragments.MoreTagsFragment;
import com.mohamed_mosabeh.cookaholics_capstone.more_fragments.filtered.FilteredByParametersFragment;
import com.mohamed_mosabeh.cookaholics_capstone.more_fragments.filtered.FilteredByTagFragment;
import com.mohamed_mosabeh.cookaholics_capstone.origin_fragments.AccountFragment;
import com.mohamed_mosabeh.cookaholics_capstone.origin_fragments.HomeFragment;
import com.mohamed_mosabeh.cookaholics_capstone.origin_fragments.HottestFragment;
import com.mohamed_mosabeh.cookaholics_capstone.origin_fragments.RecipesFragment;
import com.mohamed_mosabeh.cookaholics_capstone.origin_fragments.SearchFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OriginActivity extends AppCompatActivity {
    
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private BottomNavigationView bottomNavigationView;

    // Main Fragments
    private HomeFragment homeFragment;
    private RecipesFragment recipesFragment;
    private SearchFragment searchFragment = new SearchFragment();
    private AccountFragment accountFragment = new AccountFragment();
    private HottestFragment hottestFragment = new HottestFragment();
    
    // More Details Fragments
    private MoreCategoriesFragment moreCategories;
    private MoreCuisinesFragment moreCuisines;
    private MoreNewestFragment moreNewest;
    private MoreTagsFragment moreTags;
    private FilteredByParametersFragment filteredByParametersFragment;
    private FilteredByTagFragment filteredByTagFragment;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_origin);

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if (auth == null){
            startActivity(new Intent(OriginActivity.this, PortalActivity.class));
            finish();
        }
        
        storage = FirebaseStorage.getInstance(getString(R.string.firebase_storage));
        database = FirebaseDatabase.getInstance(getString(R.string.asia_database));

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
    
    
    
        /** following fragments fetches data at runtime, so we it won't lock up the app by fetching data immediately
         * therefore we can safely initialize it in advance **/
         filteredByParametersFragment = new FilteredByParametersFragment(this, database);
         filteredByTagFragment = new FilteredByTagFragment(this, database);
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
    
    public void alternativeFragments(String type) {
        switch (type) {
            case "home":
                    switchFragment(homeFragment);
                break;
            case "recipes":
                if (recipesFragment != null) {
                    switchFragment(recipesFragment);
                } else {
                    recipesFragment = new RecipesFragment(this, database, storage);
                    switchFragment(recipesFragment);
                }
                break;
            case "more_newest":
                if (moreNewest != null) {
                    switchFragment(moreNewest);
                } else {
                    moreNewest = new MoreNewestFragment(this, database);
                    switchFragment(moreNewest);
                }
                break;
            case "more_categories":
                if (moreCategories != null) {
                    switchFragment(moreCategories);
                } else {
                    moreCategories = new MoreCategoriesFragment(this, database);
                    switchFragment(moreCategories);
                }
                break;
            case "more_cuisines":
                if (moreCuisines != null) {
                    switchFragment(moreCuisines);
                } else {
                    moreCuisines = new MoreCuisinesFragment(this, database);
                    switchFragment(moreCuisines);
                }
                break;
            case "more_tags":
                if (moreTags != null) {
                    switchFragment(moreTags);
                } else {
                    moreTags = new MoreTagsFragment(this, database);
                    switchFragment(moreTags);
                }
                break;
            case "filtered_by":
                switchFragment(filteredByParametersFragment);
                break;
            case "filtered_by_tag":
                switchFragment(filteredByTagFragment);
                break;
            default:
                break;
        }
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
    
    public void setFilteredFragmentParameter(String type, String value, String returnsTo) {
        filteredByParametersFragment.setQueryParameters(type, value, returnsTo);
    }
    
    public void setFilteredFragmentTag(String tag) {
        filteredByTagFragment.setTagSearch(tag);
    }
}
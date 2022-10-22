package com.mohamed_mosabeh.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohamed_mosabeh.data_objects.Recipe;

import java.util.ArrayList;
import java.util.List;

public class SearchUtils {

    public static List<Recipe> DataCache = new ArrayList<>();

    public static String searchString = "";

    public static void show(Context c, String message) {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    // This method will send a recipe object to a specified activity.
    public static void sendRecipeToActivity(Context c, Recipe recipe,
                                            Class clazz) {
        Intent i = new Intent(c, clazz);
        i.putExtra("recipe_id", recipe.getId());
        c.startActivity(i);
    }

    public static void showProgressBar(ProgressBar pb) {
        pb.setVisibility(View.VISIBLE);
    }

    public static void hideProgressBar(ProgressBar pb) {
        pb.setVisibility(View.GONE);
    }

    public static DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static void search(final AppCompatActivity a, DatabaseReference db,
                              final ProgressBar pb,
                              SearchAdapter adapter, String searchTerm) {
        if (searchTerm != null && searchTerm.length() > 0) {
            char[] letters = searchTerm.toCharArray();
            String firstLetter = String.valueOf(letters[0]);
            String remainingLetters = searchTerm.substring(1);
            searchTerm.equalsIgnoreCase(firstLetter + remainingLetters);
        }

        SearchUtils.showProgressBar(pb);

        Query firebaseSearchQuery = db.child("recipes").orderByChild("name").startAt(searchTerm.toUpperCase())
                .endAt(searchTerm.toLowerCase() + "\uf8ff");

        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataCache.clear();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        // This gets recipe objects to populate array list.
                        Recipe recipe = ds.getValue(Recipe.class);
                        recipe.setId(ds.getKey());
                        if (recipe.getName().toLowerCase().startsWith(searchTerm.toLowerCase())) {
                            DataCache.add(recipe);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    SearchUtils.show(a, "No item found");
                }
                SearchUtils.hideProgressBar(pb);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FIREBASE CRUD", databaseError.getMessage());
                SearchUtils.hideProgressBar(pb);
                SearchUtils.show(a, databaseError.getMessage());
            }
        });
    }

    public static void select(final AppCompatActivity a, DatabaseReference db,
                              final ProgressBar pb,
                              final RecyclerView rv, SearchAdapter adapter) {
        SearchUtils.showProgressBar(pb);

        db.child("recipes").orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataCache.clear();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        // This gets recipe objects to populate array list.
                        Recipe recipe = ds.getValue(Recipe.class);
                        recipe.setId(ds.getKey());
                        DataCache.add(recipe);
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    SearchUtils.show(a, "No more item found");
                }
                SearchUtils.hideProgressBar(pb);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FIREBASE CRUD", databaseError.getMessage());
                SearchUtils.hideProgressBar(pb);
                SearchUtils.show(a, databaseError.getMessage());
            }
        });
    }

}
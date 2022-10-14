package com.mohamed_mosabeh.cookaholics_capstone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.mohamed_mosabeh.data_objects.SearchResultAdapter;
import com.mohamed_mosabeh.data_objects.SearchResultModel;

public class SearchActivity extends AppCompatActivity
{
   private EditText etSearchRecipe;
   MenuItem item;

   private RecyclerView rvSearchResult;
   SearchResultAdapter srAdapter;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_search);

      rvSearchResult = findViewById(R.id.rvSearchResult);
      rvSearchResult.setLayoutManager(new LinearLayoutManager(this));

      FirebaseRecyclerOptions<SearchResultModel> options =
              new FirebaseRecyclerOptions.Builder<SearchResultModel>()
                      .setQuery(FirebaseDatabase.getInstance().getReference().child("recipes"), SearchResultModel.class)
                      .build();

      srAdapter = new SearchResultAdapter(options);
      rvSearchResult.setAdapter(srAdapter);

   }

   @Override
   protected void onStart(){
      super.onStart();
      srAdapter.startListening();
   }

   @Override
   protected void onStop(){
      super.onStop();
      srAdapter.stopListening();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {

      MenuItem item = menu.findItem(R.id.etSearchBarRecipe);

      SearchView searchView = (SearchView) item.getActionView();

      searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
      {
         @Override
         public boolean onQueryTextSubmit(String s)
         {
            processsearch(s);
            return false;
         }

         @Override
         public boolean onQueryTextChange(String s)
         {
            processsearch(s);
            return false;
         }

         private void processsearch(String s)
         {
            FirebaseRecyclerOptions<SearchResultModel> options =
                    new FirebaseRecyclerOptions.Builder<SearchResultModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("recipes").orderByChild("name").startAt(s).endAt(s+"\uf8ff"), SearchResultModel.class)
                            .build();

            srAdapter = new SearchResultAdapter(options);
            srAdapter.startListening();
            rvSearchResult.setAdapter(srAdapter);
         }
      });
      return onCreateOptionsMenu(menu);
   }
}
package com.mohamed_mosabeh.cookaholics_capstone;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mohamed_mosabeh.utils.SearchAdapter;
import com.mohamed_mosabeh.utils.SearchUtils;

public class SearchActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    private SearchView searchView;
    private RecyclerView rv;
    public ProgressBar progressBar;
    private LinearLayoutManager layoutManager;
    SearchAdapter adapter;

    private void initializeViews() {
        progressBar = findViewById(R.id.SearchProgressBar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        rv = findViewById(R.id.SearchRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        adapter = new SearchAdapter(SearchUtils.DataCache);
        rv.setAdapter(adapter);

    }

    private void bindData() {
        SearchUtils.select(this, SearchUtils.getDatabaseReference(), progressBar, rv, adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.search_page_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(true);
        searchView.setQueryHint("Search");
        searchView.setMaxWidth(Integer.MAX_VALUE);
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        EditText et = searchView.findViewById(searchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null));
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        SearchUtils.searchString = query;
        SearchUtils.search(this, SearchUtils.getDatabaseReference(), progressBar, adapter, query);
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.MaterialToolbar);
        toolbar.setTitle("Browse Recipes");
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();
        initializeViews();
        bindData();
    }

    @Override
    public void onBackPressed() {
        if (searchView.hasFocus()) {
            searchView.onActionViewCollapsed();
        } else {
            finish();
        }
    }

}
//end
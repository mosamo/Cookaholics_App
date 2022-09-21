package com.mohamed_mosabeh.cookaholics_capstone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;

public class SwipeActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        
        // Setting up an widget (adapter), where fragments will be inserted
        MyPagerAdapter adapter = new MyPagerAdapter(this);
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
    }
}
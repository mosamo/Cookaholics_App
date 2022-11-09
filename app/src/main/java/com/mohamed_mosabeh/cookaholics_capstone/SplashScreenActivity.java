package com.mohamed_mosabeh.cookaholics_capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.mohamed_mosabeh.utils.ViewUtils;

public class SplashScreenActivity extends AppCompatActivity {
    
    private Button mMainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        
        mMainButton = findViewById(R.id.ssSplashStartButton);
        mMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashScreenActivity.this, PortalActivity.class));
                finish();
            }
        });
        
    }
}
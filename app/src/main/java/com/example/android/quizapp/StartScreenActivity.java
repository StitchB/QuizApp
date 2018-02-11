package com.example.android.quizapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class StartScreenActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set 'activity_start_screen' as a main design file
        setContentView(R.layout.activity_start_screen);

        //Set application context
        Context context = getApplicationContext();

        //Background
        ImageView background = findViewById(R.id.background);
        loadGlideImage(context, R.drawable.bg, background);

        //Logo
        ImageView logo = findViewById(R.id.logo);
        loadGlideImage(context, R.drawable.logo, logo);

        //Continue button
        ImageView buttonContinue = findViewById(R.id.button_continue);
        loadGlideImage(context, R.drawable.selector_continue_button, buttonContinue);

        //Capture continue button clicks
        buttonContinue.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                // Start QuestionsActivity
                Intent myIntent = new Intent(StartScreenActivity.this, QuestionsActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
}

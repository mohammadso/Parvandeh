package com.sobhani.mohammad.parvandeh;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private TextView mainTextView, seconderyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mainTextView = (TextView) findViewById(R.id.textViewSplash);
        seconderyTextView = (TextView) findViewById(R.id.textViewSecondSplash);

        Typeface face = Typeface.createFromAsset(getAssets(),
                "Streetwear.otf");
        mainTextView.setTypeface(face);
        seconderyTextView.setTypeface(face);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);

                finish();
            }
        }, 3000);
    }
}

package com.example.lkama.photosearch;

/**
 * Created by lkama on 2018-11-03.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class Intro extends AppCompatActivity {

    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(r, 1300);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(r);
    }

}

package com.kirillkultinov.saucelabschallenge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * MainActivity is the entry point of the application
 * It can be avoided as Android applications may contain no activities
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // start created service
        startService(new Intent(this, SauceService.class));
    }
}

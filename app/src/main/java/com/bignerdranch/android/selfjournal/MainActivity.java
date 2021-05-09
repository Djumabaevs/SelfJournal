package com.bignerdranch.android.selfjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Button getStartedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getStartedButton = findViewById(R.id.start_button);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        getStartedButton.setOnClickListener(view -> {
            //go to login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        });
    }
}
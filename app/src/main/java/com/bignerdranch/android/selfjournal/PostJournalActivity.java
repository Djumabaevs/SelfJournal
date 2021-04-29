package com.bignerdranch.android.selfjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PostJournalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);




       /* Bundle bundle = getIntent().getExtras(); no longer needed, instead we can use instance og singleton class

        if(bundle != null) {
            String username = bundle.getString("username");
            String userId = bundle.getString("userId");
        }*/
    }
}
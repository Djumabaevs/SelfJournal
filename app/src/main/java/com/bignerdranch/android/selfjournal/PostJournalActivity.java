package com.bignerdranch.android.selfjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bignerdranch.android.selfjournal.databinding.ActivityPostJournalBinding;

public class PostJournalActivity extends AppCompatActivity {
    private ActivityPostJournalBinding binding;

    private String currentUserId;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityPostJournalBinding variable =ActivityPostJournalBinding.inflate(this.getLayoutInflater());
        this.binding = variable;
        super.onCreate(savedInstanceState);
        variable = this.binding;
        setContentView((View)variable.getRoot());







       /* Bundle bundle = getIntent().getExtras(); no longer needed, instead we can use instance og singleton class

        if(bundle != null) {
            String username = bundle.getString("username");
            String userId = bundle.getString("userId");
        }*/
    }
}
package com.bignerdranch.android.selfjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bignerdranch.android.selfjournal.databinding.ActivityPostJournalBinding;
import com.bignerdranch.android.selfjournal.util.JournalApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class PostJournalActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_CODE = 1;
    private ActivityPostJournalBinding binding;

    private String currentUserId;
    private String currentUsername;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Journal");
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityPostJournalBinding variable =ActivityPostJournalBinding.inflate(this.getLayoutInflater());
        this.binding = variable;
        super.onCreate(savedInstanceState);
        variable = this.binding;
        setContentView((View)variable.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.postCameraButton.setOnClickListener(this);
        binding.postSaveJournalButton.setOnClickListener(this);

        if(JournalApi.getInstance() != null) {
            currentUserId = JournalApi.getInstance().getUserId();
            currentUsername = JournalApi.getInstance().getUsername();

            binding.postUsernameTextview.setText(currentUsername);
        }

        authStateListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if(user != null) {

            } else {

            }
        };



       /* Bundle bundle = getIntent().getExtras(); no longer needed, instead we can use instance og singleton class

        if(bundle != null) {
            String username = bundle.getString("username");
            String userId = bundle.getString("userId");
        }*/
    }

    @Override
    public void onClick(View v) {
        switch ((v.getId())) {
            case R.id.post_save_journal_button:

                break;
            case R.id.post_camera_button:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if(data != null) {
                imageUri = data.getData();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
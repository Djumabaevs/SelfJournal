package com.bignerdranch.android.selfjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bignerdranch.android.selfjournal.databinding.ActivityPostJournalBinding;
import com.bignerdranch.android.selfjournal.model.Journal;
import com.bignerdranch.android.selfjournal.util.JournalApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

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
        binding.postProgressBar.setVisibility(View.INVISIBLE);

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
                saveJournal();
                break;
            case R.id.post_camera_button:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;
        }
    }

    private void saveJournal() {
        String title = binding.postTitleEt.getText().toString().trim();
        String thoughts = binding.postDescriptionEt.getText().toString().trim();

        binding.postProgressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri != null) {



            StorageReference filepath = storageReference
                    .child("journal_images")
                    .child("my_image_" + Timestamp.now().getSeconds());
            filepath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl  =uri.toString();
                                Journal journal = new Journal();
                                journal.setTitle(title);
                                journal.setThought(thoughts);
                                journal.setImageUrl(imageUrl);
                                journal.setTimeAdded(new Timestamp(new Date()));
                                journal.setUserName(currentUsername);
                                journal.setUserId(currentUserId);

                                collectionReference.add(journal)
                                        .addOnSuccessListener(documentReference -> {
                                            binding.postProgressBar.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(PostJournalActivity.this,
                                                    JournalListActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {

                                        });
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.postProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        } else {
            binding.postProgressBar.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if(data != null) {
                imageUri = data.getData();
                binding.postImageBackgroundView.setImageURI(imageUri);
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
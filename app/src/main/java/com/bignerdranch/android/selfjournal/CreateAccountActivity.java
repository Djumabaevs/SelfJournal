package com.bignerdranch.android.selfjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {
    private Button loginButton;
    private Button createAcctButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firebase connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private EditText userNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth = FirebaseAuth.getInstance();

        createAcctButton = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.create_account_progress);
        emailEditText = findViewById(R.id.email_account);
        passwordEditText = findViewById(R.id.password_account);
        userNameEditText = findViewById(R.id.username_account);

        authStateListener = firebaseAuth -> {
          currentUser = firebaseAuth.getCurrentUser();

          if(currentUser != null) {

          } else {

          }
        };

        createAcctButton.setOnClickListener(view -> {
            if(!TextUtils.isEmpty(emailEditText.getText().toString()) &&
            !TextUtils.isEmpty(passwordEditText.getText().toString()) &&
            !TextUtils.isEmpty(userNameEditText.getText().toString())) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String username = userNameEditText.getText().toString().trim();
                createUserEmailAccount(email, password, username);
            } else {
                Toast.makeText(CreateAccountActivity.this, "Empty fields are not allowed",
                        Toast.LENGTH_LONG).show();
            }

        });
    }

    private void createUserEmailAccount(String email, String password, String username) {
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)) {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(authResultTask -> {

                        //we take use to addJournalActivity
                        currentUser = firebaseAuth.getCurrentUser();
                        assert currentUser != null;
                        String currentUserId = currentUser.getUid();

                        //create a user map so we could save user obj in collection
                        Map<String, String> userObj = new HashMap<>();
                        userObj.put("userId", currentUserId);
                        userObj.put("username", username);

                        //save to our firestore
                        collectionReference.add(userObj)
                                .addOnSuccessListener(documentReference -> {
                                    documentReference.get()
                                            .addOnCompleteListener(documentSnapshotTask -> {
                                                if(Objects.requireNonNull(documentSnapshotTask.getResult()).exists()) {

                                                }
                                            });
                                })
                                .addOnFailureListener(e -> {

                                });

                    })
                    .addOnFailureListener(e -> {

                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
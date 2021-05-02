package com.bignerdranch.android.selfjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bignerdranch.android.selfjournal.util.JournalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private Button createAcctButton;
    private AutoCompleteTextView emailAddress;
    private EditText password;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference  =db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.email_sign_in_button);
        createAcctButton = findViewById(R.id.create_account_button_login);

        emailAddress = findViewById(R.id.email);
        password = findViewById(R.id.password);

        firebaseAuth = FirebaseAuth.getInstance();

        createAcctButton.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
        });

        loginButton.setOnClickListener(view -> {
            loginEmailPasswordUser(emailAddress.getText().toString().trim(),
                    password.getText().toString().trim());
        });

    }

    private void loginEmailPasswordUser(String email, String password) {

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        assert user != null;
                        String currentUserId = user.getUid();

                        collectionReference
                                .whereEqualTo("userId", currentUserId)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                        @Nullable FirebaseFirestoreException error) {
                                        if(error != null) {}
                                        assert queryDocumentSnapshots != null;
                                        if(!queryDocumentSnapshots.isEmpty()) {
                                         for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {
                                             JournalApi journalApi = JournalApi.getInstance();
                                             journalApi.setUsername(snapshot.getString("username"));
                                             journalApi.setUserId(snapshot.getString("userId"));

                                             //go to ListActivity
                                             startActivity(new Intent(LoginActivity.this,
                                                     PostJournalActivity.class));
                                         }

                                        }
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {

                    });

        } else {
            Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_LONG).show();
        }
    }
}
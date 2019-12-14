package com.example.mayscanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends Activity implements
        View.OnClickListener {
    private FirebaseAuth mAuth;
    EditText emailField, passwordField;
    //TextView mStatusTextView, mDetailTextView;
    Button signUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        signUpButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        //updateUI(null);
    }

    private void createAccount(String email, String password) {
        try {
            Log.d("SIGNUP", "createAccount:" + email);
            if (!validateForm()) {
                return;
            }
            showProgressDialog();
            // [START create_user_with_email]
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("SUCCESS", "createUserWithEmail:success");
                                Toast.makeText(SignUpActivity.this, "Đăng kí thành công, nhưng chưa xác minh email", Toast.LENGTH_LONG).show();
                                //FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("FAILURE", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Đăng kí không thành công.", Toast.LENGTH_LONG).show();
                                //updateUI(null);
                            }
                            // [START_EXCLUDE]
                            hideProgressDialog();
                            // [END_EXCLUDE]
                        }
                    });
            // [END create_user_with_email]
        } catch (Exception e) {
            //mDetailTextView.setText(e.getMessage());
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

//    public void updateUI(FirebaseUser user) {
//        hideProgressDialog();
//        if (user != null) {
//            mStatusTextView.setText("Email user verified:" +
//                    user.getEmail() + user.isEmailVerified());
//            mDetailTextView.setText("Firebase user: " + user.getUid());
//
//            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
//            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
//            //findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);
//
//            //findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
//        } else {
//            mStatusTextView.setText("Sign out");
//            mDetailTextView.setText(null);
//
//            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
//            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
//            //findViewById(R.id.signedInButtons).setVisibility(View.GONE);
//        }
//    }


    public static ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Đang đăng kí");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public static void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.signUpButton) {
            createAccount(emailField.getText().toString(), passwordField.getText().toString());
        }
    }
}

package com.example.mayscanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends Activity implements
        View.OnClickListener {
    private static final int MY_REQUEST_CODE = 100;
    //List<AuthUI.IdpConfig> providers;
    private FirebaseAuth mAuth;
    // Access a Cloud Firestore instance from your Activity
//    FirebaseFirestore db;
    EditText emailField, passwordField;
    TextView mStatusTextView, mDetailTextView;
    Button signInButton, passwordLessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        signInButton = (Button) findViewById(R.id.signInButton);
        passwordLessButton = (Button) findViewById(R.id.passwordLessButton);

        signInButton.setOnClickListener(this);
        passwordLessButton.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void signIn(String email, String password) {
        Log.d("SIGNIN", "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        boolean isLogin = false;
                        try {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("SUCCESS", "signInWithEmail:success");
                                //FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(LogInActivity.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                                hideProgressDialog();
                                LogInActivity.this.setResult(Activity.RESULT_OK);
                                finish();
                                //isLogin = true;
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("FAILURE", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LogInActivity.this, "Đăng nhập thất bại.", Toast.LENGTH_LONG).show();
                                hideProgressDialog();
                                //updateUI(null);
                            }
                        } catch (Exception e) {
                            mDetailTextView.setText(e.getMessage());
                        }

                        // [START_EXCLUDE]
//                        if (!task.isSuccessful()) {
//                            Toast.makeText(LogInActivity.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
//                        }

                        // [END_EXCLUDE]

                        //Intent data = new Intent();
                        //data.putExtra("isLogin", isLogin);


                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Chưa điền email");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Chưa điền mật khẩu");
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
            mProgressDialog.setMessage("Đang kết nối");
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
        if (i == R.id.signInButton) {
            signIn(emailField.getText().toString(), passwordField.getText().toString());
        }
        else if (i == R.id.passwordLessButton) {
            Intent intent = new Intent(LogInActivity.this, PasswordlessActivity.class);
            LogInActivity.this.startActivity(intent);
        }
    }
}

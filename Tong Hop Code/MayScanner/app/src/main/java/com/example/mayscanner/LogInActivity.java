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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

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



    //------google sign in
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);

        findViewById(R.id.signInButton).setOnClickListener(this);
        findViewById(R.id.passwordLessButton).setOnClickListener(this);
        findViewById(R.id.signInWithGoogleButton).setOnClickListener(this);


        // Initialize Firebase Auth
        //mAuth = FirebaseAuth.getInstance();
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


    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Snackbar.make(findViewById(R.id.main_layout), "Đăng nhập thất bại.", Snackbar.LENGTH_LONG).show();
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            hideProgressDialog();
                            Snackbar.make(findViewById(R.id.main_layout), "Đăng nhập thành công.", Snackbar.LENGTH_LONG).show();
                            LogInActivity.this.setResult(Activity.RESULT_OK);
                            finish();
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            hideProgressDialog();
                            Snackbar.make(findViewById(R.id.main_layout), "Đăng nhập thất bại.", Snackbar.LENGTH_LONG).show();
                            //updateUI(null);
                        }

                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]



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
        else if (i == R.id.signInWithGoogleButton) {
            signIn();
        }
    }
}

package com.example.mayscanner;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInFragment extends Fragment implements
        View.OnClickListener {
    private MainActivity mainActivity;

    //RelativeLayout mainLayout;
    EditText emailField, passwordField;
    TextView mDetailTextView;

    //------google sign in
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("ORDER", "07");
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_fragment, container, false);
        //mainLayout = (RelativeLayout) view.findViewById(R.id.main_layout);
        emailField = (EditText) view.findViewById(R.id.emailField);
        passwordField = (EditText) view.findViewById(R.id.passwordField);

        view.findViewById(R.id.signInButton).setOnClickListener(this);
        view.findViewById(R.id.passwordLessButton).setOnClickListener(this);
        view.findViewById(R.id.signInWithGoogleButton).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.signInButton) {
            signIn(emailField.getText().toString(), passwordField.getText().toString());
        }
        else if (i == R.id.passwordLessButton) {
            Intent intent = new Intent(mainActivity, PasswordlessActivity.class);
            mainActivity.startActivity(intent);
        }
        else if (i == R.id.signInWithGoogleButton) {
            // Configure Google Sign In
            mainActivity.gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
            // [END config_signin]
            mainActivity.mGoogleSignInClient = GoogleSignIn.getClient(mainActivity, mainActivity.gso);
            signIn();
        }
    }

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
                //Snackbar.make(mainLayout, "Đăng nhập thất bại.", Snackbar.LENGTH_LONG).show();
                Toast.makeText(mainActivity, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                mainActivity.updateUI();
                mainActivity.navController.navigate(R.id.defaultFragment);
            }
        }
    }
    // [END onactivityresult]


    private void signIn(String email, String password) {
        Log.d("SIGNIN", "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        mainActivity.showProgressDialog("Đang kết nối");

        //[START sign_in_with_email]
        mainActivity.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mainActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("SUCCESS", "signInWithEmail:success");
                                //FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(mainActivity, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                                mainActivity.hideProgressDialog();
                                mainActivity.updateUI();
                                mainActivity.navController.navigate(R.id.defaultFragment);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("FAILURE", "signInWithEmail:failure", task.getException());
                                Toast.makeText(mainActivity, "Đăng nhập thất bại.", Toast.LENGTH_LONG).show();
                                mainActivity.hideProgressDialog();
                                mainActivity.updateUI();
                                mainActivity.navController.navigate(R.id.defaultFragment);
                            }
                        } catch (Exception e) {
                            mDetailTextView.setText(e.getMessage());
                            Log.d("ERRORLOGIN", e.getMessage());
                            mainActivity.hideProgressDialog();
                            mainActivity.updateUI();
                            mainActivity.navController.navigate(R.id.defaultFragment);
                        }
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

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        mainActivity.showProgressDialog("Đang kết nối");
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mainActivity.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mainActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //Snackbar.make(mainLayout, "Đăng nhập thành công.", Snackbar.LENGTH_LONG).show();
                            Toast.makeText(mainActivity, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                            mainActivity.hideProgressDialog();
                            mainActivity.updateUI();
                            mainActivity.navController.navigate(R.id.defaultFragment);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(mainLayout, "Đăng nhập thất bại.", Snackbar.LENGTH_LONG).show();
                            Toast.makeText(mainActivity, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                            mainActivity.hideProgressDialog();
                            mainActivity.updateUI();
                            mainActivity.navController.navigate(R.id.defaultFragment);
                        }
                    }
                });

    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mainActivity.mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

}

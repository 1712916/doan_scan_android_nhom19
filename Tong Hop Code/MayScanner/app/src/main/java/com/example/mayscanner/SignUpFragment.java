package com.example.mayscanner;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpFragment extends Fragment implements
        View.OnClickListener {

    private MainActivity mainActivity;

    EditText emailField, passwordField;
    Button signUpButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_up_fragment, container, false);
        signUpButton = (Button) view.findViewById(R.id.signUpButton);
        emailField = (EditText) view.findViewById(R.id.emailField);
        passwordField = (EditText) view.findViewById(R.id.passwordField);
        signUpButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.signUpButton) {
            createAccount(emailField.getText().toString(), passwordField.getText().toString());
        }
    }

    private void createAccount(String email, String password) {
        try {
            Log.d("SIGNUP", "createAccount:" + email);
            if (!validateForm()) {
                return;
            }
            mainActivity.showProgressDialog("Đang đăng kí");
            // [START create_user_with_email]
            mainActivity.mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(mainActivity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("SUCCESS", "createUserWithEmail:success");
                                Toast.makeText(mainActivity, "Đăng kí thành công, nhưng chưa xác minh email", Toast.LENGTH_LONG).show();
                                mainActivity.hideProgressDialog();
                                mainActivity.updateUI();
                                mainActivity.navController.navigate(R.id.defaultFragment);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("FAILURE", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(mainActivity, "Đăng kí không thành công.", Toast.LENGTH_LONG).show();
                                mainActivity.hideProgressDialog();
                                mainActivity.updateUI();
                                mainActivity.navController.navigate(R.id.defaultFragment);
                            }
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
}
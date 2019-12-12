package com.example.mayscanner;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Demonstrate Firebase Authentication without a password, using a link sent to an
 * email address.
 */
public class PasswordlessActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PasswordlessSignIn";
    private static final String KEY_PENDING_EMAIL = "key_pending_email";

    private FirebaseAuth mAuth;

    private Button mSendLinkButton;

    private EditText mEmailField;
    private TextView mStatusText;

    private String mPendingEmail;
    private String mEmailLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordless);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mSendLinkButton = findViewById(R.id.passwordlessSendEmailButton);

        mEmailField = findViewById(R.id.fieldEmail);

        mSendLinkButton.setOnClickListener(this);

        // Restore the "pending" email address
        if (savedInstanceState != null) {
            mPendingEmail = savedInstanceState.getString(KEY_PENDING_EMAIL, null);
            mEmailField.setText(mPendingEmail);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_PENDING_EMAIL, mPendingEmail);
    }

    /**
     * Send an email sign-in link to the specified email.
     */
    private void sendSignInLink(final String email) {
        showProgressDialog();
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PasswordlessActivity.this, "Link reset mật khẩu đã được gửi đến " + email, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(PasswordlessActivity.this, "Thất bại khi gửi link reset mật khẩu đến " + email, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        hideProgressDialog();
        finish();
    }

    private void onSendLinkClicked() {
        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Chưa điền email.");
            return;
        }
        sendSignInLink(email);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.passwordlessSendEmailButton:
                onSendLinkClicked();
                break;
        }
    }
}
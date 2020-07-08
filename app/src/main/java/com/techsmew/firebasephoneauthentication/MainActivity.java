package com.techsmew.firebasephoneauthentication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText textPhone, textOtp;
    Button btnSendOtp, btnResendOtp, btnVerifyOtp,btnSignOut;
    private FirebaseAuth mAuth;
    String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFields();
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    Toast.makeText(MainActivity.this,"Verification Complete", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Log.i("error",e.toString());
                    Toast.makeText(MainActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCodeSent(String verificationId,
                                       PhoneAuthProvider.ForceResendingToken token) {
                    Toast.makeText(MainActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
                    mVerificationId = verificationId;
                    mResendToken = token;
                }
            };
    }

    void initFields() {
        textPhone = findViewById(R.id.et_phone);
        textOtp = findViewById(R.id.et_otp);
        btnSendOtp = findViewById(R.id.bt_send_otp);
        btnResendOtp = findViewById(R.id.bt_resend_otp);
        btnSignOut = findViewById(R.id.bt_sign_out);
        btnVerifyOtp = findViewById(R.id.bt_verify_otp);
        btnResendOtp.setOnClickListener(this);
        btnVerifyOtp.setOnClickListener(this);
        btnSendOtp.setOnClickListener(this);
        btnSignOut.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(final View view) {

        switch (view.getId()) {
            case R.id.bt_send_otp:
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        textPhone.getText().toString(),        // Phone number to verify
                        1,                                // Timeout duration
                        TimeUnit.MINUTES,                    // Unit of timeout
                        this,                        // Activity (for callback binding)
                        mCallbacks);                         // OnVerificationStateChangedCallbacks
                break;
            case R.id.bt_resend_otp:
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        textPhone.getText().toString(),          // Phone number to verify
                        1  ,                                // Timeout duration
                        TimeUnit.MINUTES,                      // Unit of timeout
                        this,                          // Activity (for callback binding)
                        mCallbacks,                            // OnVerificationStateChangedCallbacks
                        mResendToken);                         // Force Resending Token from callbacks
                break;
              case R.id.bt_sign_out:
                  FirebaseAuth.getInstance().signOut();
                  Toast.makeText(MainActivity.this, "Signing out ", Toast.LENGTH_SHORT).show();
                  btnSignOut.setVisibility(view.INVISIBLE);
                break;
            case R.id.bt_verify_otp:
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, textOtp.getText().toString());
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Verification Success", Toast.LENGTH_SHORT).show();
                                    btnSignOut.setVisibility(View.VISIBLE);
                                } else {
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(MainActivity.this, "Verification Failed, Invalid credentials", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                break;
        }
    }
}
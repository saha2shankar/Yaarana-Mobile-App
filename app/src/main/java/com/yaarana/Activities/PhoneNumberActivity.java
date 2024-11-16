package com.yaarana.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.yaarana.R;
import com.yaarana.databinding.ActivityPhoneNumberBinding;

import java.util.concurrent.TimeUnit;

public class PhoneNumberActivity extends AppCompatActivity {
    ActivityPhoneNumberBinding binding;
    EditText enternumber;
    Button getmobilenumber;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth =FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null){
            Intent intent = new Intent(PhoneNumberActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        enternumber = findViewById(R.id.namebox);
        getmobilenumber = findViewById(R.id.setbtn);
        progressBar = findViewById(R.id.progressBar_SendOTP);

        binding.setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = enternumber.getText().toString().trim();
                if (!phoneNumber.isEmpty() && phoneNumber.length() == 10) {
                    progressBar.setVisibility(View.VISIBLE);
                    getmobilenumber.setVisibility(View.INVISIBLE);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+977" + phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            PhoneNumberActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    progressBar.setVisibility(View.GONE);
                                    getmobilenumber.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    progressBar.setVisibility(View.GONE);
                                    getmobilenumber.setVisibility(View.VISIBLE);
                                    Toast.makeText(PhoneNumberActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String backendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    progressBar.setVisibility(View.GONE);
                                    getmobilenumber.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(PhoneNumberActivity.this, OTPActivity.class);
                                    Toast.makeText(PhoneNumberActivity.this, "OTP send Successfully", Toast.LENGTH_SHORT).show();
                                    intent.putExtra("PhoneNumber", phoneNumber);
                                    intent.putExtra("backendotp", backendotp);
                                    startActivity(intent);
                                }
                            });
                } else {
                    Toast.makeText(PhoneNumberActivity.this, "Please enter a correct number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

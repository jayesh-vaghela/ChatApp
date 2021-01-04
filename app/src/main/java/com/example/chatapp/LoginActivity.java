package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private EditText code,phone_no;
    private String otp,pno="";
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private String codesent;
    private String phoneno,vcode;
    private int buttonpress=0;
    private Button resend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        code=findViewById(R.id.code);
        phone_no=findViewById(R.id.phone_no);
        auth= FirebaseAuth.getInstance();
        auth.setLanguageCode(Locale.getDefault().getLanguage());
        resend=(Button)findViewById(R.id.send_verification_code);
        findViewById(R.id.send_verification_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendVerificationCode(); }
        });
        findViewById(R.id.verify_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyCode();
            }
        });
        if(savedInstanceState!=null){
            otp=savedInstanceState.getString("otp");
            pno=savedInstanceState.getString("pno");
            phone_no.setText(pno);
            code.setText(otp);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pno=phone_no.getText().toString();
        otp=code.getText().toString();
        savedInstanceState.putString("pno",pno);
        savedInstanceState.putString("otp",otp);
    }

    private void SendVerificationCode(){
        phoneno=phone_no.getText().toString();
        if(phoneno.isEmpty()){
            phone_no.setError("Phone Number Required!!");
            phone_no.requestFocus();
            return;
        }
        if((phoneno.length()<10) || (phone_no.length()>10)){
            phone_no.setError("Invalid Number!!!!");
            phone_no.requestFocus();
            return;
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+phoneno,60, TimeUnit.SECONDS,this,mcallbacks);
        resend.setText("RESEND OTP");

    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(getApplicationContext(),"OTP Sent.",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            phone_no.setError("Invalid Number!!!!");
            phone_no.requestFocus();
            return;
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codesent=s;
            buttonpress+=1;
        }
    };

    private void VerifyCode(){
        vcode=code.getText().toString();
        phoneno=phone_no.getText().toString();
        if(phoneno.isEmpty()){
            phone_no.setError("Phone Number Required!!");
            phone_no.requestFocus();
            return;
        }
        if(vcode.isEmpty()){
            code.setError("Please Enter OTP!!!");
            code.requestFocus();
            return;
        }
        if((vcode.length()<6) || (vcode.length()>6)){
            code.setError("Invalid OTP!!!!");
            code.requestFocus();
            return;
        }
     if(buttonpress==0){
            code.setError("Invalid OTP!!!!");
            code.requestFocus();
            return;
        }
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(codesent,vcode);
        SignWithPhoneNumber(credential);
    }
    private void SignWithPhoneNumber(PhoneAuthCredential credential){
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            firebaseUser=auth.getCurrentUser();
                            databaseReference= FirebaseDatabase.getInstance().getReference().child("UsersList").child(firebaseUser.getUid());
                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put("UserId",firebaseUser.getUid());
                            hashMap.put("PhoneNo",phoneno);
                            hashMap.put("ProfileImage","default");
                            hashMap.put("Status","offline");
                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });

                        }
                        else {
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                code.setError("Invalid OTP!!!!");
                                code.requestFocus();
                                return;
                            }
                        }
                    }

                });
    }
}

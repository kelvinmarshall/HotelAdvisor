package dev.marshall.hoteladvisor;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rilixtech.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.marshall.hoteladvisor.common.Common;
import info.hoang8f.widget.FButton;

public class ResetPassword extends AppCompatActivity {
    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    // [END declare_auth]

    LinearLayout inputCodeLayout;
    LinearLayout verifyLayout;
    LinearLayout loadingProgress;
    LinearLayout userInformation;
    RelativeLayout reset_password;

    CountryCodePicker Ccode;
    FButton submit;
    FButton resendCode;
    Pinview smsCode;
    public AppCompatEditText phonenumber;
    TextView timer;

    //user detail
    Button signIn ,reset_pass;
    EditText password, confirmpass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        //views
        Ccode = (CountryCodePicker) findViewById(R.id.ccode);
        submit = (FButton) findViewById(R.id.submit);
        phonenumber = (AppCompatEditText) findViewById(R.id.phone);
        timer = (TextView) findViewById(R.id.timer);
        resendCode = (FButton) findViewById(R.id.re_sendcode);
        smsCode = (Pinview) findViewById(R.id.sms_code);

        reset_password=(RelativeLayout)findViewById(R.id.reset_pass);
        inputCodeLayout = (LinearLayout) findViewById(R.id.inputcodelayout);
        verifyLayout = (LinearLayout) findViewById(R.id.verifylayout);
        userInformation = (LinearLayout) findViewById(R.id.userInformation);
        loadingProgress = (LinearLayout) findViewById(R.id.loadingprogress);
        loadingProgress.setVisibility(View.INVISIBLE);

        signIn = (Button) findViewById(R.id.btSignIn);
        reset_pass = (Button) findViewById(R.id.sign_up);
        password = (MaterialEditText) findViewById(R.id.Epassword);
        confirmpass = (MaterialEditText) findViewById(R.id.Econfirm);


        showView(verifyLayout);//show main layout
        hideView(inputCodeLayout);//hide otp
        hideView(loadingProgress);//hide progress loading layout
        hideView(userInformation);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(phonenumber.getText().toString()).exists()) {
                            attemptSubmit();
                        }
                        else {
                            new SweetAlertDialog(ResetPassword.this,SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Account Not found")
                                    .setContentText("Account withthe entered phone number does not exist");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin = new Intent(ResetPassword.this, SignIn.class);
                startActivity(signin);
            }
        });
        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryVerify();
            }
        });

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                } else if (e instanceof FirebaseTooManyRequestsException) {

                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };
        smsCode.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean b) {

                //trigger this when the OTP code has finished typing
                final String verifyCode = smsCode.getValue();
                verifyPhoneNumberWithCode(mVerificationId, verifyCode);
            }
        });

        reset_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    //check if all fields are okay
                    String pass = password.getText().toString();
                    String confirm = confirmpass.getText().toString();

                    if (TextUtils.isEmpty(pass) && TextUtils.isEmpty(pass) ||TextUtils.isEmpty(pass) ||TextUtils.isEmpty(pass)) {
                        new SweetAlertDialog(ResetPassword.this)
                                .setTitleText("Please fill all the details")
                                .show();
                    } else {

                        if (TextUtils.equals(pass, confirm)) {
                            final SweetAlertDialog sDialog = new SweetAlertDialog(ResetPassword.this, SweetAlertDialog.PROGRESS_TYPE);
                            sDialog.getProgressHelper().setBarColor(Color.parseColor("#d2e2c0"));
                            sDialog.setTitleText("Loading");
                            sDialog.setCancelable(false);
                            sDialog.show();

                            table_user.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //check if already userphone exist
                                    if (dataSnapshot.child(phonenumber.getText().toString()).exists()) {
                                        sDialog.dismiss();
                                        Map<String,Object>resetpass=new HashMap<>();
                                        resetpass.put("password",password.getText().toString());

                                        table_user.child(phonenumber.getText().toString())
                                                .updateChildren(resetpass)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Snackbar.make(reset_pass,"Password was reset successfuly",Snackbar.LENGTH_SHORT)
                                                                .show();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Snackbar.make(reset_pass,"Password reset failed",Snackbar.LENGTH_SHORT)
                                                                .show();
                                                    }
                                                });
                                        finish();
                                    } else {
                                        sDialog.dismiss();
                                        new SweetAlertDialog(ResetPassword.this)
                                                .setTitleText("Account with phone number entered is unknown")
                                                .show();

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            new SweetAlertDialog(ResetPassword.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops!")
                                    .setContentText("Passwords dont match")
                                    .show();
                        }


                    }
                } else {
                    new SweetAlertDialog(ResetPassword.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops!Error Connecting")
                            .setContentText("Please check your internet connection and try again")
                            .show();
                }

            }
        });
    }

    private void attemptSubmit() {
        //reset errors
        phonenumber.setError(null);

        //get value from edtphone & pass it to cc
        Ccode.registerPhoneNumberTextView(phonenumber);
        String phone=Ccode.getFullNumberWithPlus();
        Log.i("phone number",Ccode.getFullNumber());


        boolean cancel=false;
        View focusView=null;
        //check if phone number is valid
        if (!isPhoneValid(phone)){
            focusView =phonenumber;
            cancel=true;
        }

        if (cancel){
            //error in length phone
            focusView.requestFocus();
            new SweetAlertDialog(ResetPassword.this)
                    .setTitleText("Phone number seems invalid")
                    .show();
        }
        else {
            //show loading screen
            hideView(verifyLayout);
            hideView(loadingProgress);
            showView(inputCodeLayout);
            hideView(userInformation);
            //proceed to verify
            startPhoneNumberVerification(phone);
            //time to show retry button
            new CountDownTimer(45000, 1000) {
                @Override
                public void onTick(long l) {
                    timer.setText("0:" + l / 1000 + " s");
                    resendCode.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFinish() {
                    timer.setText(0 + " s");
                    resendCode.startAnimation(AnimationUtils.loadAnimation(ResetPassword.this, R.anim.slide_from_right));
                    resendCode.setVisibility(View.VISIBLE);
                }
            }
                    .start();
            //timer ends here
        }


    }

    private void verifyPhoneNumberWithCode(String mVerificationId, String verifyCode) {
        hideView(verifyLayout); //hide the main layout
        hideView(inputCodeLayout); //hide the otp layout
        showView(loadingProgress); //show the progress loading layout


        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verifyCode);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void retryVerify() {
        String phone=Ccode.getFullNumber();

        resendVerificationCode(phone,mResendToken);

        new SweetAlertDialog(ResetPassword.this)
                .setTitleText("Request sent")
                .show();
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void showView(View... views) {
        for (View v: views){
            v.setVisibility(View.VISIBLE);
        }
    }
    private void hideView(View... views) {
        for (View v: views){
            v.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isPhoneValid(String phone) {
        return phone.length()>9;
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            mUser=mAuth.getCurrentUser();

                            hideView(verifyLayout);
                            hideView(loadingProgress);
                            hideView(inputCodeLayout);
                            showView(userInformation);


                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                new SweetAlertDialog(ResetPassword.this)
                                        .setTitleText("Invalid Verification Code")
                                        .show();
                            }
                        }
                    }
                });
    }

}

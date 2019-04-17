package dev.marshall.hoteladvisor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.marshall.hoteladvisor.model.User;
import dev.marshall.hoteladvisor.common.Common;
import io.paperdb.Paper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {
    Button sign_up,sign_in;
    TextView forgot;
    EditText edtphone,edtpassword;
    com.rey.material.widget.CheckBox chbRemember;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        sign_up=(Button)findViewById(R.id.btSigUp);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup=new Intent(SignIn.this,Phone_Auth.class);
                startActivity(signup);
            }
        });
        edtphone=(MaterialEditText)findViewById(R.id.Edphone);
        edtpassword=(MaterialEditText)findViewById(R.id.Edpassword);
        forgot=(TextView)findViewById(R.id.forgot);
        sign_in=(Button) findViewById(R.id.sign_in);
        chbRemember =(com.rey.material.widget.CheckBox) findViewById(R.id.chbRemember);

        //init paper
        Paper.init(this);

        //Init Firebase
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(SignIn.this, ResetPassword.class);
                startActivity(home);
            }
        });
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (Common.isConnectedToInternet(getBaseContext())) {


                        //Save user and password
                        if (chbRemember.isChecked()) {
                            Paper.book().write(Common.USER_KEY, edtphone.getText().toString());
                            Paper.book().write(Common.PWD_KEY, edtpassword.getText().toString());
                        }
                        //check if all fields are okay
                        String phone =edtphone.getText().toString();
                        String pwd=edtpassword.getText().toString();
                        if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(pwd) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(pwd) )
                        {
                            AlertDialog.Builder alertdialog=new AlertDialog.Builder(SignIn.this);
                            alertdialog.setMessage("Please fill all the required details");
                            alertdialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alertdialog.show();
                            }
                            else {
                            final SweetAlertDialog sDialog = new SweetAlertDialog(SignIn.this, cn.pedant.SweetAlert.SweetAlertDialog.PROGRESS_TYPE);
                            sDialog.getProgressHelper().setBarColor(Color.parseColor("#d2e2c0"));
                            sDialog.setTitleText("Please wait");
                            sDialog.setCancelable(false);
                            sDialog.show();

                            table_user.addValueEventListener(new ValueEventListener() {


                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    //check if user exist in database
                                    if (dataSnapshot.child(edtphone.getText().toString()).exists()) {

                                        //Get User information
                                        sDialog.dismiss();

                                        User user = dataSnapshot.child(edtphone.getText().toString()).getValue(User.class);
                                        user.setPhone(edtphone.getText().toString()); //set phone
                                        if (user.getPassword().equals(edtpassword.getText().toString())) {
                                            {
                                                Intent homeIntent = new Intent(SignIn.this, Home.class);
                                                Common.currentUser = user;
                                                startActivity(homeIntent);
                                                finish();

                                            }
                                        } else {
                                            AlertDialog.Builder alertdialog=new AlertDialog.Builder(SignIn.this);
                                            alertdialog.setTitle("Wrong Password");
                                            alertdialog.setMessage("Password you have entered is incorect.Check and try again.");
                                            alertdialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            alertdialog.show();
                                        }
                                    } else {
                                        sDialog.dismiss();
                                        AlertDialog.Builder alertdialog=new AlertDialog.Builder(SignIn.this);
                                        alertdialog.setMessage("The account user you are trying to sign in does not exist.Please try creating a new account");
                                        alertdialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alertdialog.show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }


                            });
                        }
                    }
                    else
                    {
                        new SweetAlertDialog(SignIn.this,SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops!Error Connecting")
                                .setContentText("Please check your internet connection and try again")
                                .show();
                    }
                }


        });



    }
}


package dev.marshall.hoteladvisor;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;

import dev.marshall.hoteladvisor.common.Common;
import dev.marshall.hoteladvisor.model.Hotels;
import dev.marshall.hoteladvisor.model.User;
import dev.marshall.hoteladvisor.model.Webvisitors;

public class BookingSite extends AppCompatActivity {

    WebView booksite;
    FirebaseDatabase db;
    DatabaseReference databaseReference;

    String HotelId="";

    LinearLayout webvieLayout;
    LinearLayout loadingProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_site);

        webvieLayout=(LinearLayout)findViewById(R.id.webview);
        loadingProgress=(LinearLayout)findViewById(R.id.redirect);
        loadingProgress.setVisibility(View.INVISIBLE);

        //Init firebase
        db=FirebaseDatabase.getInstance();
        databaseReference = db.getReference("Hotels");

        booksite=(WebView)findViewById(R.id.webviewbook);

        if(getIntent() !=null)
            HotelId=getIntent().getStringExtra("hotelId");
        //if(!HotelId.isEmpty())

        booksite.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                hideView(webvieLayout);//hide web
                showView(loadingProgress);//show progress loading layout
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                sendwebvisitor();
                // Page loading finished
                showView(webvieLayout);//hide web
                hideView(loadingProgress);//show progress loading layout
            }
        });

        WebSettings webSettings=booksite.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        booksite.getSettings().getLoadsImagesAutomatically();
        databaseReference.child(HotelId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Hotels hotels=dataSnapshot.getValue(Hotels.class);


                String url=hotels.getUrl();

                booksite.loadUrl(url);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendwebvisitor() {
        Webvisitors webvisitors=new Webvisitors(
                Common.currentUser.getName(),
                Common.currentUser.getImage(),
                HotelId
        );
        databaseReference.child(HotelId).child("webvisitors").push().setValue(webvisitors);
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
}

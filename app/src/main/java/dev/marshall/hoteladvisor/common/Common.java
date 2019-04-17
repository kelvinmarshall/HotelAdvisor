package dev.marshall.hoteladvisor.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import dev.marshall.hoteladvisor.Remote.IGeoCoordinates;
import dev.marshall.hoteladvisor.Remote.RetrofitClient;
import dev.marshall.hoteladvisor.model.Hotels;
import dev.marshall.hoteladvisor.model.User;

/**
 * Created by Marshall on 25/01/2018.
 */

public class Common {
    public static User currentUser;
    public static Hotels currentHotel;
    public static String currentlocation;

    public static LatLng CURRENT_LOCATION;
    public static LatLng HOTEL_LOCATION;

    public static String APPLICATION_ID="QZNK9LXV19";
    public static String API_KEY="4ac09b7d0adce89157de480429a3e747";
    public static final String ALGOLIA_INDEX_NAME = "hotel_LOCATION";
    

    public static final int PICK_IMAGE_REQUEST = 71;

    public static final String USER_KEY="User";
    public static final String PWD_KEY="Password";

    public static final String EDIT ="Edit";
    public static final String DELETE ="Delete";


    public static final String baseURl = "https://maps.googleapis.com";

    public static IGeoCoordinates getGeoCodeService(){
        return RetrofitClient.getClient(baseURl).create(IGeoCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight)
    {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);
        float scaleY = newWidth/(float)bitmap.getWidth();
        float scaleX = newHeight/(float)bitmap.getHeight();
        float pivotX=0,pivotY=0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }
    public static String convertAsal(String asa )
    {
        if (asa.equals("0"))
            return "1 Night";
        else  if (asa.equals("1"))
            return "Days";
        else  if (asa.equals("2"))
            return "1 Week";
        else  if (asa.equals("3"))
            return "Weeks";
        else  if (asa.equals("4"))
            return "1 Month";
        else  if (asa.equals("4"))
            return "Months";
        else
            return "30";
    }
    public static String convertStayed(String stayed )
    {
        if (stayed.equals("0"))
            return "Individual";
        else  if (stayed.equals("2"))
            return "Family";
        else  if (stayed.equals("3"))
            return "Partner";
        else  if (stayed.equals("4"))
            return "Committee";
        else
            return "Business Person";
    }

    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager !=null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info !=null)
            {
                for (int i=0;i<info.length;i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
    public static  String getdate(long time)
    {
        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date =new StringBuilder(
                android.text.format.DateFormat
                        .format("dd-MM-yyyy HH:mm",calendar
                                        ).toString());
        return date.toString();
    }
}

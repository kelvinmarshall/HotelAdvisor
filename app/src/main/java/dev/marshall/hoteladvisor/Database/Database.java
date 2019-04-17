package dev.marshall.hoteladvisor.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import dev.marshall.hoteladvisor.model.Favourites;

/**
 * Created by Marshall on 20/03/2018.
 */

public class Database  extends SQLiteAssetHelper {
    private static final String DB_NAME="HotelInformation.db";
    private static final int DB_VER=1;
    public Database(Context context) {
        super(context, DB_NAME,null, DB_VER);
    }
    //add favourit
    public void addToFavourites(Favourites hotel)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favourites(" +
                "HotelId,UserPhone,HotelName,HotelImage,HotelLocation,HotelPrice) " +
                "VALUES('%s','%s','%s','%s','%s','%s');",
                hotel.getHotelId(),
                hotel.getUserPhone(),
                hotel.getHotelName(),
                hotel.getHotelImage(),
                hotel.getHotelLocation(),
                hotel.getHotelPrice());
        db.execSQL(query);
    }
    public void removeFromFavourites(String hotelId,String userPhone)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favourites WHERE HotelId='%s' and UserPhone='%s';",hotelId,userPhone);
        db.execSQL(query);
    }
    public boolean isFavourites(String hotelId,String userphone)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favourites WHERE HotelId='%s' and UserPhone='%s';",hotelId,userphone);
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.getCount() <=0)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public List<Favourites>getAllFavouritesa(String userphone)
    {
        SQLiteDatabase db =getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect={"HotelId","UserPhone","HotelName","HotelImage","HotelLocation","HotelPrice"};
        String sqlTable="Favourites";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect,"UserPhone=?",new String[]{userphone},null,null,null);

        final  List<Favourites> result = new ArrayList<>();
        if(c.moveToFirst())
        {
            do{
              result.add(new Favourites(
                      c.getString(c.getColumnIndex("HotelId")),
                      c.getString(c.getColumnIndex("UserPhone")),
                      c.getString(c.getColumnIndex("HotelName")),
                      c.getString(c.getColumnIndex("HotelImage")),
                      c.getString(c.getColumnIndex("HotelLocation")),
                      c.getString(c.getColumnIndex("HotelPrice"))

              ));
            }
            while (c.moveToNext());
        }
        return result;
    }
}

package com.example.wallpaperapp.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.wallpaperapp.models.imageModel;

import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {
    final static String name = "Database.db";
    final static int DBverion = 11;

    public DBHelper(@Nullable Context context) {
        super(context, name, null, DBverion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table imageDownloading" + "(" + "id text," + "address text," + "preview_images text," + "image_name text," + "isFav text default 'false')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists imageloading");
        onCreate(sqLiteDatabase);

    }

    public boolean insertFuction(String address, String image_name, String id) {

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("image_name", image_name);
        values.put("preview_images", address);

        long check = sqLiteDatabase.insert("imageDownloading", null, values);
        return check > 0;
    }

    public boolean insertFav(String id) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put("isFav", "true");

        long check = sqLiteDatabase.update("imageDownloading", values, "id=?", new String[]{id});
        sqLiteDatabase.close();

        return check > 0;


    }

    public String getFav(String name) {

        SQLiteDatabase database = this.getWritableDatabase();

//        Cursor cursor=database.query("imageDownloading",new String[]{"isFav"},"isFav" + "=" + "true",null,null,null,null);
        Cursor cursor = database.rawQuery("Select isFav from imageDownloading where id = " + "\"" + name + "\"", null);

        cursor.moveToFirst();

        return cursor.getString(0);
    }

    public boolean insertHeavy(String address, String id) {

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ContentValues values = new ContentValues();


        values.put("address", address);

        long check = sqLiteDatabase.update("imageDownloading", values, "id=?", new String[]{id});
        Log.d("This", Long.toString(check));

        return check > 0;
    }

    public boolean removeFav(String name) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put("isFav", "false");
        long check = sqLiteDatabase.update("imageDownloading", values, "id=?", new String[]{name});
        sqLiteDatabase.close();

        return check > 0;
    }

    public ArrayList<imageModel> getOrders() {
        ArrayList<imageModel> items = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("Select * from imageDownloading", null);

        if (cursor.moveToFirst()) {
            while (true) {
                imageModel model = new imageModel();
                model.setTags(cursor.getString(3));
                model.setWebformatURL(cursor.getString(2));
                model.setLargeImageURL(cursor.getString(1));
                model.setId(cursor.getString(0));


                items.add(model);

                if (!cursor.moveToNext()) break;
            }

        }
        cursor.close();
        database.close();
        return items;
    }

    public ArrayList<imageModel> getOrdersFav() {
        ArrayList<imageModel> items = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("Select * from imageDownloading where isFav = " + "\"" + "true" + "\"", null);

        if (cursor.moveToFirst()) {
            while (true) {
                imageModel model = new imageModel();
                model.setTags(cursor.getString(3));
                model.setWebformatURL(cursor.getString(2));
                model.setLargeImageURL(cursor.getString(1));
                model.setId(cursor.getString(0));


                items.add(model);

                if (!cursor.moveToNext()) break;
            }

        }
        cursor.close();
        database.close();
        return items;
    }

    public boolean checkInDB(String id) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ContentValues values = new ContentValues();


        Cursor cursor = sqLiteDatabase.rawQuery("Select Count(*) from imageDownloading where id = " + id, null);
        cursor.moveToFirst();

        Log.d("cur", cursor.getString(0));
        sqLiteDatabase.close();

        return Integer.parseInt(cursor.getString(0)) > 0;
    }


}

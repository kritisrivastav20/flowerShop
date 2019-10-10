package com.example.android.flowershop.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.flowershop.data.ShopContract.ShopEntry;

public class ShopdbHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "inventory.db";
    public final static int DB_VERSION = 1;
    public final static String LOG_TAG = ShopdbHelper.class.getCanonicalName();

    public ShopdbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_SHOP = "CREATE TABLE " + ShopEntry.TABLE_NAME + "("
                + ShopEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ShopEntry.COLUMN_FLOWER_NAME + " TEXT, "
                + ShopEntry.COLUMN_FLOWER_PRICE + " TEXT, "
                + ShopEntry.COLUMN_FLOWER_IMAGE + " BLOB, "
                + ShopEntry.COLUMN_FLOWER_QUANTITY + " INTEGER, "
                + ShopEntry.COLUMN_SELLER_NAME + " TEXT" + ")";

        db.execSQL(CREATE_TABLE_SHOP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

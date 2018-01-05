package com.example.bittu.pinkcityretailer.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.bittu.pinkcityretailer.data.ItemContract.ItemsEntry;

public class ItemDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "inventory.db";

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ITEMS_TABLE =  "CREATE TABLE " + ItemsEntry.TABLE_NAME + " ("
                + ItemsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemsEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + ItemsEntry.COLUMN_ITEM_PRICE + " INTEGER, "
                + ItemsEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL, "
                + ItemsEntry.COLUMN_ITEM_SOLD + " INTEGER DEFAULT 0, "
                + ItemsEntry.COLUMN_ITEM_IMAGE + " BLOB, "
                + ItemsEntry.COLUMN_SUPPLIER_NAME + " TEXT);";

        db.execSQL(SQL_CREATE_ITEMS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

package com.example.android.inventoryapp.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class InvetoryDbHelper extends SQLiteOpenHelper {

    /**
     * Database version. If database schema changed, the database version must be incremented.
     */
    public static final int DATABASE_VERSION = 1;

    /** Name of the database file */
    public static final String DATABASE_NAME = "books.db";

    /**
     * Constructs a new instance of {@link InvetoryDbHelper}.
     *
     * @param context of the app
     */
    public InvetoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase bookDatabase) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + InventoryContract.InventoryEntry.TABLE_NAME + " ("
                + InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.InventoryEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME + " TEXT, "
                + InventoryContract.InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE + " INTEGER" + ");";
        // Execute the SQL statement
        bookDatabase.execSQL(SQL_CREATE_BOOKS_TABLE);

        Log.v("Database", SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

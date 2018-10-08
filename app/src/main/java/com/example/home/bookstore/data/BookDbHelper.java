package com.example.home.bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Home on 1/25/2018.
 */

public class BookDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "books.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String NN_TYPE = " NOT NULL";
    private static final String FLOAT_TYPE = " REAL";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " (" +
            BookContract.BookEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + " AUTOINCREMENT" + ", " +
            BookContract.BookEntry.COLUMN_BOOK_NAME + TEXT_TYPE + NN_TYPE + ", " +
            BookContract.BookEntry.COLUMN_BOOK_AUTHOR + TEXT_TYPE + ", " +
            BookContract.BookEntry.COLUMN_BOOK_PRICE + FLOAT_TYPE + NN_TYPE + ", " +
            BookContract.BookEntry.COLUMN_BOOK_QUANTITY + INTEGER_TYPE + NN_TYPE + " DEFAULT 0" + ", " +
            BookContract.BookEntry.COLUMN_BOOK_IMAGE + INTEGER_TYPE + ", " +
            BookContract.BookEntry.COLUMN_BOOK_SUPP_NAME + TEXT_TYPE + ", " +
            BookContract.BookEntry.COLUMN_BOOK_SUPP_EMAIL + TEXT_TYPE + ", " +
            BookContract.BookEntry.COLUMN_BOOK_SUPP_PHONE + INTEGER_TYPE +
            ")";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BookContract.BookEntry.TABLE_NAME;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}

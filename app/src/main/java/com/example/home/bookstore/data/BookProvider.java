package com.example.home.bookstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Home on 2/10/2018.
 */

public class BookProvider extends ContentProvider {
    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        //Sets the integer value for multiple rows in table books to 100. Notice that no wildcard is used
        //in the path
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);

        //Sets the code for a single row to 2. In this case, the "#" wildcard is used.
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    private BookDbHelper mdbHelper;

    @Override
    public boolean onCreate() {
        mdbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase database = mdbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case BOOKS:
                cursor = database.query(
                        BookContract.BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case BOOKS_ID:
                selection = BookContract.BookEntry._ID + "=?";
                //We have to extract the value of id at the end of the uri and convert it to a string.
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(
                        BookContract.BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the cursor
        // so we know what content URI the cursor was created for
        // If the data at this URI changes, then we need to update the cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return BookContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        // Data Validation
        String bookName = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_NAME);
        if (bookName.equals("") || (bookName == null)) {
            //throw new IllegalArgumentException("Name must be entered.");
            return null;
        }

        Float price = values.getAsFloat(BookContract.BookEntry.COLUMN_BOOK_PRICE);
        if (price != null && price < 0) {
            //throw new IllegalArgumentException("Price must be positive.");
            return null;
        }

        Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
        if (quantity != null && quantity < 0) {
            //throw new IllegalArgumentException("Quantity must be positive");
            return null;
        }

        SQLiteDatabase database = mdbHelper.getWritableDatabase();
        long newRowId = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);
        if (newRowId == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mdbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOKS_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no key/value pairs in it, then just return 0 rows affected.
        if (values.size() == 0) {
            return 0;
        }

        // Data Validation
        boolean name = values.containsKey(BookContract.BookEntry.COLUMN_BOOK_NAME);
        boolean price = values.containsKey(BookContract.BookEntry.COLUMN_BOOK_PRICE);
        boolean quantity = values.containsKey(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);

        if (name) {
            String bookName = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_NAME);
            if (bookName.equals("") || (bookName == null)) {
                //throw new IllegalArgumentException("Name must be entered.");
                return 0;
            }
        }

        if (price) {
            Float priceFloat = values.getAsFloat(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            if (priceFloat != null && priceFloat < 0) {
                //throw new IllegalArgumentException("Price must be positive.");
                return 0;
            }
        }

        if (quantity) {
            Integer quantityInt = values.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
            if (quantityInt != null && quantityInt < 0) {
                //throw new IllegalArgumentException("Quantity must be positive");
                return 0;
            }

        }
        SQLiteDatabase database = mdbHelper.getWritableDatabase();
        //Get the number of rows that are updated
        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}

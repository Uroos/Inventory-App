package com.example.home.bookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Home on 1/25/2018.
 */

public class BookContract {
    public static final String CONTENT_AUTHORITY = "com.example.home.bookstore";
    //Next, we concatenate the CONTENT_AUTHORITY constant with the scheme “content://”
    //we will create the BASE_CONTENT_URI which will be shared by every URI associated with BookContract:
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //This constants stores the path for each of the tables which will be appended to the base content URI.
    public static final String PATH_BOOKS = "books";
    /**
     * The MIME type of the for a list of books.
     */
    public static final String CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

    /**
     * The MIME type of the for a single book.
     */
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

    private BookContract() {
    }

    public static final class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "books";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_NAME = "name";
        public static final String COLUMN_BOOK_AUTHOR = "author";
        public static final String COLUMN_BOOK_PRICE = "price";
        public static final String COLUMN_BOOK_QUANTITY = "quantity";
        public static final String COLUMN_BOOK_IMAGE = "image";
        public static final String COLUMN_BOOK_SUPP_NAME = "suppliername";
        public static final String COLUMN_BOOK_SUPP_EMAIL = "supplieremail";
        public static final String COLUMN_BOOK_SUPP_PHONE = "supplierphoneno";

        //The Uri.withAppendedPath() method appends the BASE_CONTENT_URI to the path segment.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
    }
}


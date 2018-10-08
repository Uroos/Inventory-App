package com.example.home.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.home.bookstore.data.BookContract;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    // These are the book rows that we will retrieve
    static final String[] PROJECTION = new String[]{
            BookContract.BookEntry._ID,
            BookContract.BookEntry.COLUMN_BOOK_NAME,
            BookContract.BookEntry.COLUMN_BOOK_PRICE,
            BookContract.BookEntry.COLUMN_BOOK_QUANTITY};

    private BookAdapter bAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        bAdapter = new BookAdapter(this, null);
        listView.setAdapter(bAdapter);

        getLoaderManager().initLoader(0, null, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri uri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
                intent.setData(uri);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_a_book:
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_insert_dummy_data:
                insertDummyBook();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_books_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllBooks();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void insertDummyBook() {
        //Create a new map of values, where column names are the keys.
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, getString(R.string.dummy_book_name));
        values.put(BookContract.BookEntry.COLUMN_BOOK_AUTHOR, getString(R.string.dummy_author_name));
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, 1.67);
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, 23);
        values.put(BookContract.BookEntry.COLUMN_BOOK_IMAGE, 200);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPP_NAME, getString(R.string.dummy_publisher));
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPP_EMAIL, getString(R.string.dummy_email));
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPP_PHONE, 1111111);

        //Insert the new row, returning the primary key value of the new row.
        Uri uri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
    }

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookContract.BookEntry.CONTENT_URI, null, null);
        if (rowsDeleted != 0) {
            Toast.makeText(MainActivity.this, "Books deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Books deletion failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Now create and return a CursorLoader that will take care of creating a Cursor for the data being displayed.
        return new CursorLoader(this, BookContract.BookEntry.CONTENT_URI, PROJECTION, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in.
        // The framework will take care of closing the old cursor once we return.
        bAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Swap the new cursor in.
        // The framework will take care of closing the old cursor once we return.
        bAdapter.swapCursor(null);
    }
}

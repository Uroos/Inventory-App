package com.example.home.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.home.bookstore.data.BookContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText bookNameEditText;
    private EditText bookAuthorEditText;
    private EditText bookPriceEditText;
    private EditText bookQuantityEditText;
    private EditText supplierNameEditText;
    private EditText supplierPhoneEditText;
    private EditText supplierEmailEditText;
    private Button upButton;
    private Button downButton;
    private Button orderButton;
    private Uri currentUri = null;
    private boolean bookHasChanged = false;
    private int quantity = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        currentUri = intent.getData();
        if (currentUri != null) {
            this.setTitle("Edit a book");
            getLoaderManager().initLoader(0, null, this);
        } else {
            this.setTitle("Add a book");
            invalidateOptionsMenu();
        }

        bookNameEditText = findViewById(R.id.edit_book_name);
        bookAuthorEditText = findViewById(R.id.edit_author_name);
        bookPriceEditText = findViewById(R.id.edit_price);
        bookQuantityEditText = findViewById(R.id.edit_quantity);
        supplierNameEditText = findViewById(R.id.edit_supp_name);
        supplierPhoneEditText = findViewById(R.id.edit_supp_phone);
        supplierEmailEditText = findViewById(R.id.edit_supp_email);
        upButton = findViewById(R.id.up_button);
        downButton = findViewById(R.id.down_button);
        orderButton = findViewById(R.id.contact_button);
        // Attach listeners to edit fields to know if they have been changed.
        bookNameEditText.setOnTouchListener(mTouchListener);
        bookAuthorEditText.setOnTouchListener(mTouchListener);
        bookPriceEditText.setOnTouchListener(mTouchListener);
        bookQuantityEditText.setOnTouchListener(mTouchListener);
        supplierNameEditText.setOnTouchListener(mTouchListener);
        supplierPhoneEditText.setOnTouchListener(mTouchListener);
        supplierEmailEditText.setOnTouchListener(mTouchListener);

        //Setting up click listeners
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUri != null) {
                    quantity = Integer.parseInt(bookQuantityEditText.getText().toString().trim());
                    quantity += 1;
                    bookQuantityEditText.setText(String.valueOf(quantity));

                } else {
                    // A new book is being inserted
                    String quantityString = bookQuantityEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(quantityString)) {
                        quantity = Integer.parseInt(quantityString);
                    }
                    quantity = quantity + 1;
                    bookQuantityEditText.setText(String.valueOf(quantity));
                }
            }
        });
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUri != null) {
                    quantity = Integer.parseInt(bookQuantityEditText.getText().toString().trim());
                    quantity -= 1;
                    if (quantity < 0) {
                        quantity = 0;
                    }
                    bookQuantityEditText.setText(String.valueOf(quantity));

                } else {
                    //A new book is being inserted
                    String quantityString = bookQuantityEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(quantityString)) {
                        quantity = Integer.parseInt(quantityString);
                    }
                    quantity = quantity - 1;
                    if (quantity < 0) {
                        quantity = 0;
                    }
                    bookQuantityEditText.setText(String.valueOf(quantity));
                }
            }
        });
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = supplierPhoneEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });


    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            return false;
        }
    };

    private void deleteBook() {
        if (currentUri != null) {
            int rowId = getContentResolver().delete(currentUri, null, null);
            if (rowId == 0) {
                Toast.makeText(this, "Row deletion failed.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Row deleted successfully", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void saveBook() {
        String bookNameString = bookNameEditText.getText().toString().trim();
        String authorString = bookAuthorEditText.getText().toString().trim();
        String priceString = bookPriceEditText.getText().toString().trim();
        String quantityString = bookQuantityEditText.getText().toString().trim();
        String suppNameString = supplierNameEditText.getText().toString().trim();
        String suppEmailString = supplierEmailEditText.getText().toString().trim();
        String suppPhoneString = supplierPhoneEditText.getText().toString().trim();
        float price = 0.0f;
        int phoneNo = 000000;

        //Checking whether user has entered anything
        if (TextUtils.isEmpty(bookNameString) ||
                TextUtils.isEmpty(authorString) ||
                TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) ||
                TextUtils.isEmpty(suppNameString) ||
                TextUtils.isEmpty(suppEmailString) ||
                TextUtils.isEmpty(suppPhoneString)) {
            Toast.makeText(this, "No fields should be blank.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If quantity is not entered, then make it 0.
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        if (!TextUtils.isEmpty(priceString)) {
            price = Float.parseFloat(priceString);
        }
        if (!TextUtils.isEmpty(suppPhoneString)) {
            phoneNo = Integer.parseInt(suppPhoneString);
        }
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, bookNameString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_AUTHOR, authorString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPP_NAME, suppNameString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPP_EMAIL, suppEmailString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPP_PHONE, phoneNo);


        // (uri == null) means EditorActivity is in insert mode
        // (uri != null) means EditorActivity is in edit mode
        if (currentUri == null) {
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Error inserting new book.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "New book inserted successfully.",
                        Toast.LENGTH_SHORT).show();
                // This will exit the current activity and return to main activity.
                finish();
            }
        } else {
            int rowsUpdated = getContentResolver().update(currentUri, values, null, null);

            if (rowsUpdated == 0) {
                Toast.makeText(this, "Book not updated. ",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Book updated successfully. ",
                        Toast.LENGTH_SHORT).show();
                // This will exit the current activity and return to main activity.
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_book, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (currentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveBook();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!bookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, currentUri, null, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            String bookname = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME));
            bookNameEditText.setText(bookname);

            String author = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_AUTHOR));
            bookAuthorEditText.setText(author);

            float price = cursor.getFloat(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE));
            bookPriceEditText.setText(String.valueOf(price));

            int quantity = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY));
            bookQuantityEditText.setText(String.valueOf(quantity));

            String suppname = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPP_NAME));
            supplierNameEditText.setText(suppname);

            String suppemail = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPP_EMAIL));
            supplierEmailEditText.setText(suppemail);

            int suppphone = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPP_PHONE));
            supplierPhoneEditText.setText(String.valueOf(suppphone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookNameEditText.setText("");
        bookAuthorEditText.setText("");
        bookPriceEditText.setText("");
        bookPriceEditText.setText("");
        supplierNameEditText.setText("");
        supplierEmailEditText.setText("");
        supplierPhoneEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
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
    public void onBackPressed() {
        if (!bookHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }
}

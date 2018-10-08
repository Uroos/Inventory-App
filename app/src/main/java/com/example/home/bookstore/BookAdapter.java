package com.example.home.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.home.bookstore.data.BookContract;

/**
 * Created by Home on 2/10/2018.
 */

public class BookAdapter extends CursorAdapter {

    public BookAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    static class ViewHolder {
        private TextView bookName;
        private TextView bookPrice;
        private TextView bookQuantity;
        private Button saleButton;

        public ViewHolder(View view) {
            bookName = view.findViewById(R.id.item_book_name);
            bookPrice = view.findViewById(R.id.item_book_price);
            bookQuantity = view.findViewById(R.id.item_book_quantity);
            saleButton = view.findViewById(R.id.item_sale_button);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        String bookName = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME));
        float bookPrice = cursor.getFloat(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE));
        final int bookQuantity = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY));
        final int id = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry._ID));

        holder.bookName.setText(context.getString(R.string.book_name) + ": " + bookName);
        holder.bookPrice.setText(context.getString(R.string.editor_price) + ": $" + String.valueOf(bookPrice));
        holder.bookQuantity.setText(context.getString(R.string.editor_quantity) + ": " + String.valueOf(bookQuantity));

        holder.saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempQuantity = bookQuantity - 1;
                if (tempQuantity < 0) {
                    tempQuantity = 0;
                }
                ContentValues cv = new ContentValues();
                cv.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, tempQuantity);
                Uri uri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
                context.getContentResolver().update(uri, cv, null, null);
                context.getContentResolver().notifyChange(uri, null);
            }
        });
    }

}



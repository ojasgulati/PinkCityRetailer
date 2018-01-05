package com.example.bittu.pinkcityretailer;


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

import com.example.bittu.pinkcityretailer.data.ItemContract;

public class ItemCursorAdapter extends CursorAdapter {

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemsEntry._ID));
        final int itemQty = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemsEntry.COLUMN_ITEM_QUANTITY));
        final int sale = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemsEntry.COLUMN_ITEM_SOLD));
        final TextView nameTextView = (TextView) view.findViewById(R.id.item_name);
        final TextView priceTextView = (TextView) view.findViewById(R.id.item_price);
        final TextView soldTextView = (TextView) view.findViewById(R.id.sold);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.item_quantity);
        final Button saleButton = (Button) view.findViewById(R.id.saleButton);
        final String PRICE = "Price: ";

        String itemName = cursor.getString(cursor.getColumnIndex(ItemContract.ItemsEntry.COLUMN_ITEM_NAME));
        String itemPrice = cursor.getString(cursor.getColumnIndex(ItemContract.ItemsEntry.COLUMN_ITEM_PRICE));
        String itemQuantity = cursor.getString(cursor.getColumnIndex(ItemContract.ItemsEntry.COLUMN_ITEM_QUANTITY));
        String itemSold = cursor.getString(cursor.getColumnIndex(ItemContract.ItemsEntry.COLUMN_ITEM_SOLD));

        nameTextView.setText(itemName);
        priceTextView.setText(PRICE + itemPrice);
        quantityTextView.setText(itemQuantity);
        soldTextView.setText(itemSold);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                if (itemQty > 0) {
                    int mItemQty;
                    int mSale;
                    mItemQty = (itemQty - 1);
                    mSale = (sale+1);
                    values.put(ItemContract.ItemsEntry.COLUMN_ITEM_QUANTITY, mItemQty);
                    values.put(ItemContract.ItemsEntry.COLUMN_ITEM_SOLD,mSale);
                    Uri uri = ContentUris.withAppendedId(ItemContract.ItemsEntry.CONTENT_URI, itemId);
                    context.getContentResolver().update(uri, values, null, null);
                }
                context.getContentResolver().notifyChange(ItemContract.ItemsEntry.CONTENT_URI, null);
            }
        });
    }


}

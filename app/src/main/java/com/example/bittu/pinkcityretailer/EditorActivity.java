package com.example.bittu.pinkcityretailer;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bittu.pinkcityretailer.data.ItemContract.ItemsEntry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "MainActivity";
    Uri mCurrentUri;
    ImageView itemImage;
    EditText mItemNameEditText;
    EditText mItemPriceEditText;
    EditText mItemQuantityEditText;
    EditText mSupplierName;

    private boolean mItemHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent i = getIntent();
        mCurrentUri = i.getData();
        if (mCurrentUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_item));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));


            getLoaderManager().initLoader(0, null, this);
        }
        itemImage = (ImageView) findViewById(R.id.itemImage);
        mItemNameEditText = (EditText) findViewById(R.id.edit_item_name);
        mItemPriceEditText = (EditText) findViewById(R.id.edit_item_price);
        mItemQuantityEditText = (EditText) findViewById(R.id.edit_item_quantity);
        mSupplierName = (EditText) findViewById(R.id.edit_supplier_name);
        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

        itemImage.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mItemPriceEditText.setOnTouchListener(mTouchListener);
        mItemQuantityEditText.setOnTouchListener(mTouchListener);
        mItemNameEditText.setOnTouchListener(mTouchListener);
    }

    private void saveItem() {
        String itemNameString = mItemNameEditText.getText().toString().trim();
        String itemPriceString = mItemPriceEditText.getText().toString().trim();
        String itemQuantityString = mItemQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierName.getText().toString().trim();


        if (mCurrentUri == null &&
                TextUtils.isEmpty(itemNameString) &&
                TextUtils.isEmpty(itemQuantityString)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ItemsEntry.COLUMN_ITEM_NAME, itemNameString);
        values.put(ItemsEntry.COLUMN_SUPPLIER_NAME, supplierNameString);

        if(itemImage.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) itemImage.getDrawable()).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] img = bos.toByteArray();
            values.put(ItemsEntry.COLUMN_ITEM_IMAGE, img);
        }

        int price = 0;
        if (!TextUtils.isEmpty(itemPriceString)) {
            price = Integer.parseInt(itemPriceString);
        }
        int quantity = 0;
        if (!TextUtils.isEmpty(itemQuantityString)) {
            quantity = Integer.parseInt(itemQuantityString);
        }
        values.put(ItemsEntry.COLUMN_ITEM_PRICE, price);
        values.put(ItemsEntry.COLUMN_ITEM_QUANTITY, quantity);

        if (mCurrentUri == null) {

            Uri newUri = getContentResolver().insert(ItemsEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_item_insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_item_insert_success),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_item_insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_item_insert_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mItemHasChanged) {
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
            case R.id.action_order:
                orderMore();
                return true;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentUri == null) {
            MenuItem deleteItem = menu.findItem(R.id.action_delete);
            MenuItem orderItem = menu.findItem(R.id.action_order);
            deleteItem.setVisible(false);
            orderItem.setVisible(false);
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    String path = getPathFromURI(selectedImageUri);
                    Log.i(TAG, "Image Path : " + path);
                    // Set the image in ImageView
                    itemImage.setImageURI(selectedImageUri);
                }
            }
        }
    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemsEntry._ID,
                ItemsEntry.COLUMN_ITEM_NAME,
                ItemsEntry.COLUMN_ITEM_PRICE,
                ItemsEntry.COLUMN_ITEM_QUANTITY,
                ItemsEntry.COLUMN_SUPPLIER_NAME,
                ItemsEntry.COLUMN_ITEM_IMAGE};

        return new CursorLoader(this, mCurrentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            String itemName = cursor.getString(cursor.getColumnIndex(ItemsEntry.COLUMN_ITEM_NAME));
            int itemPrice = cursor.getInt(cursor.getColumnIndex(ItemsEntry.COLUMN_ITEM_PRICE));
            int itemQuantity = cursor.getInt(cursor.getColumnIndex(ItemsEntry.COLUMN_ITEM_QUANTITY));
            String supplierName = cursor.getString(cursor.getColumnIndex(ItemsEntry.COLUMN_SUPPLIER_NAME));

            if(cursor.getBlob(cursor.getColumnIndex((ItemsEntry.COLUMN_ITEM_IMAGE)))!=null){
                byte[] itemImg = cursor.getBlob(cursor.getColumnIndex(ItemsEntry.COLUMN_ITEM_IMAGE));
                ByteArrayInputStream imageStream = new ByteArrayInputStream(itemImg);
                Bitmap bitmapImage = BitmapFactory.decodeStream(imageStream);
                itemImage.setImageBitmap(bitmapImage);
            }


            mItemNameEditText.setText(itemName);
            mItemQuantityEditText.setText(Integer.toString(itemQuantity));
            mItemPriceEditText.setText(Integer.toString(itemPrice));
            mSupplierName.setText(supplierName);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemNameEditText.setText("");
        mSupplierName.setText("");
        mItemPriceEditText.setText("");
        mItemQuantityEditText.setText("");
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
                deleteItem();
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

    private void deleteItem() {
        if (mCurrentUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_item_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_item_delete_success),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
    private void orderMore(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        String name = mItemNameEditText.getText().toString();
        intent.putExtra(Intent.EXTRA_SUBJECT, "I want to order " + name);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}

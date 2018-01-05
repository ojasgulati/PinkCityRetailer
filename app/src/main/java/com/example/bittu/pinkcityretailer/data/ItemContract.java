package com.example.bittu.pinkcityretailer.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ItemContract {
    public ItemContract() {
    }
    public static final String CONTENT_AUTHORITY = "com.example.bittu.pinkcityretailer";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";

    public static final class ItemsEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public final static String TABLE_NAME = "items";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_ITEM_NAME ="item_name";

        public final static String COLUMN_ITEM_PRICE ="item_price";

        public final static String COLUMN_ITEM_QUANTITY = "item_quantity";

        public final static String COLUMN_ITEM_SOLD = "item_sold";

        public final static String COLUMN_ITEM_IMAGE = "item_image";

        public final static String COLUMN_SUPPLIER_NAME ="supplier_name";

    }
}

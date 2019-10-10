package com.example.android.flowershop.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ShopContract {
    private ShopContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.flowershop";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SHOP = "flowers";

    public static final class ShopEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SHOP);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOP;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOP;

        public final static String TABLE_NAME = "Flowers";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_FLOWER_NAME = "name";
        public final static String COLUMN_FLOWER_QUANTITY = "quantity";
        public final static String COLUMN_FLOWER_IMAGE = "image";
        public final static String COLUMN_FLOWER_PRICE = "price";
        public final static String COLUMN_SELLER_NAME = "seller";

    }
}

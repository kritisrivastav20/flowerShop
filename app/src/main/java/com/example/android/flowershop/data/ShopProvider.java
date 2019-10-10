package com.example.android.flowershop.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.flowershop.data.ShopContract.ShopEntry;

public class ShopProvider extends ContentProvider {
    public static final String LOG_TAG = ShopProvider.class.getSimpleName();
    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ShopContract.CONTENT_AUTHORITY, ShopContract.PATH_SHOP, ITEMS);
        sUriMatcher.addURI(ShopContract.CONTENT_AUTHORITY, ShopContract.PATH_SHOP + "/#", ITEM_ID);
    }

    private ShopdbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ShopdbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                cursor = database.query(ShopContract.ShopEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ITEM_ID:
                selection = ShopEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ShopEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {

        String name = values.getAsString(ShopEntry.COLUMN_FLOWER_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }
        Integer quantity = values.getAsInteger(ShopEntry.COLUMN_FLOWER_QUANTITY);
        if (quantity == null && quantity <= 0) {
            throw new IllegalArgumentException("Item requires valid quantity");
        }
        String price = values.getAsString(ShopEntry.COLUMN_FLOWER_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Item requires valid price");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ShopEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selection = ShopEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ShopEntry.COLUMN_FLOWER_NAME)) {
            String name = values.getAsString(ShopEntry.COLUMN_FLOWER_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }
        if (values.containsKey(ShopEntry.COLUMN_FLOWER_QUANTITY)) {
            Integer QUANTITY = values.getAsInteger(ShopEntry.COLUMN_FLOWER_QUANTITY);
            if (QUANTITY == null) {
                throw new IllegalArgumentException("Item requires valid quantity");
            }
        }
        if (values.containsKey(ShopEntry.COLUMN_FLOWER_PRICE)) {
            String PRICE = values.getAsString(ShopEntry.COLUMN_FLOWER_PRICE);
            if (PRICE == null) {
                throw new IllegalArgumentException("Item requires valid price");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ShopEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                rowsDeleted = database.delete(ShopEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                selection = ShopEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ShopEntry.TABLE_NAME, selection, selectionArgs);
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
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return ShopEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ShopEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

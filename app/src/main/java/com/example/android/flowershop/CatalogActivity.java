package com.example.android.flowershop;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.android.flowershop.data.ShopContract.ShopEntry;
import java.io.ByteArrayOutputStream;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int SHOP_LOADER = 0;
    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();
    ShopCursorAdapter mShopAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView ShopListView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        ShopListView.setEmptyView(emptyView);
        mShopAdapter = new ShopCursorAdapter(CatalogActivity.this, null);
        ShopListView.setAdapter(mShopAdapter);
        ShopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(LOG_TAG, "clicked " + position);
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(ShopEntry.CONTENT_URI, id);

                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(SHOP_LOADER, null, this);
    }

    private void insertPet() {

        Drawable myDrawable = getResources().getDrawable(R.drawable.flower);
        Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        myLogo.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
//        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        ContentValues values = new ContentValues();
        values.put(ShopEntry.COLUMN_FLOWER_NAME, "Orchids");
        values.put(ShopEntry.COLUMN_FLOWER_QUANTITY, "20");
        values.put(ShopEntry.COLUMN_FLOWER_PRICE, "3");
        values.put(ShopEntry.COLUMN_SELLER_NAME, "Isha");
        values.put(ShopEntry.COLUMN_FLOWER_IMAGE, b);

        Uri newUri = getContentResolver().insert(ShopEntry.CONTENT_URI, values);
    }

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(ShopEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from shop database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.insert_dummy_data:
                insertPet();
                return true;

            case R.id.action_delete:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ShopEntry._ID,
                ShopEntry.COLUMN_FLOWER_NAME,
                ShopEntry.COLUMN_FLOWER_PRICE,
                ShopEntry.COLUMN_FLOWER_QUANTITY,
                ShopEntry.COLUMN_SELLER_NAME,
                ShopEntry.COLUMN_FLOWER_IMAGE};


        return new CursorLoader(this,
                ShopEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mShopAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mShopAdapter.swapCursor(null);
    }
}



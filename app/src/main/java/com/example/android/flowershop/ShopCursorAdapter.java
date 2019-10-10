package com.example.android.flowershop;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.flowershop.data.ShopContract;

public class ShopCursorAdapter extends CursorAdapter {

    public ShopCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final int id = cursor.getInt(cursor.getColumnIndex(ShopContract.ShopEntry._ID));

        TextView productName = (TextView) view.findViewById(R.id.product_name);
        TextView quantity = (TextView) view.findViewById(R.id.quantity);
        TextView price = (TextView) view.findViewById(R.id.product_price);
        Button sale = (Button) view.findViewById(R.id.sale);
        ImageView image = (ImageView) view.findViewById(R.id.product_image);
        int nameColumnIndex = cursor.getColumnIndex(ShopContract.ShopEntry.COLUMN_FLOWER_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ShopContract.ShopEntry.COLUMN_FLOWER_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ShopContract.ShopEntry.COLUMN_FLOWER_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(ShopContract.ShopEntry.COLUMN_FLOWER_IMAGE);

        byte[] Image = cursor.getBlob(imageColumnIndex);
        Bitmap bp = BitmapFactory.decodeByteArray(Image, 0, Image.length);
        String name = cursor.getString(nameColumnIndex);
        String flower_price = cursor.getString(priceColumnIndex);
        String quant = cursor.getString(quantityColumnIndex);
        final int Quantity = Integer.parseInt(quant);

        productName.setText(name);
        quantity.setText(String.format("In stock - " + Integer.toString(Quantity)));
        price.setText("Price/unit - Rs." + flower_price);
        image.setImageBitmap(bp);

        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver resolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();
                Uri currentProductUri = ContentUris.withAppendedId(ShopContract.ShopEntry.CONTENT_URI, id);

                if (Quantity > 0) {

                    values.put(ShopContract.ShopEntry.COLUMN_FLOWER_QUANTITY, Quantity - 1);
                    resolver.update(

                            currentProductUri,
                            values,
                            null,
                            null);

                    Toast.makeText(view.getContext(), "Item sold", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(view.getContext(), "No stock", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}



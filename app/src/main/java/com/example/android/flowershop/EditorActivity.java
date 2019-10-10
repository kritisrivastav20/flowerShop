package com.example.android.flowershop;

import android.app.Activity;
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
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.android.flowershop.data.ShopContract.ShopEntry;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentItemUri;
    EditText flowerName;
    EditText sellerName;
    EditText flowerPrice;
    EditText flowerQuantity;
    Button reduceQuantity;
    Button increaseQuantity;
    Button addImage;
    ImageView productImage;
    Button orderItem;
    private boolean mItemHasChanged = false;
    private static final int SHOP_LOADER = 0;
    public static final int GET_FROM_GALLERY = 3;

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
        setContentView(R.layout.add_product);
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        if (mCurrentItemUri == null) {
            setTitle("Add an item");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit item");
            getLoaderManager().initLoader(SHOP_LOADER, null, EditorActivity.this);
        }
        flowerName = (EditText) findViewById(R.id.flower_name);
        flowerQuantity = (EditText) findViewById(R.id.add_quant);
        flowerPrice = (EditText) findViewById(R.id.flower_price);
        sellerName = (EditText) findViewById(R.id.seller_name);
        reduceQuantity = (Button) findViewById(R.id.minus_button);
        increaseQuantity = (Button) findViewById(R.id.plus_button);
        addImage = (Button) findViewById(R.id.upload_image);
        orderItem = (Button) findViewById(R.id.order);
        productImage = (ImageView) findViewById(R.id.flower_image);
        flowerName.setOnTouchListener(mTouchListener);
        flowerQuantity.setOnTouchListener(mTouchListener);
        flowerPrice.setOnTouchListener(mTouchListener);
        sellerName.setOnTouchListener(mTouchListener);

        reduceQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReducefromQuantity();
            }
        });
        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IncreasefromQuantity();
            }
        });
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
        orderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Item required for flower shop";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Order via"));
            }
        });
    }

    private boolean saveItem() {
        String nameString = flowerName.getText().toString().trim();
        if (nameString.length() == 0) {
            Toast.makeText(this, "Add the item name",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        String quantity = flowerQuantity.getText().toString().trim();
        if (quantity.length() == 0) {
            Toast.makeText(this, "Quantity is invalid",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        String price = flowerPrice.getText().toString().trim();
        if (price.length() == 0) {
            Toast.makeText(this, "Enter a valid price",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        String SellerName = sellerName.getText().toString().trim();
        if (SellerName.length() == 0) {
            Toast.makeText(this, "Enter the seller name",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        Bitmap bitmap = ((BitmapDrawable) productImage.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        ContentValues values = new ContentValues();
        values.put(ShopEntry.COLUMN_FLOWER_NAME, nameString);
        values.put(ShopEntry.COLUMN_FLOWER_QUANTITY, quantity);
        values.put(ShopEntry.COLUMN_FLOWER_PRICE, price);
        values.put(ShopEntry.COLUMN_FLOWER_IMAGE, byteArray);
        values.put(ShopEntry.COLUMN_SELLER_NAME, SellerName);
        if (mCurrentItemUri == null) {
            Uri newUri = getContentResolver().insert(ShopEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Error with saving item",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item saved",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Error updating item",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item updated",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
//                ByteArrayOutputStream baos=new  ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
//                byte [] b=baos.toByteArray();
//                String temp= Base64.encodeToString(b, Base64.DEFAULT);
                productImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void ReducefromQuantity() {
        int quantity = Integer.parseInt(flowerQuantity.getText().toString());

        if (quantity != 0) {
            quantity = quantity - 1;
            String quant = Integer.toString(quantity);
            flowerQuantity.setText(quant);
        }
        mItemHasChanged = true;
    }

    public void IncreasefromQuantity() {
        int quantity = Integer.parseInt(flowerQuantity.getText().toString());

        if (quantity != 100) {
            quantity = quantity + 1;
            String quant = Integer.toString(quantity);
            flowerQuantity.setText(quant);
        } else {
            Toast.makeText(EditorActivity.this, "Quantity should not be more than 100",
                    Toast.LENGTH_SHORT).show();
        }
        mItemHasChanged = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveItem();
                return true;
            case R.id.delete:
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
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
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
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ShopEntry.COLUMN_FLOWER_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ShopEntry.COLUMN_FLOWER_PRICE);
            int quantColumnIndex = cursor.getColumnIndex(ShopEntry.COLUMN_FLOWER_QUANTITY);
            int sellerColumnIndex = cursor.getColumnIndex(ShopEntry.COLUMN_SELLER_NAME);
            int imageColumnIndex = cursor.getColumnIndex(ShopEntry.COLUMN_FLOWER_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            String seller = cursor.getString(sellerColumnIndex);
            String quantity = cursor.getString(quantColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            byte[] Image = cursor.getBlob(imageColumnIndex);
            Bitmap bp = BitmapFactory.decodeByteArray(Image, 0, Image.length);

            flowerName.setText(name);
            sellerName.setText(seller);
            flowerQuantity.setText(quantity);
            flowerPrice.setText(price);
            productImage.setImageBitmap(bp);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        flowerName.setText("");
        flowerQuantity.setText("");
        flowerPrice.setText("");
        sellerName.setText("");
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard changes");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
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
        builder.setMessage("Delete item");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Failed to delete the item",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

}

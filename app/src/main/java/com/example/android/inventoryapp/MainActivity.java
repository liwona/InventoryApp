package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.inventoryapp.data.InvetoryDbHelper;

public class MainActivity extends AppCompatActivity {

    private InvetoryDbHelper mDbHelper;
    Cursor cursor;

    /** EditText field to enter the book's name */
    private EditText mNameEditText;

    /** EditText field to enter price*/
    private EditText mPriceEditText;

    /** Spiiner field to enter quantity*/
    private Spinner mQuantitySpinner;

    /** EditText field to enter supplier name*/
    private EditText mSupplierName;

    /** EditText field to enter supplier phone number*/
    private EditText mSupplierPhone;

    /**
     * Quantity The possible values are:
     * 0 for out of stock, 1 <5, 2 >=5.
     */
    private int mQuantity = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_text_book_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_text_price);
        mQuantitySpinner = (Spinner) findViewById(R.id.spinner_quantity);
        mSupplierName = (EditText) findViewById(R.id.edit_text_supplier_name);
        mSupplierPhone = (EditText) findViewById(R.id.edit_text_supplier_phone_number);

        setupSpinner();
        saveButtonClick();

        mDbHelper = new InvetoryDbHelper(this);
        cursor = queryData();
        displayDatabaseInfo(cursor);
    }

    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter quantitySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_quantity_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        quantitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mQuantitySpinner.setAdapter(quantitySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mQuantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.less_than))) {
                        mQuantity = InventoryEntry.QUANTITY_LESS_THAN; // Male
                    } else if (selection.equals(getString(R.string.more_than))) {
                        mQuantity = InventoryEntry.QUANTITY_MORE_THAN; // Female
                    } else {
                        mQuantity = InventoryEntry.QUANTITY_OUT_OF_STOCK; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mQuantity = 0; // Unknown
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo(cursor);
    }

    private void insertData(){
        // Insert into database.
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        int price = Integer.parseInt(priceString);
        String supplierString = mSupplierName.getText().toString().trim();
        String supplierPhoneString = mSupplierPhone.getText().toString().trim();
        int supplierPhone = Integer.parseInt(supplierPhoneString);

        InvetoryDbHelper mDbHelper = new InvetoryDbHelper(this);
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_BOOK_NAME, nameString);
        values.put(InventoryEntry.COLUMN_BOOK_PRICE, price);
        values.put(InventoryEntry.COLUMN_BOOK_QUANTITY, mQuantity);
        values.put(InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierString);
        values.put(InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhone);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
        long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, values);

        if (newRowId == -1){
            Toast.makeText(this, "Error with saving the book", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Book saved with row id: " + newRowId, Toast.LENGTH_LONG).show();
        }
    }

    private Cursor queryData(){
        /**
         * Query the database.
         * Always close the cursor when you're done reading from it.
         * This releases all its resources and makes it invalid.
         */
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        InvetoryDbHelper mDbHelper = new InvetoryDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_BOOK_NAME,
                InventoryContract.InventoryEntry.COLUMN_BOOK_PRICE,
                InventoryContract.InventoryEntry.COLUMN_BOOK_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE
        };

        cursor = db.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, null, null,
                null, null, null);

        Log.v("Query message:", cursor.toString());

        return cursor;
    }

    private void displayDatabaseInfo(Cursor cursor) {

        TextView displayView = (TextView) findViewById(R.id.books_text_view);

        try{
            //Create a header in the Text View that looks like this;
            //
            // The books table contains <number of rows in Cursor> books:
            // _id - name - price - quantity - supplier name - supplier phone
            //
            //In the while loop below, iterate through the rows of th cursor and display
            //the information from each column in this order.
            displayView.setText("The books table contains " + cursor.getCount() + " books.\n\n");
            displayView.append(InventoryEntry._ID + " - " +
                    InventoryEntry.COLUMN_BOOK_NAME + " - " +
                    InventoryEntry.COLUMN_BOOK_PRICE + " - " +
                    InventoryEntry.COLUMN_BOOK_QUANTITY + " - " +
                    InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME + " - " +
                    InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            //Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()){
                //Use that index to extract the String or Int value of the word
                //at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                int currentSupplierPhone = cursor.getInt(supplierPhoneColumnIndex);
                // Display the values from each column of the current row in the cursor ine the TextView
                displayView.append("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhone);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }


    private void saveButtonClick(){
        Button button = (Button) findViewById(R.id.action_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insertData();
                cursor = queryData();
                displayDatabaseInfo(cursor);
            }
        });
    }
}

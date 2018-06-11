package com.example.android.inventoryapp.data;


import android.provider.BaseColumns;

public class InventoryContract {

    private InventoryContract() {

    }

    public static abstract class InventoryEntry implements BaseColumns{

        public static final String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_NAME = "product_name";
        public static final String COLUMN_BOOK_PRICE = "price";
        public static final String COLUMN_BOOK_QUANTITY = "quantity";
        public static final String COLUMN_BOOK_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_BOOK_SUPPLIER_PHONE = "supplier_phone_number";

        /**
         * Possible values for the quantity 0- Out of Stock, 1: <5, 2: >=5
         */
        public static final int QUANTITY_OUT_OF_STOCK = 0;
        public static final int QUANTITY_LESS_THAN = 1;
        public static final int QUANTITY_MORE_THAN = 2;
    }
}

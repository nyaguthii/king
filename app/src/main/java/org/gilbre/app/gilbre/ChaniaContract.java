package org.gilbre.app.gilbre;

import android.provider.BaseColumns;

/**
 * Created by nyaguthii on 11/25/17.
 */

public class ChaniaContract {
    public static final String DB_NAME="chania.db";
    public static final int DB_VERSION=1;
  //creates the Receipts Table
    public static final String SQL_CREATE_ENTRIES=
            "CREATE TABLE "+ReceiptEntry.TABLE_NAME+"("+
            ReceiptEntry.COLUMN_NAME_ID+" INTEGER PRIMARY KEY,"+
            ReceiptEntry.COLUMN_NAME_CUSTOMER+" TEXT,"+
            ReceiptEntry.COLUMN_NAME_AMOUNT+" INTEGER,"+
            ReceiptEntry.COLUMN_NAME_REGISTRATION+" TEXT,"+
            ReceiptEntry.COLUMN_NAME_SERVED_BY+" TEXT,"+
            ReceiptEntry.COLUMN_NAME_MEMBER_ID+" TEXT,"+
            ReceiptEntry.COLUMN_NAME_TYPE+" TEXT)";
    public static final String SQL_DELETE_ENTRIES=
            "DROP TABLE IF EXISTS "+ReceiptEntry.TABLE_NAME;

    public static final String SQL_CREATE_TYPES=
            "CREATE TABLE "+PaymentTypeEntry.TABLE_NAME+"("+
                    PaymentTypeEntry.COLUMN_NAME_ID+" INTEGER PRIMARY KEY,"+
                    PaymentTypeEntry.COLUMN_NAME_NAME+" TEXT)";
    public static final String SQL_DELETE_TYPES=
            "DROP TABLE IF EXISTS "+PaymentTypeEntry.TABLE_NAME;

    public static final String SQL_CREATE_CUSTOMERS=
            "CREATE TABLE "+CustomerEntry.TABLE_NAME+"("+
                    CustomerEntry.COLUMN_NAME_ID+" INTEGER PRIMARY KEY,"+
                    CustomerEntry.COLUMN_NAME_NAME+" TEXT)";
    public static final String SQL_DELETE_CUSTOMERS=
            "DROP TABLE IF EXISTS "+CustomerEntry.TABLE_NAME;
    public static final String SQL_CREATE_VEHICLES=
            "CREATE TABLE "+VehicleEntry.TABLE_NAME+"("+
                    VehicleEntry.COLUMN_NAME_ID+" INTEGER PRIMARY KEY,"+
                    VehicleEntry.COLUMN_NAME_REGISTRATION+" TEXT)";
    public static final String SQL_DELETE_VEHICLES=
            "DROP TABLE IF EXISTS "+VehicleEntry.TABLE_NAME;
    public static final String SQL_CREATE_PLACES=
            "CREATE TABLE "+PlaceEntry.TABLE_NAME+"("+
                    PlaceEntry.COLUMN_NAME_ID+" INTEGER PRIMARY KEY,"+
                    PlaceEntry.COLUMN_NAME_NAME+" TEXT)";
    public static final String SQL_DELETE_PLACES=
            "DROP TABLE IF EXISTS "+PlaceEntry.TABLE_NAME;
    public static final String SQL_CREATE_PARCELS=
            "CREATE TABLE "+ParcelEntry.TABLE_NAME+"("+
                    ParcelEntry.COLUMN_NAME_ID+" INTEGER PRIMARY KEY,"+
                    ParcelEntry.COLUMN_NAME_AMOUNT+" INTEGER,"+
                    ParcelEntry.COLUMN_NAME_TYPE+" TEXT,"+
                    ParcelEntry.COLUMN_NAME_TO+" TEXT,"+
                    ParcelEntry.COLUMN_NAME_FROM+" TEXT,"+
                    ParcelEntry.COLUMN_NAME_RECEIVER+" TEXT,"+
                    ParcelEntry.COLUMN_NAME_SENDER_NAME+" TEXT,"+
                    ParcelEntry.COLUMN_NAME_RECEIVER_NAME+" TEXT,"+
                    ParcelEntry.COLUMN_NAME_SENDER+" TEXT)";
    public static final String SQL_DELETE_PARCELS=
            "DROP TABLE IF EXISTS "+ParcelEntry.TABLE_NAME;

    public static class ReceiptEntry {
        public static final String TABLE_NAME="receipts";
        public static final String COLUMN_NAME_ID="id";
        public static final String COLUMN_NAME_CUSTOMER="customer";
        public static final String COLUMN_NAME_AMOUNT="amount";
        public static final String COLUMN_NAME_TYPE="type";
        public static final String COLUMN_NAME_REGISTRATION="registration";
        public static final String COLUMN_NAME_SERVED_BY="served_by";
        public static final String COLUMN_NAME_MEMBER_ID="member_id";



    }
    public static class PaymentTypeEntry {
        public static final String TABLE_NAME="payment_types";
        public static final String COLUMN_NAME_ID="id";
        public static final String COLUMN_NAME_NAME="name";
    }

    public static class CustomerEntry {
        public static final String TABLE_NAME="customers";
        public static final String COLUMN_NAME_ID="id";
        public static final String COLUMN_NAME_NAME="name";
    }
    public static class VehicleEntry {
        public static final String TABLE_NAME="vehicles";
        public static final String COLUMN_NAME_ID="id";
        public static final String COLUMN_NAME_REGISTRATION="registration";
    }
    public static class PlaceEntry {
        public static final String TABLE_NAME="places";
        public static final String COLUMN_NAME_ID="id";
        public static final String COLUMN_NAME_NAME="name";
    }
    public static class ParcelEntry {
        public static final String TABLE_NAME="parcels";
        public static final String COLUMN_NAME_ID="id";
        public static final String COLUMN_NAME_TYPE="type";
        public static final String COLUMN_NAME_SENDER="sender";
        public static final String COLUMN_NAME_RECEIVER="receiver";
        public static final String COLUMN_NAME_TO="to_place";
        public static final String COLUMN_NAME_FROM="from_place";
        public static final String COLUMN_NAME_SERVED_BY="servedBy";
        public static final String COLUMN_NAME_SENDER_NAME="sender_name";
        public static final String COLUMN_NAME_RECEIVER_NAME="receiver_name";
        public static final String COLUMN_NAME_AMOUNT="amount";
    }
}

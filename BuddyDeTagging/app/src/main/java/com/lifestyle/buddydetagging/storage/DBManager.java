/**
 *
 */
package com.lifestyle.buddydetagging.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;


import com.lifestyle.buddydetagging.base.ContextManager;

import java.util.ArrayList;
import java.util.Calendar;

public class DBManager {


    public static final String TBL_TRANSACTION = "covid_survey_data";

    private static final String CREATE_TRANSACTION_TABLE = "create table " + TBL_TRANSACTION
            + "(_id integer primary key autoincrement, transaction_id integer not null, client_generated_transid text not null, "
            + "trans_data text not null,  trans_date text not null,   "
            + "trans_status text not null, trans_submit_to_server_status text not null, "
            + "customer_code text not null,  customer_name text not null, "
            + "customer_contact text not null, emp_id text not null);";


    public static final String INSERT_INTO_TRANSACTION_TABLE = "insert into " + TBL_TRANSACTION
            + "( transaction_id, client_generated_transid, trans_data, trans_date, trans_status, " +
            "trans_submit_to_server_status, " +
            "customer_code, customer_name,  customer_contact, emp_id)"
            + "VALUES(?,?,?,?,?,?,?,?,?,?)";

    public static final String INSERT_INTO_TRANSACTION_TABLE22 = "insert into " + TBL_TRANSACTION
            + "( transaction_id, client_generated_transid, trans_data, trans_date, trans_status,  trans_submit_to_server_status, customer_code, customer_name,  customer_contact, emp_id)"
            + "VALUES(?,?,?,?,?,?,?,?,?,?)";


    private static final String DB_NAME = "mstore.db";
    private static final int DB_VER = 1;
    /* ** ** */
    private final static String TAG = DBManager.class.getCanonicalName();
    private static DBManager iSelf;
    private SQLiteDatabase iDB;
    private DatabaseHelper iDbHelper;
    private Context iContext;
    private SQLiteStatement iStmt;

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     */
    public static DBManager getInstance() {
        if (iSelf == null) {
            iSelf = new DBManager();
            return iSelf;

        } else {
            return iSelf;
        }
    }

    /**
     * this method is used for creating or opening the connection.
     *
     * @return
     * @throws SQLException
     */
    public DBManager openDB() throws SQLException {
        iContext = ContextManager.getAppContext();
        iDbHelper = new DatabaseHelper(iContext);
        iDB = iDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * this method is used for closing the connection.
     */
    public void close() {
        try {
            iDbHelper.close();
        } catch (Exception e) {
            Log.e("[ " + TAG + " ] :", "'" + TAG + "' - close() : ", e);
        }
    }

    public void createTables() {
        if (iDB == null) {
            // OPENING DATABASE CONN.
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            new DatabaseHelper(ContextManager.getAppContext()).onCreate(iDB);
        } catch (Exception e) {
            Log.e("[ " + TAG + " ]", "createTables()", e);
            e.printStackTrace();
        }
    }

    public void createNamedTable(String CREATE_NAMED_TABLE) {
        if (iDB == null) {
            // OPENING DATABASE CONN.
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            // iDB.execSQL(CREATE_NAMED_TABLE);
            new DatabaseHelper(ContextManager.getAppContext()).onCreateNamedTable(iDB, CREATE_NAMED_TABLE);
        } catch (Exception e) {
            Log.e("[ " + TAG + " ] : ", "createNamedTable() : ", e);
        }
    }

    public void insertTables(String TBL_NAME, Object aData) {
        if (iDB == null) {
            // OPENING DATABASE CONN.
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                Log.e("[ " + TAG + " ] :", "insertTables() :", e);
            }
        }
        try {
            String query = "insert into ";
            if (TBL_NAME.equalsIgnoreCase(TBL_TRANSACTION)) {
                TransactionData temp = (TransactionData) aData;

                query += TBL_TRANSACTION + "(transaction_id, client_generated_transid, trans_data, trans_date, trans_status,  trans_submit_to_server_status, customer_code, customer_name,  customer_contact, emp_id)"
                        + " values('" + temp.getTransaction_id() + "','" + temp.getClient_generated_transid() + "', '" + temp.getTrans_data() + "', '" + temp.getTrans_date() + "',  '" + temp.getTrans_status() + "', '"
                        + temp.getTrans_submit_to_server_status() + "',   '" + temp.getCustomer_code() + "', '" + temp.getCustomer_name() + "', '" + temp.getCustomer_contact() + "', '" + temp.getEmp_id() + "');";

                temp = null;
            }

            if (iStmt != null)
                iStmt = null;
            else
                iDB.execSQL(query);

        } catch (Exception e) {
            Log.e("[" + TAG + "]: ", "insertTables(): ", e);
        }
    }

    /**
     * @param TBL_NAME
     * @param aData
     */
    public void updateTableRow(String TBL_NAME, Object aData, String whatToUpdate) {
        if (iDB == null) {
            // OPENING DATABASE CONN.
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                Log.e("[ " + TAG + " ] :", "SftDBManager - updateTableRow() :", e);
            }
        }

        try {
            ContentValues args = new ContentValues();

             /*if (TBL_NAME.equalsIgnoreCase(TBL_CUSTOMER_MASTER)) {
                CustomerData cData = (CustomerData) aData;
                args.put("customer_name", cData.getCustomerName());

                iDB.update( DBManager.TBL_CUSTOMER_MASTER, args, "customer_id = " + cData.getCustomerId(), null);
                cData = null;
            }*/
        } catch (Exception e) {
            Log.e("[" + TAG + "]: ", "updateTableRow() : ", e);
        }
    }

    // get all data of specified Table.
    public ArrayList<Object> getAllTableData(String TBL_NAME, String id, String ARGS1, int whatToFetch) {
        ArrayList<Object> aData = new ArrayList<Object>();
        if (iDB == null) {
            // OPENING DATABASE CONN.
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                Log.e("[ " + TAG + " ] :", "'" + TAG + "' - getAllTableData() :", e);
            }
        }

        if (TBL_NAME.equalsIgnoreCase(TBL_TRANSACTION)) {
            try {
                Cursor cr = null;
                cr = iDB.query(TBL_TRANSACTION, null, null, null, null, null, null);
                if (cr.moveToFirst()) {
                    do {
                        TransactionData temp = new TransactionData();

                        temp.set_id(cr.getInt(cr.getColumnIndex("_id")));
                        temp.setTransaction_id(cr.getInt(cr.getColumnIndex("transaction_id")));
                        temp.setClient_generated_transid(cr.getString(cr.getColumnIndex("client_generated_transid")));
                        temp.setTrans_data(cr.getString(cr.getColumnIndex("trans_data")));
                        temp.setTrans_date(cr.getString(cr.getColumnIndex("trans_date")));

                         temp.setTrans_status(cr.getString(cr.getColumnIndex("trans_status")));
                        temp.setTrans_submit_to_server_status(cr.getString(cr.getColumnIndex("trans_submit_to_server_status")));
                        temp.setCustomer_code(cr.getString(cr.getColumnIndex("customer_code")));
                        temp.setCustomer_name(cr.getString(cr.getColumnIndex("customer_name")));
                        temp.setCustomer_contact(cr.getString(cr.getColumnIndex("customer_contact")));
                        temp.setEmp_id(cr.getString(cr.getColumnIndex("emp_id")));
                        aData.add(temp);
                    } while (cr.moveToNext());
                }

                if (cr != null && !cr.isClosed()) {
                    cr.close();
                }
            } catch (Exception e) {
                Log.e("[" + TAG + "]: ", "TBL_TRANSACTION - getAllTableData(): ", e);
            }
        }
        return aData;
    }

    /**
     * @param TBL_NAME
     * @param id
     * @return
     */
    public Object getTableData(String TBL_NAME, int id, String ARGS1, int whatToFetch) {
        Object retVal = null;

        if (iDB == null) {
            // OPENING DATABASE CONN.
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                // Logger.write("Failed to OPEN DATABASE : SftDBManager -
                // getTableData() : - "
                // + e);
            }
        }

        if (TBL_NAME.equalsIgnoreCase(TBL_TRANSACTION)) {
            try {
                Cursor cr = null;

                /*cr = iDB.query(TBL_TRANSACTION, null, null, null, null, null, null);*/
                /*if (whatToFetch == AppConstant.REQUEST_FOR_LAST_INVOICE_NUMBER)
                    cr = iDB.rawQuery("SELECT * FROM " + TBL_TRANSACTION + " WHERE invoice_number=(SELECT max(invoice_number) FROM " + TBL_TRANSACTION + " where trans_type like '" + ARGS1 + "')", null);
                else if (whatToFetch == AppConstant.REQUEST_FOR_TRANS)
                    cr = iDB.query(TBL_TRANSACTION, null, "_id=?", new String[]{ARGS1}, null, null, null);

                else*/
                    cr = iDB.query(TBL_TRANSACTION, null, "_id=?", new String[]{"" + id}, null, null, null);

                if (!cr.moveToFirst()) {
                    return null;
                }

                TransactionData temp = new TransactionData();


                temp.set_id(cr.getInt(cr.getColumnIndex("_id")));
                temp.setTransaction_id(cr.getInt(cr.getColumnIndex("transaction_id")));
                temp.setClient_generated_transid(cr.getString(cr.getColumnIndex("client_generated_transid")));
                temp.setTrans_data(cr.getString(cr.getColumnIndex("trans_data")));
                temp.setTrans_date(cr.getString(cr.getColumnIndex("trans_date")));

                temp.setTrans_status(cr.getString(cr.getColumnIndex("trans_status")));
                temp.setTrans_submit_to_server_status(cr.getString(cr.getColumnIndex("trans_submit_to_server_status")));
                temp.setCustomer_code(cr.getString(cr.getColumnIndex("customer_code")));
                temp.setCustomer_name(cr.getString(cr.getColumnIndex("customer_name")));
                temp.setCustomer_contact(cr.getString(cr.getColumnIndex("customer_contact")));
                temp.setEmp_id(cr.getString(cr.getColumnIndex("emp_id")));
                retVal = temp;


                if (cr != null && !cr.isClosed()) {
                    cr.close();
                }
            } catch (Exception e) {
                Log.e("[" + TAG + "]: ", "TBL_TRANSACTION - getTableData(): ", e);
            }
        }
        return retVal;
    }

    public int getRowId(String TBL_NAME, String id) {
        int rowId = 0;
        if (iDB == null) {
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                Log.e("[ " + TAG + " ] :", "'" + TAG + "' - getRowId()", e);
            }
        }
        Cursor cr = null;
        try {
            if (TBL_NAME.equalsIgnoreCase(TBL_TRANSACTION)) {
                cr = iDB.rawQuery("SELECT _id FROM " + TBL_NAME + " WHERE brand = ?", new String[]{id});
            }
            if (cr.moveToFirst())
                rowId = cr.getInt(0);
            else
                rowId = 0;

        } catch (Exception e) {
            Log.e("[ " + TAG + " ] :", "getRowId() : ", e);
        } finally {
            // close the SqlCursor object.
            if (cr != null && !cr.isClosed()) {
                cr.close();
            }
        }
        return rowId;
    }

    public int lastRowId(String TBL_NAME, String... args) {
        int lastRowCount = 0;
        if (iDB == null) {
            // OPENING DATABASE CONN.
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                Log.e("[ " + TAG + " ] :", "'" + TAG + "' - lastRowId()", e);
            }
        }
        Cursor cr = null;
        try {
            if (args != null & args.length == 2) {
                cr = iDB.rawQuery("SELECT max(_id) FROM " + TBL_NAME + " WHERE " + args[0] + " = " + "'" + args[1] + "'", null);
            } else
                cr = iDB.rawQuery("SELECT max(_id) FROM " + TBL_NAME, null);
            if (cr.moveToFirst()) {
                lastRowCount = cr.getInt(0);
            } else {
                lastRowCount = 0;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Log.e("[ " + TAG + " ] :", "lastRowId() : ", e);
        } finally {
            // close the SqlCursor object.
            if (cr != null && !cr.isClosed()) {
                cr.close();
            }
        }
        return lastRowCount;
    }

    public boolean checkIfDataExists(String TBL_NAME, String id) {
        boolean retVal = false;
        if (iDB == null) {
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                Log.e("[ " + TAG + " ] :", "'" + TAG + "' - checkIfDataExists()", e);
            }
        }
        Cursor cr = null;
        try {
            if (id != null && !TextUtils.isEmpty(id))
                cr = iDB.rawQuery("SELECT COUNT(*) FROM " + TBL_NAME + " where " + id, null);
            else
                cr = iDB.rawQuery("SELECT COUNT(*) FROM " + TBL_NAME, null);

            if (cr != null) {
                cr.moveToFirst(); // Always one row returned.
                retVal = cr.getInt(0) != 0;
            } else {
                retVal = false;
            }
        } catch (Exception e) {
            Log.e("[ " + TAG + " ] :", "checkIfDataExists() : ", e);
        } finally {
            // close the SqlCursor object.
            if (cr != null && !cr.isClosed()) {
                cr.close();
            }
        }
        return retVal;
    }

    public int getTotalCount(String TBL_NAME, String id) {
        int count = 0;
        if (iDB == null) {
            // OPENING DATABASE CONN.
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (TBL_NAME == null || iDB == null || !iDB.isOpen())
            return 0;

        Cursor cursor;
        if (id == null)
            cursor = iDB.rawQuery("SELECT * from " + TBL_NAME, null);
        else
            cursor = iDB.rawQuery("SELECT * FROM " + TBL_NAME + " WHERE " + id, null);
        count = cursor.getCount();

        // close the SqlCursor object.
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return count;
    }

    public boolean statusDataExists(String TBL_NAME, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        boolean rtrnValue = false;
        try {
            Cursor cr = null;
            cr = iDB.query(TBL_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
            if (cr.moveToFirst() && cr.getCount() != 0)
                rtrnValue = true;
            if (cr != null && !cr.isClosed()) {
                cr.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "statusDataExists() : ", e);
        }
        return rtrnValue;
    }

    public boolean isTableExists(String TBL_NAME) {
        if (iDB == null) {
            // OPENING DATABASE CONN.
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (TBL_NAME == null || iDB == null || !iDB.isOpen()) {
            return false;
        }
        Cursor cursor = iDB.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", TBL_NAME});
        if (!cursor.moveToFirst()) {
            return false;
        }
        int count = cursor.getInt(0);
        // close the SqlCursor object.
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return count > 0;
    }

    public boolean deleteRows(String TBL_NAME, String rowid) {
        if (iDB == null) {
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            if (TBL_NAME.equalsIgnoreCase(TBL_TRANSACTION)) {
                return iDB.delete(TBL_TRANSACTION, "_id" + "=" + rowid, null) > 0;
            }
        } catch (Exception e) {
            Log.e("[ " + TAG + " ] :", "'" + TAG + "' - deleteRows() : ", e);
            return 0 > 0;
        }
        return 0 > 0;
    }

    public boolean deleteRows(String TBL_NAME, String id, String whatToDelete, String... args1) {

        if (iDB == null) {
            // OPENING DATABASE CONN.
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            if (TBL_NAME.equalsIgnoreCase(TBL_TRANSACTION)) {
                return iDB.delete(TBL_TRANSACTION, "customer_code=?", args1) > 0;
            } else
                return iDB.delete(TBL_NAME, id, args1) > 0;
        } catch (Exception e) {
            Log.e("[ " + TAG + " ] :", "'" + TAG + "' - deleteRows() : ", e);
            return 0 > 0;
        }
        /*return 0 > 0;*/
    }

    public boolean deleteAllRows(String TBL_NAME) {
        if (iDB == null) {
            // OPENING DATABASE CONN.
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            return iDB.delete(TBL_NAME, null, null) > 0;
        } catch (Exception e) {
            Log.e("[ " + TAG + " ] :", "deleteAllRows() : ", e);
            return 0 > 0;
        }
    }

    public void dropNamedTable(String TBL_NAME) {
        if (iDB == null) {
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            iDB.execSQL("Drop table if exists " + TBL_NAME);
        } catch (Exception e) {
            // Log.e("[ " + TAG + " ] : ", "dropNamedTable(): ", e);
        }
    }

    public void dropTables() {
        if (iDB == null) {
            // OPENING DATABASE CONN.
            try {
                DBManager.getInstance().openDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            iDB.execSQL("Drop table if exists " + TBL_TRANSACTION);
        } catch (Exception e) {
            Log.e("[" + TAG + "] : ", "dropTables()", e);
            // e.printStackTrace();
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VER);
        }

        public void onCreateNamedTable(SQLiteDatabase iDB, String CREATE_NAMED_TABLE) {
            try {
                iDB.execSQL(CREATE_NAMED_TABLE);
            } catch (Exception e) {
                Log.e("[ " + TAG + " ] : ", "onCreateNamedTable() : ", e);
            }
        }

        @Override
        public void onCreate(SQLiteDatabase iDB) {
            Log.i("[" + TAG + "]: ", "Creating DataBase: Tables");
            //iDB.execSQL(CREATE_CUSTOMER_MASTER_TABLE);
            try {
                iDB.execSQL(CREATE_TRANSACTION_TABLE);
            } catch (Exception e) {// do nothing
            }
            Calendar cal = Calendar.getInstance();
            // add SYSTEM_DATE into shared_preference.
        }

        @Override
        public void onUpgrade(SQLiteDatabase iDB, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            iDB.execSQL("Drop table if exists " + TBL_TRANSACTION);
            iDB.execSQL(CREATE_TRANSACTION_TABLE);
            // RE-CREATE DATABSE STRUCTURE.
            onCreate(iDB);
        }
    }
}
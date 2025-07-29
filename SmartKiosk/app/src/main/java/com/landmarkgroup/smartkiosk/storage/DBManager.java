package com.landmarkgroup.smartkiosk.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.lifestyle.retail.utils.GeneralConstant;

import java.util.ArrayList;

public class DBManager {

    public static final String TBL_TRANSACTION = "pos_activity_transaction";
    public static final String TBL_TRANSACTION_MAIN = "pos_activity_transaction_main";

    private static final String CREATE_TRANSACTION_TABLE = "create table " + TBL_TRANSACTION
            + "(_id integer primary key autoincrement, transaction_id integer not null,   "
            + "  invoice_date text not null,  "
            + "trans_status text not null,  trans_submit_to_server_status text not null, "
            + "store_id text not null,  ord_confirm_url text not null,  kiosk_id text not null, " +
            " cust_mobile_num text not null, emp_id text not null  );";

    private static final String CREATE_TRANSACTION_TABLE_MAIN = "create table " + TBL_TRANSACTION_MAIN
            + "( _id integer primary key autoincrement, transaction_id integer not null,   "
            + "  invoice_date text not null,  "
            + "trans_status text not null,  trans_submit_to_server_status text not null, "
            + "store_id text not null,  ord_confirm_url text not null,  kiosk_id text not null, " +
            " cust_mobile_num text not null, emp_id text, order_type text  );";

    public static final String INSERT_INTO_TRANSACTION_TABLE = "insert into " + TBL_TRANSACTION + "(  transaction_id,  invoice_date,  trans_status, trans_submit_to_server_status,  store_id,  ord_confirm_url,  kiosk_id,  cust_mobile_num, emp_id)" + "VALUES(?,?,?,?,?,?,?,?,?)";
    public static final String INSERT_INTO_TRANSACTION_TABLE_MAIN = "insert into " + TBL_TRANSACTION_MAIN + "(  transaction_id,  invoice_date,  trans_status, trans_submit_to_server_status,  store_id,  ord_confirm_url,  kiosk_id,  cust_mobile_num, emp_id, order_type)" + "VALUES(?,?,?,?,?,?,?,?,?,?)";

    private static final String DB_NAME = "smartkiosk.db";
    private static final int DB_VER = 5;
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

                query += TBL_TRANSACTION + "(transaction_id,     invoice_date,   trans_status, trans_submit_to_server_status,  store_id,  ord_confirm_url,  kiosk_id,  cust_mobile_num, emp_id)"
                        + " values('" + temp.getTransaction_id() + "', '" + temp.getInvoice_date() + "', '" + temp.getTrans_status() + "',  '"
                        + temp.getTrans_submit_to_server_status() + "',  '" + temp.getStoreId() + "', '" + temp.getOrdConfirmUrl() + "','" + temp.getKioskId() + "','" + temp.getCustMobileNum() + "','" + temp.getEmpId() + "');";

                temp = null;

            } else if (TBL_NAME.equalsIgnoreCase(TBL_TRANSACTION_MAIN)) {
                TransactionData temp = (TransactionData) aData;

                Log.e("", "");
                Log.e("1", "Trans ID : " + temp.getTransaction_id());
                Log.e("2", "Trans Status : " + temp.getTrans_status());
                Log.e("3", "Server STatus : " + temp.getTrans_submit_to_server_status());
                Log.e("4", "Store ID : " + temp.getStoreId());
                Log.e("5", "Con url :" + temp.getOrdConfirmUrl());
                Log.e("6", "Kiosk Id : " + temp.getKioskId());
                Log.e("7", "Mobile Num :" + temp.getCustMobileNum());
                Log.e("8", "EMP ID :" + temp.getEmpId());
                Log.e("9", "Order Type :" + temp.getOrderType());

                query += TBL_TRANSACTION_MAIN + "(transaction_id,     invoice_date,   trans_status, trans_submit_to_server_status,  store_id,  ord_confirm_url,  kiosk_id,  cust_mobile_num, emp_id, order_type)"
                        + " values('" + temp.getTransaction_id() + "', '" + temp.getInvoice_date() + "', '" + temp.getTrans_status() + "',  '"
                        + temp.getTrans_submit_to_server_status() + "',  '" + temp.getStoreId() + "', '" + temp.getOrdConfirmUrl() + "','" + temp.getKioskId() + "','" + temp.getCustMobileNum() + "','" + temp.getEmpId() + "','" + temp.getOrderType() + "');";

                temp = null;

                /*iStmt = iDB.compileStatement(INSERT_INTO_TRANSACTION_TABLE_MAIN);
                //iStmt.bindLong(0, temp.getTransaction_id());
                iStmt.bindLong(1, temp.getTransaction_id());
                iStmt.bindString(2, temp.getTrans_status());
                iStmt.bindString(3, temp.getTrans_submit_to_server_status());
                iStmt.bindString(4, temp.getStoreId());
                iStmt.bindString(5, temp.getOrdConfirmUrl());
                iStmt.bindString(6, temp.getKioskId());
                iStmt.bindString(7, temp.getCustMobileNum());
                iStmt.bindString(8, temp.getEmpId());
                iStmt.executeInsert();*/

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

            if (TBL_NAME.equalsIgnoreCase(TBL_TRANSACTION)) {
                if (whatToUpdate.equals("")) {
                    TransactionData tdata = (TransactionData) aData;
                    args.put("trans_submit_to_server_status", tdata.getTrans_submit_to_server_status());
                    iDB.update(DBManager.TBL_TRANSACTION, args, "_id = " + tdata.get_id(), null);
                    /*File outputFile = new File(new File(Environment.getExternalStorageDirectory() + ContextManager.getAppContext().getResources().getString(R.string.backup_relativepath_on_sdcard)), tdata.getInvoice_number() + ".json");
                    try {
                        *//* -- DELETE THE FILE IF EXISTS -- *//*
                        if (outputFile.exists())
                            outputFile.delete();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }*/
                }

            } else if (TBL_NAME.equalsIgnoreCase(TBL_TRANSACTION_MAIN)) {
                if (whatToUpdate.equals("")) {
                    TransactionData tdata = (TransactionData) aData;
                    args.put("trans_submit_to_server_status", tdata.getTrans_submit_to_server_status());
                    iDB.update(DBManager.TBL_TRANSACTION_MAIN, args, "_id = " + tdata.get_id(), null);
                    /*File outputFile = new File(new File(Environment.getExternalStorageDirectory() + ContextManager.getAppContext().getResources().getString(R.string.backup_relativepath_on_sdcard)), tdata.getInvoice_number() + ".json");
                    try {
                        *//* -- DELETE THE FILE IF EXISTS -- *//*
                        if (outputFile.exists())
                            outputFile.delete();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }*/
                }

            }
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

                        temp.setStoreId(cr.getString(cr.getColumnIndex("store_id")));
                        temp.setOrdConfirmUrl(cr.getString(cr.getColumnIndex("ord_confirm_url")));
                        temp.setKioskId(cr.getString(cr.getColumnIndex("kiosk_id")));
                        temp.setCustMobileNum(cr.getString(cr.getColumnIndex("cust_mobile_num")));
                        temp.setEmpId(cr.getString(cr.getColumnIndex("emp_id")));

                        temp.setInvoice_date(cr.getString(cr.getColumnIndex("invoice_date")));
                        temp.setTrans_status(cr.getString(cr.getColumnIndex("trans_status")));
                        temp.setTrans_submit_to_server_status(cr.getString(cr.getColumnIndex("trans_submit_to_server_status")));

                        aData.add(temp);
                    } while (cr.moveToNext());
                }

                if (cr != null && !cr.isClosed()) {
                    cr.close();
                }
            } catch (Exception e) {
                Log.e("[" + TAG + "]: ", "TBL_TRANSACTION - getAllTableData(): ", e);
            }
        } else if (TBL_NAME.equalsIgnoreCase(TBL_TRANSACTION_MAIN)) {
            try {
                Cursor cr = null;
                cr = iDB.query(TBL_TRANSACTION_MAIN, null, null, null, null, null, null);
                if (cr.moveToFirst()) {
                    do {
                        TransactionData temp = new TransactionData();

                        temp.set_id(cr.getInt(cr.getColumnIndex("_id")));
                        temp.setTransaction_id(cr.getInt(cr.getColumnIndex("transaction_id")));

                        temp.setStoreId(cr.getString(cr.getColumnIndex("store_id")));
                        temp.setOrdConfirmUrl(cr.getString(cr.getColumnIndex("ord_confirm_url")));
                        temp.setKioskId(cr.getString(cr.getColumnIndex("kiosk_id")));
                        temp.setCustMobileNum(cr.getString(cr.getColumnIndex("cust_mobile_num")));
                        temp.setEmpId(cr.getString(cr.getColumnIndex("emp_id")));
                        temp.setInvoice_date(cr.getString(cr.getColumnIndex("invoice_date")));
                        temp.setTrans_status(cr.getString(cr.getColumnIndex("trans_status")));
                        temp.setTrans_submit_to_server_status(cr.getString(cr.getColumnIndex("trans_submit_to_server_status")));
                        temp.setOrderType(cr.getString(cr.getColumnIndex("order_type")));

                        aData.add(temp);
                    } while (cr.moveToNext());
                }

                if (cr != null && !cr.isClosed()) {
                    cr.close();
                }
            } catch (Exception e) {
                Log.e("[" + TAG + "]: ", "TBL_TRANSACTION_MAIN - getAllTableData(): ", e);
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
                if (whatToFetch == GeneralConstant.REQUEST_FOR_LAST_INVOICE_NUMBER)
                    cr = iDB.rawQuery("SELECT * FROM " + TBL_TRANSACTION + " WHERE invoice_number=(SELECT max(invoice_number) FROM " + TBL_TRANSACTION + " where trans_type like '" + ARGS1 + "')", null);
                else if (whatToFetch == GeneralConstant.REQUEST_FOR_TRANS)
                    cr = iDB.query(TBL_TRANSACTION, null, "_id=?", new String[]{ARGS1}, null, null, null);

                else
                    cr = iDB.query(TBL_TRANSACTION, null, "_id=?", new String[]{"" + id}, null, null, null);

                if (!cr.moveToFirst()) {
                    return null;
                }

                TransactionData temp = new TransactionData();

                temp.set_id(cr.getInt(cr.getColumnIndex("_id")));
                temp.setTransaction_id(cr.getInt(cr.getColumnIndex("transaction_id")));

                temp.setStoreId(cr.getString(cr.getColumnIndex("store_id")));
                temp.setOrdConfirmUrl(cr.getString(cr.getColumnIndex("ord_confirm_url")));
                temp.setKioskId(cr.getString(cr.getColumnIndex("kiosk_id")));
                temp.setCustMobileNum(cr.getString(cr.getColumnIndex("cust_mobile_num")));
                temp.setEmpId(cr.getString(cr.getColumnIndex("emp_id")));

                temp.setInvoice_date(cr.getString(cr.getColumnIndex("invoice_date")));
                temp.setTrans_status(cr.getString(cr.getColumnIndex("trans_status")));
                temp.setTrans_submit_to_server_status(cr.getString(cr.getColumnIndex("trans_submit_to_server_status")));
               // temp.setOrderType(cr.getString(cr.getColumnIndex("order_type")));

                retVal = temp;


                if (cr != null && !cr.isClosed()) {
                    cr.close();
                }
            } catch (Exception e) {
                Log.e("[" + TAG + "]: ", "TBL_TRANSACTION - getTableData(): ", e);
            }
        } else if (TBL_NAME.equalsIgnoreCase(TBL_TRANSACTION_MAIN)) {
            try {
                Cursor cr = null;

                /*cr = iDB.query(TBL_TRANSACTION_MAIN, null, null, null, null, null, null);*/
                if (whatToFetch == GeneralConstant.REQUEST_FOR_LAST_INVOICE_NUMBER)
                    cr = iDB.rawQuery("SELECT * FROM " + TBL_TRANSACTION_MAIN + " WHERE invoice_number=(SELECT max(invoice_number) FROM " + TBL_TRANSACTION_MAIN + " where trans_type like '" + ARGS1 + "')", null);
                else if (whatToFetch == GeneralConstant.REQUEST_FOR_TRANS)
                    cr = iDB.query(TBL_TRANSACTION_MAIN, null, "_id=?", new String[]{ARGS1}, null, null, null);

                else
                    cr = iDB.query(TBL_TRANSACTION_MAIN, null, "_id=?", new String[]{"" + id}, null, null, null);

                if (!cr.moveToFirst()) {
                    return null;
                }

                TransactionData temp = new TransactionData();

                temp.set_id(cr.getInt(cr.getColumnIndex("_id")));
                temp.setTransaction_id(cr.getInt(cr.getColumnIndex("transaction_id")));

                temp.setStoreId(cr.getString(cr.getColumnIndex("store_id")));
                temp.setOrdConfirmUrl(cr.getString(cr.getColumnIndex("ord_confirm_url")));
                temp.setKioskId(cr.getString(cr.getColumnIndex("kiosk_id")));
                temp.setCustMobileNum(cr.getString(cr.getColumnIndex("cust_mobile_num")));
                temp.setEmpId(cr.getString(cr.getColumnIndex("emp_id")));

                temp.setInvoice_date(cr.getString(cr.getColumnIndex("invoice_date")));
                temp.setTrans_status(cr.getString(cr.getColumnIndex("trans_status")));
                temp.setTrans_submit_to_server_status(cr.getString(cr.getColumnIndex("trans_submit_to_server_status")));
                temp.setOrderType(cr.getString(cr.getColumnIndex("order_type")));

                retVal = temp;


                if (cr != null && !cr.isClosed()) {
                    cr.close();
                }
            } catch (Exception e) {
                Log.e("[" + TAG + "]: ", "TBL_TRANSACTION_MAIN - getTableData(): ", e);
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
            } else if (TBL_NAME.equalsIgnoreCase(TBL_TRANSACTION_MAIN)) {
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
            } else if (TBL_NAME.equalsIgnoreCase(TBL_TRANSACTION_MAIN)) {
                return iDB.delete(TBL_TRANSACTION_MAIN, "_id" + "=" + rowid, null) > 0;
            }
        } catch (Exception e) {
            Log.e("[ " + TAG + " ] :", "'" + TAG + "' - deleteRows() : ", e);
            return 0 > 0;
        }
        return 0 > 0;
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
            iDB.execSQL("Drop table if exists " + TBL_TRANSACTION_MAIN);
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
                iDB.execSQL(CREATE_TRANSACTION_TABLE_MAIN);
            } catch (Exception e) {// do nothing
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase iDB, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            //iDB.execSQL("Drop table if exists " + TBL_CUSTOMER_MASTER);
            // iDB.execSQL(CREATE_CUSTOMER_MASTER_TABLE);
            iDB.execSQL("Drop table if exists " + TBL_TRANSACTION);
            iDB.execSQL(CREATE_TRANSACTION_TABLE);
            iDB.execSQL("Drop table if exists " + TBL_TRANSACTION_MAIN);
            iDB.execSQL(CREATE_TRANSACTION_TABLE_MAIN);
            // RE-CREATE DATABSE STRUCTURE.
            onCreate(iDB);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

    }
}

// DBManager.getInstance().insertTables(DBManager.TBL_MERCHANDISE_HIERARCHY, merchandiseHierarchyDTO);

//       ArrayList<MerchandiseHierarchyData>   merchandiseHierarchyDTOS = ((ArrayList<MerchandiseHierarchyData>) (ArrayList<?>) (DBManager.getInstance().getAllTableData(DBManager.TBL_MERCHANDISE_HIERARCHY, "", "" + DivisonCode, AppConstant.FETCH_GROUPDATA_ACC_TO_DIVISON)));
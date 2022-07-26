package com.ls.creditcheckapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.ls.creditcheckapp.Profile.CREATE_PROFILE_TABLE;
import static com.ls.creditcheckapp.Profile.PROFILE_EMAIL;
import static com.ls.creditcheckapp.Profile.PROFILE_ID;
import static com.ls.creditcheckapp.Profile.PROFILE_NAME;
import static com.ls.creditcheckapp.Profile.PROFILE_PASSWORD;
import static com.ls.creditcheckapp.Profile.PROFILE_PHONE;
import static com.ls.creditcheckapp.Profile.PROFILE_TABLE;
import static com.ls.creditcheckapp.Profile.PROFILE_USERNAME;

public class DBHelper extends SQLiteOpenHelper {
    private SQLiteDatabase myDB;
    private SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase sqLiteDatabase;
    public static String DB_PATH = "/data/D";

    public static final String DATABASE_NAME = "dB.Credit";
    private static final String LOG = DBHelper.class.getName();
    private Context context;
    public static final int DATABASE_VERSION = 1;
    public static final int DATABASE_NEW_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("table", CREATE_PROFILE_TABLE);


    }
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(CREATE_PROFILE_TABLE);
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
            android.util.Log.e("DBHelper Table error", e.getMessage());
        }
        finally{

            sqLiteDatabase.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PROFILE_TABLE);
        onCreate(sqLiteDatabase);

    }
    private synchronized void close(SQLiteDatabase db) {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DATABASE_NAME;
        myDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON");
    }
    public SQLiteDatabase openDataBase(SQLiteDatabase db) {
        if(db.isOpen()){
            sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON");
            return sqLiteDatabase;
        }
        //sqLiteDatabase = db.getWritableDatabase();
        sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON");
        return sqLiteDatabase;
    }
    private boolean checkDataBase() {
        SQLiteDatabase tempDB = null;
        try {
            String myPath = DB_PATH + DATABASE_NAME;
            tempDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
            Log.e("Skylight App - check", e.getMessage());
        }
        if (tempDB != null)
            //tempDB.close();
            return tempDB != null ? true : false;
        return false;
    }

    public void copyDataBase() throws IOException {
        try {
            InputStream myInput = context.getAssets().open(DATABASE_NAME);
            String outputFileName = DB_PATH + DATABASE_NAME;
            OutputStream myOutput = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            Log.e("tle99 - copyDatabase", e.getMessage());
        }

    }



    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            openDataBase();
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e("App - create", e.getMessage());
            }
        }

    }


    @SuppressLint("StaticFieldLeak")
    private static DBHelper instance;

    public static synchronized DBHelper getHelper(Context context) {
        if (instance == null)
            instance = new DBHelper(context);
        return instance;
    }
    public long insertNewUser(int profileID,String name, String userName, String password,String email,String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROFILE_ID, profileID);
        values.put(PROFILE_NAME, name);
        values.put(PROFILE_USERNAME, userName);
        values.put(PROFILE_PASSWORD, password);
        values.put(PROFILE_EMAIL, email);
        values.put(PROFILE_PHONE, phone);
        return db.insert(PROFILE_TABLE, null, values);

    }

}

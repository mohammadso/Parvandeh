package com.sobhani.mohammad.parvandeh.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.sobhani.mohammad.parvandeh.Contact;

import java.io.File;
import java.util.ArrayList;

public class DBBackup extends SQLiteOpenHelper {


    public DBBackup(Context context) {
        super(context, Environment.getExternalStorageDirectory()
                + File.separator + "/Parvandeh/DBBackup/DataBackup", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Constants.CREATE_TB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(Constants.DROP_TB);
        onCreate(sqLiteDatabase);
    }

    public void addContacts(ArrayList<Contact> contacts){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TB_NAME);
        onCreate(db);
        for(int i=0; i<contacts.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.COL_FILE_ID,contacts.get(i).getFileID());
            contentValues.put(Constants.COL_NAME, contacts.get(i).getName());
            contentValues.put(Constants.COL_TEL_NO, contacts.get(i).getTel_number());
            contentValues.put(Constants.COL_PH_NO, contacts.get(i).getPhone_number());
            contentValues.put(Constants.COL_PH_NO1, contacts.get(i).getPhone_number1());
            contentValues.put(Constants.COL_ADDRESS, contacts.get(i).getAddress());
            contentValues.put(Constants.COL_TELLER, contacts.get(i).getTeller());
            contentValues.put(Constants.COL_VISIT_DATE,contacts.get(i).getVisitDate());
            contentValues.put(Constants.COL_DESCRIPTION, contacts.get(i).getDescription());
            db.insert(Constants.TB_NAME, Constants.COL_ID, contentValues);
        }
    }

    public Cursor getAllContacts(){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TB_NAME,null);
        return cursor;
    }
}

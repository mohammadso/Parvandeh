package com.sobhani.mohammad.parvandeh.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.sobhani.mohammad.parvandeh.Contact;

import java.util.ArrayList;

public class DBAdapter {
    private Context c;
    private SQLiteDatabase db;
    private DBHelper helper;

    public DBAdapter(Context c) {
        this.c = c;
        helper = new DBHelper(c);
    }

    //Open DB
    public void openDB()
    {
        try
        {
            db = helper.getWritableDatabase();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //Close DB
    public void closeDB()
    {
        try
        {
            helper.close();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //Insert a Contact
    public boolean addContact(Contact contact){

        try
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.COL_FILE_ID,contact.getFileID());
            contentValues.put(Constants.COL_NAME, contact.getName());
            contentValues.put(Constants.COL_TEL_NO, contact.getTel_number());
            contentValues.put(Constants.COL_PH_NO, contact.getPhone_number());
            contentValues.put(Constants.COL_PH_NO1, contact.getPhone_number1());
            contentValues.put(Constants.COL_ADDRESS, contact.getAddress());
            contentValues.put(Constants.COL_TELLER, contact.getTeller());
            contentValues.put(Constants.COL_VISIT_DATE,contact.getVisitDate());
            contentValues.put(Constants.COL_DESCRIPTION, contact.getDescription());
            db.insert(Constants.TB_NAME, Constants.COL_ID, contentValues);
            return true;
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;

    }

    //Retrieve or Filtering by Name
    public Cursor retrieveName(String searchTerm)
    {
        String[] columns = {Constants.COL_ID, Constants.COL_FILE_ID, Constants.COL_NAME};
        Cursor c = null;
        if(searchTerm != null && searchTerm.length()>0)
        {
            String sql = "SELECT * FROM " + Constants.TB_NAME + " WHERE " + Constants.COL_NAME + " LIKE '%" + searchTerm + "%'";
            c = db.rawQuery(sql,null);
            return c;
        }
        c = db.query(Constants.TB_NAME,columns,null,null,null,null,null);
        return c;
    }

    //Retrieve or Filtering by File_id
    public Cursor retrieveFile_id(String searchTerm)
    {
        String[] columns = {Constants.COL_ID, Constants.COL_FILE_ID, Constants.COL_NAME};
        Cursor c = null;
        if(searchTerm != null && searchTerm.length()>0)
        {
            String sql = "SELECT * FROM " + Constants.TB_NAME + " WHERE " + Constants.COL_FILE_ID + " LIKE '%" + searchTerm + "%' " ;
            c = db.rawQuery(sql,null);
            return c;
        }
        c = db.query(Constants.TB_NAME,columns,null,null,null,null,null);
        return c;
    }

    //Return all data of a contact
    public Cursor retrieve(String searchFileId, String searchName)
    {
        String[] columns = {Constants.COL_ID, Constants.COL_FILE_ID, Constants.COL_NAME, Constants.COL_TEL_NO, Constants.COL_PH_NO, Constants.COL_PH_NO1, Constants.COL_ADDRESS, Constants.COL_TELLER, Constants.COL_VISIT_DATE, Constants.COL_DESCRIPTION};
        Cursor c = null;
            String sql = "SELECT * FROM " + Constants.TB_NAME + " WHERE " + Constants.COL_NAME + " LIKE '%" + searchName + "%' " + " AND " + Constants.COL_FILE_ID + " LIKE '%" + searchFileId + "%' " ;
            c = db.rawQuery(sql,null);
            return c;
    }

    //Returns cursor of all Contacts
    public Cursor getAllContacts(){
        Cursor cursor;
        String sql = " SELECT * FROM " + Constants.TB_NAME;
        cursor = db.rawQuery(sql,null);
        return cursor;
    }

    //Update a contact by it's fileID and name
    public boolean updateContact(String fileId, String name, Contact contact){

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.COL_FILE_ID,contact.getFileID());
        contentValues.put(Constants.COL_NAME, contact.getName());
        contentValues.put(Constants.COL_TEL_NO, contact.getTel_number());
        contentValues.put(Constants.COL_PH_NO, contact.getPhone_number());
        contentValues.put(Constants.COL_PH_NO1, contact.getPhone_number1());
        contentValues.put(Constants.COL_ADDRESS, contact.getAddress());
        contentValues.put(Constants.COL_TELLER, contact.getTeller());
        contentValues.put(Constants.COL_VISIT_DATE, contact.getVisitDate());
        contentValues.put(Constants.COL_DESCRIPTION, contact.getDescription());
        db.update(Constants.TB_NAME, contentValues, "file_id = ? AND name = ?", new String[] {fileId, name});
        return true;
    }

    //Delete a contact by it's fileID and name
    public int deleteContact(String fileId, String name){

        return db.delete(Constants.TB_NAME, "file_id = ? AND name = ? ", new String[] {fileId, name});
    }

    public void addContacts(ArrayList<Contact> contacts){
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TB_NAME);
        db.execSQL(Constants.CREATE_TB);
        for(int i=0; i<contacts.size(); i++){
            Contact contact = contacts.get(i);
            addContact(contact);
        }
    }

}

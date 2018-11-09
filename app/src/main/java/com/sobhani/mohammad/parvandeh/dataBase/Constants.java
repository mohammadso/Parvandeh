package com.sobhani.mohammad.parvandeh.dataBase;


public class Constants {

    //columns
    static final String COL_ID = "id";
    static final String COL_FILE_ID = "file_id";
    static final String COL_NAME = "name";
    static final String COL_TEL_NO = "tel_number";
    static final String COL_PH_NO = "phone_number";
    static final String COL_PH_NO1 = "phone_number1";
    static final String COL_ADDRESS = "address";
    static final String COL_TELLER = "teller" ;
    static final String COL_VISIT_DATE = "visit_date";
    static final String COL_DESCRIPTION = "description";

    //DB
    static final String DB_NAME = "contacts_DB";
    static final String TB_NAME = "contacts_TB";
    static final int DB_VERSION = 1;


    //CREATE statement
    //id is not usable currently
    static final String CREATE_TB = "CREATE TABLE contacts_TB(id INTEGER,file_id TEXT,"
            + "name TEXT,tel_number TEXT,phone_number TEXT,phone_number1 TEXT,address TEXT,teller TEXT,visit_date TEXT,description TEXT);";

    //DROP  TB statement
    static final String DROP_TB = "DRP TABLE IF EXISTS" + TB_NAME;
}

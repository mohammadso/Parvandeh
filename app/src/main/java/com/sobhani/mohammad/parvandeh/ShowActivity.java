package com.sobhani.mohammad.parvandeh;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.sobhani.mohammad.parvandeh.dataBase.DBAdapter;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowActivity extends AppCompatActivity {

    //Declare variables
    private TextView fileId, name, phoneNumber, phoneNumberSecond, telNumber, address, teller, visitDate, description;
    private String nameIntent, fileIdIntent;
    private Toolbar toolbar;

    //Apply font to this activity
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        Intent intent = getIntent();
        nameIntent = intent.getStringExtra("name");
        fileIdIntent = intent.getStringExtra("file_id");

        //Set Toolbar
        toolbar = (Toolbar) findViewById(R.id.show_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_showing));

        //Cast TextViews
        fileId = (TextView) findViewById(R.id.textViewFileIdShow);
        name = (TextView) findViewById(R.id.textViewNameShow);
        phoneNumber = (TextView) findViewById(R.id.textViewPhoneNumberShow);
        phoneNumberSecond = (TextView) findViewById(R.id.textViewPhoneNumberSecondShow);
        telNumber = (TextView) findViewById(R.id.textViewTelNumberShow);
        address = (TextView) findViewById(R.id.textViewAddressShow);
        teller = (TextView) findViewById(R.id.textViewTellerShow);
        visitDate = (TextView) findViewById(R.id.textViewVisitDateShow);
        description = (TextView) findViewById(R.id.textViewDescriptionShow);


        //Search Database for Contact using name and fileId
        DBAdapter db = new DBAdapter(this);
        db.openDB();
        Cursor c = db.retrieve(fileIdIntent, nameIntent);
        while (c.moveToNext()) {

            fileId.setText(c.getString(1));
            name.setText(c.getString(2));
            telNumber.setText(c.getString(3));
            phoneNumber.setText(c.getString(4));
            phoneNumberSecond.setText(c.getString(5));
            address.setText(c.getString(6));
            teller.setText(c.getString(7));
            visitDate.setText(c.getString(8));
            description.setText(c.getString(9));
        }
        db.closeDB();
    }
}

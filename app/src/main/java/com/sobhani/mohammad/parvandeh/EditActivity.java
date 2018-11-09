package com.sobhani.mohammad.parvandeh;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.sobhani.mohammad.parvandeh.dataBase.DBAdapter;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText fileId, name, phoneNumber, phoneNumber1, telNumber, address, teller, visitDate, description;
    private Button updateButton;
    private TextInputLayout fileIdIL, nameIL, phoneNumberIL;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        final String nameStringOld = intent.getStringExtra("name");
        final String fileIdStringOld = intent.getStringExtra("file_id");

        //Set toolbar
        toolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_edit));

        //Set textInputLayout
        fileIdIL = (TextInputLayout) findViewById(R.id.inputLayoutFileId);
        nameIL = (TextInputLayout) findViewById(R.id.inputLayoutName);
        phoneNumberIL = (TextInputLayout) findViewById(R.id.inputLayoutPhone);

        //Set editTexts
        fileId = (EditText) findViewById(R.id.editTextFileID);
        name = (EditText) findViewById(R.id.editTextName);
        phoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        phoneNumber1 = (EditText) findViewById(R.id.editTextPhoneNumber2);
        telNumber = (EditText) findViewById(R.id.editTextTel);
        address = (EditText) findViewById(R.id.editTextAddress);
        teller = (EditText) findViewById(R.id.editTextTeller);
        visitDate = (EditText) findViewById(R.id.editTextVisitDate);
        description = (EditText) findViewById(R.id.editTextDescription);

        //Set text to editTexts
        DBAdapter db = new DBAdapter(this);
        db.openDB();
        Cursor cursor = db.retrieve(fileIdStringOld,nameStringOld);
        while (cursor.moveToNext()) {
            fileId.setText(cursor.getString(1));
            name.setText(cursor.getString(2));
            telNumber.setText(cursor.getString(3));
            phoneNumber.setText(cursor.getString(4));
            phoneNumber1.setText(cursor.getString(5));
            address.setText(cursor.getString(6));
            teller.setText(cursor.getString(7));
            visitDate.setText(cursor.getString(8));
            description.setText(cursor.getString(9));
        }

        db.closeDB();

        //Set DateButton
        visitDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersianCalendar persianCalendar = new PersianCalendar();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        visitDate.setText(year + "/" + (monthOfYear+1) + "/" + dayOfMonth);
                    }
                },persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay());
                datePickerDialog.show(getFragmentManager(),"datePickerDialog");
            }
        });
        //Set UpdateButton
        updateButton = (Button) findViewById(R.id.buttonUpdate);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Set error if didn't get input
                if(fileId.getText().toString().isEmpty()) {
                    fileIdIL.setError(getString(R.string.required));
                    return;
                }

                if(name.getText().toString().isEmpty()){
                    nameIL.setError(getString(R.string.required));
                    fileIdIL.setErrorEnabled(false);
                    return;
                }

                if(phoneNumber.getText().toString().isEmpty()){
                    fileIdIL.setErrorEnabled(false);
                    phoneNumberIL.setError(getString(R.string.required));
                    nameIL.setErrorEnabled(false);

                    return;
                }


                //update the contact using input
                Contact contact = new Contact();
                contact.setFileID(fileId.getText().toString());
                contact.setName(name.getText().toString());
                contact.setPhone_number(phoneNumber.getText().toString());
                contact.setPhone_number1(phoneNumber1.getText().toString());
                contact.setTel_number(telNumber.getText().toString());
                contact.setAddress(address.getText().toString());
                contact.setTeller(teller.getText().toString());
                contact.setVisitDate(visitDate.getText().toString());
                contact.setDescription(description.getText().toString());

                DBAdapter db = new DBAdapter(EditActivity.this);
                db.openDB();
                db.updateContact(fileIdStringOld, nameStringOld,contact);

                Intent mainActivity = new Intent(EditActivity.this,MainActivity.class);
                startActivity(mainActivity);
            }
        });

    }

}

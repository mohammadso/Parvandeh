package com.sobhani.mohammad.parvandeh.fragment;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.sobhani.mohammad.parvandeh.Contact;
import com.sobhani.mohammad.parvandeh.MainActivity;
import com.sobhani.mohammad.parvandeh.R;
import com.sobhani.mohammad.parvandeh.dataBase.DBAdapter;
import com.valdesekamdem.library.mdtoast.MDToast;

public class EditFragment extends Fragment {

    //Declare variable
    private Toolbar toolbar;
    private EditText fileId, name, phoneNumber, phoneNumber1, telNumber, address, teller, visitDate, description;
    private Button updateButton;
    private TextInputLayout fileIdIL, nameIL, phoneNumberIL;
    private Context context;

    //Declare TAGs
    private static final String FILE_ID = "fileId";
    private static final String NAME = "name";

    //Declare Strings of Bundle
    private String fileIdBundle;
    private String nameBundle;


    public EditFragment() {
        // Required empty public constructor
    }


    public static EditFragment newInstance(String fileId, String name) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putString(FILE_ID, fileId);
        args.putString(NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fileIdBundle = getArguments().getString(FILE_ID);
            nameBundle = getArguments().getString(NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        //Set toolbar
        toolbar = (Toolbar) view.findViewById(R.id.edit_toolbar);
        toolbar.setTitle(getString(R.string.title_activity_edit));
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) context).deleteEditFragment();
            }
        });

        //Set textInputLayout
        fileIdIL = (TextInputLayout) view.findViewById(R.id.inputLayoutFileId);
        nameIL = (TextInputLayout) view.findViewById(R.id.inputLayoutName);
        phoneNumberIL = (TextInputLayout) view.findViewById(R.id.inputLayoutPhone);

        //Set editTexts
        fileId = (EditText) view.findViewById(R.id.editTextFileID);
        name = (EditText) view.findViewById(R.id.editTextName);
        phoneNumber = (EditText) view.findViewById(R.id.editTextPhoneNumber);
        phoneNumber1 = (EditText) view.findViewById(R.id.editTextPhoneNumber2);
        telNumber = (EditText) view.findViewById(R.id.editTextTel);
        address = (EditText) view.findViewById(R.id.editTextAddress);
        teller = (EditText) view.findViewById(R.id.editTextTeller);
        visitDate= (EditText) view.findViewById(R.id.editTextVisitDate);
        description = (EditText) view.findViewById(R.id.editTextDescription);

        //Retrieve data using fileIdBundle and nameBundle & Set text to editTexts
        DBAdapter db = new DBAdapter(context);
        db.openDB();
        Cursor cursor = db.retrieve(fileIdBundle, nameBundle);
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
                datePickerDialog.show(((MainActivity) context).getFragmentManager(),"datePickerDialog");
            }
        });
        //Set button
        updateButton = (Button) view.findViewById(R.id.buttonUpdate);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Set error if didn't get input
                if (fileId.getText().toString().isEmpty()) {
                    fileIdIL.setError(getString(R.string.required));
                    return;
                }

                if (name.getText().toString().isEmpty()) {
                    nameIL.setError(getString(R.string.required));
                    fileIdIL.setErrorEnabled(false);
                    return;
                }

                if (phoneNumber.getText().toString().isEmpty()) {
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

                DBAdapter db = new DBAdapter(context);
                db.openDB();
                if(db.updateContact(fileIdBundle, nameBundle, contact)){

                    MDToast.makeText(context,"Updated Successfully",MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS).show();
                }else{

                    MDToast.makeText(context,"Unable to update",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                }
                db.closeDB();
                ((MainActivity) context).getContacts(null);
                ((MainActivity) context).deleteEditFragment();
            }
        });

        return view;
    }
}

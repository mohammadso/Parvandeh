package com.sobhani.mohammad.parvandeh.fragment;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sobhani.mohammad.parvandeh.MainActivity;
import com.sobhani.mohammad.parvandeh.R;
import com.sobhani.mohammad.parvandeh.dataBase.DBAdapter;


public class ShowFragment extends Fragment {

    //Declare variables
    private TextView fileId, name, phoneNumber, phoneNumberSecond, telNumber, address, teller, visitDate, description;
    private Toolbar toolbar;
    private Context context;

    //Declare TAGs
    private static final String FILE_ID = "fileId";
    private static final String NAME = "name";

    //Declare Strings of Bundle
    private String fileIdBundle;
    private String nameBundle;


    public ShowFragment() {
        // Required empty public constructor
    }

    public static ShowFragment newInstance(String fileId, String name) {
        ShowFragment fragment = new ShowFragment();
        Bundle args = new Bundle();
        args.putString(FILE_ID, fileId);
        args.putString(NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    //I called this method to get context
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
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        //Set Toolbar
        toolbar = (Toolbar) view.findViewById(R.id.show_fragment_toolbar);
        toolbar.setTitle(getString(R.string.title_activity_showing));
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) context).deleteShowFragment();
            }
        });

        //Set TextViews
        fileId = (TextView) view.findViewById(R.id.textViewFileIdShow);
        name = (TextView) view.findViewById(R.id.textViewNameShow);
        phoneNumber = (TextView) view.findViewById(R.id.textViewPhoneNumberShow);
        phoneNumberSecond = (TextView) view.findViewById(R.id.textViewPhoneNumberSecondShow);
        telNumber= (TextView) view.findViewById(R.id.textViewTelNumberShow);
        address= (TextView) view.findViewById(R.id.textViewAddressShow);
        teller = (TextView) view.findViewById(R.id.textViewTellerShow);
        visitDate = (TextView) view.findViewById(R.id.textViewVisitDateShow);
        description = (TextView) view.findViewById(R.id.textViewDescriptionShow);


        //Search Database for Contact using name and fileId
        DBAdapter db = new DBAdapter(context);
        db.openDB();
        Cursor c = db.retrieve(fileIdBundle, nameBundle);
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
        return view;
    }

}

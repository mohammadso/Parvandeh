package com.sobhani.mohammad.parvandeh;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.ajts.androidmads.library.SQLiteToExcel;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.sobhani.mohammad.parvandeh.dataBase.DBAdapter;
import com.sobhani.mohammad.parvandeh.dataBase.DBBackup;
import com.sobhani.mohammad.parvandeh.fragment.AddFragment;
import com.sobhani.mohammad.parvandeh.fragment.EditFragment;
import com.sobhani.mohammad.parvandeh.fragment.ShowFragment;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //To show the device is tablet or phone
    private boolean mTwoPane;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private ArrayList<Contact> contacts = new ArrayList<>();
    private File root, directory;
    private static File logs;

    private String FRAGMENT_ADD_TAG = "FRAGMENT_ADD_TAG";
    private String FRAGMENT_SHOW_TAG = "FRAGMENT_SHOW_TAG";
    private String FRAGMENT_EDIT_TAG = "FRAGMENT_EDIT_TAG";

    //Apply font to this activity
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_main));

        //Set DrawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.main_drawer);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //Set RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter(this, contacts);

        //Is two pane or not
        if (findViewById(R.id.main_frame_layout) != null) {
            mTwoPane = true;
        }

        //Set Floating Action Button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * check if it's tablet or phone
                 * if it's phone or tablet in portrait-mode it opens AddActivity
                 * else it deletes last used fragments and opens AddFragment
                 */
                if (mTwoPane) {

                    //Delete AddFragment if exists
                    deleteAddFragment();

                    //Delete ShowFragment if exists
                    deleteShowFragment();

                    //Delete EditFragment if exists
                    deleteEditFragment();

                    //Add AddFragment
                    AddFragment addFragment = new AddFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.main_frame_layout, addFragment, FRAGMENT_ADD_TAG)
                            .addToBackStack(null)
                            .commit();
                } else {

                    //Start AddActivity
                    Intent addActivity = new Intent(MainActivity.this, AddActivity.class);
                    startActivity(addActivity);
                }
            }
        });

        //Sending null to getPlanets method to show items's of RecyclerView as opening the app
        getContacts(null);

        //Create folders of app
        root = Environment.getExternalStorageDirectory();
        directory = new File(root.getAbsolutePath() + "/Parvandeh/Import");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        directory = new File(root.getAbsolutePath() + "/Parvandeh/XLS");
        if (!directory.exists()) {
            directory.mkdirs();
        }

    }

    /**
     * Search dataBase using searchTerm and return a list of matching contacts
     *
     * @param searchTerm String used to search dataBase
     */
    public void getContacts(String searchTerm) {
        contacts.clear();
        DBAdapter db = new DBAdapter(this);
        db.openDB();
        Contact contact;
        if (isInteger(searchTerm)) {
            Cursor c = db.retrieveFile_id(searchTerm);
            while (c.moveToNext()) {
                String name = c.getString(2);
                String id = c.getString(1);
                contact = new Contact();
                contact.setFileID(id);
                contact.setName(name);
                contacts.add(contact);

            }
        } else {
            Cursor c = db.retrieveName(searchTerm);
            while (c.moveToNext()) {
                String name = c.getString(2);
                String id = c.getString(1);
                contact = new Contact();
                contact.setFileID(id);
                contact.setName(name);
                contacts.add(contact);
            }
        }
        db.closeDB();
        recyclerView.setAdapter(adapter);
    }


    /**
     * checks if the string is integer or not
     *
     * @param string String used to check if number or String
     * @return
     */
    public boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

//
//    public static void addLog(String log){
//        try {
//            FileOutputStream f = new FileOutputStream(logs);
//            PrintWriter pw = new PrintWriter(f);
//            pw.println(log);
//            pw.flush();
//            pw.close();
//            f.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        //Set SearchView
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                getContacts(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.toolbar_importExportExcel:

                AlertDialog.Builder excelDialog = new AlertDialog.Builder(this);
                CharSequence excelItems[] = new CharSequence[] {"Export to Excel", "Import from Excel", "Share Excel File"};
                excelDialog.setIcon(R.drawable.ic_touch)
                .setTitle("Choose an item...")
                        .setItems(excelItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i == 0){
                                    //Export Excel
                                    AlertDialog.Builder exportDialog = new AlertDialog.Builder(MainActivity.this);
                                    exportDialog.setTitle("Export Alert")
                                    .setCancelable(true)
                                    .setIcon(R.drawable.ic_alert)
                                    .setMessage("Are You Sure of Exporting to Excel?")
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });
                                    exportDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
                                            String currentTime = dateFormat.format(new Date());
                                            root = Environment.getExternalStorageDirectory();
                                            directory = new File(root.getAbsolutePath() + "/Parvandeh/XLS");
                                            if (!directory.exists()) {
                                                directory.mkdirs();
                                            }
                                            //Export to Excel using SQLite2XL library
                                            SQLiteToExcel sqLiteToExcel = new SQLiteToExcel(MainActivity.this, "contacts_DB", directory.getPath());
                                            sqLiteToExcel.exportSingleTable("contacts_TB", currentTime + ".xls", new SQLiteToExcel.ExportListener() {
                                                @Override
                                                public void onStart() {

                                                }

                                                @Override
                                                public void onCompleted(String filePath) {
                                                    MDToast.makeText(MainActivity.this, "Exported successfully", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    MDToast.makeText(MainActivity.this, "Unable to export", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                                }
                                            });
                                        }
                                    }).show();

                                }else if (i == 1){
                                    //Import Excel
                                    //Alert for importing
                                    AlertDialog.Builder importDialog = new AlertDialog.Builder(MainActivity.this);
                                    importDialog.setCancelable(true)
                                    .setIcon(R.drawable.ic_alert)
                                    .setTitle("Import Alert")
                                    .setMessage("Are You Sure of Importing from Excel? \n" +
                                            "imported data will be added to currents. \n " +
                                            "** Export current data to excel before importing new data...**");

                                    //AlertDialog No statement
                                    importDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });
                                    //AlertDialog Yes statement
                                    importDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            root = Environment.getExternalStorageDirectory();
                                            directory = new File(root.getAbsolutePath() + "/Parvandeh/Import");
                                            if (!directory.exists()) {
                                                directory.mkdirs();
                                            }

                                            //Dialog is defined using FilePickerDialog library
                                            DialogProperties properties = new DialogProperties();
                                            properties.selection_mode = DialogConfigs.SINGLE_MODE;
                                            properties.selection_type = DialogConfigs.FILE_SELECT;
                                            properties.root = directory;
                                            properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                                            String[] extensions = {"xls"};
                                            properties.extensions = extensions;

                                            FilePickerDialog filePickerDialog = new FilePickerDialog(MainActivity.this, properties);
                                            filePickerDialog.setTitle("Select an xml File to Import from:");
                                            filePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
                                                @Override
                                                public void onSelectedFilePaths(String[] files) {
                                                    String filePath = files[0];
                                                    //ExcelToSQLite using library
                                                    ExcelToSQLite excelToSQLite = new ExcelToSQLite(MainActivity.this, "contacts_DB");
                                                    excelToSQLite.importFromFile(filePath, new ExcelToSQLite.ImportListener() {
                                                        @Override
                                                        public void onStart() {

                                                        }

                                                        @Override
                                                        public void onCompleted(String dbName) {
                                                            getContacts(null);
                                                            MDToast.makeText(MainActivity.this, "Imported successfully", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();

                                                        }

                                                        @Override
                                                        public void onError(Exception e) {
                                                            getContacts(null);
                                                            MDToast.makeText(MainActivity.this, "Unable to import", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();


                                                        }
                                                    });
                                                }
                                            });
                                            filePickerDialog.show();
                                        }
                                    }).show();

                                }else {
                                    root = Environment.getExternalStorageDirectory();
                                    directory = new File(root.getAbsolutePath() + "/Parvandeh/XLS");

                                    DialogProperties properties = new DialogProperties();
                                    properties.selection_mode = DialogConfigs.SINGLE_MODE;
                                    properties.selection_type = DialogConfigs.FILE_SELECT;
                                    properties.root = directory;
                                    properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                                    String[] extensions = {"xls"};
                                    properties.extensions = extensions;

                                    FilePickerDialog filePickerDialog = new FilePickerDialog(MainActivity.this, properties);
                                    filePickerDialog.setTitle("Select an xml File to Share:");
                                    filePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
                                        @Override
                                        public void onSelectedFilePaths(String[] files) {
                                            String filePath = files[0];
                                            File shareFile = new File(filePath);
                                            getFilesDir();
                                            Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                            sendIntent.setType("text/plain");
                                            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareFile));
                                            startActivity(sendIntent.createChooser(sendIntent, "shareFile"));
                                        }
                                    });
                                    filePickerDialog.show();
                                }
                            }
                        }).show();
                break;

            case R.id.toolbar_importExportData:

                AlertDialog.Builder dataAlert = new AlertDialog.Builder(this);
                CharSequence dataItems[] = new CharSequence[] {"Export Data", "Import  Data"};
                dataAlert.setIcon(R.drawable.ic_touch);
                dataAlert.setItems(dataItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(i == 0){
                            AlertDialog.Builder exportDataAlert = new AlertDialog.Builder(MainActivity.this);
                            exportDataAlert.setTitle("Export Alert")
                                    .setIcon(R.drawable.ic_alert)
                                    .setMessage("Are You Sure of Exporting Data? \n " +
                                            "it will DELETE the last backup. \n" +
                                            "Please Export to Excel first :) \n" +
                                            "** for more info contact your administrator... **")
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });
                            exportDataAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    DBBackup dbBackup = new DBBackup(MainActivity.this);
                                    DBAdapter dbAdapter = new DBAdapter(MainActivity.this);
                                    dbAdapter.openDB();
                                    Cursor cursor = dbAdapter.getAllContacts();
                                    ArrayList<Contact> contactArrayList = new ArrayList<Contact>();
                                    contactArrayList.clear();
                                    while (cursor.moveToNext()){
                                        Contact contact = new Contact();
                                        contact.setFileID(cursor.getString(1));
                                        contact.setName(cursor.getString(2));
                                        contact.setTel_number(cursor.getString(3));
                                        contact.setPhone_number(cursor.getString(4));
                                        contact.setPhone_number1(cursor.getString(5));
                                        contact.setAddress(cursor.getString(6));
                                        contact.setTeller(cursor.getString(7));
                                        contact.setVisitDate(cursor.getString(8));
                                        contact.setDescription(cursor.getString(9));
                                        contactArrayList.add(contact);
                                    }
                                    dbAdapter.closeDB();
                                    dbBackup.addContacts(contactArrayList);
                                }
                            }).show();

                        }else{

                            AlertDialog.Builder importDateAlert = new AlertDialog.Builder(MainActivity.this);
                            importDateAlert.setTitle("Import Alert")
                                    .setIcon(R.drawable.ic_alert)
                                    .setMessage("Are You Sure of Importing Data? \n" +
                                            "Current data will be DELETED. \n" +
                                            "Export current data to an excel file first..." )
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });
                            importDateAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    DBAdapter dbAdapter = new DBAdapter(MainActivity.this);
                                    dbAdapter.openDB();
                                    DBBackup dbBackup = new DBBackup(MainActivity.this);
                                    Cursor cursor = dbBackup.getAllContacts();
                                    ArrayList<Contact> contactsArrayList = new ArrayList<Contact>();
                                    contactsArrayList.clear();
                                    while (cursor.moveToNext()) {
                                        Contact contact = new Contact();
                                        contact.setFileID(cursor.getString(1));
                                        contact.setName(cursor.getString(2));
                                        contact.setTel_number(cursor.getString(3));
                                        contact.setPhone_number(cursor.getString(4));
                                        contact.setPhone_number1(cursor.getString(5));
                                        contact.setAddress(cursor.getString(6));
                                        contact.setTeller(cursor.getString(7));
                                        contact.setVisitDate(cursor.getString(8));
                                        contact.setDescription(cursor.getString(9));
                                        contactsArrayList.add(contact);
                                    }
                                    dbAdapter.addContacts(contactsArrayList);
                                    getContacts(null);
                                }
                            }).show();

                        }
                    }
                });
                dataAlert.setTitle("Choose an item...");
                dataAlert.show();
                break;

            case R.id.toolbar_allContacts:

                DBAdapter db = new DBAdapter(this);
                db.openDB();
                Cursor cursor = db.getAllContacts();
                int count = 0;
                while (cursor.moveToNext()) {
                    count++;
                }
                AlertDialog.Builder countDialog = new AlertDialog.Builder(this);
                countDialog.setCancelable(true)
                        .setTitle("All Contacts")
                        .setIcon(R.drawable.ic_info)
                        .setMessage("There Are " + count + " Contacts in DataBase")
                        .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();

                break;

            case R.id.toolbar_exit:
                final AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
                exitDialog.setCancelable(true).setMessage("Exit Alert").setIcon(R.drawable.ic_alert);
                exitDialog.setMessage("Are you sure of closing the app?").setCancelable(true).
                        setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_setting:
//                Intent setting = new Intent(MainActivity.this,SettingsActivity.class);
//                startActivity(setting);
                break;

            case R.id.navigation_about_us:
                Intent aboutUs = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(aboutUs);
                break;

            case R.id.navigation_feedback:

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"m.sobhani76@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Text");

                startActivity(Intent.createChooser(emailIntent, "Open an Email app..."));
                break;

            case R.id.navigation_telegram:

                Intent telegramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/Mohammadso76"));
                startActivity(Intent.createChooser(telegramIntent, "Open Telegram..."));
                break;
        }
        //Hide's drawer as an item selected
        hideDrawer();
        return true;
    }

    /**
     * It shows Navigation drawer when called
     */
    public void showDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    /**
     * It closes Navigation drawer when called
     */
    public void hideDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            hideDrawer();


        else {
//            AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
//            exitDialog.setCancelable(true)
//                    .setTitle("Exit Alert")
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            MainActivity.super.onBackPressed();
//                        }
//                    }).show();
            super.onBackPressed();

        }

    }

    public void deleteAddFragment() {

        Fragment fragmentAddReference =
                getSupportFragmentManager()
                        .findFragmentByTag(FRAGMENT_ADD_TAG);
        if (fragmentAddReference != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragmentAddReference)
                    .commit();
        }
    }

    public void deleteShowFragment() {

        Fragment fragmentShowReference =
                getSupportFragmentManager()
                        .findFragmentByTag(FRAGMENT_SHOW_TAG);
        if (fragmentShowReference != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragmentShowReference)
                    .commit();
        }
    }

    public void deleteEditFragment() {

        Fragment fragmentEditRefrence =
                getSupportFragmentManager()
                        .findFragmentByTag(FRAGMENT_EDIT_TAG);
        if (fragmentEditRefrence != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragmentEditRefrence)
                    .commit();
        }
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

        private Context context;
        private ArrayList<Contact> contacts;

        public RecyclerAdapter(Context context, ArrayList<Contact> contacts) {
            this.context = context;
            this.contacts = contacts;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_main, parent, false);
            MyViewHolder holder = new MyViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.textViewName.setText(contacts.get(position).getName());
            holder.textViewId.setText(contacts.get(position).getFileID());
            holder.imageView.setImageResource(R.drawable.ic_user);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /**
                     * check if it's tablet or phone
                     * if it's phone or tablet in portrait-mode it opens ShowActivity
                     * else it deletes last used fragments and opens ShowFragment
                     */
                    if (mTwoPane) {

                        //Delete AddFragment if exists
                        deleteAddFragment();

                        //Delete ShowFragment if exists
                        deleteShowFragment();

                        //Delete EditFragment if exists
                        deleteEditFragment();

                        ShowFragment showFragment = ShowFragment.newInstance(contacts.get(position).getFileID(),
                                contacts.get(position).getName());

                        getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.main_frame_layout, showFragment, FRAGMENT_SHOW_TAG)
                                .commit();
                    } else {

                        //Start ShowActivity
                        Intent showActivity = new Intent(context, ShowActivity.class);
                        showActivity.putExtra("file_id", contacts.get(position).getFileID());
                        showActivity.putExtra("name", contacts.get(position).getName());
                        startActivity(showActivity);
                    }

                }
            });

            holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //TODO don't Hardcode
                    //Dialog to accept request of deleting contact
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setMessage("Are you sure of deleting " + contacts.get(position).getName() + "'s info ?").setCancelable(false).
                            setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DBAdapter db = new DBAdapter(context);
                                    db.openDB();
                                    db.deleteContact(contacts.get(position).getFileID(), contacts.get(position).getName());
                                    ((MainActivity) context).getContacts(null);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    AlertDialog dialog = alertDialog.create();
                    dialog.setTitle("Alert");
                    dialog.setCancelable(true);
                    dialog.show();


                }
            });

            holder.imageViewEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mTwoPane) {

                        //Delete AddFragment if exists
                        deleteAddFragment();

                        //Delete ShowFragment if exists
                        deleteShowFragment();

                        //Delete EditFragment if exists
                        deleteEditFragment();

                        EditFragment fragment = EditFragment.newInstance(contacts.get(position).getFileID()
                                , contacts.get(position).getName());
                        getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.main_frame_layout, fragment, FRAGMENT_EDIT_TAG)
                                .commit();

                    } else {

                        //Start EditActivity
                        Intent editActivity = new Intent(context, EditActivity.class);
                        editActivity.putExtra("file_id", contacts.get(position).getFileID());
                        editActivity.putExtra("name", contacts.get(position).getName());
                        startActivity(editActivity);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView, imageViewEdit, imageViewDelete;
            TextView textViewName, textViewId;

            public MyViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.imageViewCard);
                textViewName = (TextView) itemView.findViewById(R.id.textViewNameCard);
                textViewId = (TextView) itemView.findViewById(R.id.textViewIdCard);
                imageViewEdit = (ImageView) itemView.findViewById(R.id.editCardView);
                imageViewDelete = (ImageView) itemView.findViewById(R.id.deleteCardView);

            }
        }
    }
}

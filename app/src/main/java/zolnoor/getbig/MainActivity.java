package zolnoor.getbig;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Date;

/*
         REMINDER!!! I need to go through and make sure cursors and dbs get closed after all my querying.
    Need to do that at some point - 08/22/14
*/


public class MainActivity extends ListActivity {

    private static final int DELETE_ID = Menu.FIRST + 3;
    private DatabaseHelper db = null;
    private Cursor workoutsCursor = null;
    public SimpleCursorAdapter adapter;
    public static boolean isViewingPast;
    public static String currentViewingDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        workoutsCursor = db
                .getReadableDatabase()
                .rawQuery("SELECT _ID, title, fulldate " +
                                "FROM workouts ORDER BY _ID",
                        null
                );
       // workoutsCursor.moveToFirst();
        //Log.d("SHITFUCK", ""+workoutsCursor.getString(2));

        adapter = new SimpleCursorAdapter(this,
                R.layout.list_view_item, workoutsCursor,
                new String[]{DatabaseHelper.TITLE, DatabaseHelper.FULLDATE},
                new int[]{R.id.textViewItem, R.id.textViewDate}, 0);

        setListAdapter(adapter);
        registerForContextMenu(getListView());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        workoutsCursor.close();
        db.close();
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
    }

    //populates the actionbar with that one item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(!isViewingPast) {
            ActionBar bar = getActionBar();
            bar.setBackgroundDrawable(new ColorDrawable(0xffFF2400));
            bar.setTitle(Html.fromHtml("<font color='#FFFFFF'><b>GetBig</b></font>"));
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);
            return super.onCreateOptionsMenu(menu);
        }
        else{
            ActionBar bar = getActionBar();
            bar.setBackgroundDrawable(new ColorDrawable(0xffFF2400));
            bar.setTitle(Html.fromHtml("<font color='#FFFFFF'><b>Workouts on "+currentViewingDate+"</b></font>"));
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main3, menu);
            return super.onCreateOptionsMenu(menu);
        }



    }

    //Decides what to do for what item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_workout:
                add();
                return true;
            case R.id.action_workout_calendar:
                calendarDialog();
                return true;
            case R.id.return_main_view:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, "Delete")
                .setAlphabeticShortcut('d');
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                delete(info.id);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }



    //Opens a dialog button with an edittext, positive and negative button. if ok is selected, showThatShit()
    //is called and the edittext field value is saved to a string
    public void add() {
        final EditText input = new EditText(this);
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

       AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle("Create new workout")
                .setMessage("Please name your workout!")
                .setView(input)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                Editable value = input.getText();
                                String name = value.toString();
                                processAdd(name);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do nothing.
                            }
                        }
                );

            AlertDialog dialog;
                dialog = builder.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();
    }

    public void delete(final long rowId) {
        if (rowId > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("You sure you want to delete this Workout?")
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    processDelete(rowId);
                                }
                            }
                    )
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Do nothing.
                                }
                            }
                    )
                    .show();


        }
    }

    public void calendarDialog(){


        View calendarDialog = View.inflate(this, R.layout.calendar, null);

        final DatePicker datePicker = (DatePicker) calendarDialog.findViewById(R.id.datePicker);

        new AlertDialog.Builder(this)
                .setTitle("View past workouts!")
                .setView(calendarDialog)
                .setPositiveButton("View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String month, day;

                        month = ""+(datePicker.getMonth()+1);
                        day = ""+datePicker.getDayOfMonth();

                        if(datePicker.getDayOfMonth()<10){
                            day="0"+datePicker.getDayOfMonth();
                        }
                        if((datePicker.getMonth()+1)<10){
                            month="0"+(datePicker.getMonth()+1);
                        }
                        String DATE = ""+datePicker.getYear()+month+day;
                        int dateInt = Integer.parseInt(DATE);
                        refresh(dateInt);
                        Toast.makeText(getBaseContext(), ""+dateInt, Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //do nuttin
                    }
                })
                .show();


    }

    public void processAdd(String name){
        ContentValues values=new ContentValues(1);

        values.put(DatabaseHelper.TITLE, name);

        db.getWritableDatabase().insert("workouts", DatabaseHelper.TITLE, values);
        refresh();
    }

    public void processDelete(long rowId){
        String[] args={String.valueOf(rowId)};

        db.getWritableDatabase().delete("workouts", "_ID=?", args);
        refresh();
    }

    //Refreshes the current list view when adding or deleting items
    public void refresh(){

        db = new DatabaseHelper(this);
        workoutsCursor = db
                .getReadableDatabase()
                .rawQuery("SELECT _ID, title, fulldate " +
                                "FROM workouts ORDER BY _ID",
                        null
                );
        adapter = new SimpleCursorAdapter(this,
                R.layout.list_view_item, workoutsCursor,
                new String[]{DatabaseHelper.TITLE, DatabaseHelper.FULLDATE},
                new int[]{R.id.textViewItem, R.id.textViewDate}, 0);

        setListAdapter(adapter);
        registerForContextMenu(getListView());
        isViewingPast=false;
        invalidateOptionsMenu();

        //workoutsCursor.close();
        //db.close();

    }

    public void refresh(int dateInt){
        db = new DatabaseHelper(this);
        workoutsCursor = db
                .getReadableDatabase()
                .rawQuery("SELECT _ID, title, fulldate " +
                                "FROM workouts WHERE date = "+dateInt,
                        null
                );
        adapter = new SimpleCursorAdapter(this,
                R.layout.list_view_item, workoutsCursor,
                new String[]{DatabaseHelper.TITLE, DatabaseHelper.FULLDATE},
                new int[]{R.id.textViewItem, R.id.textViewDate}, 0);

        setListAdapter(adapter);
        registerForContextMenu(getListView());
        isViewingPast=true;

        String fullDate="";
        String dayte = Integer.toString(dateInt);

        SimpleDateFormat smf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat two = new SimpleDateFormat("MM/dd/yyyy");
        //SimpleDateFormat two = new SimpleDateFormat("EEE MMM dd, yyyy");
        try {
            currentViewingDate = two.format(smf.parse(dayte));
        }catch (Exception e){
            Log.d("Date", "exception was "+e);
            currentViewingDate="wow the date shit didnt work";
        }


        invalidateOptionsMenu();

        //workoutsCursor.close();
        //db.close();


    }

    //Finds the _ID of the workout clicked, puts it in an intent and sends it to
    //ExerciseView.java in order to make correct PID and get right title
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        int bigID=-14;
        workoutsCursor.moveToFirst();
        while(bigID==-14){
            if(workoutsCursor.getInt(0)==id){
                bigID=workoutsCursor.getInt(0);
                Intent intent = new Intent(this, ExerciseView.class);
                intent.putExtra("PID", bigID);
                startActivity(intent);

            }
            else{
                workoutsCursor.moveToNext();
            }
        }
    }




}


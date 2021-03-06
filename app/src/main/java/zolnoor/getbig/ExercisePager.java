package zolnoor.getbig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;



public class ExercisePager extends FragmentActivity implements ActionBar.TabListener {


    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     *
     */
    ViewPager mViewPager;
    Intent nIntent;
    public static int EID, WID;
    String DATE, day, month, year;
    static int typeOfView, numberOfSets, dateInt;
    private Cursor paramCursor, exerciseCursor, dataCursor;
    private DatabaseHelper db, pdb;
    public static int currentTab;


    //Called when activity is created. Sets up Actionbar, queries DB for the current exercise's data
    //If it has no data, it creates a row for the current date, if it does, it uses it
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);

        //Get EID from ExerciseView.java
        nIntent = getIntent();
        EID = nIntent.getIntExtra("EID", 1);
        WID = nIntent.getIntExtra("WID", 1);

        //set type of view
        typeOfView = getTypeOfView(EID);

        //query database
        db = new DatabaseHelper(this);
        paramCursor = db
                .getReadableDatabase()
                .rawQuery("SELECT _ID, sets, reps " +
                                "FROM parameters where pid="+EID,
                        null
                );
        paramCursor.moveToFirst();
        numberOfSets = paramCursor.getInt(1);



        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(false);

        String title = getTitle(EID);
        if(title==null){
            actionBar.setTitle("Null error!");
        }
        else{
            if(MainActivity.isViewingPast) {
                actionBar.setTitle(title+"("+MainActivity.currentViewingDate+")");
            }
            else{
                actionBar.setTitle(title);
            }
            }

        actionBar.setBackgroundDrawable(new ColorDrawable(0xffFF2400));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        Calendar c1 = Calendar.getInstance();
        if(c1.get(Calendar.DAY_OF_MONTH)<10){
            day="0"+c1.get(Calendar.DAY_OF_MONTH);
        }
            else {
                day=""+c1.get(Calendar.DAY_OF_MONTH);
            }

        if((c1.get(Calendar.MONTH)+1)<10){
            month="0"+(c1.get(Calendar.MONTH)+1);
        }
            else{
            month=""+(c1.get(Calendar.MONTH)+1);
        }
        DATE = ""+c1.get(Calendar.YEAR)+month+day;
        dateInt = Integer.parseInt(DATE);

        pdb = new DatabaseHelper(this);
        dataCursor = pdb
                .getWritableDatabase()
                .rawQuery("SELECT * " +
                        "FROM data where eid="+EID+" AND wid="+WID+" AND date="+dateInt,
                null
        );

        if(dataCursor.moveToFirst()){
            dataCursor.close();
        }
        else{
            ContentValues cv = new ContentValues(3);
            cv.put(DatabaseHelper.WID, WID);
            cv.put(DatabaseHelper.EID, EID);
            cv.put(DatabaseHelper.DATE, dateInt);
            pdb.getWritableDatabase().insert("data", DatabaseHelper.WID, cv);
            dataCursor.close();
        }
        paramCursor.close();
        pdb.close();

        Log.d("PagerOnCreate", "number of sets is "+numberOfSets);
        if(numberOfSets == 1){
            actionBar.addTab(
                    actionBar.newTab()
                            .setText("Data")
                            .setTabListener(this));
            Log.d("TAB", "made the one tab");

        }
        else if (numberOfSets > 1){
            // For each of the sections in the app, add a tab to the action bar.
            for (int i = 0; i < numberOfSets; i++) {
                // Create a tab with text corresponding to the page title defined by
                // the adapter. Also specify this Activity object, which implements
                // the TabListener interface, as the callback (listener) for when
                // this tab is selected.
                actionBar.addTab(
                        actionBar.newTab()
                                .setText("Set " + (i + 1))
                                .setTabListener(this)
                );
            }
        }


    }

    //Decides which view will be inflated based on paramaters set at exercise creation
    private int getTypeOfView(int parent){
        int view=-1;

        //set cursor and query paramater table
        db = new DatabaseHelper(this);
        paramCursor = db
                .getReadableDatabase()
                .rawQuery("SELECT _ID, reps, notes, weight " +
                                "FROM parameters where pid="+parent,
                        null
                );
        paramCursor.moveToFirst();

        //Just one paramater
        if(paramCursor.getInt(1)==1 && paramCursor.getInt(2)==0 && paramCursor.getInt(3)==0){
            view = 0;
        }
        else if(paramCursor.getInt(1)==0 && paramCursor.getInt(2)==1 && paramCursor.getInt(3)==0){
            view = 1;
        }
        else if(paramCursor.getInt(1)==0 && paramCursor.getInt(2)==0 && paramCursor.getInt(3)==1){
            view = 2;
        }
        //Two paramaters
        else if(paramCursor.getInt(1)==1 && paramCursor.getInt(2)==1 && paramCursor.getInt(3)==0){
            view = 3;
        }
        else if(paramCursor.getInt(1)==0 && paramCursor.getInt(2)==1 && paramCursor.getInt(3)==1){
            view = 4;
        }
        else if(paramCursor.getInt(1)==1 && paramCursor.getInt(2)==0 && paramCursor.getInt(3)==1){
            view = 5;
        }
        //ALL THREE
        else if(paramCursor.getInt(1)==1 && paramCursor.getInt(2)==1 && paramCursor.getInt(3)==1){
            view = 6;
        }

        return view;
    }


    //Returns current tab int as shown in the GUI
    //Not used so much anymore. Will probably get rid of this in the future (09/29/14)
    public int currentTab(int newCurrentTab){
        currentTab = newCurrentTab+1;

        return currentTab;
    }

    //*Kanye Shrug*
    //(09/24/14)
    public static int currentTab(){
        return currentTab;
    }

    //Just grabs the title and returns it as a string so I can put it in the AB
    private String getTitle(Integer number){

        String name =null;

        pdb = new DatabaseHelper(this);
        exerciseCursor = pdb.getReadableDatabase()
                .rawQuery("SELECT _ID, title " +
                                "FROM exercises WHERE _ID = "+number,
                        null
                );
        try {
            exerciseCursor.moveToFirst();
            name = exerciseCursor.getString(1);

        } catch (Exception e){
            Log.d("INDEXING", "caught exception " + e);
        }

        return name;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        currentTab(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return  PlaceholderFragment.newInstance(position+1);
        }

        @Override
        public int getCount() {

            return numberOfSets;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                   // return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    //return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                   // return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment{
        EditText repsEdit, weightEdit, notesEdit;
        TextView repsText, weightText, notesText;
        List<String> repsList = new ArrayList<String>();
        List<String> weightList = new ArrayList<String>();
        int repsItem=0;
        int weightItem=50;
        int sectionArgument;
        Cursor dataCursor, excCursor, woutCursor;
        DatabaseHelper db;



        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt("sect", sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        //Called when fragments are created. This uses the typeOfView int in a view populating switch statement
        //Also they are two different switches depending on whether we're viewing past data or not
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            sectionArgument = getArguments() != null ? getArguments().getInt("sect") : 1;

            if(!MainActivity.isViewingPast) {

                db = new DatabaseHelper(getActivity());
                dataCursor = db
                        .getReadableDatabase()
                        .rawQuery("SELECT reps"+sectionArgument+", weight"+sectionArgument+", notes"+sectionArgument+" " +
                                        "FROM data WHERE eid="+ExercisePager.EID+" AND date="+dateInt,
                                null
                        );
                dataCursor.moveToFirst();

                switch (typeOfView) {
                    case 0:
                        rootView = inflater.inflate(R.layout.fragment_vp, container, false);
                        repsEdit = (EditText) rootView.findViewById(R.id.repsEdit);
                        initializeEditListener(repsEdit, "reps");
                        if(dataCursor.getInt(0)==0){
                            repsEdit.setText("");
                            repsEdit.setHint("0");
                        }
                        else {
                            repsEdit.setText(Integer.toString(dataCursor.getInt(0)));
                        }
                        return rootView;
                    case 1:
                        rootView = inflater.inflate(R.layout.fragment_vp1, container, false);
                        notesEdit = (EditText) rootView.findViewById(R.id.notes);
                        initializeNoteListener(notesEdit);
                        notesEdit.setText(dataCursor.getString(2));
                        return rootView;
                    case 2:
                        rootView = inflater.inflate(R.layout.fragment_vp2, container, false);
                        weightEdit = (EditText) rootView.findViewById(R.id.weightEdit);
                        initializeEditListener(weightEdit, "weight");
                        if(dataCursor.getInt(1)==0){
                            weightEdit.setText("");
                            weightEdit.setHint("0");
                        }
                        else {
                            weightEdit.setText(Integer.toString(dataCursor.getInt(1)));
                        }
                        return rootView;
                    case 3:
                        rootView = inflater.inflate(R.layout.fragment_vp3, container, false);
                        repsEdit = (EditText) rootView.findViewById(R.id.repsEdit);
                        initializeEditListener(repsEdit, "reps");
                        if(dataCursor.getInt(0)==0){
                            repsEdit.setText("");
                            repsEdit.setHint("0");
                        }
                        else {
                            repsEdit.setText(Integer.toString(dataCursor.getInt(0)));
                        }
                        notesEdit = (EditText) rootView.findViewById(R.id.notes);
                        initializeNoteListener(notesEdit);
                        notesEdit.setText(dataCursor.getString(2));
                        return rootView;
                    case 4:
                        rootView = inflater.inflate(R.layout.fragment_vp4, container, false);
                        weightEdit = (EditText) rootView.findViewById(R.id.weightEdit);
                        initializeEditListener(weightEdit, "weight");
                        if(dataCursor.getInt(1)==0){
                            weightEdit.setText("");
                            weightEdit.setHint("0");
                        }
                        else {
                            weightEdit.setText(Integer.toString(dataCursor.getInt(1)));
                        }
                        notesEdit = (EditText) rootView.findViewById(R.id.notes);
                        initializeNoteListener(notesEdit);
                        notesEdit.setText(dataCursor.getString(2));
                        return rootView;
                    case 5:
                        rootView = inflater.inflate(R.layout.fragment_vp5, container, false);
                        repsEdit = (EditText) rootView.findViewById(R.id.repsEdit);
                        initializeEditListener(repsEdit, "reps");
                        if(dataCursor.getInt(0)==0){
                            repsEdit.setText("");
                            repsEdit.setHint("0");
                        }
                        else {
                            repsEdit.setText(Integer.toString(dataCursor.getInt(0)));
                        }
                        weightEdit = (EditText) rootView.findViewById(R.id.weightEdit);
                        initializeEditListener(weightEdit, "weight");
                        if(dataCursor.getInt(1)==0){
                            weightEdit.setText("");
                            weightEdit.setHint("0");
                        }
                        else {
                            weightEdit.setText(Integer.toString(dataCursor.getInt(1)));
                        }
                        initializeEditListener(weightEdit, "weight");
                        return rootView;
                    case 6:
                        rootView = inflater.inflate(R.layout.fragment_vp6, container, false);
                        repsEdit = (EditText) rootView.findViewById(R.id.repsEdit);
                        initializeEditListener(repsEdit, "reps");
                        if(dataCursor.getInt(0)==0){
                            repsEdit.setText("");
                            repsEdit.setHint("0");
                        }
                        else {
                            repsEdit.setText(Integer.toString(dataCursor.getInt(0)));
                        }
                        weightEdit = (EditText) rootView.findViewById(R.id.weightEdit);
                        initializeEditListener(weightEdit, "weight");
                        if(dataCursor.getInt(1)==0){
                            weightEdit.setText("");
                            weightEdit.setHint("0");
                        }
                        else {
                            weightEdit.setText(Integer.toString(dataCursor.getInt(1)));
                        }
                        notesEdit = (EditText) rootView.findViewById(R.id.notes);
                        initializeNoteListener(notesEdit);
                        notesEdit.setText(dataCursor.getString(2));
                        return rootView;
                }
            }
            else{
                db = new DatabaseHelper(getActivity());
                dataCursor = db
                        .getReadableDatabase()
                        .rawQuery("SELECT reps"+sectionArgument+", weight"+sectionArgument+", notes"+sectionArgument+" " +
                                        "FROM data WHERE eid="+ExercisePager.EID+" AND date="+MainActivity.currentViewingDateInt,
                                null
                        );
                dataCursor.moveToFirst();

                switch (typeOfView){
                    case 0:
                        rootView = inflater.inflate(R.layout.fragment_past, container, false);
                        repsText = (TextView) rootView.findViewById(R.id.repsDisplay);
                        repsText.setText(Integer.toString(dataCursor.getInt(0)));
                        return rootView;
                    case 1:
                        rootView = inflater.inflate(R.layout.fragment_past1, container, false);
                        notesText = (TextView) rootView.findViewById(R.id.notesDisplay);
                        notesText.setText(dataCursor.getString(2));
                        return rootView;
                    case 2:
                        rootView = inflater.inflate(R.layout.fragment_past2, container, false);
                        weightText = (TextView) rootView.findViewById(R.id.weightDisplay);
                        weightText.setText(Integer.toString(dataCursor.getInt(1)));

                        return rootView;
                    case 3:
                        rootView = inflater.inflate(R.layout.fragment_past3, container, false);
                        repsText = (TextView) rootView.findViewById(R.id.repsDisplay);
                        repsText.setText(Integer.toString(dataCursor.getInt(0)));
                        notesText = (TextView) rootView.findViewById(R.id.notesDisplay);
                        notesText.setText(dataCursor.getString(2));
                        return rootView;
                    case 4:
                        rootView = inflater.inflate(R.layout.fragment_past4, container, false);
                        weightText = (TextView) rootView.findViewById(R.id.weightDisplay);
                        weightText.setText(Integer.toString(dataCursor.getInt(1)));
                        notesText = (TextView) rootView.findViewById(R.id.notesDisplay);
                        notesText.setText(dataCursor.getString(2));
                        return rootView;
                    case 5:
                        rootView = inflater.inflate(R.layout.fragment_past5, container, false);
                        repsText = (TextView) rootView.findViewById(R.id.repsDisplay);
                        repsText.setText(Integer.toString(dataCursor.getInt(0)));
                        weightText = (TextView) rootView.findViewById(R.id.weightDisplay);
                        weightText.setText(Integer.toString(dataCursor.getInt(1)));

                        return rootView;
                    case 6:
                        rootView = inflater.inflate(R.layout.fragment_past6, container, false);
                        repsText = (TextView) rootView.findViewById(R.id.repsDisplay);
                        repsText.setText(Integer.toString(dataCursor.getInt(0)));
                        weightText = (TextView) rootView.findViewById(R.id.weightDisplay);
                        weightText.setText(Integer.toString(dataCursor.getInt(1)));
                        notesText = (TextView) rootView.findViewById(R.id.notesDisplay);
                        notesText.setText(dataCursor.getString(2));
                        return rootView;
                }
            }

            dataCursor.close();
            db.close();

            return rootView;

        }

        //Initializes the EditText listener for the notes.
        //If I tried hard enough I could have made one initializer method for all three
        //I'll do it later (09/29/14)
        // OH, also this crashes if a single apostrophe is used bc of SQL. need ot fix that
        public void initializeNoteListener(final EditText edit){
            try {
                edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        String DATE, month, day;
                        String item = edit.getText().toString();
                        item = item.replace("'", "''");

                        Calendar c1 = Calendar.getInstance();

                        month = ""+(c1.get(Calendar.MONTH)+1);
                        day = ""+c1.get(Calendar.DAY_OF_MONTH);

                        if(c1.get(Calendar.DAY_OF_MONTH)<10){
                            day="0"+c1.get(Calendar.DAY_OF_MONTH);
                        }
                        if((c1.get(Calendar.MONTH)+1)<10){
                            month="0"+(c1.get(Calendar.MONTH)+1);
                        }
                        DATE = ""+c1.get(Calendar.YEAR)+month+day;

                        int dateInt = Integer.parseInt(DATE);

                        db = new DatabaseHelper(getActivity());
                        dataCursor = db
                                .getReadableDatabase()
                                .rawQuery("SELECT _ID " +
                                                "FROM data WHERE eid="+ExercisePager.EID+" AND date="+dateInt,
                                        null
                                );
                        dataCursor.moveToFirst();

                        String update = "UPDATE data SET notes" + sectionArgument + " = '" + item + "' WHERE eid = " + ExercisePager.EID + " AND date = " + dateInt;
                        db.getWritableDatabase().execSQL(update);

                        updateDate(dateInt);
                        dataCursor.close();
                        db.close();


                    }
                });
            }catch(Exception e){
                Log.d("SPINNAH", "The exception was "+e);
            }
        }

        //Initializes the reps/weight edittext listeners and turns them into INTS
        public void initializeEditListener(final EditText edit, final String repsorweight){


            try {
                edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        String DATE, month, day;
                        String item = edit.getText().toString();
                        if(item.equals("")){
                            item="0";
                        }

                        int itemInt = Integer.parseInt(item);

                        Calendar c1 = Calendar.getInstance();

                        month = ""+(c1.get(Calendar.MONTH)+1);
                        day = ""+c1.get(Calendar.DAY_OF_MONTH);

                        if(c1.get(Calendar.DAY_OF_MONTH)<10){
                            day="0"+c1.get(Calendar.DAY_OF_MONTH);
                        }
                        if((c1.get(Calendar.MONTH)+1)<10){
                            month="0"+(c1.get(Calendar.MONTH)+1);
                        }
                        Log.d("REPSLISTENER", "The month is "+month+" and the day is "+day);
                        DATE = ""+c1.get(Calendar.YEAR)+month+day;
                        Log.d("REPSLISTENER", "DATE is "+DATE);
                        int dateInt = Integer.parseInt(DATE);
                        Log.d("REPSLISTENER", "dateInt is "+dateInt);
                        db = new DatabaseHelper(getActivity());
                        dataCursor = db
                                .getReadableDatabase()
                                .rawQuery("SELECT _ID " +
                                                "FROM data WHERE eid="+ExercisePager.EID+" AND date="+dateInt,
                                        null
                                );
                        dataCursor.moveToFirst();
                        int tab = currentTab();

                        String update = "UPDATE data SET "+repsorweight+"" + sectionArgument + " = " + itemInt + " WHERE eid = " + ExercisePager.EID + " AND date = " + dateInt;
                        db.getWritableDatabase().execSQL(update);
                        updateDate(dateInt);
                        dataCursor.close();
                        db.close();


                    }
                });
            }catch(Exception e){
                Log.d("SPINNAH", "The exception was "+e);
            }


        }

        //I dont remember why I made this. It's probably necessary though...
        public void updateDate(int recentDate){
            String Update;
            int workoutID;
            String fullDate="";
            String dayte = Integer.toString(recentDate);

            SimpleDateFormat smf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat two = new SimpleDateFormat("MM/dd/yyyy");
            //SimpleDateFormat two = new SimpleDateFormat("EEE MMM dd, yyyy");
            try {
                fullDate = two.format(smf.parse(dayte));
            }catch (Exception e){
                Log.d("Date", "exception was "+e);
                fullDate="Date error! This should never happen.";
            }

            db = new DatabaseHelper(getActivity());
            excCursor = db
                    .getWritableDatabase()
                    .rawQuery("SELECT _ID, pid " +
                                    "FROM exercises WHERE _ID="+ExercisePager.EID,
                            null
                    );
            excCursor.moveToFirst();
            Update = "UPDATE exercises SET date = "+recentDate+" WHERE _ID="+ExercisePager.EID;
            db.getWritableDatabase().execSQL(Update);

            Update = "UPDATE exercises SET fulldate = '"+fullDate+"' WHERE _ID="+ExercisePager.EID;
            db.getWritableDatabase().execSQL(Update);
            workoutID = excCursor.getInt(1);

            excCursor.close();

            woutCursor = db
                    .getWritableDatabase()
                    .rawQuery("SELECT _ID " +
                                    "FROM workouts WHERE _ID="+workoutID,
                            null
                    );
            Update = "UPDATE workouts SET date = "+recentDate+" WHERE _ID="+workoutID;
            db.getWritableDatabase().execSQL(Update);
           // Update = "UPDATE workouts SET date = "+recentDate+" AND fulldate = "+fullDate+" WHERE _ID="+workoutID;
            Update = "UPDATE workouts SET fulldate = '"+fullDate+"' WHERE _ID="+workoutID;
            db.getWritableDatabase().execSQL(Update);
            woutCursor.close();
            db.close();


        }



    }

}

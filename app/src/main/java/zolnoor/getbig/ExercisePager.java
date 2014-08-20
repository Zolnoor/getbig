package zolnoor.getbig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
//import android.app.Fragment;
//import android.app.FragmentManager;
import android.app.FragmentTransaction;
//import android.support.v13.app.FragmentPagerAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class ExercisePager extends FragmentActivity implements ActionBar.TabListener {


    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    Intent nIntent;
    int PID;
    static int typeOfView;
    private Cursor paramCursor, exerciseCursor;
    private DatabaseHelper db, pdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);

        //Get PID from ExerciseView.java
        nIntent = getIntent();
        PID = nIntent.getIntExtra("PID", 1);

        //set type of view
        typeOfView = getTypeOfView(PID);

        //query database
        db = new DatabaseHelper(this);
        paramCursor = db
                .getReadableDatabase()
                .rawQuery("SELECT _ID, sets, reps " +
                                "FROM parameters where pid="+PID,
                        null
                );
        paramCursor.moveToFirst();


        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(false);

        String title = getTitle(PID);
        if(title==null){
            actionBar.setTitle("Null error!");
        }
        else{
            actionBar.setTitle(title);
        }

        actionBar.setBackgroundDrawable(new ColorDrawable(0xffCE0F0F));

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

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < paramCursor.getInt(1); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText("Set "+(i+1))
                            .setTabListener(this));
        }
    }

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
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = new PlaceholderFragment();
            return  frag;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
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
    public static class PlaceholderFragment extends Fragment {
        Spinner repsSpinner, weightSpinner;
        List<String> repsList = new ArrayList<String>();
        List<String> weightList = new ArrayList<String>();
        int repsItem=0;
        int weightItem=50;






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
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;

            //populate lists and initialize spinners
            for (repsItem=0;repsItem<51;repsItem++){
                repsList.add(Integer.toString(repsItem));
                weightList.add(Integer.toString(weightItem));
                weightItem=weightItem+5;

            }

            ArrayAdapter<String> repsAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
                    android.R.layout.simple_spinner_item, repsList);
            ArrayAdapter<String> weightAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
                    android.R.layout.simple_spinner_item, weightList);
            repsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            switch(typeOfView){
                case 0:
                    rootView = inflater.inflate(R.layout.fragment_vp, container, false);
                    repsSpinner = (Spinner) rootView.findViewById(R.id.repsSpinner);
                    repsSpinner.setAdapter(repsAdapter);
                    return rootView;
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_vp1, container, false);
                    return rootView;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_vp2, container, false);
                    weightSpinner = (Spinner) rootView.findViewById(R.id.weightSpinner);
                    weightSpinner.setAdapter(weightAdapter);
                    return rootView;
                case 3:
                    rootView = inflater.inflate(R.layout.fragment_vp3, container, false);
                    repsSpinner = (Spinner) rootView.findViewById(R.id.repsSpinner);
                    repsSpinner.setAdapter(repsAdapter);
                    return rootView;
                case 4:
                    rootView = inflater.inflate(R.layout.fragment_vp4, container, false);
                    weightSpinner = (Spinner) rootView.findViewById(R.id.weightSpinner);
                    weightSpinner.setAdapter(weightAdapter);
                    return rootView;
                case 5:
                    rootView = inflater.inflate(R.layout.fragment_vp5, container, false);
                    repsSpinner = (Spinner) rootView.findViewById(R.id.repsSpinner);
                    repsSpinner.setAdapter(repsAdapter);
                    weightSpinner = (Spinner) rootView.findViewById(R.id.weightSpinner);
                    weightSpinner.setAdapter(weightAdapter);
                    return rootView;
                case 6:
                    rootView = inflater.inflate(R.layout.fragment_vp6, container, false);
                    repsSpinner = (Spinner) rootView.findViewById(R.id.repsSpinner);
                    repsSpinner.setAdapter(repsAdapter);
                    weightSpinner = (Spinner) rootView.findViewById(R.id.weightSpinner);
                    weightSpinner.setAdapter(weightAdapter);
                    return rootView;
            }
            return null;

        }
    }

}

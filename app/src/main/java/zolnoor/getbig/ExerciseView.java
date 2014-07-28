package zolnoor.getbig;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class ExerciseView extends ListActivity {

    private static final int DELETE_ID = Menu.FIRST + 3;
    private DatabaseHelper db = null;
    private DatabaseHelper pdb = null;
    private Cursor exerciseCursor = null;
    private Cursor workoutCursor = null;
    public SimpleCursorAdapter adapter;
    private Intent nIntent;
    private int PID;

    //calls updateList() in an attempt to get saved workouts
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nIntent = getIntent();
        PID = nIntent.getIntExtra("PID", 0);
        String eyedee = Integer.toString(PID);
        db = new DatabaseHelper(this);
        exerciseCursor = db
                .getReadableDatabase()
                .rawQuery("SELECT _ID, title " +
                                "FROM exercises where pid = "+eyedee,
                        null
                );

        adapter = new SimpleCursorAdapter(this,
                R.layout.list_view_item, exerciseCursor,
                new String[]{DatabaseHelper.TITLE},
                new int[]{R.id.textViewItem}, 0);

        setListAdapter(adapter);
        registerForContextMenu(getListView());
        ActionBar ab = getActionBar();
        String title = getTitle(PID);

        if(title == null){
            ab.setTitle(R.string.null_message);
        }
        else{
            ab.setTitle(title);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        exerciseCursor.close();
        db.close();
    }

    private String getTitle(Integer number){

        String name =null;

        pdb = new DatabaseHelper(this);
        workoutCursor = pdb.getReadableDatabase()
                       .rawQuery("SELECT _ID, title " +
                                       "FROM workouts WHERE _ID = "+number,
                               null
                       );
        try {
            workoutCursor.moveToFirst();
            name = workoutCursor.getString(1);

        } catch (Exception e){
            Log.d("INDEXING", "caught exception"+e);
        }
       
        return name;

    }

    //populates the actionbar with that one item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    //Decides what to do for what item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_workout:
                add();
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
        new AlertDialog.Builder(this)
                .setTitle("Create new workout")
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
                )
                .show();
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

    public void processAdd(String name){
        nIntent = getIntent();
        PID = nIntent.getIntExtra("PID", 0);

        ContentValues values=new ContentValues(2);

        values.put(DatabaseHelper.TITLE, name);
        values.put(DatabaseHelper.PID, PID);

        db.getWritableDatabase().insert("exercises", DatabaseHelper.TITLE, values);
        refresh();
    }

    public void processDelete(long rowId){
        String[] args={String.valueOf(rowId)};

        db.getWritableDatabase().delete("exercises", "_ID=?", args);
        refresh();
    }

    public void refresh(){

        nIntent = getIntent();
        PID = nIntent.getIntExtra("PID", 0);

        db = new DatabaseHelper(this);
        exerciseCursor = db
                .getReadableDatabase()
                .rawQuery("SELECT _ID, title " +
                                "FROM exercises WHERE pid = "+PID,
                        null
                );
        adapter = new SimpleCursorAdapter(this,
                R.layout.list_view_item, exerciseCursor,
                new String[]{DatabaseHelper.TITLE},
                new int[]{R.id.textViewItem}, 0);

        setListAdapter(adapter);
        registerForContextMenu(getListView());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        int bigID=-14;
        exerciseCursor.moveToFirst();
        while(bigID==-14){
            if(exerciseCursor.getInt(0)==id){
                bigID=exerciseCursor.getInt(0);


                Toast.makeText(this, "id is" + bigID, Toast.LENGTH_SHORT).show();

            }
            else{
                exerciseCursor.moveToNext();
            }



        }


    }
}

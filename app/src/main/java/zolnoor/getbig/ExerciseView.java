package zolnoor.getbig;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
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
    private int PID, REPS, WEIGHT, NOTES, SETS, setss;
    private NumberPicker np;
    private CheckBox reps, weight, notes, isSets;


    //gets PID from previous activity and uses it to set title. creates cursors and queries DB
    //Also sets list adapter
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
                .rawQuery("SELECT _ID, title, clicked " +
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
        ab.setBackgroundDrawable(new ColorDrawable(0xffCE0F0F));

        if(title == null){
            ab.setTitle(R.string.null_message);
        }
        else{
            ab.setTitle(Html.fromHtml("<font color='#FFFFFF'><b>"+title+"</b></font>"));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        exerciseCursor.close();
        db.close();
    }

    //Pulls the title by using the PID from the 'workouts' table
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
            Log.d("INDEXING", "caught exception "+e);
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

    //Creates the parameter view, then inflates a dialog box with it
    //Made for deciding how the exercise fragment will appear
    public void fragParams(final int igd){
        REPS = 0;
        SETS = 0;
        NOTES = 0;
        WEIGHT = 0;

        View checkBoxView = View.inflate(this, R.layout.paramaters, null);

        try {
           /* np = (NumberPicker) checkBoxView.findViewById(R.id.sets);
            np.setMaxValue(10);
            np.setMinValue(1);
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                    SETS = i;
                }
            }); */

            isSets = (CheckBox) checkBoxView.findViewById(R.id.isSets);
            isSets.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b == true) {
                        SETS = 1;
                    }  if (b == false) {
                        SETS = 0;
                    }
                }
            });






            reps = (CheckBox) checkBoxView.findViewById(R.id.reps);
            reps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b == true) {
                        REPS = 1;
                    } else if (b == false) {
                        REPS = 0;
                    }
                }
            });

            weight = (CheckBox) checkBoxView.findViewById(R.id.weight);
            weight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b == true) {
                        WEIGHT = 1;
                    } else if (b == false) {
                        WEIGHT = 0;
                    }
                }
            });


            notes = (CheckBox) checkBoxView.findViewById(R.id.notes);
            notes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b == true) {
                        NOTES = 1;
                    } else if (b == false) {
                        NOTES = 0;
                    }
                }
            });



            new AlertDialog.Builder(this)
                    .setTitle("What data would you like to record?")
                    .setMessage("Choose necessary parameters")
                    .setView(checkBoxView)
                    .setCancelable(false)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("VALUEOFSETS", "SETS is equal to "+SETS);
                            if (SETS == 1) {
                                DialogExtension(igd, REPS, NOTES, WEIGHT);
                            } else if (SETS==0){
                                ContentValues values = new ContentValues(6);
                                int one=1;
                                values.put(DatabaseHelper.PID, igd);
                                values.put(DatabaseHelper.SETS, 1);
                                values.put(DatabaseHelper.REPS, REPS);
                                values.put(DatabaseHelper.NOTES, NOTES);
                                values.put(DatabaseHelper.WEIGHT, WEIGHT);
                                values.put(DatabaseHelper.CLICKED, one);
                                db.getWritableDatabase().insert("parameters", DatabaseHelper.SETS, values);

                                ContentValues click = new ContentValues();
                                click.put("CLICKED", 1);
                                db.getWritableDatabase().update("exercises", click, "_id="+igd, null);
                                Log.d("FIRSTDIALOG", "The ID was "+igd);
                                refresh();

                                    Intent intent = new Intent(getBaseContext(), ExercisePager.class);
                                    intent.putExtra("PID", igd);
                                    startActivity(intent);
                                // Toast.makeText(getBaseContext(), "Just testing " + SETS, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Do nothing.
                                }
                            }
                    )
                    .show();

        }catch (Exception e){
            Log.d("FUCK", "The exception was "+e);
        }
    }

    //Extends the first parameter dialog if the number of sets must be specified
    public void DialogExtension(final int P, final int Re, final int N, final int W){

        View numberPickerView = View.inflate(this, R.layout.number_of_sets, null);

        np = (NumberPicker) numberPickerView.findViewById(R.id.sets);
        np.setMaxValue(10);
        np.setMinValue(1);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
               setss = i2;
            }
        });


        new AlertDialog.Builder(this)
                .setTitle("Specify number of sets.")
                .setMessage("Choose how many sets you will do for this exercise.")
                .setView(numberPickerView)
                .setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ContentValues values = new ContentValues(6);
                        int one=1;
                        values.put(DatabaseHelper.PID, P);
                        values.put(DatabaseHelper.SETS, setss);
                        values.put(DatabaseHelper.REPS, Re);
                        values.put(DatabaseHelper.NOTES, N);
                        values.put(DatabaseHelper.WEIGHT, W);
                        values.put(DatabaseHelper.CLICKED, one);
                        db.getWritableDatabase().insert("parameters", DatabaseHelper.CLICKED, values);

                        ContentValues click = new ContentValues(1);
                        click.put(DatabaseHelper.CLICKED, 1);
                        db.getWritableDatabase().update("exercises", click, "_id="+P, null);
                        Log.d("SECONDDIALOG", "the ID was "+P);
                        refresh();

                        Intent intent = new Intent(getBaseContext(), ExercisePager.class);
                        intent.putExtra("PID", P);
                        startActivity(intent);
                        //Toast.makeText(getBaseContext(), "Just testing " + SETS, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do nothing.
                            }
                        }
                )
                .show();
    }



    //Opens a dialog button with an edittext, positive and negative button. if ok is selected
    //then processAdd() is called
    public void add() {
        final EditText input = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle("Create new exercise")
                .setMessage("Please name your exercise!")
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

    //Deletes an entry from the DB
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

    //Adds a new row to the table 'exercises'
    public void processAdd(String name){
        nIntent = getIntent();
        PID = nIntent.getIntExtra("PID", 0);

        ContentValues values=new ContentValues(2);

        values.put(DatabaseHelper.TITLE, name);
        values.put(DatabaseHelper.PID, PID);
        //values.put(DatabaseHelper.CLICKED, 0);

        db.getWritableDatabase().insert("exercises", DatabaseHelper.TITLE, values);
        refresh();
    }

    //processes the deletion
    public void processDelete(long rowId){
        String[] args={String.valueOf(rowId)};

        db.getWritableDatabase().delete("exercises", "_ID=?", args);
        refresh();
    }

    //Refreshes the current list view when adding or deleting items
    public void refresh(){

        nIntent = getIntent();
        PID = nIntent.getIntExtra("PID", 0);

        db = new DatabaseHelper(this);
        exerciseCursor = db
                .getReadableDatabase()
                .rawQuery("SELECT _ID, title, clicked " +
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

    //Gets the ID of the item. Checks to see if it has ever been clicked before
    //If it has been clicked, it opens the respective fragment
    //If not, it opens the dialog to change the fragment parameters
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);



        int bigID=-14;
        exerciseCursor.moveToFirst();
        Log.d("CLICKING", "ID is "+exerciseCursor.getInt(0)+"versus position id which is "+id);
        while(bigID==-14){
            if(exerciseCursor.getInt(0)==id){
                bigID=(int)id;
                Log.d("ONLISTCLICK", "The ID being passed everywhere is "+bigID);

                if(exerciseCursor.getInt(2)!=1){
                    Log.d("HASITBEENCLICKED", "The clicked value is "+exerciseCursor.getInt(1));
                    fragParams(bigID);


                }
                else{
                    //this is where I will open the fragment
                    Intent intent = new Intent(this, ExercisePager.class);
                    intent.putExtra("PID", bigID);
                    startActivity(intent);

                }
            }
            else{
                exerciseCursor.moveToNext();
            }
            if(exerciseCursor.getInt(0)!=id){
                Log.d("CLICKING", "SHIT THEY WERENT THE SAME");
            }
        }
    }



}

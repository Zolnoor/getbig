package zolnoor.getbig;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;

/**
 * Created by Nick on 7/8/2014.
 */
public class WorkoutView extends Activity {
   // Intent intent = getIntent();
   // final Workout wout = (Workout)intent.getSerializableExtra(MainActivity.NAME);

    //Creates view and sets custom ActionBar title
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_view);

        Intent intent = getIntent();
        final Workout wout = (Workout)intent.getSerializableExtra(MainActivity.NAME);
        String title = wout.getName();

        ActionBar ab = getActionBar();
        ab.setTitle(title);

    }


    //populates the actionbar with that one item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main2, menu);
        return super.onCreateOptionsMenu(menu);

    }

    //Decides what to do for what item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_add_exercise:
                addExercise();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Dialog builder. What pops up after clicking the plus sign
    public void addExercise(){
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Create new exercise")
                .setMessage("Please name your exercise!")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        String name = value.toString();
                        placeExrcs(name);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();

    }
    //takes name from dialog, passes it to custom class function, updates list
    public void placeExrcs(String name){
        Intent intent = getIntent();
        final Workout wout = (Workout)intent.getSerializableExtra(MainActivity.NAME);
        int workoutSize = wout.getSize();
        wout.add(name);
        updateList();

    }

    //gets intents originally, then updates the current list, also sets onclicklistener for testing
    void updateList(){


        final ListView listview = (ListView) findViewById(R.id.exercises);
        Intent intent = getIntent();
        final Workout wout = (Workout)intent.getSerializableExtra(MainActivity.NAME);
        int i;


        final ArrayList<String> list = new ArrayList<String>();

        for(i=wout.list.size()-1;i>=0;i--){

            list.add(0, wout.list.get(i).name);

        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
        registerForContextMenu(listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
                Toast.makeText(getBaseContext(), wout.list.get(arg2).name, Toast.LENGTH_SHORT).show();

            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                wout.list.remove(i);
                updateList();
                return false;
            }
        });

    }

   /* @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        Toast.makeText(getBaseContext(), "TEST", Toast.LENGTH_SHORT).show();
      if (v.getId()==R.id.exercises) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(wout.list.get(info.position).name);
            String[] menuItems = getResources().getStringArray(R.array.items);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    } */




}

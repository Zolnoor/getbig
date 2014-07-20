package zolnoor.getbig;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import java.util.List;


public class MainActivity extends Activity {

    public final static String NAME = "zolnoor.getbig.MainActivity";
    String name;
    List<Workout> wOuts = new ArrayList<Workout>();
    static int numberOfWorkouts;
    int i;
    public String json = new Gson().toJson(wOuts);

    //calls updateList() in an attempt to get saved workouts
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //updateList();

        Type type = new TypeToken<List<Workout>>(){}.getType();
        List<Workout> inpList = new Gson().fromJson(json, type);
        wOuts = inpList;

    }

    //Creates a listview, populates an ArrayList of strings with names of the workouts from the workout arraylist
    //then loops to populate the list, then makes an adapter and sets the listview adapter to that

   /* void recoverList(){
        if(wOuts.size()==0){
            Type type = new TypeToken<List<Workout>>(){}.getType();
            List<Workout> inpList = new Gson().fromJson(json, type);
            wOuts = inpList;

        }


    }*/

    @Override
    public void onPause(){
        super.onPause();
        json = new Gson().toJson(wOuts);


    }

    void updateList(){


        final ListView listview = (ListView) findViewById(R.id.workouts);
        final Intent intent = new Intent(this, WorkoutView.class);
        final ArrayList<String> list = new ArrayList<String>();

        for(i=wOuts.size()-1;i>=0;i--){

            list.add(0, wOuts.get(i).name);

        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){

                intent.putExtra(NAME, wOuts.get(arg2));
                startActivity(intent);
            }
        });
       json = new Gson().toJson(wOuts);
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
        switch(item.getItemId()){
            case R.id.action_add_workout:
                addWorkout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Opens a dialog button with an edittext, positive and negative button. if ok is selected, showThatShit()
    //is called and the edittext field value is saved to a string
    public void addWorkout(){
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Create new workout")
                .setMessage("Please name your workout!")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        name = value.toString();
                        showThatShit();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();



    }

    //assigns ID by using size of the arraylist of workouts, creates a new Workout for the name used, adds it
    //to the arraylist, and then calls updateList()
    private void showThatShit(){

        numberOfWorkouts=wOuts.size();
        Workout newOne = new Workout(name, numberOfWorkouts);
        wOuts.add(newOne);
        updateList();

    }



}


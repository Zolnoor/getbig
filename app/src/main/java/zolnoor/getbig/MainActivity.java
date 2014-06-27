package zolnoor.getbig;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.util.Log;
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
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {

    String name;
    List<Workout> wOuts = new ArrayList<Workout>();
    static int numberOfWorkouts;
    int i;

    //calls updateList() in an attempt to get saved workouts
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateList();


    }

    //Creates a listview, populates an ArrayList of strings with names of the workouts from the workout arraylist
    //then loops to populate the list, then makes an adapter and sets the listview adapter to that
    void updateList(){
       /* Gson gson = new Gson();
        String jsonWorkouts = gson.toJson(wOuts);
        Log.d("TAG","jsonWorkouts = " + jsonWorkouts);

        if (wOuts==null){

            Type type = new TypeToken<ArrayList<Workout>>(){}.getType();
            wOuts = gson.fromJson(jsonWorkouts, type);
        }*/

        final ListView listview = (ListView) findViewById(R.id.workouts);

        final ArrayList<String> list = new ArrayList<String>();
        for(i=wOuts.size()-1;i>=0;i--){

            list.add(wOuts.get(i).name);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);




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


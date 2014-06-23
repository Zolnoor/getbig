package zolnoor.getbig;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.widget.EditText;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.Button;
import zolnoor.getbig.Workout;
import android.app.ListActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    String name;
    int boobs;
    List<Workout> wOuts = new ArrayList<Workout>();
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Button workout=(Button)findViewById(R.id.action_add_workout);
        //workout.setOnClickListener(btnListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

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


       //Toast.makeText(getBaseContext(), "Add workout", Toast.LENGTH_SHORT).show();
    }

    public void showThatShit(){
       // Toast.makeText(getBaseContext(), name, Toast.LENGTH_SHORT).show();
       Workout newOne = Workout.newWOut(name);
        wOuts.add(newOne);
    }


}


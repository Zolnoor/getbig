package zolnoor.getbig;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import zolnoor.getbig.Exercise;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import zolnoor.getbig.MainActivity;

/**
 * Created by Nick on 6/23/2014.
 */
public class Workout implements Serializable {

    List<Exercise> list = new ArrayList<Exercise>();
    public static String lastdate;
    public final String name;
    public static Calendar c;
    public final int itemId;

    public Workout(String newName, int id){

        this.name=newName;
        this.c=Calendar.getInstance();
        this.itemId=id;


    }

    public String getName(){
        return this.name;
    }

    public List<Exercise> getList(){
        return this.list;
    }
    public int getSize(){
        return list.size();

    }

  /*  public void updateList(){
        int i;

        final ListView listview = (ListView) findViewById(R.id.exercises);
        final Intent intent = new Intent(this, WorkoutView.class);


        final ArrayList<String> list = new ArrayList<String>();

        for(i=list.size()-1;i>=0;i--){

            list.add(0, list.get(i).name);

        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){


            }
        });


    }
    */

    public void add(String name){
        Exercise newOne = new Exercise(name, list.size());
        list.add(0, newOne);


    }


}

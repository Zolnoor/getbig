package zolnoor.getbig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import zolnoor.getbig.MainActivity;

/**
 * Created by Nick on 6/23/2014.
 */
public class Workout {

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


}

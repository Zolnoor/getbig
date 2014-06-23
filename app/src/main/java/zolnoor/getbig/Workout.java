package zolnoor.getbig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import zolnoor.getbig.MainActivity;

/**
 * Created by Nick on 6/23/2014.
 */
public class Workout extends Object {

    List<Exercise> list = new ArrayList<Exercise>();
    static String lastdate;
    static String name;
    static Calendar c;

    public static Workout newWOut(String newName){
        Workout getSwole = new Workout();

        Workout.name=newName;
        Workout.c=Calendar.getInstance();

        return getSwole;
    }


}

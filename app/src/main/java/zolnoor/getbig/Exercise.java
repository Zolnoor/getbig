package zolnoor.getbig;

import java.util.Calendar;

/**
 * Created by Nick on 6/23/2014.
 */
public class Exercise extends Object {

    public static String lastdate;
    public final String name;
    public static Calendar c;
    public final int itemId;

    public Exercise(String newName, int id){

        this.name=newName;
        this.c=Calendar.getInstance();
        this.itemId=id;


    }

}

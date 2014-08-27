package zolnoor.getbig;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.SensorManager;
/**
 * Created by Nick on 7/20/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="db";
    static final String TITLE="title";
    static final String TABLE1="CREATE TABLE workouts (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT);";
    static final String TABLE2="CREATE TABLE exercises (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, pid INTEGER, clicked INTEGER);";
    static final String TABLE3="CREATE TABLE parameters (_id INTEGER PRIMARY KEY AUTOINCREMENT, sets INTEGER, reps INTEGER, pid INTEGER, notes INTEGER, weight INTEGER, clicked INTEGER);";
    static final String TABLE4="CREATE TABLE data (_id INTEGER PRIMARY KEY AUTOINCREMENT, wid INTEGER, eid INTEGER, date INTEGER, reps1 INTEGER, weight1 INTEGER, notes1 STRING, " +
            "reps2 INTEGER, weight2 INTEGER, notes2 STRING, reps3 INTEGER, weight3 INTEGER, notes3 STRING, reps4 INTEGER, weight4 INTEGER, notes4 STRING, reps5 INTEGER, weight5 INTEGER, " +
            "notes5 STRING, reps6 INTEGER, weight6 INTEGER, notes6 STRING, reps7 INTEGER, weight7 INTEGER, notes7 STRING, reps8 INTEGER, weight8 INTEGER, notes8 INTEGER, " +
            "reps9 INTEGER, weight9 INTEGER, notes9 STRING, reps10 INTEGER, weight10 INTEGER, notes10 STRING);";
    static final String PID="pid";
    static final String EID="eid";
    static final String WID="wid";
    static final String DATE="date";
    static final String CLICKED="clicked";
    static final String REPS="reps";
    static final String SETS="sets";
    static final String NOTES="notes";
    static final String WEIGHT="weight";


    public DatabaseHelper(Context context) { super(context, DATABASE_NAME, null, 2); }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(TABLE1);
        db.execSQL(TABLE2);
        db.execSQL(TABLE3);
        db.execSQL(TABLE4);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){
        android.util.Log.v("Workouts", "Upgrading database, which will destroy old date");
        db.execSQL("DROP TABLE IF EXISTS workouts");
        db.execSQL("DROP TABLE IF EXISTS exercises");
        db.execSQL("DROP TABLE IF EXISTS parameters");
        onCreate(db);
    }





}

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
    static final String TABLE2="CREATE TABLE exercises (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, pid INTEGER);";
    static final String PID="pid";


    public DatabaseHelper(Context context) { super(context, DATABASE_NAME, null, 2); }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(TABLE1);
        db.execSQL(TABLE2);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){
        android.util.Log.v("Workouts", "Upgrading database, which will destroy old date");
        db.execSQL("DROP TABLE IF EXISTS workouts");
        db.execSQL("DROP TABLE IF EXISTS exercises");
        onCreate(db);
    }





}

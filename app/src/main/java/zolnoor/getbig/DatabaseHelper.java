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


    public DatabaseHelper(Context context) { super(context, DATABASE_NAME, null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE workouts (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT);");
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){
        android.util.Log.v("Workouts", "Upgrading database, which will destroy old date");
        db.execSQL("DROP TABLE IF EXISTS workouts");
        onCreate(db);
    }





}

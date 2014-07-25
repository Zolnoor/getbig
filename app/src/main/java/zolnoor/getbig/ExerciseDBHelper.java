package zolnoor.getbig;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by Nick on 7/20/2014.
 */
public class ExerciseDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="db";
    static final String TITLE="title";
    static final String PID="pid";


    public ExerciseDBHelper(Context context) { super(context, DATABASE_NAME, null, 2); }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE exercises (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, pid INTEGER);");
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){
        android.util.Log.v("Exercises", "Upgrading database, which will destroy old date");
        db.execSQL("DROP TABLE IF EXISTS exercises");
        onCreate(db);
    }
}

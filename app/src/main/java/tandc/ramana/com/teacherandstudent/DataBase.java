package tandc.ramana.com.teacherandstudent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by india on 07-11-2016.
 */
public class DataBase  extends SQLiteOpenHelper {

    public DataBase(Context context) {
        super(context, "RamanaDatabase", null, 1);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        String aql = "create table Data(type varchar(20),data varchar(20),fileType varchar(20))";
        db.execSQL(aql);
        db.execSQL("insert into Data values('1','hai welcome','1')");
        db.execSQL("insert into Data values('2','hai guest','1')");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        onCreate(db);

    }


}
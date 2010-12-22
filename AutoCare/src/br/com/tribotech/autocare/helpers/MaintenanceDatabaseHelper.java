package br.com.tribotech.autocare.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.com.tribotech.autocare.model.Maintenance;

public class MaintenanceDatabaseHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 2;
	
	public static final String DATABASE_NAME = "Maintenance_DB";
	public static final String TABLE_NAME = "Maintenance";
	public static final String TAG = "MaintenanceProvider";
	
	public MaintenanceDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
        		"CREATE TABLE " + TABLE_NAME + " (" +
    				Maintenance._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    				Maintenance._COUNT + " INT," +
    				Maintenance.COLUMN_ID_VEHICLE + " INTEGER," +
    				Maintenance.COLUMN_ID_WORKSHOP + " INTEGER," +
    				Maintenance.COLUMN_TYPE + " INT," +
    				Maintenance.COLUMN_ID_PARTSET + " INT," +
    				Maintenance.COLUMN_DESCRIPTION + " TEXT," +
    				Maintenance.COLUMN_KM_PERIODIC + " INT," +
    				Maintenance.COLUMN_KM_REALIZED + " INT," +
    				Maintenance.COLUMN_DATE_REALIZED + " COLUMN_DATE" +
    			");"
    	);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}

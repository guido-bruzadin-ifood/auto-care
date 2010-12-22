package br.com.tribotech.autocare.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.com.tribotech.autocare.model.Vehicle;
import br.com.tribotech.autocare.model.VehicleManufacturer;
import br.com.tribotech.autocare.model.VehicleModel;

public class VehicleDatabaseHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 2;
	
	public static final String DATABASE_NAME = "Vehicle_DB";
	public static final String TABLE_NAME = "Vehicle";
	public static final String TABLE_MANUFACTURER = "Vehicle_Manufacturer";
	public static final String TABLE_MODEL = "Vehicle_Model";


	public static final String TAG = "VehicleProvider";

	public VehicleDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
				"CREATE TABLE " + TABLE_NAME + " (" +
				Vehicle._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				Vehicle._COUNT + " INT," +
				Vehicle.COLUMN_ID_MANUFACTURER + " INT," +
				Vehicle.COLUMN_ID_MODEL + " INT," +
				Vehicle.COLUMN_TYPE + " INT," +
				Vehicle.COLUMN_DESCRIPTION + " TEXT," +
				Vehicle.COLUMN_YEAR_MAKE + " COLUMN_DATE," +
				Vehicle.COLUMN_YEAR_MODEL + " COLUMN_DATE" +
				");"
		);

		db.execSQL(
				"CREATE TABLE IF NOT EXISTS " + TABLE_MANUFACTURER + " (" +
				VehicleManufacturer._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				VehicleManufacturer.COLUMN_MANUFACTURER +	" TEXT UNIQUE " +
				");"
		);

		db.execSQL(
				"CREATE TABLE IF NOT EXISTS " + TABLE_MODEL + " (" +
				VehicleModel._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				VehicleModel.COLUMN_ID_MANUFACTURER + " INT NOT NULL," +
				VehicleModel.COLUMN_MODEL + " TEXT" +
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

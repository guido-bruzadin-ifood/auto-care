package br.com.tribotech.autocare.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import br.com.tribotech.autocare.model.FuelFill;

public class FuelFillDatabaseHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 2;

	public static final String DATABASE_NAME = "FuelFill_DB";
	public static final String TABLE_NAME = "Fuel_Fill";

	public static final String TAG = "FuelFillProvider";

	public FuelFillDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
				"CREATE TABLE " + TABLE_NAME + " (" +
					BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					FuelFill.COLUMN_ID_VEHICLE+ " INT," +
					FuelFill.COLUMN_FUEL_TYPE + " INT," +
					FuelFill.COLUMN_FUEL_QTD + " DOUBLE," +
					FuelFill.COLUMN_FUEL_PRICE + " DOUBLE," +
					FuelFill.COLUMN_ODOMETER + " DOUBLE," +
					FuelFill.COLUMN_FULLTANk + " INT," +
					FuelFill.COLUMN_NEW_MARK + " INT," +
					FuelFill.COLUMN_DATE + " DATE" +
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

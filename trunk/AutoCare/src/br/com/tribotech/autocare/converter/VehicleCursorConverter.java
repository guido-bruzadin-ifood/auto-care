package br.com.tribotech.autocare.converter;

import android.database.Cursor;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import br.com.tribotech.autocare.model.Vehicle;

public class VehicleCursorConverter implements CursorToStringConverter{

	@Override
	public CharSequence convertToString(Cursor cursor) {
		
		int index = cursor.getColumnIndex(Vehicle.COLUMN_DESCRIPTION);
		return cursor.getString(index);
	}

}

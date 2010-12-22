package br.com.tribotech.autocare.converter;

import br.com.tribotech.autocare.model.VehicleManufacturer;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;

public class VehicleManufacturerCursorConverter implements CursorToStringConverter{

	@Override
	public CharSequence convertToString(Cursor cursor) {
		
		int index = cursor.getColumnIndex(VehicleManufacturer.COLUMN_MANUFACTURER);
		return cursor.getString(index);
	}

}

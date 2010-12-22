package br.com.tribotech.autocare.converter;

import android.database.Cursor;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import br.com.tribotech.autocare.model.VehicleModel;

public class VehicleModelCursorConverter implements CursorToStringConverter{

	@Override
	public CharSequence convertToString(Cursor cursor) {
		
		int index = cursor.getColumnIndex(VehicleModel.COLUMN_MODEL);
		return cursor.getString(index);
	}

}

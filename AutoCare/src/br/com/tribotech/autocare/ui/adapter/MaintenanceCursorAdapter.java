package br.com.tribotech.autocare.ui.adapter;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.tribotech.autocare.R;
import br.com.tribotech.autocare.model.Maintenance;

public class MaintenanceCursorAdapter extends CursorAdapter implements Filterable {

	private Context context;
	private int layout;

	public MaintenanceCursorAdapter(Context context, Cursor c) {
		super(context, c);

		this.context = context;
		this.layout = R.layout.list_maintenance_item;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int typeCol = cursor.getColumnIndex(Maintenance.COLUMN_TYPE);
		int descriptionCol = cursor.getColumnIndex(Maintenance.COLUMN_DESCRIPTION);
		int kmPeriodicCol = cursor.getColumnIndex(Maintenance.COLUMN_KM_PERIODIC);
		int kmRealizedCol = cursor.getColumnIndex(Maintenance.COLUMN_KM_REALIZED);
		int dateRealizedCol = cursor.getColumnIndex(Maintenance.COLUMN_DATE_REALIZED);

		Integer type = cursor.getInt(typeCol);
		String description = cursor.getString(descriptionCol);
		Double kmPeriod = cursor.getDouble(kmPeriodicCol);
		Double kmRealized = cursor.getDouble(kmRealizedCol);
		Date dateRealized = new Date(cursor.getLong(dateRealizedCol));

		ImageView vType = (ImageView) view.findViewById(R.id.type);
		if (vType != null) {
			switch (type) {
			case 1:
				vType.setImageResource(R.drawable.ic_edit);
				break;
			default:
				vType.setImageResource(R.drawable.ic_add);
				break;
			}
		}

		TextView vDescription = (TextView)view.findViewById(R.id.description);
		if(vDescription != null)
			vDescription.setText(description);

		TextView vPeriodic = (TextView)view.findViewById(R.id.periodic);
		if(vPeriodic != null)
			vPeriodic.setText(kmPeriod.toString());
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		Cursor c = getCursor();

		int typeCol = c.getColumnIndex(Maintenance.COLUMN_TYPE);
		int descriptionCol = c.getColumnIndex(Maintenance.COLUMN_DESCRIPTION);
		int kmPeriodicCol = c.getColumnIndex(Maintenance.COLUMN_KM_PERIODIC);
		int kmRealizedCol = c.getColumnIndex(Maintenance.COLUMN_KM_REALIZED);
		int dateRealizedCol = c.getColumnIndex(Maintenance.COLUMN_DATE_REALIZED);

		Integer type = c.getInt(typeCol);
		String description = c.getString(descriptionCol);
		Integer kmPeriod = c.getInt(kmPeriodicCol);
		Integer kmRealized = c.getInt(kmRealizedCol);
		Date dateRealized = new Date(c.getLong(dateRealizedCol));

		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(layout, parent, false);

		ImageView vType = (ImageView) v.findViewById(R.id.type);
		if (vType != null) {
			switch (type) {
			case 1:
				vType.setImageResource(R.drawable.ic_tab_maintenance_grey);
				break;
			default:
				vType.setImageResource(R.drawable.ic_tab_maintenance_white);
				break;
			}
		}

		TextView vDescription = (TextView)v.findViewById(R.id.description);
		if(vDescription != null)
			vDescription.setText(description);

		TextView vPeriodic = (TextView)v.findViewById(R.id.periodic);
		if(vPeriodic != null)
			vPeriodic.setText(kmPeriod.toString());

		return v;
	}


	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		if (getFilterQueryProvider() != null) { 
			return getFilterQueryProvider().runQuery(constraint); 
		}

		StringBuilder buffer = null;
		String[] args = null;
		if (constraint != null) {
			buffer = new StringBuilder();
			buffer.append(Maintenance.COLUMN_DESCRIPTION);
			buffer.append(" like ?");
			args = new String[] { constraint.toString().toUpperCase() + "*" };
		}

		return context.getContentResolver().query(Maintenance.CONTENT_URI, null,	buffer == null ? null : buffer.toString(), args, Maintenance.COLUMN_DESCRIPTION + " ASC");
	}

}

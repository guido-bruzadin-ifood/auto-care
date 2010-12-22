package br.com.tribotech.autocare.ui;

import java.util.Date;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import br.com.tribotech.autocare.R;
import br.com.tribotech.autocare.converter.VehicleCursorConverter;
import br.com.tribotech.autocare.model.Maintenance;
import br.com.tribotech.autocare.model.Vehicle;


public class MaintenanceDialog extends Dialog implements OnClickListener {

	private Spinner mComboTypes;
	private Spinner mComboVehicles;
	private EditText mQuantity;
	private EditText mPrice;
	private EditText mOdometer;
	
	private Button mOk;
	private Button mCancel;
	
	private final Context context;
	private final ContentResolver contentResolver;
	
	private Maintenance maintenance;
	private Cursor mCursorVehicles; 

	public MaintenanceDialog(Context context) {
		super(context);

		this.setContentView(R.layout.form_maintenance);
		this.setTitle(R.string.maintenance);
		this.context = context;
		
		contentResolver = context.getContentResolver();

		final String[] arrayTipos = new String[3];
		arrayTipos[Maintenance.PREVENTIVE] = context.getString(R.string.preventive);
		arrayTipos[Maintenance.CORRECTIVE] = context.getString(R.string.corrective);
		arrayTipos[Maintenance.PREDICTIVE] = context.getString(R.string.predictive);
		
		ArrayAdapter<String> adapterTypes = new ArrayAdapter<String>(context, R.layout.list_autocomplete_item, arrayTipos);
		mComboTypes = (Spinner) findViewById(R.id.combo_types);
		mComboTypes.setAdapter(adapterTypes); 
		
		mCursorVehicles = contentResolver.query(Vehicle.CONTENT_URI,new String[] {Vehicle._ID, Vehicle.COLUMN_DESCRIPTION} , null, null, null);
		final String[] mFrom = new String[] {Vehicle.COLUMN_DESCRIPTION};
		final int[] mTo = new int[]{R.id.list_item};
		SimpleCursorAdapter adapterVehicles = new SimpleCursorAdapter(context, R.layout.list_autocomplete_item, mCursorVehicles, mFrom, mTo);
		adapterVehicles.setCursorToStringConverter(new VehicleCursorConverter());
		mComboVehicles = (Spinner) findViewById(R.id.combo_vehicles);
		mComboVehicles.setAdapter(adapterVehicles);
		
		mQuantity = (EditText) findViewById(R.id.quantity);
		mPrice = (EditText) findViewById(R.id.price);
		mOdometer = (EditText) findViewById(R.id.odometer);
		
		mOk = (Button) findViewById(R.id.OkButton);
		mOk.setOnClickListener(this);

		mCancel = (Button) findViewById(R.id.CancelButton);
		mCancel.setOnClickListener(this);

	}

	//1967 2009
	private boolean validate(){
		boolean valid = true;
		StringBuffer erro = new StringBuffer();

		if(mComboTypes == null && mComboTypes.getSelectedItem() == null){
			valid = false;
			erro.append(context.getString(R.string.type)+" é obrigatório<br/>");
		}

		if(mComboVehicles == null && mComboVehicles.getSelectedItem() == null){
			valid = false;
			erro.append(context.getString(R.string.vehicle)+" é obrigatório<br/>");
		}

		if(mQuantity == null || mQuantity.getText().length() == 0){
			valid = false;
			erro.append(context.getString(R.string.quantity)+" é obrigatório\n");
		}

		//final int anoHoje = new Date().getYear()+1900;
		//Pattern patternYear = Pattern.compile("[1][9][5-9][0-9]");
		//Matcher matcherMake = patternYear.matcher(mYearMake.getText());      
		//!matcherMake.matches()

		if(mQuantity == null || mQuantity.getText().length() == 0){
			valid = false;
			erro.append(context.getString(R.string.quantity)+" é obrigatório\n");
		}

		if(mPrice == null || mPrice.getText().length() == 0){
			valid = false;
			erro.append(context.getString(R.string.price)+" é obrigatório\n");
		}

		if(mOdometer == null || mOdometer.getText().length() == 0){
			valid = false;
			erro.append(context.getString(R.string.odometer)+" é obrigatório\n");
		}

		if(!valid){
			Toast.makeText(context,erro.toString(),Toast.LENGTH_LONG).show();
		}

		return valid;
	}

	@Override
	public void onClick(View v) {
		if (v == mCancel){
			cancel();
		}
		if (v == mOk){
			if(validate()){
				
				maintenance = new Maintenance();
				maintenance.setType(mComboTypes.getSelectedItemPosition());
				
				Cursor c = (Cursor)this.mComboVehicles.getItemAtPosition(this.mComboVehicles.getSelectedItemPosition());
				Integer idV = c.getInt(c.getColumnIndex(Vehicle._ID));
				maintenance.setIdVehicle(idV);
				c.close();
				
				Uri uri = contentResolver.insert(maintenance.CONTENT_URI, maintenance.getContentValues());
				maintenance.setId(uri.getLastPathSegment());
				
				dismiss();
			
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		if(mCursorVehicles != null)
			mCursorVehicles.requery();

	}

	@Override
	protected void onStop() {
		super.onStop();
		
		mComboTypes.setSelection(0);
		mComboVehicles.setSelection(0);
		mQuantity.setText("");
		mPrice.setText("");
		mOdometer.setText("");
		
		if(mCursorVehicles != null) mCursorVehicles.deactivate();
	}

}

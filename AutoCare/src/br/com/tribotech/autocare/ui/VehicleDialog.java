package br.com.tribotech.autocare.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import br.com.tribotech.autocare.R;
import br.com.tribotech.autocare.converter.VehicleManufacturerCursorConverter;
import br.com.tribotech.autocare.converter.VehicleModelCursorConverter;
import br.com.tribotech.autocare.model.Maintenance;
import br.com.tribotech.autocare.model.Vehicle;
import br.com.tribotech.autocare.model.VehicleManufacturer;
import br.com.tribotech.autocare.model.VehicleModel;

public class VehicleDialog extends Dialog implements OnClickListener,TextWatcher {

	private Spinner mComboTipos;
	private AutoCompleteTextView mManufacturer;
	private AutoCompleteTextView mModel;
	private EditText mYearMake;
	private EditText mYearModel;
	private EditText mDescription;

	private Button mOk;
	private Button mCancel;
	
	private final Context context;
	private final ContentResolver contentResolver;
	
	private Vehicle vehicle;
	private Cursor mCursorManufacturers; 
	private Cursor mCursorManufacturer;
	private Cursor mCursorModels;

	public VehicleDialog(Context context) {
		super(context);

		this.setContentView(R.layout.form_vehicle);
		this.setTitle(R.string.vehicle);
		this.context = context;
		
		contentResolver = context.getContentResolver();

		final String[] arrayTipos = new String[3];
		arrayTipos[Vehicle.CAR] = context.getString(R.string.car);
		arrayTipos[Vehicle.MOTORCYCLE] = context.getString(R.string.bike);
		arrayTipos[Vehicle.TRUCK] = context.getString(R.string.truck);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.list_autocomplete_item, arrayTipos);
		mComboTipos = (Spinner) findViewById(R.id.combo_tipos);
		mComboTipos.setAdapter(adapter); 

		mCursorManufacturers = contentResolver.query(VehicleManufacturer.CONTENT_URI,new String[] {VehicleManufacturer._ID, VehicleManufacturer.COLUMN_MANUFACTURER} , null, null, null);
		
		final String[] mFrom = new String[] {VehicleManufacturer.COLUMN_MANUFACTURER};
		final int[] mTo = new int[]{R.id.list_item};
		SimpleCursorAdapter adapterManufactures = new SimpleCursorAdapter(context, R.layout.list_autocomplete_item, mCursorManufacturers, mFrom, mTo);
		adapterManufactures.setCursorToStringConverter(new VehicleManufacturerCursorConverter());
		adapterManufactures.setFilterQueryProvider(new FilterQueryProvider(){
			private Cursor aResult;
			@Override
			public Cursor runQuery(CharSequence constraint){
				StringBuilder buffer = null;
				String[] args = null;

				if (constraint != null){
					buffer = new StringBuilder();
					buffer.append(mFrom[0]);
					buffer.append(" like ?");
					String filter = constraint.toString() + "%";
					args = new String[] { filter };
				}

				aResult = contentResolver.query(VehicleManufacturer.CONTENT_URI,new String[] {VehicleManufacturer._ID, VehicleManufacturer.COLUMN_MANUFACTURER} ,buffer == null ? null : buffer.toString(), args, VehicleManufacturer.COLUMN_MANUFACTURER);
				return aResult;
			}

			@Override
			protected void finalize() throws Throwable {
				super.finalize();
				aResult.close();
			}
		} );


		mManufacturer = (AutoCompleteTextView) findViewById(R.id.autocomplete_manufacturer);
		mManufacturer.setAdapter(adapterManufactures);
		mManufacturer.addTextChangedListener(this);

		mModel = (AutoCompleteTextView) findViewById(R.id.autocomplete_model);

		mYearMake = (EditText) findViewById(R.id.year_make);
		mYearModel = (EditText) findViewById(R.id.year_model);
		mDescription = (EditText) findViewById(R.id.description);

		mOk = (Button) findViewById(R.id.OkButton);
		mOk.setOnClickListener(this);

		mCancel = (Button) findViewById(R.id.CancelButton);
		mCancel.setOnClickListener(this);

	}

	//1967 2009
	private boolean validate(){
		boolean valid = true;
		StringBuffer erro = new StringBuffer();

		if(mComboTipos == null && mComboTipos.getSelectedItem() == null){
			valid = false;
			erro.append(context.getString(R.string.type)+" é obrigatório<br/>");
		}

		if(mManufacturer == null || mManufacturer.getText().length() == 0){
			valid = false;
			erro.append(context.getString(R.string.manufacturer)+" é obrigatório\n");
		}

		if(mModel == null || mModel.getText().length() == 0){
			valid = false;
			erro.append(context.getString(R.string.model)+" é obrigatório\n");
		}

		final int anoHoje = new Date().getYear()+1900;
		//Pattern patternYear = Pattern.compile("[1][9][5-9][0-9]");
		//Matcher matcherMake = patternYear.matcher(mYearMake.getText());      
		//!matcherMake.matches()

		if(mYearMake == null || mYearMake.getText().length() == 0){
			valid = false;
			erro.append(context.getString(R.string.year_make)+" deve estar entre 1950 - "+anoHoje+"\n");
		}


		if(mYearModel == null || mYearModel.getText().length() == 0){
			valid = false;
			erro.append(context.getString(R.string.year_model)+" deve estar entre 1950 - "+anoHoje+"\n");
		}

		if(mDescription == null || mDescription.getText().length() == 0){
			valid = false;
			erro.append(context.getString(R.string.description)+" é obrigatório\n");
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
				
				vehicle = new Vehicle();
				vehicle.setType(mComboTipos.getSelectedItemPosition());

				Cursor cManufacturer = contentResolver.query(VehicleManufacturer.CONTENT_URI, 
						new String[] {VehicleManufacturer._ID}, 
						new String(VehicleManufacturer.COLUMN_MANUFACTURER + " like ?"), 
						new String[]{ mManufacturer.getEditableText().toString() }, null);
				if(cManufacturer != null && cManufacturer.getCount() != 0 && cManufacturer.moveToFirst()){
					final int columnIndex = cManufacturer.getColumnIndex(VehicleManufacturer._ID);
					vehicle.setIdManufacturer(cManufacturer.getInt(columnIndex));				
				}else{
					ContentValues values = new ContentValues();
					values.put(VehicleManufacturer.COLUMN_MANUFACTURER, mManufacturer.getEditableText().toString());
					Uri uri = contentResolver.insert(VehicleManufacturer.CONTENT_URI, values);
					vehicle.setIdManufacturer(uri.getLastPathSegment());
				}
				cManufacturer.close();

				Cursor cModel = contentResolver.query(VehicleModel.CONTENT_URI, 
						new String[] {VehicleModel._ID}, 
						new String(VehicleModel.COLUMN_MODEL+ " like ?"), 
						new String[]{ mModel.getEditableText().toString() }, null);
				if(cModel != null && cModel.getCount() != 0 && cModel.moveToFirst()){
					final int columnIndex = cModel.getColumnIndex(VehicleModel._ID);
					vehicle.setIdModel(cModel.getInt(columnIndex));
				}else{
					ContentValues values = new ContentValues();
					values.put(VehicleModel.COLUMN_ID_MANUFACTURER, vehicle.getIdManufacturer());
					values.put(VehicleModel.COLUMN_MODEL, mModel.getEditableText().toString());
					Uri uri = contentResolver.insert(VehicleModel.CONTENT_URI, values);
					vehicle.setIdModel(uri.getLastPathSegment());
				}
				cModel.close();

				vehicle.setYearMake(Integer.parseInt(mYearMake.getText().toString()));
				vehicle.setYearModel(Integer.parseInt(mYearModel.getText().toString()));
				vehicle.setDescription(mDescription.getText().toString());
								
				Uri uri = contentResolver.insert(Vehicle.CONTENT_URI, vehicle.getContentValues());
				vehicle.setId(uri.getLastPathSegment());
				
				insereManutencoesPadrao(vehicle);
				
				dismiss();
			
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		if(mCursorManufacturer != null) mCursorManufacturer.requery();
		if(mCursorManufacturers != null) mCursorManufacturers.requery();
		if(mCursorModels != null) mCursorModels.requery();
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		mComboTipos.setSelection(0);
		mManufacturer.setText("");
		mModel.setText("");
		mYearMake.setText("");
		mYearModel.setText("");
		mDescription.setText("");
		
		if(mCursorManufacturer != null) mCursorManufacturer.deactivate();
		if(mCursorManufacturers != null) mCursorManufacturers.deactivate();
		if(mCursorModels != null) mCursorModels.deactivate();
	}

	@Override
	public void afterTextChanged(Editable s) {

		final String[] projection = new String[] {VehicleManufacturer._ID, VehicleManufacturer.COLUMN_MANUFACTURER};
		final String selection = new String(VehicleManufacturer.COLUMN_MANUFACTURER + " like ?");
		final String[] selectionArgs = new String[]{ s.toString() };

		mCursorManufacturer = contentResolver.query(VehicleManufacturer.CONTENT_URI, projection, selection, selectionArgs, null);

		if(mCursorManufacturer != null && mCursorManufacturer.getCount() != 0 && mCursorManufacturer.moveToFirst()){

			final int columnIndex = mCursorManufacturer.getColumnIndex(VehicleManufacturer._ID);
			final int idManufacturer = mCursorManufacturer.getInt(columnIndex);

			final String[] mProjection = new String[] {VehicleModel._ID, VehicleModel.COLUMN_ID_MANUFACTURER, VehicleModel.COLUMN_MODEL};
			final String[] mFrom = new String[] {VehicleModel.COLUMN_MODEL};
			final int[] mTo = new int[]{R.id.list_item};

			final String sSelection = VehicleModel.COLUMN_ID_MANUFACTURER+" = ?";
			final String[] sArgs = new String[]{ Integer.toString(idManufacturer)};

			mCursorModels = contentResolver.query(VehicleModel.CONTENT_URI, mProjection, sSelection, sArgs, VehicleModel.COLUMN_MODEL);
			SimpleCursorAdapter adapterModels = new SimpleCursorAdapter(this.getContext(), R.layout.list_autocomplete_item, mCursorModels, mFrom, mTo);
			adapterModels.setCursorToStringConverter(new VehicleModelCursorConverter());
			adapterModels.setFilterQueryProvider(new FilterQueryProvider(){
				@Override
				public Cursor runQuery(CharSequence constraint){
					StringBuilder buffer = null;
					String[] args = null;

					if (constraint != null){
						buffer = new StringBuilder();
						buffer.append(VehicleModel.COLUMN_MODEL);
						buffer.append(" like ? and ");
						buffer.append(VehicleModel.COLUMN_ID_MANUFACTURER);
						buffer.append(" = ? ");
						String filter = constraint.toString() + "%";
						args = new String[] { filter, Integer.toString(idManufacturer) };
					}

					Cursor aResult = contentResolver.query(VehicleModel.CONTENT_URI, mProjection,buffer == null ? sSelection : buffer.toString(), buffer == null ? sArgs : args, VehicleModel.COLUMN_MODEL);
					return aResult;
				}
			} );
			mModel.setAdapter(adapterModels);
		}
		mCursorManufacturer.close();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	public void insereManutencoesPadrao(Vehicle vehicle){

		//Insere as manutenções padroes para cada veículo.
		InputStream in = null;
		InputStreamReader ir = null;
		BufferedReader reader = null;
		try {
			
			in = context.getAssets().open("standard_maintenances.csv");
			ir = new InputStreamReader(in);
			reader = new BufferedReader(ir);
			
			System.out.println("INSERINDO MANUTENCAO VEHICLE: "+vehicle.getId());
			
			String text = null;
			while ((text = reader.readLine()) != null){
				if(text.startsWith("//") || text.startsWith("#"))
					continue;
				
				String[] sp = text.split(";");

				int tipoVeiculo = Integer.parseInt(sp[0]);
				String tipoManutencao = sp[1];
				String descricao = sp[2];
				String periodoManutencao = sp[3];
				
				if(tipoVeiculo == vehicle.getType().intValue()){
					
					ContentValues values = new ContentValues();
					values.put(Maintenance.COLUMN_ID_VEHICLE, vehicle.getId());
					values.put(Maintenance.COLUMN_TYPE, tipoManutencao);
					values.put(Maintenance.COLUMN_DESCRIPTION, descricao);
					values.put(Maintenance.COLUMN_KM_PERIODIC, periodoManutencao);
					
					contentResolver.insert(Maintenance.CONTENT_URI, values);
				}

			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader != null)
					reader.close();
				if(ir != null)
					ir.close();
				if(in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

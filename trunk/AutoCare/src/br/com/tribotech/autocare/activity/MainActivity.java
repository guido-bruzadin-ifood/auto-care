package br.com.tribotech.autocare.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import br.com.tribotech.autocare.R;
import br.com.tribotech.autocare.charts.AverageFuelConsumptionChartView;
import br.com.tribotech.autocare.charts.BarChartExampleView;
import br.com.tribotech.autocare.model.Maintenance;
import br.com.tribotech.autocare.model.Vehicle;
import br.com.tribotech.autocare.ui.FuelFillDialog;
import br.com.tribotech.autocare.ui.VehicleDialog;
import br.com.tribotech.autocare.ui.adapter.MaintenanceCursorAdapter;
import br.com.tribotech.autocare.util.Preferences;

public class MainActivity extends Activity {

	private static final int DIALOG_VEHICLE_ID = 0;
	private static final int DIALOG_MAINTENANCE_ID = 1;
	private static final int DIALOG_FUELFILL_ID = 2;

	private ContentResolver contentResolver;

	private LinearLayout mVehiclesList; 
	private ViewFlipper mMainFlipper;
	private ListView mMaintenancesList;
	private float oldTouchValue;

	private SharedPreferences preferences;
	
	private Cursor cMaintenances;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		contentResolver = getContentResolver();

		criaAtualizaListaVeiculos();

		mMainFlipper = (ViewFlipper) findViewById(R.id.main_flipper);
		criaAtualizaListaManutencoes();

		//Grafico 1 - Consumo Médio por período (fixo ou não): Linha ou Área.
		//Grafico 2 - Preço do Combustível por período (fixo ou não): Barras, Linha ou Área.
		//Grafico 3 - Gasto Total (R$) por mês: Barras.
		AverageFuelConsumptionChartView g1 = new AverageFuelConsumptionChartView(this);
		g1.setMinimumWidth(200);
		g1.setMinimumHeight(100);

		BarChartExampleView g2 = new BarChartExampleView(this);
		g2.setMinimumWidth(200);
		g2.setMinimumHeight(100);

		LinearLayout painel = (LinearLayout)this.findViewById(R.id.painel);
		painel.addView(g1);
		painel.addView(g2);

	}
	
	private int getActiveVehicle(){
		if(preferences == null)
			preferences = getSharedPreferences(Preferences.APP,0);
		return preferences.getInt(Preferences.APP_ACTIVE_VEHICLE, 1);
	}
	
	private void setActiveVehicle(int id){
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(Preferences.APP_ACTIVE_VEHICLE, id);
		editor.commit();
	}

	private void criaAtualizaListaManutencoes() {

		mMaintenancesList = (ListView)this.findViewById(R.id.list_maintenance);

		Integer id = getActiveVehicle();
		String[] projection = new String[] {Maintenance._ID, Maintenance.COLUMN_DESCRIPTION, Maintenance.COLUMN_TYPE, Maintenance.COLUMN_KM_PERIODIC, Maintenance.COLUMN_KM_REALIZED, Maintenance.COLUMN_DATE_REALIZED};
		String selection = Maintenance.COLUMN_ID_VEHICLE+" = "+id;
		cMaintenances = contentResolver.query(Maintenance.CONTENT_URI, projection, selection, null, null);
		
		startManagingCursor(cMaintenances);
		
		mMaintenancesList.setAdapter(new MaintenanceCursorAdapter(this, cMaintenances));
		mMaintenancesList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.maintenance_context_menu, menu);
			}
		});
	}

	private void criaAtualizaListaVeiculos() {
		Cursor c = contentResolver.query(Vehicle.CONTENT_URI, new String[] {Vehicle._ID, Vehicle.COLUMN_DESCRIPTION, Vehicle.COLUMN_TYPE}, null, null, Vehicle._ID);

		//Verifica se existe algum veiculo cadastrado, se nao houver, deve cadastrar
		if(c != null && c.getCount() == 0){
			//cadastro do primeiro veiculo
			showDialog(DIALOG_VEHICLE_ID);
		}

		mVehiclesList = (LinearLayout)this.findViewById(R.id.list_vehicles);
		mVehiclesList.removeAllViews();

		if(c != null && c.moveToFirst()){
			int indexDescription = c.getColumnIndex(Vehicle.COLUMN_DESCRIPTION);
			int indexId = c.getColumnIndex(Vehicle._ID);
			do {
				String description = c.getString(indexDescription);
				final int id = c.getInt(indexId);

				TextView child = (TextView) getLayoutInflater().inflate(R.layout.list_vehicles_horizontal_item, null);
				child.setBackgroundColor(1);
				child.setText(description);
				child.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						setActiveVehicle(id);
						criaAtualizaListaManutencoes();
					}
				});

				mVehiclesList.addView(child);
			}while(c.moveToNext());
		}
		c.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add_vehicle:
			showDialog(DIALOG_VEHICLE_ID);
			return true;
		case R.id.del_vehicle:
			showVehicles();
			return true;
		case R.id.add_fuelfill:
			showDialog(DIALOG_FUELFILL_ID);
			return true;
		case R.id.quit:
			quit();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void newMaintenance() {
		Intent mainTab = new Intent(MainActivity.this, AddMaintenanceActivity.class);
		startActivity(mainTab);
	}

	private void showVehicles() {
		//Intent mainTab = new Intent(MainActivity.this, VehicleActivity.class);
		//startActivity(mainTab);
	}

	private void quit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.sureExit)
		.setCancelable(false)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				MainActivity.this.finish();
			}
		})
		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;

		switch(id) {
		case DIALOG_VEHICLE_ID:
			dialog = new VehicleDialog(this);
			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					criaAtualizaListaVeiculos();
					criaAtualizaListaManutencoes();
				}
			});

			break;
		case DIALOG_FUELFILL_ID:
			dialog = new FuelFillDialog(this);
			break;
		default:
			dialog = null;
		}
		return dialog;

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()){
		case MotionEvent.ACTION_DOWN:
			oldTouchValue = event.getX();
			break;

		case MotionEvent.ACTION_UP:
			//if(this.searchOk==false) 
			//	return false;
			float currentX = event.getX();
			if (oldTouchValue < currentX){
				mMainFlipper.setInAnimation(AnimationHelper.inFromLeftAnimation());
				mMainFlipper.setOutAnimation(AnimationHelper.outToRightAnimation());
				mMainFlipper.showNext();
			}
			if (oldTouchValue > currentX){
				mMainFlipper.setInAnimation(AnimationHelper.inFromRightAnimation());
				mMainFlipper.setOutAnimation(AnimationHelper.outToLeftAnimation());
				mMainFlipper.showPrevious();
			}

			break;
		}

		return false;
	}

	static class AnimationHelper {
		//for the previous movement
		public static Animation inFromRightAnimation() {

			Animation inFromRight = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
					Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
			);
			inFromRight.setDuration(350);
			inFromRight.setInterpolator(new AccelerateInterpolator());
			return inFromRight;
		}
		public static Animation outToLeftAnimation() {
			Animation outtoLeft = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
					Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
			);
			outtoLeft.setDuration(350);
			outtoLeft.setInterpolator(new AccelerateInterpolator());
			return outtoLeft;
		}    
		// for the next movement
		public static Animation inFromLeftAnimation() {
			Animation inFromLeft = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
					Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
			);
			inFromLeft.setDuration(350);
			inFromLeft.setInterpolator(new AccelerateInterpolator());
			return inFromLeft;
		}
		public static Animation outToRightAnimation() {
			Animation outtoRight = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
					Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
			);
			outtoRight.setDuration(350);
			outtoRight.setInterpolator(new AccelerateInterpolator());
			return outtoRight;
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.edit:
			//editNote(info.id);
			return true;
		case R.id.delete:
			deleteMaintenance(info);
			return true;
		case R.id.cancel:
			//deleteNote(info.id);
			return true;
		default:
			return super.onContextItemSelected(item);
		}

	}

	private void deleteMaintenance(AdapterContextMenuInfo info) {
		Uri url = Uri.parse(Maintenance.CONTENT_URI+"/"+info.id);
		int count = contentResolver.delete(url, null, null);
		criaAtualizaListaManutencoes();
		
	}

}
package br.com.tribotech.autocare.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.tribotech.autocare.R;
import br.com.tribotech.autocare.model.VehicleManufacturer;
import br.com.tribotech.autocare.model.VehicleModel;
import br.com.tribotech.autocare.util.Preferences;

public class SplashActivity extends Activity {
	
	private static final int PROGRESS = 0x1;

	private TextView mProgressText;
    private ProgressBar mProgressBar;
    private int mProgressStatus = 0;

    private Handler mHandler = new Handler();

	protected int tempo_padrao = 1000;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		
		mProgressBar = (ProgressBar)this.findViewById(R.id.progressBar);
		
		TextView progressText = (TextView)this.findViewById(R.id.progressText);
		progressText.setText(R.string.initializing);
				
		//Thread responsável por carregar informações como preferências e dados do BD
		Thread splashThread = new Thread(){
			public void run() {
				try {
					//Contagem do tempo para carregamento
					long tempo_inicial = System.currentTimeMillis();

					//Realiza todas as operaçoes para inicio da aplicação.
					SharedPreferences preferences = getSharedPreferences(Preferences.APP,0);
					if(preferences.getBoolean(Preferences.APP_FIRST_USE, true)){
						inicializaDadosBD();

						SharedPreferences.Editor editor = preferences.edit();
						editor.putBoolean(Preferences.APP_FIRST_USE, false);

						// Commit the edits!
						editor.commit();
					}

					long tempo_espera = System.currentTimeMillis() - tempo_inicial;
					while(tempo_espera < tempo_padrao) {
						sleep(100);
						tempo_espera += 100;
					}
				} catch(InterruptedException e) {
					// do nothing
				} finally {
					finish();
					Intent mainTab = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(mainTab);
					//stop();
				}
			}

		};

		splashThread.start();
	}

	private void inicializaDadosBD() {
		//Insere os carros e marcas
		//Le do XML os veiculos e marcas
		InputStream in = null;
		InputStreamReader ir = null;
		BufferedReader reader = null;
		try {
			in = getApplication().getAssets().open("vehicle_manufactures.csv");
			ir = new InputStreamReader(in);
			reader = new BufferedReader(ir);

			ContentResolver cr = getContentResolver();

			mProgressText.setText(R.string.insertingMake);
			
			HashMap<String, Uri> manufacturers = new HashMap<String, Uri>();
			String text = null;
			while ((text = reader.readLine()) != null){
				if(text.startsWith("//") || text.startsWith("#"))
					continue;
				
				String[] sp = text.split(";");
				String codigoMarca = sp[0];
				
				ContentValues values = new ContentValues();
				values.put(VehicleManufacturer.COLUMN_MANUFACTURER, sp[1]);

				Uri m = cr.insert(VehicleManufacturer.CONTENT_URI, values);
				manufacturers.put(codigoMarca, m);
				
				 // Update the progress bar
                mHandler.post(new Runnable() {
                    public void run() {
                        mProgressBar.setProgress(mProgressStatus);
                    }
                });

			}

			in = getApplication().getAssets().open("vehicle_models.csv");
			ir = new InputStreamReader(in);
			reader = new BufferedReader(ir);
			
			mProgressText.setText(R.string.insertingModel);
			//mProgressBar.setMax(max)
			
			while ((text = reader.readLine()) != null){
				if(text.startsWith("//") || text.startsWith("#"))
					continue;
				
				String[] sp = text.split(";");

				Uri manufacturer = manufacturers.get(sp[0]);
				
				ContentValues values = new ContentValues();
				values.put(VehicleModel.COLUMN_ID_MANUFACTURER, manufacturer.getLastPathSegment());
				values.put(VehicleModel.COLUMN_MODEL, sp[1]);

				cr.insert(VehicleModel.CONTENT_URI, values);
				 // Update the progress bar
                mHandler.post(new Runnable() {
                    public void run() {
                        mProgressBar.setProgress(mProgressStatus);
                    }
                });

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

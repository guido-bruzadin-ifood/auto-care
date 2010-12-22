package br.com.tribotech.autocare.activity;

import android.app.Activity;
import android.os.Bundle;
import br.com.tribotech.autocare.R;

public class AddMaintenanceActivity extends Activity {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_maintenance);
	}

}

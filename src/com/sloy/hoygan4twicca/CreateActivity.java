package com.sloy.hoygan4twicca;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateActivity extends Activity {

	private InputMethodManager inputMethodManager;
	private ClipboardManager clipboard;
	private AsyncTask<String, Void, String> miHoyganaiser;
	private View progress;
	private EditText txtEsp, txtHoygan;
	private Button btTraducir, btLimpiar, btCopiar, btCompartir, btCanselar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create);

		progress = findViewById(R.id.create_layout_progress);
		txtEsp = (EditText)findViewById(R.id.create_edit_esp);
		txtHoygan = (EditText)findViewById(R.id.create_edit_hoygan);
		btTraducir = (Button)findViewById(R.id.create_bt_traducir);
		btLimpiar = (Button)findViewById(R.id.create_bt_limpiar);
		btCopiar = (Button)findViewById(R.id.create_bt_copiar);
		btCompartir = (Button)findViewById(R.id.create_bt_compartir);
		btCanselar = (Button)findViewById(R.id.create_bt_canselar);

		btTraducir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				traducir();
			}
		});
		btLimpiar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				limpiar();
			}
		});
		btCopiar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				copiar();
			}
		});
		btCompartir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				compartir();
			}
		});
		btCanselar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				canselar();
			}
		});
	}

	private void traducir() {
		// Quita el puto teclado
		if(inputMethodManager == null){
			inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		}
		inputMethodManager.hideSoftInputFromWindow(txtEsp.getWindowToken(), 0);
		//mira si hay internet
		if(!isNetworkAvailable(this)){
			Toast.makeText(this, "Es necesaria una conexión a Internet", Toast.LENGTH_SHORT).show();
		}
		// traduse
		miHoyganaiser = new CreateHoyganaiser().execute(txtEsp.getText().toString());
		progress.setVisibility(View.VISIBLE);
		btTraducir.setEnabled(false);

	}

	private void limpiar() {
		txtEsp.setText("");
		txtHoygan.setText("");
	}

	private void copiar() {
		if(clipboard == null){
			clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		}
		clipboard.setText(txtHoygan.getText().toString());
		Toast.makeText(this, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show();
	}

	private void compartir() {
		Intent i = new Intent(android.content.Intent.ACTION_SEND);
		i.putExtra(Intent.EXTRA_TEXT, txtHoygan.getText().toString());
		i.setType("text/plain");
		startActivity(Intent.createChooser(i, "KOMPARTIR KON..."));
	}

	private void canselar() {
		if(miHoyganaiser != null){
			miHoyganaiser.cancel(true);
		}
	}

	public static boolean isNetworkAvailable(Context ctx) {
		ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private class CreateHoyganaiser extends Hoyganaiser {
		@Override
		protected void onPostExecute(String result) {
			txtHoygan.setText(result);
			progress.setVisibility(View.INVISIBLE);
			btTraducir.setEnabled(true);
		}

		@Override
		protected void onCancelled() {
			progress.setVisibility(View.INVISIBLE);
			btTraducir.setEnabled(true);
		}
	}

}

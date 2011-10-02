package com.sloy.hoygan4twicca;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TransformActivity extends Activity {

	private Intent mIntent;
	private String mTweet;
	private Hoyganaiser miHoyganaiser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* Instancia el hoyganiser */
		miHoyganaiser = new Hoyganaiser() {
			@Override
			protected void onPostExecute(String result) {
				/* Y ya lo devuelve */
				mTweet = result;
				/* Pone el hashtag */
				mTweet += " #HOYGANMODE";
				mIntent.putExtra(Intent.EXTRA_TEXT, mTweet);
				if(mIntent.getAction().equals("jp.r246.twicca.ACTION_EDIT_TWEET")){
					// Es para twicca
					setResult(RESULT_OK, mIntent);
				}else{
					// Compartir normalmente
					Intent i = new Intent(android.content.Intent.ACTION_SEND);
					i.putExtra(Intent.EXTRA_TEXT, mTweet);
					i.setType("text/plain");
					startActivity(Intent.createChooser(i, "KOMPARTIR KON..."));
				}
				finish();
			}

			@Override
			protected void onCancelled() {
				setResult(RESULT_CANCELED);
			}
		};

		mIntent = getIntent();
		mTweet = mIntent.getStringExtra(Intent.EXTRA_TEXT);

		if(mTweet == null){
			finish();
		}else{
			setContentView(R.layout.main);
			Button btCanselar = (Button)findViewById(R.id.bt_canselar);
			btCanselar.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(miHoyganaiser != null){
						miHoyganaiser.cancel(true);
					}
					setResult(RESULT_CANCELED);
					finish();
				}
			});
			miHoyganaiser.execute(mTweet);
		}
	}

}
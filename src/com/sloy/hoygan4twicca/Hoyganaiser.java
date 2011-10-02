package com.sloy.hoygan4twicca;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public abstract class Hoyganaiser extends AsyncTask<String, Void, String> {

	private static final String KEY = "<div id=\"tranks\">";

	@Override
	protected String doInBackground(String... params) {
		try{
			String mTweet = params[0];
			/* Guarda las menciones, enlaces y hashtags */
			boolean quitarHoygan = false; // TODO opciones
			if(mTweet.startsWith("@")){
				quitarHoygan = true;
			}
			mTweet = mTweet.toLowerCase();
			int start = -1;
			int end = -1;
			int cont = 0;
			List<String> guardar = new ArrayList<String>();
			String searchPattern = "@"; // empieza por las menciones
			while(true){
				start = mTweet.indexOf(searchPattern, end);
				if(start < 0){
					if(searchPattern.equals("@")){
						searchPattern = "#";
					}else if(searchPattern.equals("#")){
						searchPattern = "https://";
					}else if(searchPattern.equals("https://")){
						searchPattern = "http://";
					}else{
						break;
					}
					start = -1;
					end = -1;
					continue;
				}
				end = mTweet.indexOf(" ", start);
				if(end < 0){ // Hasta el final
					end = mTweet.length();
				}
				String mention = (end > 0) ? mTweet.substring(start, end) : mTweet.substring(start);
				guardar.add(mention);
				mTweet = mTweet.replace(mention, "%" + cont);
				cont++;
			}

			/* Obtiene toda la polla esa */
			mTweet = mTweet.replace("?", "").replace("¿", "").replace("!", "").replace("¡", "").replace(",", "").replace(".", "");
			String tweetFormated = URLEncoder.encode(mTweet);
			URL url = new URL("http://chevismo.com/hoygan?txt=" + tweetFormated + "&trans=TRADUSIR");
			HttpURLConnection c = (HttpURLConnection)url.openConnection();
			c.setRequestMethod("GET");
			c.setReadTimeout(15 * 1000);
			c.setUseCaches(false);
			c.connect();
			// read the output from the server
			BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				stringBuilder.append(line + "\n");
			}
			String fuck = stringBuilder.toString();
			/* Coge la parte importante */
			int index = fuck.indexOf(KEY);
			if(index < 0){
				throw new Exception("HERROR AL TRADUSIR: No se encontró la traducción en el resultado");
			}
			start = index + KEY.length();
			end = fuck.indexOf("</div>");
			String hoygan = fuck.substring(start, end);
			if(quitarHoygan){
				hoygan = hoygan.replaceFirst("HOYGAN ", "");
			}
			mTweet = hoygan;

			/* Restauramos las menciones */
			for(int i = 0; i < guardar.size(); i++){
				if(guardar.get(i).startsWith("http")){
					mTweet = mTweet.replace("%" + i, guardar.get(i).toLowerCase());
				}else{
					mTweet = mTweet.replace("%" + i, guardar.get(i).toUpperCase());
				}
			}
			return mTweet;
		}catch(Exception e){
			return null;
		}
	}

	@Override
	protected abstract void onPostExecute(String result);

	@Override
	protected abstract void onCancelled();

}
package com.el1t.iolite.utils;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by El1t on 8/2/15.
 */
public abstract class AbstractRequestActivity extends AppCompatActivity {
	protected abstract String getAuthKey();
	protected abstract View getContainer();

	public abstract class IonRequest<T> extends AsyncTask<Void, Integer, T> {
		private String TAG = "IonRequest";

		/**
		 * Get the URL to open a connection to
		 * @return A valid URL, preferably from {@link com.el1t.iolite.utils.Utils.API}
		 */
		protected abstract String getURL();

		@Override
		protected T doInBackground(Void... params) {
			HttpsURLConnection urlConnection = null;
			try {
				urlConnection = (HttpsURLConnection) new URL(getURL()).openConnection();
				// Add authKey to header
				urlConnection.setRequestProperty("Authorization", getAuthKey());
				urlConnection.setUseCaches(false);
				// Call wrapped method
				final T result = doInBackground(urlConnection);
				urlConnection.disconnect();
				return result;
			} catch (FileNotFoundException e) {
				try {
					if (urlConnection != null && urlConnection.getResponseCode() == 401) {
						Snackbar.make(getContainer(), "Not logged in", Snackbar.LENGTH_SHORT).show();
					}
				} catch (IOException err) {
					Log.e(TAG, "Cannot read response code", err);
				}
			} catch (IOException e) {
				Snackbar.make(getContainer(), "Cannot connect to server", Snackbar.LENGTH_LONG)
						.setAction("Retry", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO: create new instance of implemented class and call execute()
							}
						}).show();
			} catch (JSONException | ParseException e) {
				Log.e(TAG, "Parse error", e);
			} catch (Exception e) {
				Log.e(TAG, "Unexpected error", e);
			}
			return null;
		}

		/**
		 * A wrapped version of {@link #doInBackground(Void...)} that will handle specific exceptions
		 * @param urlConnection A connection to the URL specified by {@link #getURL()}. It is not connected
		 *                      initially, but disconnection is handled automatically.
		 * @return T Result of operation
		 * @throws Exception IOException, JSONException, and ParseException are expected
		 */
		protected abstract T doInBackground(HttpsURLConnection urlConnection) throws Exception;

		@Override
		protected void onProgressUpdate(Integer... progress) {
			onProgressUpdate(progress[0]);
		}

		protected void onProgressUpdate(int i) { }
	}
}
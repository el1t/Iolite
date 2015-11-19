package com.el1t.iolite.util;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.el1t.iolite.R;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by El1t on 8/2/15.
 */
public class RequestActivity extends AppCompatActivity {
	protected String mAuthKey;
	protected int mContainerId = R.id.container;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Retrieve key from preferences
		final SharedPreferences preferences = getSharedPreferences(Utils.PREFS_NAME, MODE_PRIVATE);
		mAuthKey = Utils.getAuthKey(preferences);
	}

	private View getContainer() {
		return findViewById(mContainerId);
	}

	public abstract class IonRequest<T> extends AsyncTask<Void, Integer, T> {
		private String TAG = "IonRequest";

		/**
		 * Get the URL to open a connection to
		 *
		 * @return A valid URL, preferably from {@link com.el1t.iolite.util.Utils.API}
		 */
		protected abstract String getURL();

		protected abstract IonRequest getNewInstance();

		@Override
		protected T doInBackground(Void... params) {
			HttpsURLConnection urlConnection = null;
			try {
				urlConnection = (HttpsURLConnection) new URL(getURL()).openConnection();
				// Add authKey to header
				urlConnection.setRequestProperty("Authorization", mAuthKey);
				urlConnection.setUseCaches(false);
				// Call wrapped method
				final T result = doInBackground(urlConnection);
				urlConnection.disconnect();
				return result;
			} catch (FileNotFoundException e) {
				if (urlConnection == null) {
					Log.e(TAG, "URLConnection is null", e);
				} else {
					try {
						final int responseCode = urlConnection.getResponseCode();
						if (responseCode == 401) {
							Snackbar.make(getContainer(), "Not logged in", Snackbar.LENGTH_SHORT).show();
						} else {
							Snackbar.make(getContainer(), "Server error (" + responseCode + ")", Snackbar.LENGTH_SHORT).show();
							Log.e(TAG, "Server response " + responseCode, e);
						}
					} catch (IOException err) {
						Log.e(TAG, "Cannot read response code", err);
					}
				}
			} catch (IOException e) {
				Snackbar.make(getContainer(), "Cannot connect to server", Snackbar.LENGTH_LONG)
						.setAction("Retry", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								getNewInstance().execute();
								// TODO: create new instance of implemented class and call execute()
							}
						}).show();
			} catch (JSONException | ParseException e) {
				Snackbar.make(getContainer(), "Something went wrong...", Snackbar.LENGTH_SHORT).show();
				Log.e(TAG, "Parse error", e);
			} catch (Exception e) {
				Snackbar.make(getContainer(), "Something went wrong...", Snackbar.LENGTH_SHORT).show();
				Log.e(TAG, "Unexpected error", e);
			}
			return null;
		}

		/**
		 * A wrapped version of {@link #doInBackground(Void...)} that will handle specific exceptions
		 *
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

		protected void onProgressUpdate(int i) {
		}
	}
}
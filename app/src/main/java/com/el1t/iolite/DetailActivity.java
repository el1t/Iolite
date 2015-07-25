package com.el1t.iolite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.el1t.iolite.item.EighthActivityItem;
import com.el1t.iolite.parser.DetailJsonParser;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by El1t on 7/22/15.
 */
public class DetailActivity extends AppCompatActivity implements DetailFragment.OnFragmentInteractionListener
{
	private final static String TAG = "Detail Activity";
	private DetailFragment mDetailFragment;
	private String mAuthKey;
	private EighthActivityItem mEighthActivity;
	private boolean fake;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// Check if restoring from previously destroyed instance
		if (savedInstanceState == null) {
			final Intent intent = getIntent();
			mEighthActivity = intent.getParcelableExtra("activity");
			if (mEighthActivity == null) {
				Log.e(TAG, "Missing EighthActivityItem", new IllegalArgumentException());
			}
			setTitle(mEighthActivity.getName());
			mDetailFragment = DetailFragment.newInstance(mEighthActivity);
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mDetailFragment)
					.commit();

			// Check if fake information should be used
			if ((fake = intent.getBooleanExtra("fake", false))) {
				Log.d(TAG, "Loading test details");
				// Pretend fake list was received
				try {
					DetailJsonParser.parse(getAssets().open("testActivityDetail.json"));
				} catch (IOException | JSONException | ParseException e) {
					Log.e(TAG, "Error loading test activity details", e);
				}
			}
		} else {
			fake = savedInstanceState.getBoolean("fake");
			mDetailFragment = (DetailFragment) getFragmentManager().getFragment(savedInstanceState, "fragment");
		}

		// Use material design toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (!fake) {
			// Retrieve authKey from shared preferences
			final SharedPreferences preferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
			mAuthKey = Utils.getAuthKey(preferences);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable("fake", fake);
		getFragmentManager().putFragment(savedInstanceState, "detailFragment", mDetailFragment);
	}

	// When back button in actionbar is activated
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}

	public void refresh() {
		new DetailRequest(mEighthActivity.getAID()).execute();
	}

	// Get list of blocks
	private class DetailRequest extends AsyncTask<Void, Void, Boolean> {
		private static final String TAG = "Detail Connection";
		private static final String URL = "https://ion.tjhsst.edu/api/activities/";
		private int AID;

		public DetailRequest(int AID) {
			this.AID = AID;
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			HttpsURLConnection urlConnection;
			try {
				urlConnection = (HttpsURLConnection) new URL(URL + AID).openConnection();
				// Add authKey to header
				urlConnection.setRequestProperty("Authorization", mAuthKey);
				// Begin connection
				urlConnection.connect();
				// Parse JSON from server
				DetailJsonParser.parse(urlConnection.getInputStream(), mDetailFragment.getDetails());
				// Close connection
				urlConnection.disconnect();
				return true;
			} catch (IOException e) {
				Log.e(TAG, "IO Error", e);
			} catch (Exception e) {
				Log.e(TAG, "Connection Error", e);
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}
}
package com.el1t.iolite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.el1t.iolite.item.EighthActivity;
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
	private int AID;
	private boolean fake;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		// Check if restoring from previously destroyed instance
		if (savedInstanceState == null) {
			final Intent intent = getIntent();
			final EighthActivity activityItem = intent.getParcelableExtra("activity");
			if (activityItem == null) {
				Log.e(TAG, "Missing EighthActivity", new IllegalArgumentException());
			} else {
				AID = activityItem.getAID();
				((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar)).setTitle(activityItem.getName());
				mDetailFragment = DetailFragment.newInstance(activityItem);
				getFragmentManager().beginTransaction()
						.replace(R.id.container, mDetailFragment)
						.commit();
			}

			// Check if fake information should be used
			fake = intent.getBooleanExtra("fake", false);
		} else {
			AID = savedInstanceState.getInt("AID");
			fake = savedInstanceState.getBoolean("fake");
			mDetailFragment = (DetailFragment) getFragmentManager().getFragment(savedInstanceState, "fragment");
		}

		// Bind FAB click
		findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(findViewById(R.id.container), "Noticed", Snackbar.LENGTH_SHORT).show();
			}
		});

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
		if (fake) {
			Log.d(TAG, "Loading test details");
			// Pretend fake list was received
			try {
				mDetailFragment.update(DetailJsonParser.parse(getAssets().open("testActivityDetail.json"),
						mDetailFragment.getEighth()));
			} catch (IOException | JSONException | ParseException e) {
				Log.e(TAG, "Error loading test activity details", e);
			}
		} else {
			new DetailRequest(AID).execute();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("AID", AID);
		savedInstanceState.putSerializable("fake", fake);
		getFragmentManager().putFragment(savedInstanceState, "detailFragment", mDetailFragment);
	}

	// When back button in actionbar is activated
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}

	// Get list of blocks
	private class DetailRequest extends AsyncTask<Void, Void, EighthActivity> {
		private static final String TAG = "Detail Connection";
		private static final String URL = "https://ion.tjhsst.edu/api/activities/";
		private int AID;

		public DetailRequest(int AID) {
			this.AID = AID;
		}

		@Override
		protected EighthActivity doInBackground(Void... params) {

			HttpsURLConnection urlConnection;
			try {
				urlConnection = (HttpsURLConnection) new URL(URL + AID).openConnection();
				// Add authKey to header
				urlConnection.setRequestProperty("Authorization", mAuthKey);
				// Begin connection
				urlConnection.connect();
				// Parse JSON from server
				final EighthActivity details = DetailJsonParser.parse(urlConnection.getInputStream(),
						mDetailFragment.getEighth());
				// Close connection
				urlConnection.disconnect();
				return details;
			} catch (IOException e) {
				Log.e(TAG, "IO Error", e);
			} catch (Exception e) {
				Log.e(TAG, "Connection Error", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(EighthActivity result) {
			super.onPostExecute(result);
			if (result != null) {
				mDetailFragment.update(result);
			}
		}
	}
}
package com.el1t.iolite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.el1t.iolite.item.EighthActivity;
import com.el1t.iolite.parser.DetailJsonParser;
import com.el1t.iolite.utils.AbstractRequestActivity;
import com.el1t.iolite.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by El1t on 7/22/15.
 */
public class DetailActivity extends AbstractRequestActivity implements DetailFragment.OnFragmentInteractionListener {
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

	@Override
	protected View getContainer() {
		return findViewById(R.id.container);
	}

	@Override
	protected String getAuthKey() {
		return mAuthKey;
	}

	private class DetailRequest extends IonRequest<EighthActivity> {
		private int AID;

		public DetailRequest(int AID) {
			this.AID = AID;
		}

		@Override
		protected String getURL() {
			return Utils.API.activity(AID);
		}

		@Override
		protected EighthActivity doInBackground(HttpsURLConnection urlConnection) throws Exception {
			urlConnection.connect();
			return DetailJsonParser.parse(urlConnection.getInputStream(), mDetailFragment.getEighth());
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
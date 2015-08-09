package com.el1t.iolite;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.el1t.iolite.item.EighthActivity;
import com.el1t.iolite.parser.DetailJsonParser;
import com.el1t.iolite.utils.RequestActivity;
import com.el1t.iolite.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by El1t on 7/22/15.
 */
public class DetailActivity extends RequestActivity implements DetailFragment.OnFragmentInteractionListener {
	private final static String TAG = "Detail Activity";
	private final static String ARG_AID = "aid";
	private final static String ARG_FAKE = "fake";
	private final static String ARG_FRAGMENT = "fragment";
	private final static String ARG_EIGHTH_ACTIVITY = "activity";
	private DetailFragment mDetailFragment;
	private EighthActivity mEighthActivity;
	private int AID;
	private boolean fake;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		// Check if restoring from previously destroyed instance
		if (savedInstanceState == null) {
			final Intent intent = getIntent();
			mEighthActivity = intent.getParcelableExtra(ARG_EIGHTH_ACTIVITY);
			if (mEighthActivity == null) {
				Log.e(TAG, "Missing EighthActivity", new IllegalArgumentException());
			} else {
				AID = mEighthActivity.getAID();
				((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar)).setTitle(mEighthActivity.getName());
				mDetailFragment = DetailFragment.newInstance(mEighthActivity);
				getFragmentManager().beginTransaction()
						.replace(R.id.container, mDetailFragment)
						.commit();
			}

			// Check if fake information should be used
			fake = intent.getBooleanExtra(ARG_FAKE, false);
		} else {
			AID = savedInstanceState.getInt(ARG_AID);
			fake = savedInstanceState.getBoolean(ARG_FAKE);
			mDetailFragment = (DetailFragment) getFragmentManager().getFragment(savedInstanceState, ARG_FRAGMENT);
			mEighthActivity = savedInstanceState.getParcelable(ARG_EIGHTH_ACTIVITY);
		}

		// Bind FAB click
		findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(findViewById(R.id.container), "Sorry, not implemented yet", Snackbar.LENGTH_SHORT).show();
				// TODO: favorite();
			}
		});

		// Use material design toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
		savedInstanceState.putInt(ARG_AID, AID);
		savedInstanceState.putBoolean(ARG_FAKE, fake);
		getFragmentManager().putFragment(savedInstanceState, ARG_FRAGMENT, mDetailFragment);
		savedInstanceState.putParcelable(ARG_EIGHTH_ACTIVITY, mEighthActivity);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Favorite an activity
	public void favorite() {
		// Note: the server uses the UID field as the AID in its API
		new FavoriteRequest().execute();
		final String message;
		if (mEighthActivity.changeFavorite()) {
			message = "Favorited";
		} else {
			message = "Unfavorited";
		}
		Snackbar.make(findViewById(R.id.container), message, Snackbar.LENGTH_SHORT)
				.setAction("Undo", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						new FavoriteRequest().execute();
						mEighthActivity.changeFavorite();
					}
				}).show();
		Log.d(TAG, message + " AID " + AID);
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

	private class FavoriteRequest extends IonRequest<Boolean> {
		@Override
		protected String getURL() {
			return "https://iodine.tjhsst.edu/eighth/vcp_schedule/favorite/uid/" + AID
					+ "/bids/1234"; // TODO: switch to ion
		}

		@Override
		protected Boolean doInBackground(HttpsURLConnection urlConnection) throws Exception {
			urlConnection.connect();
			urlConnection.getInputStream();
			return true;
		}
	}
}
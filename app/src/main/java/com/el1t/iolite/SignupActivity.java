package com.el1t.iolite;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.el1t.iolite.item.EighthActivity;
import com.el1t.iolite.parser.EighthActivityJsonParser;
import com.el1t.iolite.utils.AbstractRequestActivity;
import com.el1t.iolite.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupActivity extends AbstractRequestActivity implements SignupFragment.OnFragmentInteractionListener
{
	private static final String TAG = "Signup Activity";

	private SignupFragment mSignupFragment;
	private int BID;
	private String mAuthKey;
	private boolean fake;
	private ArrayList<AsyncTask> mTasks;

	public enum Response {
		SUCCESS, CAPACITY, RESTRICTED, CANCELLED, PRESIGN, ATTENDANCE_TAKEN, FAIL
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		final Intent intent = getIntent();
		BID = intent.getIntExtra("BID", -1);
		mTasks = new ArrayList<>();

		// Check if restoring from previously destroyed instance that matches the BID
		if (savedInstanceState == null) {
			fake = intent.getBooleanExtra("fake", false);
		} else {
			fake = savedInstanceState.getBoolean("fake");
			if (BID == savedInstanceState.getInt("BID")) {
				mSignupFragment = (SignupFragment) getFragmentManager().getFragment(savedInstanceState, "fragment");
			}
		}

		// Retrieve authKey from shared preferences
		if (!fake) {
			final SharedPreferences preferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
			mAuthKey = Utils.getAuthKey(preferences);
		}

		// Use material design toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Load list of blocks from web
		refresh();
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("BID", BID);
		savedInstanceState.putBoolean("fake", fake);
		getFragmentManager().putFragment(savedInstanceState, "fragment", mSignupFragment);
	}

	@Override
	public void onDestroy() {
		// Try to cancel tasks when destroying
		for (AsyncTask a : mTasks) {
			a.cancel(true);
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.eighth_signup, menu);
		final SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
		if (searchView != null) {
			searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
			searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextChange(String query) {
					if (mSignupFragment != null) {
						mSignupFragment.filter(query);
					}
					return true;
				}

				@Override
				public boolean onQueryTextSubmit(String query) {
					return true;
				}
			});
		}
		return super.onCreateOptionsMenu(menu);
	}

	public void refresh() {
		if (!fake) {
			// Set loading fragment, if necessary
			if (mSignupFragment == null) {
				// Set loading fragment
				getFragmentManager().beginTransaction()
						.add(R.id.container, new LoadingFragment())
						.commit();
			}

			// Retrieve list for bid using cookies
			mTasks.add(new ActivityListRequest(BID).execute());
		} else {
			// Reload offline list
			postRequest(getList());
		}
	}

	// Try signing up for an activity
	public void submit(EighthActivity item) {
		// Perform checks before submission
		// Note that server performs checks as well
		 if (item.isCancelled()) {
			showSnackbar(Response.CANCELLED);
		} else if (item.isFull()) {
			showSnackbar(Response.CAPACITY);
		} else if (item.isRestricted()) {
			showSnackbar(Response.RESTRICTED);
		} else {
			mTasks.add(new SignupRequest(item.getAID(), item.getBID()).execute());
		}
	}

	// Display details for activity
	public void viewDetails(EighthActivity activityItem) {
		final Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra("activity", activityItem);
		intent.putExtra("fake", fake);
		startActivity(intent);
	}

	// Notify the user of server response
	void showSnackbar(Response result) {
		switch(result) {
			case SUCCESS:
				// TODO: Pass success to home activity
				Log.d(TAG, "Sign up success");
				finish();
				break;
			case CAPACITY:
				Snackbar.make(findViewById(R.id.container), "Capacity exceeded", Snackbar.LENGTH_SHORT).show();
				Log.d(TAG, "Capacity exceeded");
				break;
			case RESTRICTED:
				Snackbar.make(findViewById(R.id.container), "Activity restricted", Snackbar.LENGTH_SHORT).show();
				Log.d(TAG, "Restricted activity");
				break;
			case CANCELLED:
				Snackbar.make(findViewById(R.id.container), "Activity cancelled", Snackbar.LENGTH_SHORT).show();
				Log.d(TAG, "Cancelled activity");
				break;
			case PRESIGN:
				Snackbar.make(findViewById(R.id.container), "Activity signup not open yet", Snackbar.LENGTH_SHORT).show();
				Log.d(TAG, "Presign activity");
				break;
			case ATTENDANCE_TAKEN:
				Snackbar.make(findViewById(R.id.container), "Signup closed", Snackbar.LENGTH_SHORT).show();
				Log.d(TAG, "Attendance taken");
				break;
			case FAIL:
				// TODO: Make this error message reflect the actual error
				Snackbar.make(findViewById(R.id.container), "Fatal error", Snackbar.LENGTH_SHORT).show();
				Log.w(TAG, "Sign up failure");
				break;
		}
	}

	// Do after getting list of activities
	private void postRequest(EighthActivity[] result) {
		if (mSignupFragment == null) {
			// Create the content view
			mSignupFragment = SignupFragment.newInstance(result);
			// Switch to BlockFragment view, remove LoadingFragment
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mSignupFragment)
					.commit();
		} else {
			mSignupFragment.updateAdapter(result);
		}
	}

	// Favorite an activity
	public void favorite(final int AID, final int BID, final EighthActivity item) {
		// Note: the server uses the UID field as the AID in its API
		// Sending the BID is useless, but it is required by the server
		mTasks.add(new ServerRequest("eighth/vcp_schedule/favorite/uid/" + AID + "/bids/" + BID).execute());
		if (item.changeFavorite()) {
			Snackbar.make(findViewById(R.id.container), "Favorited", Snackbar.LENGTH_SHORT)
					.setAction("Undo", new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							mTasks.add(new ServerRequest("eighth/vcp_schedule/favorite/uid/" + AID + "/bids/" + BID)
									.execute());
							item.changeFavorite();
							mSignupFragment.updateAdapter();
						}
					}).show();
		} else {
			Snackbar.make(findViewById(R.id.container), "Unfavorited", Snackbar.LENGTH_SHORT)
					.setAction("Undo", new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							mTasks.add(new ServerRequest("eighth/vcp_schedule/favorite/uid/" + AID + "/bids/" + BID)
									.execute());
							item.changeFavorite();
							mSignupFragment.updateAdapter();
						}
					}).show();
		}
		Log.d(TAG, "Favorited AID " + AID);
	}

	// Get a fake list of activities for debugging
	private EighthActivity[] getList() {
		try {
			return EighthActivityJsonParser.parseAll(getAssets().open("testActivityList.json"));
		} catch (Exception e) {
			Log.e(TAG, "Error parsing activity xml", e);
		}
		return null;
	}

	protected String getAuthKey() {
		return mAuthKey;
	}

	protected View getContainer() {
		return findViewById(R.id.container);
	}

	// Retrieve activity list for BID from server
	private class ActivityListRequest extends IonRequest<EighthActivity[]> {
		private static final String TAG = "ActivityListRequest";
		private int BID;

		public ActivityListRequest(int BID) {
			this.BID = BID;
		}

		@Override
		protected String getURL() {
			return Utils.API.block(BID);
		}

		@Override
		protected EighthActivity[] doInBackground(HttpsURLConnection urlConnection) throws Exception {
			urlConnection.connect();
			// Parse JSON from server
			return EighthActivityJsonParser.parseAll(urlConnection.getInputStream());
		}

		@Override
		protected void onPostExecute(EighthActivity[] result) {
			super.onPostExecute(result);
			mTasks.remove(this);
			// Add ArrayList to the ListView in SignupFragment
			postRequest(result);
		}
	}

	// Web request for activity signup
	private class SignupRequest extends IonRequest<Boolean> {
		private static final String TAG = "SignupRequest";
		private final String AID;
		private final String BID;

		public SignupRequest(int AID, int BID) {
			this.AID = Integer.toString(AID);
			this.BID = Integer.toString(BID);
		}

		@Override
		protected String getURL() {
			return Utils.API.SIGNUP;
		}

		@Override
		protected Boolean doInBackground(HttpsURLConnection urlConnection) throws Exception {
			// Add parameters
			urlConnection.addRequestProperty("block", BID);
			urlConnection.addRequestProperty("activity", AID);

			// Send request
			urlConnection.connect();
			urlConnection.getInputStream();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mTasks.remove(this);
			if (result != null) {
				showSnackbar(Response.SUCCESS);
			}
		}
	}

	// Ping the server, discard response and do nothing afterwards
	private class ServerRequest extends AsyncTask<Void, Void, Boolean> {
		private static final String TAG = "Server Ping";
		private static final String URL = "https://iodine.tjhsst.edu/"; // TODO: switch to ion
		private final String domain;

		public ServerRequest(String domain) {
			this.domain = domain;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			final HttpsURLConnection urlConnection;
			try {
				urlConnection = (HttpsURLConnection) new URL(URL + domain).openConnection();
				// Add auth token
				urlConnection.setRequestProperty("Authorization", mAuthKey);
				// Begin connection
				urlConnection.connect();
				urlConnection.getInputStream();
				// Close connection
				urlConnection.disconnect();
				return true;
			} catch (IOException e) {
				Log.e(TAG, "Connection error.", e);
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mTasks.remove(this);
		}
	}
}
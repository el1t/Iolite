package com.el1t.iolite.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.el1t.iolite.R;
import com.el1t.iolite.model.EighthActivity;
import com.el1t.iolite.parser.ActivityHandler;
import com.el1t.iolite.util.RequestActivity;
import com.el1t.iolite.util.Utils;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupActivity extends RequestActivity implements SignupFragment.OnFragmentInteractionListener {
	private static final String TAG = "Signup Activity";
	private static final String ARG_FAKE = "fake";
	private static final String ARG_BID = "bid";
	private static final String ARG_FRAGMENT = "fragment";

	private SignupFragment mSignupFragment;
	private int BID;
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
			fake = savedInstanceState.getBoolean(ARG_FAKE);
			if (BID == savedInstanceState.getInt(ARG_BID)) {
				mSignupFragment = (SignupFragment) getFragmentManager().getFragment(savedInstanceState, ARG_FRAGMENT);
			}
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
		savedInstanceState.putInt(ARG_BID, BID);
		savedInstanceState.putBoolean(ARG_FAKE, fake);
		getFragmentManager().putFragment(savedInstanceState, ARG_FRAGMENT, mSignupFragment);
	}

	@Override
	public void onDestroy() {
		// Try to cancel running tasks
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
		if (fake) {
			// Reload offline list
			postRequest(getList());
		} else {
			// Set loading fragment, if necessary
			if (mSignupFragment == null) {
				// Set loading fragment
				getFragmentManager().beginTransaction()
						.add(R.id.container, new LoadingFragment())
						.commit();
			}
			mTasks.add(new ActivityListRequest(BID).execute());
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
			mTasks.add(new SignupRequest(item.getAID(), item.getBID(), item.getSID()).execute());
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
		final String message;
		switch (result) {
			case SUCCESS:
				// TODO: Pass success to home activity
				Log.d(TAG, "Sign up success");
				finish();
				return;
			case CAPACITY:
				message = "Capacity exceeded";
				Log.d(TAG, "Capacity exceeded");
				break;
			case RESTRICTED:
				message = "Activity restricted";
				Log.d(TAG, "Restricted activity");
				break;
			case CANCELLED:
				message = "Activity cancelled";
				Log.d(TAG, "Cancelled activity");
				break;
			case PRESIGN:
				message = "Activity signup not open yet";
				Log.d(TAG, "Presign activity");
				break;
			case ATTENDANCE_TAKEN:
				message = "Signup closed";
				Log.d(TAG, "Attendance taken");
				break;
			case FAIL:
				// TODO: Make this error message reflect the actual error
				message = "Fatal error";
				Log.w(TAG, "Sign up failure");
				break;
			default:
				message = "Something went wrong...";
				Log.e(TAG, "Unknown response", new IllegalArgumentException());
				break;
		}
		Snackbar.make(findViewById(R.id.container), message, Snackbar.LENGTH_SHORT).show();
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

	// Get a fake list of activities for debugging
	private EighthActivity[] getList() {
		try {
			return ActivityHandler.parseAll(getAssets().open("testActivityList.json"));
		} catch (Exception e) {
			Log.e(TAG, "Error parsing activity xml", e);
		}
		return null;
	}

	// Retrieve activity list for BID
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
		protected ActivityListRequest getNewInstance() {
			return new ActivityListRequest(BID);
		}

		@Override
		protected EighthActivity[] doInBackground(HttpsURLConnection urlConnection) throws Exception {
			urlConnection.connect();
			// Parse JSON from server
			return ActivityHandler.parseAll(urlConnection.getInputStream());
		}

		@Override
		protected void onPostExecute(EighthActivity[] result) {
			super.onPostExecute(result);
			mTasks.remove(this);
			postRequest(result);
		}
	}

	// Request sign-up for specified activity
	private class SignupRequest extends IonRequest<Boolean> {
		private final int AID;
		private final int BID;
		private final int SID;

		public SignupRequest(int AID, int BID, int SID) {
			this.AID = AID;
			this.BID = BID;
			this.SID = SID;
		}

		@Override
		protected String getURL() {
			return Utils.API.SIGNUP;
		}

		@Override
		protected SignupRequest getNewInstance() {
			return new SignupRequest(AID, BID, SID);
		}

		@Override
		protected Boolean doInBackground(HttpsURLConnection urlConnection) throws Exception {
			// Add parameters
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			urlConnection.setDoInput(true);
			final JSONObject request = new JSONObject();
			request.put("block", BID);
			request.put("activity", AID);
			request.put("scheduled_activity", SID);
			request.put("use_scheduled_activity", true);
			request.put("force", false);

			// Send request
			urlConnection.connect();
			final OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
			writer.write(request.toString());
			writer.flush();
			writer.close();

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
}
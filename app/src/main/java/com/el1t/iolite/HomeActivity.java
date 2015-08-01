package com.el1t.iolite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.el1t.iolite.drawer.AbstractDrawerActivity;
import com.el1t.iolite.item.EighthActivity;
import com.el1t.iolite.item.EighthBlock;
import com.el1t.iolite.item.Schedule;
import com.el1t.iolite.item.User;
import com.el1t.iolite.parser.EighthActivityJsonParser;
import com.el1t.iolite.parser.EighthBlockJsonParser;
import com.el1t.iolite.parser.ScheduleJsonParser;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by El1t on 10/24/14.
 */

public class HomeActivity extends AbstractDrawerActivity implements BlockFragment.OnFragmentInteractionListener,
		ScheduleFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener
{
	private static final String TAG = "Block Activity";
	public static final int INITIAL_DAYS_TO_LOAD = 14;
	public static final int DAYS_TO_LOAD = 7;

	private BlockFragment mBlockFragment;
	private ScheduleFragment mScheduleFragment;
	private String mAuthKey;
	private User mUser;
	private boolean fake;
	private Section activeView;

	public enum Section {
		BLOCK, SCHEDULE, LOADING
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Check if restoring from previously destroyed instance
		if (savedInstanceState == null) {
			final Intent intent = getIntent();
			mUser = intent.getParcelableExtra("user");
			activeView = Section.BLOCK;

			// Check if fake information should be used
			if ((fake = intent.getBooleanExtra("fake", false))) {
				Log.d(TAG, "Loading test info");
				// Pretend fake list was received
				postBlockRequest(getBlockList());
			}
		} else {
			mUser = savedInstanceState.getParcelable("user");
			fake = savedInstanceState.getBoolean("fake");
			activeView = Section.valueOf(savedInstanceState.getString("activeView"));
			switch (activeView) {
				case BLOCK:
					mBlockFragment = (BlockFragment) getFragmentManager().getFragment(savedInstanceState, "blockFragment");
					break;
				case SCHEDULE:
					mScheduleFragment = (ScheduleFragment) getFragmentManager().getFragment(savedInstanceState, "scheduleFragment");
					break;
			}
		}

		// Set header text
		if (mUser != null) {
			((TextView) findViewById(R.id.header_name)).setText(mUser.getShortName());
			((TextView) findViewById(R.id.header_username)).setText(mUser.getUsername());
		} else {
			((TextView) findViewById(R.id.header_name)).setText("Unknown");
			((TextView) findViewById(R.id.header_username)).setText("Unknown");
		}

		if (!fake) {
			// Retrieve authKey from shared preferences
			final SharedPreferences preferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
			mAuthKey = Utils.getAuthKey(preferences);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Load list of blocks from web
		refresh(false);
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable("fake", fake);
		savedInstanceState.putString("activeView", activeView.name());
		switch (activeView) {
			case BLOCK:
				if (mBlockFragment != null) {
					getFragmentManager().putFragment(savedInstanceState, "blockFragment", mBlockFragment);
				}
				break;
			case SCHEDULE:
				if (mScheduleFragment != null) {
					getFragmentManager().putFragment(savedInstanceState, "scheduleFragment", mScheduleFragment);
				}
				break;
		}
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.nav_eighth:
				switchView(Section.BLOCK);
				break;
			case R.id.nav_schedule:
				switchView(Section.SCHEDULE);
				break;
//			case R.id.nav_settings:
//				break;
			case R.id.nav_about:
				startActivity(new Intent(this, AboutActivity.class));
				break;
			case R.id.nav_logout:
				logout();
				break;
			default:
				return false;
		}
		return super.onNavigationItemSelected(item);
	}

	// Switch and refresh view if a new view is selected
	private void switchView(Section newView) {
		if (activeView != newView) {
			activeView = newView;
			refresh(true);
		}
	}

	// Select a BID to display activities for
	public void select(int BID) {
		// Send data to SignupActivity
		final Intent intent = new Intent(this, SignupActivity.class);
		intent.putExtra("BID", BID);
		intent.putExtra("fake", fake);
		startActivity(intent);
	}

	// Display details for activity
	public void viewDetails(EighthActivity activityItem) {
		final Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra("activity", activityItem);
		intent.putExtra("fake", fake);
		startActivity(intent);
	}

	// Clear the selected activity
	public void clear(int BID) {
		new ClearRequest(BID).execute();
	}

	// Get a fake list of blocks for debugging
	private ArrayList<EighthBlock> getBlockList() {
		try {
			return EighthBlockJsonParser.parse(getAssets().open("testBlockList.json"));
		} catch(Exception e) {
			Log.e(TAG, "Error parsing block XML", e);
		}
		// Don't die?
		return new ArrayList<>();
	}

	public void refresh() {
		refresh(false);
	}

	/**
	 * @param changeView True when new view is created
	 */
	private void refresh(boolean changeView) {
		if (fake) {
			switch (activeView) {
				case BLOCK:
					// Reload offline list
					postBlockRequest(getBlockList());
					setTitle("Blocks");
					break;
				case SCHEDULE:
					setTitle("Schedule");
					break;
			}
		} else if (activeView == Section.SCHEDULE) {
			// The schedule uses iodine's api
			if (mScheduleFragment == null) {
				getFragmentManager().beginTransaction()
						.replace(R.id.container, new LoadingFragment())
						.commit();
				activeView = Section.LOADING;
				setTitle("Schedule");
			} else if (changeView) {
				getFragmentManager().beginTransaction()
						.replace(R.id.container, mScheduleFragment)
						.commit();
				setTitle("Schedule");
			}

			// Retrieve schedule
			new ScheduleRequest(Calendar.getInstance().getTime()).execute(INITIAL_DAYS_TO_LOAD);
		} else if (mAuthKey == null) {
			expired();
		} else switch (activeView) {
			case BLOCK:
				// Set loading fragment, if necessary
				if (mBlockFragment == null) {
					getFragmentManager().beginTransaction()
							.replace(R.id.container, new LoadingFragment())
							.commit();
					activeView = Section.LOADING;
					setTitle("Blocks");
				} else if (changeView) {
					getFragmentManager().beginTransaction()
							.replace(R.id.container, mBlockFragment)
							.commit();
					setTitle("Blocks");
				}

				// Retrieve list of bids using authKey
				new BlockListRequest().execute();
				break;
		}
	}

	// Load more posts/days
	public void load() {
		new ScheduleRequest(mScheduleFragment.getLastDay().getTomorrow()).execute(DAYS_TO_LOAD);
	}

	void logout() {
		mAuthKey = null;
		// Start login activity
		final Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("logout", true);
		startActivity(intent);
		finish();
	}

	void expired() {
		mAuthKey = null;
		final Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("expired", true);
		startActivity(intent);
		finish();
	}

	private void postBlockRequest(ArrayList<EighthBlock> result) {
		// Check if creating a new fragment is necessary
		// This should probably be done in onCreate, without a bundle
		if (mBlockFragment == null) {
			// Create the content view
			mBlockFragment = new BlockFragment();
			// Add ArrayList to the ListView in BlockFragment
			Bundle args = new Bundle();
			args.putParcelableArrayList("list", result);
			mBlockFragment.setArguments(args);
			// Switch to BlockFragment view, remove LoadingFragment
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mBlockFragment)
					.commit();
			activeView = Section.BLOCK;
		} else {
			mBlockFragment.setListItems(result);
		}
	}

	private void postScheduleRequest(Schedule[] result) {
		if (mScheduleFragment == null) {
			// Create the content view
			mScheduleFragment = new ScheduleFragment();
			final Bundle args = new Bundle();
			args.putParcelableArray("schedule", result);
			mScheduleFragment.setArguments(args);
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mScheduleFragment)
					.commit();
			activeView = Section.SCHEDULE;
		} else if (result.length == INITIAL_DAYS_TO_LOAD) {
			mScheduleFragment.reset(result);
			mScheduleFragment.setRefreshing(false);
		} else {
			mScheduleFragment.addSchedules(result);
			mScheduleFragment.setRefreshing(false);
		}
	}

	// Get list of blocks
	private class BlockListRequest extends AsyncTask<Void, Void, ArrayList<EighthBlock>> {
		private static final String TAG = "Block List Connection";

		@Override
		protected ArrayList<EighthBlock> doInBackground(Void... params) {
			HttpsURLConnection urlConnection;
			ArrayList<EighthBlock> response = null;
			try {
				urlConnection = (HttpsURLConnection) new URL(Utils.API.BLOCKS).openConnection();
				// Add authKey to header
				urlConnection.setRequestProperty("Authorization", mAuthKey);
				// Begin connection
				urlConnection.connect();
				// Parse JSON from server
				response = EighthBlockJsonParser.parse(urlConnection.getInputStream());
				// Close connection
				urlConnection.disconnect();
			} catch (IOException e) {
				Log.e(TAG, "IO Error", e);
			} catch (Exception e) {
				Log.e(TAG, "Connection Error", e);
			}
			return response;
		}

		@Override
		protected void onPostExecute(ArrayList<EighthBlock> result) {
			super.onPostExecute(result);
			if (result == null) {
				expired();
			} else {
				postBlockRequest(result);
			}
		}
	}

	// Get list of activity signups
	private class ActivitySignupListRequest extends AsyncTask<Void, Void, EighthActivity[]> {
		private static final String TAG = "Block List Connection";
		private static final String URL = "https://ion.tjhsst.edu/api/signups/user"; // TODO: fix

		@Override
		protected EighthActivity[] doInBackground(Void... urls) {
			HttpsURLConnection urlConnection;
			EighthActivity[] response = null;
			try {
				urlConnection = (HttpsURLConnection) new URL(URL).openConnection();
				// Add authKey to header
				urlConnection.setRequestProperty("Authorization", mAuthKey);
				// Begin connection
				urlConnection.connect();
				// Parse JSON from server
				response = EighthActivityJsonParser.parseAll(urlConnection.getInputStream());
				// Close connection
				urlConnection.disconnect();
			} catch (IOException e) {
				Log.e(TAG, "IO Error", e);
			} catch (Exception e) {
				Log.e(TAG, "Connection Error", e);
			}
			return response;
		}

		@Override
		protected void onPostExecute(EighthActivity[] result) {
			super.onPostExecute(result);
			if (result == null) {
				expired();
			} else {
				postBlockRequest(null);
			}
		}
	}

	// Web request for clearing activity
	private class ClearRequest extends AsyncTask<Void, Void, Boolean> {
		private static final String TAG = "Clear Connection";
		private final String BID;

		public ClearRequest(int BID) {
			this.BID = Integer.toString(BID);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			final HttpsURLConnection urlConnection;
			try {
				urlConnection = (HttpsURLConnection) new URL(Utils.API.SIGNUP).openConnection();
				// Add auth token
				urlConnection.setRequestProperty("Authorization", mAuthKey);

				// Add parameters
				urlConnection.addRequestProperty("block", BID);
				urlConnection.addRequestProperty("activity", "999");

				// Send request
				urlConnection.connect();
				urlConnection.getInputStream();
				urlConnection.disconnect();
				return true;
			} catch (IOException e) {
				Log.e(TAG, "Connection error.", e);
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			refresh();
		}
	}

	private class ScheduleRequest extends AsyncTask<Integer, Void, Schedule[]> {
		private static final String TAG = "Schedule Connection";
		private final DateFormat mFormat = new SimpleDateFormat("yyyyMMdd");
		private Date mStartDate;

		public ScheduleRequest (Date startDate) {
			mStartDate = startDate;
		}

		public ScheduleRequest (String startDate) {
			try {
				mStartDate = mFormat.parse(startDate);
			} catch (ParseException e) {
				Log.e(TAG, "Date Parse Error.", e);
				mStartDate = null;
			}
		}

		@Override
		protected Schedule[] doInBackground(Integer... days) {
			final Date endDate = computeDays(days[0]);
			HttpsURLConnection urlConnection = null;
			Schedule[] response = null;
			try {
				urlConnection = (HttpsURLConnection) new URL(Utils.API.SCHEDULE +
						"?start=" + mFormat.format(mStartDate) + "&end=" + mFormat.format(endDate))
						.openConnection();
				// Begin connection
				urlConnection.connect();
				// Parse JSON from server
				response = ScheduleJsonParser.parseAll(Utils.inputStreamToJSON(urlConnection.getInputStream()));
			} catch (Exception e) {
				Log.e(TAG, "Error retrieving schedule", e);
			} finally {
				if (urlConnection != null) {
					// Close connection
					urlConnection.disconnect();
				}
			}
			return response;
		}

		private Date computeDays(int daysAfter) {
			final Calendar c = Calendar.getInstance();
			c.setTime(mStartDate);
			// Current day is included, but last day is not included
			c.add(Calendar.DATE, daysAfter);
			return c.getTime();
		}

		@Override
		protected void onPostExecute(Schedule[] result) {
			super.onPostExecute(result);
			if (result == null) {
				Log.e(TAG, "Schedule Listing Aborted");
			} else {
				postScheduleRequest(result);
			}
		}
	}
}
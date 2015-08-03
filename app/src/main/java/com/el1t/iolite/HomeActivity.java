package com.el1t.iolite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.el1t.iolite.item.EighthActivity;
import com.el1t.iolite.item.EighthBlock;
import com.el1t.iolite.item.Schedule;
import com.el1t.iolite.item.User;
import com.el1t.iolite.parser.EighthBlockJsonParser;
import com.el1t.iolite.parser.ProfileJsonParser;
import com.el1t.iolite.parser.ScheduleJsonParser;
import com.el1t.iolite.utils.AbstractDrawerActivity;
import com.el1t.iolite.utils.Utils;

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
			activeView = Section.BLOCK;

			// Check if fake information should be used
			if ((fake = intent.getBooleanExtra("fake", false))) {
				Log.d(TAG, "Loading test info");
				try {
					// Use a test profile
					mUser = ProfileJsonParser.parse(getAssets().open("testProfile.json"));
					updateUser();
					// Pretend block list was received
					postBlockRequest(EighthBlockJsonParser.parse(getAssets().open("testBlockList.json")));
				} catch (Exception e) {
					Log.e(TAG, "Error parsing test JSON files", e);
				}
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

		if (!fake) {
			// Retrieve authKey from shared preferences
			final SharedPreferences preferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
			mAuthKey = Utils.getAuthKey(preferences);
		}

		if (mUser == null) {
			new Authentication().execute();
		} else {
			updateUser();
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

	@Override
	protected String getAuthKey() {
		return mAuthKey;
	}

	@Override
	protected View getContainer() {
		return findViewById(R.id.container);
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
			new ScheduleRequest(Calendar.getInstance().getTime(), INITIAL_DAYS_TO_LOAD).execute();
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
		new ScheduleRequest(mScheduleFragment.getLastDay().getTomorrow(), DAYS_TO_LOAD).execute();
	}

	public void updateUser() {
		// Set header text
		((TextView) findViewById(R.id.header_name)).setText(mUser.getShortName());
		((TextView) findViewById(R.id.header_username)).setText(mUser.getUsername());
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
			final Bundle args = new Bundle();
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

	// Load student profile data
	private class Authentication extends IonRequest<User> {
		private static final String TAG = "Authentication";

		@Override
		protected String getURL() {
			return Utils.API.PROFILE;
		}

		@Override
		protected User doInBackground(HttpsURLConnection urlConnection) throws Exception {
			// Begin connection
			urlConnection.connect();
			// Parse JSON from server
			return ProfileJsonParser.parse(urlConnection.getInputStream());
		}

		@Override
		protected void onPostExecute(User result) {
			super.onPostExecute(result);
			if (result != null) {
				mUser = result;
				updateUser();
			}
		}
	}

	// Get list of blocks
	private class BlockListRequest extends IonRequest<ArrayList<EighthBlock>> {
		private static final String TAG = "Block List Connection";

		@Override
		protected String getURL() {
			return Utils.API.BLOCKS;
		}

		protected ArrayList<EighthBlock> doInBackground(HttpsURLConnection urlConnection) throws Exception {
			// Begin connection
			urlConnection.connect();
			// Parse JSON from server
			return EighthBlockJsonParser.parse(urlConnection.getInputStream());
		}

		@Override
		protected void onPostExecute(ArrayList<EighthBlock> result) {
			super.onPostExecute(result);
			if (result != null) {
				postBlockRequest(result);
			}
		}
	}

	// Web request for clearing activity
	private class ClearRequest extends IonRequest<Boolean> {
		private static final String TAG = "Clear Connection";
		private final String BID;

		public ClearRequest(int BID) {
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
			urlConnection.addRequestProperty("activity", "999");

			// Send request
			urlConnection.connect();
			urlConnection.getInputStream();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			refresh();
		}
	}

	private class ScheduleRequest extends IonRequest<Schedule[]> {
		private static final String TAG = "Schedule Connection";
		private final DateFormat mFormat = new SimpleDateFormat("yyyyMMdd");
		private Date mStartDate;
		private Date mEndDate;

		public ScheduleRequest(Date startDate, int days) {
			mStartDate = startDate;
			mEndDate = computeDays(days);
		}

		public ScheduleRequest(String startDate, int days) {
			try {
				mStartDate = mFormat.parse(startDate);
				mEndDate = computeDays(days);
			} catch (ParseException e) {
				Log.e(TAG, "Date Parse Error.", e);
				mStartDate = null;
				mEndDate = null;
			}
		}

		@Override
		protected String getURL() {
			return Utils.API.SCHEDULE + "?start=" + mFormat.format(mStartDate) + "&end=" + mFormat.format(mEndDate);
		}

		@Override
		protected Schedule[] doInBackground(HttpsURLConnection urlConnection) throws Exception {
			// Begin connection
			urlConnection.connect();
			// Parse JSON from server
			return ScheduleJsonParser.parseAll(Utils.inputStreamToJSON(urlConnection.getInputStream()));
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
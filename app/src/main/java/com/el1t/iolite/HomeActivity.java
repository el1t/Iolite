package com.el1t.iolite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.el1t.iolite.drawer.AbstractDrawerActivity;
import com.el1t.iolite.drawer.NavDrawerActivityConfig;
import com.el1t.iolite.drawer.NavDrawerAdapter;
import com.el1t.iolite.drawer.NavMenuBuilder;
import com.el1t.iolite.drawer.NavMenuItem;
import com.el1t.iolite.item.EighthBlockItem;
import com.el1t.iolite.item.Schedule;
import com.el1t.iolite.item.User;
import com.el1t.iolite.parser.EighthBlockXmlParser;
import com.el1t.iolite.parser.ScheduleJsonParser;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by El1t on 10/24/14.
 */

public class HomeActivity extends AbstractDrawerActivity implements BlockFragment.OnFragmentInteractionListener,
		ScheduleFragment.OnFragmentInteractionListener
{
	private static final String TAG = "Block Activity";
	public static final int INITIAL_DAYS_TO_LOAD = 14;
	public static final int DAYS_TO_LOAD = 7;

	private BlockFragment mBlockFragment;
	private ScheduleFragment mScheduleFragment;
	private Cookie[] mCookies;
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
				Log.d(TAG, "Loading fake info");
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
		}

		if (!fake) {
			// Retrieve cookies from shared preferences
			final SharedPreferences preferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
			mCookies = LoginActivity.getCookies(preferences);
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
	public NavDrawerActivityConfig getNavDrawerConfiguration() {
		final NavDrawerAdapter adapter = new NavDrawerAdapter(this, R.layout.nav_item);
		adapter.setItems(new NavMenuBuilder()
				.addItem(NavMenuItem.create(101, "Eighth", R.drawable.ic_event_black_24dp))
				.addItem(NavMenuItem.create(102, "Schedule", R.drawable.ic_event_black_24dp))
				.addSeparator()
				.addItem(NavMenuItem.createButton(201, "Settings", R.drawable.ic_settings_black_24dp))
				.addItem(NavMenuItem.createButton(202, "About", R.drawable.ic_help_black_24dp))
				.addItem(NavMenuItem.createButton(203, "Logout", R.drawable.ic_exit_to_app_black_24dp))
				.build());

		return new NavDrawerActivityConfig.Builder()
				.mainLayout(R.layout.drawer_layout)
				.drawerLayoutId(R.id.drawer_layout)
				.drawerContainerId(R.id.drawer_container)
				.leftDrawerId(R.id.drawer)
				.checkedPosition(0)
				.drawerShadow(R.drawable.drawer_shadow)
				.drawerOpenDesc(R.string.action_drawer_open)
				.drawerCloseDesc(R.string.action_drawer_close)
				.adapter(adapter)
				.build();
	}

	@Override
	public void onNavItemSelected(int id) {
		switch (id) {
			case 101:
				switchView(Section.BLOCK);
				break;
			case 102:
				switchView(Section.SCHEDULE);
				break;
			case 202:
				startActivity(new Intent(this, AboutActivity.class));
				break;
			case 203:
				logout();
				break;
		}
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

	// Get a fake list of blocks for debugging
	private ArrayList<EighthBlockItem> getBlockList() {
		try {
			return EighthBlockXmlParser.parse(getAssets().open("testBlockList.xml"), getApplicationContext());
		} catch(Exception e) {
			Log.e(TAG, "Error parsing block xml", e);
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
					break;
				case SCHEDULE:
					break;
			}
		} else if (activeView == Section.SCHEDULE) {
			// The schedule does not need cookies to function
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
		} else if (mCookies == null) {
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

				// Retrieve list of bids using cookies
				new BlockListRequest().execute("https://iodine.tjhsst.edu/api/eighth/list_blocks");
				break;
		}
	}

	// Load more posts/days
	public void load() {
		new ScheduleRequest(mScheduleFragment.getLastDay().getTomorrow()).execute(DAYS_TO_LOAD);
	}

	void logout() {
		mCookies = null;
		// Start login activity
		final Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("logout", true);
		startActivity(intent);
		finish();
	}

	void expired() {
		mCookies = null;
		final Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("expired", true);
		startActivity(intent);
		finish();
	}

	private void postBlockRequest(ArrayList<EighthBlockItem> result) {
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
			Bundle args = new Bundle();
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
	private class BlockListRequest extends AsyncTask<String, Void, ArrayList<EighthBlockItem>> {
		private static final String TAG = "Block List Connection";

		@Override
		protected ArrayList<EighthBlockItem> doInBackground(String... urls) {

			HttpURLConnection urlConnection;
			ArrayList<EighthBlockItem> response = null;
			try {
				urlConnection = (HttpURLConnection) new URL(urls[0]).openConnection();
				// Add cookies to header
				for(Cookie cookie : mCookies) {
					urlConnection.setRequestProperty("Cookie", cookie.getName() + "=" + cookie.getValue());
				}
				// Begin connection
				urlConnection.connect();
				// Parse xml from server
				response = EighthBlockXmlParser.parse(urlConnection.getInputStream(), getApplicationContext());
				// Close connection
				urlConnection.disconnect();
			} catch (XmlPullParserException e) {
				Log.e(TAG, "XML error.", e);
			} catch (Exception e) {
				Log.e(TAG, "Connection error.", e);
			}
			return response;
		}

		@Override
		protected void onPostExecute(ArrayList<EighthBlockItem> result) {
			super.onPostExecute(result);
			if (result == null) {
				expired();
			} else {
				postBlockRequest(result);
			}
		}
	}

	private class ScheduleRequest extends AsyncTask<Integer, Void, Schedule[]> {
		private static final String TAG = "Schedule Connection";
		private static final String API_URL = "https://iodine.tjhsst.edu/ajax/dayschedule/json_exp";
		private final DateFormat mFormat = new SimpleDateFormat("yyyyMMdd");
		private Date mStartDate;

		public ScheduleRequest (Date startDate) {
			mStartDate = startDate;
		}

		public ScheduleRequest (String startDate) {
			try {
				mStartDate = mFormat.parse(startDate);
			} catch (ParseException e) {
				Log.e(TAG, "Date parse error.", e);
				mStartDate = null;
			}
		}

		@Override
		protected Schedule[] doInBackground(Integer... days) {
			final Date endDate = computeDays(days[0]);
			HttpURLConnection urlConnection = null;
			Schedule[] response = null;
			try {
				urlConnection = (HttpURLConnection) new URL(API_URL +
						"?start=" + mFormat.format(mStartDate) + "&end=" + mFormat.format(endDate))
						.openConnection();
				// Begin connection
				urlConnection.connect();
				// Parse JSON from server
				response = ScheduleJsonParser.parseSchedules(inputStreamToJSON(urlConnection.getInputStream()));
			} catch (JSONException e) {
				Log.e(TAG, "JSON error.", e);
			} catch (Exception e) {
				Log.e(TAG, "Connection error.", e);
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

		// Parse the InputStream into a JSONObject
		private JSONObject inputStreamToJSON(InputStream inputStream) throws JSONException, IOException {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			final StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return new JSONObject(sb.toString());
		}

		@Override
		protected void onPostExecute(Schedule[] result) {
			super.onPostExecute(result);
			if (result == null) {
				Log.e(TAG, "Schedule listing aborted");
			} else {
				postScheduleRequest(result);
			}
		}
	}
}
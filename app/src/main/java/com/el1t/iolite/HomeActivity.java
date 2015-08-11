package com.el1t.iolite;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.el1t.iolite.item.EighthActivity;
import com.el1t.iolite.item.EighthBlock;
import com.el1t.iolite.item.NewsPost;
import com.el1t.iolite.item.Schedule;
import com.el1t.iolite.item.User;
import com.el1t.iolite.parser.EighthBlockJsonParser;
import com.el1t.iolite.parser.NewsJsonParser;
import com.el1t.iolite.parser.ProfileJsonParser;
import com.el1t.iolite.parser.ScheduleJsonParser;
import com.el1t.iolite.parser.UserBlockJsonParser;
import com.el1t.iolite.utils.AbstractDrawerActivity;
import com.el1t.iolite.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by El1t on 10/24/14.
 */

public class HomeActivity extends AbstractDrawerActivity implements BlockFragment.OnFragmentInteractionListener,
		ScheduleFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener,
		NewsFragment.OnFragmentInteractionListener {
	private static final String TAG = "Block Activity";
	private static final String ARG_USER = "user";
	private static final String ARG_FAKE = "fake";
	private static final String ARG_ACTIVE_VIEW = "active";
	private static final String ARG_FRAGMENT = "fragment";
	public static final int INITIAL_DAYS_TO_LOAD = 14;
	public static final int DAYS_TO_LOAD = 7;

	private BlockFragment mBlockFragment;
	private ScheduleFragment mScheduleFragment;
	private NewsFragment mNewsFragment;
	private User mUser;
	private boolean fake;
	private Section activeView;

	public enum Section {
		BLOCK, SCHEDULE, NEWS, LOADING
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Check if restoring from previously destroyed instance
		if (savedInstanceState == null) {
			final Intent intent = getIntent();
			activeView = Section.BLOCK;

			// Check if fake information should be used
			if ((fake = intent.getBooleanExtra(ARG_FAKE, false))) {
				Log.d(TAG, "Loading test profile");
				try {
					// Use a test profile
					mUser = ProfileJsonParser.parse(getAssets().open("testProfile.json"));
					updateUser();
				} catch (Exception e) {
					Log.e(TAG, "Error parsing test profile", e);
				}
			}
		} else {
			mUser = savedInstanceState.getParcelable(ARG_USER);
			fake = savedInstanceState.getBoolean(ARG_FAKE);
			activeView = Section.valueOf(savedInstanceState.getString(ARG_ACTIVE_VIEW));
			switch (activeView) {
				case BLOCK:
					mBlockFragment = (BlockFragment) getFragmentManager().getFragment(savedInstanceState, ARG_FRAGMENT);
					break;
				case SCHEDULE:
					mScheduleFragment = (ScheduleFragment) getFragmentManager().getFragment(savedInstanceState, ARG_FRAGMENT);
					break;
				case NEWS:
					mNewsFragment = (NewsFragment) getFragmentManager().getFragment(savedInstanceState, ARG_FRAGMENT);
					break;
			}
		}

		if (mUser == null) {
			new ProfileRequest().execute();
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
		savedInstanceState.putParcelable(ARG_USER, mUser);
		savedInstanceState.putSerializable(ARG_FAKE, fake);
		savedInstanceState.putString(ARG_ACTIVE_VIEW, activeView.name());
		switch (activeView) {
			case BLOCK:
				if (mBlockFragment != null) {
					getFragmentManager().putFragment(savedInstanceState, ARG_FRAGMENT, mBlockFragment);
				}
				break;
			case SCHEDULE:
				if (mScheduleFragment != null) {
					getFragmentManager().putFragment(savedInstanceState, ARG_FRAGMENT, mScheduleFragment);
				}
				break;
			case NEWS:
				if (mNewsFragment != null) {
					getFragmentManager().putFragment(savedInstanceState, ARG_FRAGMENT, mNewsFragment);
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
			case R.id.nav_news:
				switchView(Section.NEWS);
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

	public void select(NewsPost post, View view) {
		final Intent intent = new Intent(this, NewsDetailActivity.class);
		final ActivityOptionsCompat options =
				ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, getString(R.string.transition_news));
		intent.putExtra("post", post);
		ActivityCompat.startActivity(this, intent, options.toBundle());
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
			try {
				switch (activeView) {
					case BLOCK:
						if (mBlockFragment != null && changeView) {
							getFragmentManager().beginTransaction()
									.replace(R.id.container, mBlockFragment)
									.commit();
						}
						postBlockRequest(EighthBlockJsonParser.parse(getAssets().open("testBlockList.json")));
						setTitle("Blocks");
						break;
					case SCHEDULE:
						setTitle("Schedule");
						break;
					case NEWS:
						if (mNewsFragment != null && changeView) {
							getFragmentManager().beginTransaction()
									.replace(R.id.container, mNewsFragment)
									.commit();
						}
						postNewsRequest(NewsJsonParser.parse(getAssets().open("testNewsList.json")));
						setTitle("News");
						break;
				}
			} catch (Exception e) {
				Log.e(TAG, "Error loading test data", e);
			}
		} else switch (activeView) {
			case BLOCK:
				// Set loading fragment, if necessary
				if (mBlockFragment == null) {
					getFragmentManager().beginTransaction()
							.replace(R.id.container, new LoadingFragment())
							.commit();
					activeView = Section.LOADING;
				} else if (changeView) {
					getFragmentManager().beginTransaction()
							.replace(R.id.container, mBlockFragment)
							.commit();
				}
				setTitle("Blocks");
				new BlockListRequest().execute();
				break;
			case SCHEDULE:
				// The schedule uses iodine's api
				if (mScheduleFragment == null) {
					getFragmentManager().beginTransaction()
							.replace(R.id.container, new LoadingFragment())
							.commit();
					activeView = Section.LOADING;
				} else if (changeView) {
					getFragmentManager().beginTransaction()
							.replace(R.id.container, mScheduleFragment)
							.commit();
				}
				setTitle("Schedule");
				new ScheduleRequest(Calendar.getInstance().getTime(), INITIAL_DAYS_TO_LOAD).execute();
				break;
			case NEWS:
				// Set loading fragment, if necessary
				if (mNewsFragment == null) {
					getFragmentManager().beginTransaction()
							.replace(R.id.container, new LoadingFragment())
							.commit();
					activeView = Section.LOADING;
				} else if (changeView) {
					getFragmentManager().beginTransaction()
							.replace(R.id.container, mNewsFragment)
							.commit();
				}
				setTitle("News");
				new NewsRequest().execute();
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
		// Start login activity
		final Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("logout", true);
		startActivity(intent);
		finish();
	}

	private void postBlockRequest(EighthBlock[] result) {
		// Check if creating a new fragment is necessary
		// This should probably be done in onCreate, without a bundle
		if (mBlockFragment == null) {
			// Create the content view
			mBlockFragment = BlockFragment.newInstance(result);
			// Switch to BlockFragment view, remove LoadingFragment
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mBlockFragment)
					.commit();
			activeView = Section.BLOCK;
		} else {
			mBlockFragment.updateAdapter(result);
		}
	}

	private void postScheduleRequest(Schedule[] result) {
		if (mScheduleFragment == null) {
			// Create the content view
			mScheduleFragment = ScheduleFragment.newInstance(result);
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mScheduleFragment)
					.commit();
			activeView = Section.SCHEDULE;
		} else {
			if (result != null) {
				if (result.length == INITIAL_DAYS_TO_LOAD) {
					mScheduleFragment.clear();
				}
				mScheduleFragment.addSchedules(result);
			}
			mScheduleFragment.setRefreshing(false);
		}
	}

	private void postNewsRequest(NewsPost[] result) {
		if (mNewsFragment == null) {
			// Create the content view
			mNewsFragment = NewsFragment.newInstance(result);
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mNewsFragment)
					.commit();
			activeView = Section.NEWS;
		} else {
			mNewsFragment.updateAdapter(result);
		}
	}

	// Load student profile data
	private class ProfileRequest extends IonRequest<User> {
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
	private class BlockListRequest extends IonRequest<EighthBlock[]> {
		@Override
		protected String getURL() {
			return Utils.API.BLOCKS;
		}

		@Override
		protected EighthBlock[] doInBackground(HttpsURLConnection urlConnection) throws Exception {
			// Begin connection
			urlConnection.connect();
			// Parse JSON from server
			return EighthBlockJsonParser.parse(urlConnection.getInputStream());
		}

		@Override
		protected void onPostExecute(EighthBlock[] result) {
			super.onPostExecute(result);
			postBlockRequest(result);
			if (result != null) {
				new UserBlockRequest().execute();
			}
		}
	}

	private class UserBlockRequest extends IonRequest<EighthActivity[]> {
		@Override
		protected String getURL() {
			return Utils.API.SIGNUP;
		}

		protected EighthActivity[] doInBackground(HttpsURLConnection urlConnection) throws Exception {
			urlConnection.connect();
			return UserBlockJsonParser.parse(urlConnection.getInputStream());
		}

		@Override
		protected void onPostExecute(EighthActivity[] result) {
			super.onPostExecute(result);
			mBlockFragment.updateAdapter(result);
		}
	}

	// Web request for clearing activity
	private class ClearRequest extends IonRequest<Boolean> {
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
			postScheduleRequest(result);
		}
	}

	private class NewsRequest extends IonRequest<NewsPost[]> {
		@Override
		protected String getURL() {
			return Utils.API.NEWS;
		}

		protected NewsPost[] doInBackground(HttpsURLConnection urlConnection) throws Exception {
			// Begin connection
			urlConnection.connect();
			// Parse JSON from server
			return NewsJsonParser.parse(urlConnection.getInputStream());
		}

		@Override
		protected void onPostExecute(NewsPost[] result) {
			super.onPostExecute(result);
			postNewsRequest(result);
		}
	}
}
package com.el1t.iolite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.cookie.Cookie;
import org.xmlpull.v1.XmlPullParserException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by El1t on 10/24/14.
 */

public class BlockActivity extends AbstractDrawerActivity implements BlockFragment.OnFragmentInteractionListener
{
	private static final String TAG = "Block Activity";

	private BlockFragment mBlockFragment;
	private Cookie[] mCookies;
	private boolean fake;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Check if restoring from previously destroyed instance
		if (savedInstanceState == null) {
			final Intent intent = getIntent();

			// Check if fake information should be used
			if ((fake = intent.getBooleanExtra("fake", false))) {
				Log.d(TAG, "Loading fake info");
				// Pretend fake list was received
				postRequest(getList());
			}
		} else {
			fake = savedInstanceState.getBoolean("fake");
			mBlockFragment = (BlockFragment) getFragmentManager().getFragment(savedInstanceState, "fragment");
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
		refresh();
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable("fake", fake);
		if (mBlockFragment != null) {
			getFragmentManager().putFragment(savedInstanceState, "fragment", mBlockFragment);
		}
	}

	@Override
	protected NavDrawerActivityConfig getNavDrawerConfiguration() {
		final NavDrawerAdapter adapter = new NavDrawerAdapter(this, R.layout.nav_item);
		adapter.setItems(new NavMenuBuilder()
				.addItem(NavMenuItem.create(101, "Eighth", R.drawable.ic_event_black_24dp))
				.addItem(NavMenuItem.create(102, "Test!", R.drawable.ic_event_black_24dp))
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
	protected void onNavItemSelected(int id) {
		switch (id) {
			case 202:
				startActivity(new Intent(this, AboutActivity.class));
				break;
			case 203:
				logout();
				break;
//			case 101:
//				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FriendMainFragment()).commit();
//				break;
//			case 102:
//				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AirportFragment()).commit();
//				break;
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
	private ArrayList<EighthBlockItem> getList() {
		try {
			return EighthBlockXmlParser.parse(getAssets().open("testBlockList.xml"), getApplicationContext());
		} catch(Exception e) {
			Log.e(TAG, "Error parsing block xml", e);
		}
		// Don't die?
		return new ArrayList<EighthBlockItem>();
	}

	public void refresh() {
		if (!fake) {
			if (mCookies == null) {
				logout();
			} else {
				// Set loading fragment, if necessary
				if(mBlockFragment == null) {
					getFragmentManager().beginTransaction()
							.replace(R.id.container, new LoadingFragment())
							.commit();
				}

				// Retrieve list of bids using cookies
				new BlockListRequest().execute("https://iodine.tjhsst.edu/api/eighth/list_blocks");
			}
		} else {
			// Reload offline list
			postRequest(getList());
		}
	}

	void logout() {
		mCookies = null;
		// Start login activity
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("logout", true);
		startActivity(intent);
		finish();
	}

	private void postRequest(ArrayList<EighthBlockItem> result) {
		// Check if creating a new fragment is necessary
		// This should probably be done in onCreate, without a bundle
		if (mBlockFragment == null) {
			// Create the content view
			mBlockFragment = new BlockFragment();
			// Add ArrayList to the ListView in BlockFragment
			Bundle args = new Bundle();
			args.putSerializable("list", result);
			mBlockFragment.setArguments(args);
			// Switch to BlockFragment view, remove LoadingFragment
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mBlockFragment)
					.commit();
		} else {
			mBlockFragment.setListItems(result);
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
				logout();
			} else {
				postRequest(result);
			}
		}
	}
}
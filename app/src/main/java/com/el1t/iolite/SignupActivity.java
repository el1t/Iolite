package com.el1t.iolite;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.el1t.iolite.item.EighthActivityItem;
import com.el1t.iolite.parser.EighthActivityJsonParser;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupActivity extends AppCompatActivity implements SignupFragment.OnFragmentInteractionListener
{
	private static final String TAG = "Signup Activity";

	private SignupFragment mSignupFragment;
	private Cookie[] mCookies;
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
		if (savedInstanceState == null || BID != savedInstanceState.getInt("BID")) {
			// Check if fake information should be used
			if (fake = intent.getBooleanExtra("fake", false)) {
				Log.d(TAG, "Loading fake info");
				// Pretend fake list was received
				postRequest(getList());
			}
		} else {
			fake = savedInstanceState.getBoolean("fake");
			mSignupFragment = (SignupFragment) getFragmentManager().getFragment(savedInstanceState, "fragment");
		}

		if (!fake) {
			// Retrieve cookies from shared preferences
			final SharedPreferences preferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
//			mCookies = LoginActivity.getCookies(preferences);
		}

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
	public void submit(EighthActivityItem item) {
		// Perform checks before submission
		// Note that server performs checks as well
		 if (item.isCancelled()) {
			postSubmit(Response.CANCELLED);
		} else if (item.isFull()) {
			postSubmit(Response.CAPACITY);
		} else if (item.isRestricted()) {
			postSubmit(Response.RESTRICTED);
		} else {
			mTasks.add(new SignupRequest(item.getAID(), item.getBID()).execute("https://iodine.tjhsst.edu/api/eighth/signup_activity"));
		}
	}

	// Notify the user after submission
	void postSubmit(Response result) {
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
	private void postRequest(EighthActivityItem[] result) {
		if (mSignupFragment == null) {
			// Create the content view
			mSignupFragment = new SignupFragment();
			// Add ArrayList to the ListView in BlockFragment
			final Bundle args = new Bundle();
			args.putParcelableArray("list", result);
			mSignupFragment.setArguments(args);
			// Switch to BlockFragment view, remove LoadingFragment
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mSignupFragment)
					.commit();
		} else {
			mSignupFragment.setListItems(result);
		}
	}

	// Favorite an activity
	public void favorite(final int AID, final int BID, final EighthActivityItem item) {
		// Note: the server uses the UID field as the AID in its API
		// Sending the BID is useless, but it is required by the server
		mTasks.add(new ServerRequest().execute("https://iodine.tjhsst.edu/eighth/vcp_schedule/favorite/uid/" + AID + "/bids/" + BID));
		if (item.changeFavorite()) {
			Snackbar.make(findViewById(R.id.container), "Favorited", Snackbar.LENGTH_SHORT)
					.setAction("Undo", new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							mTasks.add(new ServerRequest()
									.execute("https://iodine.tjhsst.edu/eighth/vcp_schedule/favorite/uid/" + AID + "/bids/" + BID));
							item.changeFavorite();
							mSignupFragment.updateAdapter();
						}
					}).show();
		} else {
			Snackbar.make(findViewById(R.id.container), "Unfavorited", Snackbar.LENGTH_SHORT)
					.setAction("Undo", new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							mTasks.add(new ServerRequest()
									.execute("https://iodine.tjhsst.edu/eighth/vcp_schedule/favorite/uid/" + AID + "/bids/" + BID));
							item.changeFavorite();
							mSignupFragment.updateAdapter();
						}
					}).show();
		}
		Log.d(TAG, "Favorited AID " + AID);
	}

	// Get a fake list of activities for debugging
	private EighthActivityItem[] getList() {
		try {
			return EighthActivityJsonParser.parseAll(getAssets().open("testActivityList.json"));
		} catch (Exception e) {
			Log.e(TAG, "Error parsing activity xml", e);
		}
		return null;
	}

	// Retrieve activity list for BID from server using HttpURLConnection
	private class ActivityListRequest extends AsyncTask<Void, Void, EighthActivityItem[]> {
		private static final String TAG = "ActivityListRequest";
		private static final String URL = "https://ion.tjhsst.edu/api/blocks/";
		private int BID;

		public ActivityListRequest(int BID) {
			this.BID = BID;
		}

		@Override
		protected EighthActivityItem[] doInBackground(Void... params) {
			final HttpsURLConnection urlConnection;
			EighthActivityItem[] response = null;
			try {
				urlConnection = (HttpsURLConnection) new URL(URL + BID).openConnection();
				// Add authKey to header
				urlConnection.setRequestProperty("Authorization", mAuthKey);
				// Begin connection
				urlConnection.connect();
				// Parse xml from server
				response = EighthActivityJsonParser.parseAll(urlConnection.getInputStream());
				// Close connection
				urlConnection.disconnect();
			} catch (JSONException | ParseException e) {
				Log.e(TAG, "Parsing error.", e);
			} catch (IOException e) {
				Log.e(TAG, "Connection error.", e);
			}
			return response;
		}

		@Override
		protected void onPostExecute(EighthActivityItem[] result) {
			super.onPostExecute(result);
			mTasks.remove(this);
			// Add ArrayList to the ListView in SignupFragment
			postRequest(result);
		}
	}

	// Web request for activity signup using HttpClient
	private class SignupRequest extends AsyncTask<String, Void, Boolean> {
		private static final String TAG = "Signup Connection";
		private final String AID;
		private final String BID;

		public SignupRequest(int AID, int BID) {
			this.AID = Integer.toString(AID);
			this.BID = Integer.toString(BID);
		}

		@Override
		protected Boolean doInBackground(String... urls) {
			final DefaultHttpClient client = new DefaultHttpClient();
			try {
				// Setup client
				client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
				HttpPost post = new HttpPost(new URI(urls[0]));
				// Add cookies
				for(Cookie cookie : mCookies) {
					post.setHeader("Cookie", cookie.getName() + "=" + cookie.getValue());
				}
				// Add parameters
				final List<NameValuePair> data = new ArrayList<>(2);
				data.add(new BasicNameValuePair("aid", AID));
				data.add(new BasicNameValuePair("bid", BID));
				post.setEntity(new UrlEncodedFormEntity(data));

				// Send request
				client.execute(post);

				return true;
			} catch (URISyntaxException e) {
				Log.e(TAG, "URL -> URI error");
			} catch (IOException e) {
				Log.e(TAG, "Connection error.", e);
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mTasks.remove(this);
			if(result) {
				postSubmit(Response.SUCCESS);
			} else {
				postSubmit(Response.FAIL);
			}
		}
	}

	// Ping the server, discard response and do nothing afterwards
	private class ServerRequest extends AsyncTask<String, Void, Boolean> {
		private static final String TAG = "Server Ping";

		@Override
		protected Boolean doInBackground(String... urls) {
			final HttpsURLConnection urlConnection;
			try {
				urlConnection = (HttpsURLConnection) new URL(urls[0]).openConnection();
				// Add cookies to header
				for (Cookie cookie : mCookies) {
					urlConnection.setRequestProperty("Cookie", cookie.getName() + "=" + cookie.getValue());
				}
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
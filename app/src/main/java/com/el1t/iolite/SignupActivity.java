package com.el1t.iolite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.el1t.iolite.item.EighthActivityItem;
import com.el1t.iolite.parser.EighthActivityXmlParser;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import javax.net.ssl.HttpsURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupActivity extends ActionBarActivity implements SignupFragment.OnFragmentInteractionListener
{
	private static final String TAG = "Signup Activity";

	private SignupFragment mSignupFragment;
	private Cookie[] mCookies;
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

		// Check if restoring from previously destroyed instance that matches the BID
		if (savedInstanceState == null || BID != savedInstanceState.getInt("BID")) {
			mTasks = new ArrayList<>();
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
			mCookies = LoginActivity.getCookies(preferences);
		}

		// Use material design toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if(toolbar != null) {
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
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.eighth_signup, menu);
		final MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//		searchView.setOnQueryTextFocusChangeListener(this);
		return super.onCreateOptionsMenu(menu);
	}

	public void refresh() {
		if (!fake) {
			if (mCookies == null) {
				expired();
			} else {
				// Set loading fragment, if necessary
				if (mSignupFragment == null) {
					// Set loading fragment
					getFragmentManager().beginTransaction()
							.add(R.id.container, new LoadingFragment())
							.commit();
				}

				// Retrieve list for bid using cookies
				mTasks.add(new ActivityListRequest().execute("https://iodine.tjhsst.edu/api/eighth/list_activities/" + BID));
			}
		} else {
			// Reload offline list
			postRequest(getList());
		}
	}

	void expired() {
		mCookies = null;
		final Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("expired", true);
		startActivity(intent);
		finish();
	}

	// Try signing up for an activity
	public void submit(EighthActivityItem item) {
		// Perform checks before submission
		// Note that server performs checks as well
		 if (item.isCancelled()) {
			postSubmit(Response.CANCELLED);
		} else if (item.isFull()) {
			postSubmit(Response.CAPACITY);
//		} else if (item.isRestricted()) {
//			postSubmit(Response.RESTRICTED);
		} else if (item.isAttendanceTaken()) {
			postSubmit(Response.ATTENDANCE_TAKEN);
		} else {
			mTasks.add(new SignupRequest(item.getAID(), item.getBID()).execute("https://iodine.tjhsst.edu/api/eighth/signup_activity"));
		}
	}

	// Notify the user after submission
	void postSubmit(Response result) {
		switch(result) {
			case SUCCESS:
				Toast.makeText(getApplicationContext(), "Signed up!", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Sign up success");
				finish();
				break;
			case CAPACITY:
				Toast.makeText(getApplicationContext(), "Capacity exceeded", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Capacity exceeded");
				break;
			case RESTRICTED:
				Toast.makeText(getApplicationContext(), "Activity restricted", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Restricted activity");
				break;
			case CANCELLED:
				Toast.makeText(getApplicationContext(), "Activity cancelled", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Cancelled activity");
				break;
			case PRESIGN:
				Toast.makeText(getApplicationContext(), "Too early!", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Presign activity");
				break;
			case ATTENDANCE_TAKEN:
				Toast.makeText(getApplicationContext(), "Signup closed", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Attendance taken");
				break;
			case FAIL:
				// TODO: Make this error message reflect the actual error
				Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
				Log.w(TAG, "Sign up failure");
				break;
		}
	}

	// Do after getting list of activities
	private void postRequest(ArrayList<EighthActivityItem> result) {
		if (mSignupFragment == null) {
			// Create the content view
			mSignupFragment = new SignupFragment();
			// Add ArrayList to the ListView in BlockFragment
			final Bundle args = new Bundle();
			args.putParcelableArrayList("list", result);
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
	public void favorite(int AID, int BID, boolean status) {
		// Note: the server uses the UID field as the AID in its API
		// Sending the BID is useless, but it is required by the server
		mTasks.add(new ServerRequest().execute("https://iodine.tjhsst.edu/eighth/vcp_schedule/favorite/uid/" + AID + "/bids/" + BID));
		if (status) {
			Toast.makeText(getApplicationContext(), "Favorited", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Unfavorited", Toast.LENGTH_SHORT).show();
		}
		Log.d(TAG, "Favorited AID " + AID);
	}

	// Get a fake list of activities for debugging
	private ArrayList<EighthActivityItem> getList() {
		try {
			return EighthActivityXmlParser.parse(getAssets().open("testActivityList.xml"));
		} catch(Exception e) {
			Log.e(TAG, "Error parsing activity xml", e);
		}
		// Don't die?
		return new ArrayList<>();
	}

	// Retrieve activity list for BID from server using HttpURLConnection
	private class ActivityListRequest extends AsyncTask<String, Void, ArrayList<EighthActivityItem>> {
		private static final String TAG = "Activity List Connection";

		@Override
		protected ArrayList<EighthActivityItem> doInBackground(String... urls) {
			final HttpsURLConnection urlConnection;
			ArrayList<EighthActivityItem> response = null;
			try {
				urlConnection = (HttpsURLConnection) new URL(urls[0]).openConnection();
				// Add cookies to header
				for(Cookie cookie : mCookies) {
					urlConnection.setRequestProperty("Cookie", cookie.getName() + "=" + cookie.getValue());
				}
				// Begin connection
				urlConnection.connect();
				// Parse xml from server
				response = EighthActivityXmlParser.parse(urlConnection.getInputStream());
				// Close connection
				urlConnection.disconnect();
			} catch (XmlPullParserException e) {
				Log.e(TAG, "XML error.", e);
			} catch (IOException e) {
				Log.e(TAG, "Connection error.", e);
			}
			return response;
		}

		@Override
		protected void onPostExecute(ArrayList<EighthActivityItem> result) {
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
			boolean result = false;
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
				HttpResponse response = client.execute(post);

				// Parse response
				result = EighthActivityXmlParser.parseSuccess(response.getEntity().getContent());
			} catch (XmlPullParserException e) {
				Log.e(TAG, "XML error.", e);
			} catch (URISyntaxException e) {
				Log.e(TAG, "URL -> URI error");
			} catch (IOException e) {
				Log.e(TAG, "Connection error.", e);
			}
			return result;
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
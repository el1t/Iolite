package com.el1t.iolite;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupActivity extends ActionBarActivity implements SignupFragment.OnFragmentInteractionListener
{
	private final String TAG = "Signup Activity";

	private SignupFragment mSignupFragment;
	private ArrayList<SerializedCookie> mCookies;

	public enum Response {
		SUCCESS, CAPACITY, RESTRICTED, CANCELLED, PRESIGN, ATTENDANCE_TAKEN, FAIL
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		Intent intent = getIntent();

		// Check if restoring from previously destroyed instance
		if (savedInstanceState == null) {
			// Check if fake information should be used
			if (intent.getBooleanExtra("fake", false)) {
				Log.d(TAG, "Loading fake info");
				// Pretend fake list was received
				postRequest(getList());
			} else {
				// Set loading fragment
				getFragmentManager().beginTransaction()
						.add(R.id.container, new LoadingFragment())
						.commit();
				// Retrieve cookies from previous activity
				mCookies = (ArrayList<SerializedCookie>) intent.getSerializableExtra("cookies");
				// Retrieve list for bid using cookies
				new ActivityListRequest().execute("https://iodine.tjhsst.edu/api/eighth/list_activities/" + intent.getStringExtra("BID"));
			}
		}

		// Use material design toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if(toolbar != null) {
			setSupportActionBar(toolbar);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		inflater.inflate(R.menu.login, menu);
////		super.onCreateOptionsMenu(menu, inflater);
//	}

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
			new SignupRequest(item.getAID(), item.getBid()).execute("https://iodine.tjhsst.edu/api/eighth/signup_activity");
		}
	}

	// Notify the user after submission
	public void postSubmit(Response result) {
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
		// Sort the array
		Collections.sort(result, new DefaultSortComp());
		// Create the content view
		mSignupFragment = new SignupFragment();
		// Add ArrayList to the ListView in BlockFragment
		Bundle args = new Bundle();
		args.putSerializable("list", result);
		mSignupFragment.setArguments(args);
		// Switch to BlockFragment view, remove LoadingFragment
		getFragmentManager().beginTransaction()
				.replace(R.id.container, mSignupFragment)
				.commit();
	}

	// Favorite an activity
	public void favorite(int AID, int BID) {
		// Note: the server uses the UID field as the AID in its API
		// Sending the BID is useless, but it is required by the server
		new ServerRequest().execute("https://iodine.tjhsst.edu/eighth/vcp_schedule/favorite/uid/" + AID + "/bids/" + BID);
	}

	// Get a fake list of activities for debugging
	private ArrayList<EighthActivityItem> getList() {
		try {
			return EighthActivityXmlParser.parse(getAssets().open("testActivityList.xml"));
		} catch(Exception e) {
			Log.e(TAG, "Error parsing activity xml", e);
		}
		// Don't die?
		return new ArrayList<EighthActivityItem>();
	}

	// Retrieve activity list for BID from server using HttpURLConnection
	private class ActivityListRequest extends AsyncTask<String, Void, ArrayList<EighthActivityItem>> {
		private static final String TAG = "Activity List Connection";

		@Override
		protected ArrayList<EighthActivityItem> doInBackground(String... urls) {
			assert(urls.length == 1);
			HttpURLConnection urlConnection;
			ArrayList<EighthActivityItem> response = null;
			try {
				urlConnection = (HttpURLConnection) new URL(urls[0]).openConnection();
				// Add cookies to header
				for(SerializedCookie cookie : mCookies) {
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
			// Add ArrayList to the ListView in SignupFragment
			postRequest(result);
		}
	}

	// Web request for activity signup using HttpClient
	private class SignupRequest extends AsyncTask<String, Void, Boolean> {
		private static final String TAG = "Signup Connection";
		private String AID;
		private String BID;

		public SignupRequest(int AID, int BID) {
			this.AID = Integer.toString(AID);
			this.BID = Integer.toString(BID);
		}

		@Override
		protected Boolean doInBackground(String... urls) {
			assert(urls.length == 1);
			DefaultHttpClient client = new DefaultHttpClient();
			boolean result = false;
			try {
				// Setup client
				client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
				HttpPost post = new HttpPost(new URI(urls[0]));
				// Add cookies
				for(SerializedCookie cookie : mCookies) {
					post.setHeader("Cookie", cookie.getName() + "=" + cookie.getValue());
				}
				// Add parameters
				List<NameValuePair> data = new ArrayList<NameValuePair>(2);
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
			assert(urls.length == 1);
			HttpURLConnection urlConnection;
			try {
				urlConnection = (HttpURLConnection) new URL(urls[0]).openConnection();
				// Add cookies to header
				for (SerializedCookie cookie : mCookies) {
					urlConnection.setRequestProperty("Cookie", cookie.getName() + "=" + cookie.getValue());
				}
				// Begin connection
				urlConnection.connect();
				// Close connection
				urlConnection.disconnect();
				return true;
			} catch (IOException e) {
				Log.e(TAG, "Connection error.", e);
			}
			return false;
		}
	}

	// Sort by favorites, alphabetically
	private class DefaultSortComp implements Comparator<EighthActivityItem>
	{
		@Override
		public int compare(EighthActivityItem e1, EighthActivityItem e2) {
			// Compare by name if both or neither are favorites, or return the favorite
			if (e1.isFavorite()) {
				if (e2.isFavorite())
					return e1.getName().compareToIgnoreCase(e2.getName());
				return -1;
			}
			if (e2.isFavorite())
				return 1;

			// Check for special
			if (!(e1.isSpecial() ^ e2.isSpecial()))
				return e1.getName().compareToIgnoreCase(e2.getName());
			if (e1.isSpecial())
				return -1;
			return 1;
		}
	}
}
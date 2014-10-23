package com.el1t.iocane;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.xmlpull.v1.XmlPullParserException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupActivity extends Activity implements SignupFragment.OnFragmentInteractionListener
{
	private SignupFragment mSignupFragment;
	private String AID;
	private String BID;
	private CookieStore mCookieStore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		Intent intent = getIntent();

		mSignupFragment = new SignupFragment();
		// Check if fake information should be used
		if(intent.getBooleanExtra("fake", false)) {
			Bundle args = new Bundle();
			args.putSerializable("list", getList());
			mSignupFragment.setArguments(args);
		}
		// Set fragment
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, mSignupFragment)
					.commit();
		}

		// Retrieve cookies from previous activity
		mCookieStore = new BasicCookieStore();
		ArrayList<SerializedCookie> cookieArray = (ArrayList<SerializedCookie>) intent.getSerializableExtra("cookies");
		for(SerializedCookie c : cookieArray) {
			mCookieStore.addCookie(c.toCookie());
		}

		// Retrieve list for bid using cookies
		new ActivityListRequest().execute("https://iodine.tjhsst.edu/api/eighth/list_activities/2829");
	}

	// Try signing up for an activity
	public void submit(int AID, int BID) {
		this.BID = Integer.toString(BID);
		this.AID = Integer.toString(AID);
		new SignupRequest().execute("https://iodine.tjhsst.edu/api/eighth/signup_activity");
	}

	// Notify the user after submission
	public void postSubmit(boolean result) {
		if(result) {
			Toast.makeText(getApplicationContext(), "Signed up!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
		}
	}

	// Get a fake list of activities for debugging
	private ArrayList<EighthActivityItem> getList() {
		try {
			return new EighthActivityXmlParser().parse(getAssets().open("testActivityList.xml"));
		} catch(Exception e) {
			Log.e("hi", e.toString());
		}
		return null;
	}

	// AsyncTask to handle contacting the server
	private class ActivityListRequest extends AsyncTask<String, Void, ArrayList<EighthActivityItem>> {
		private static final String TAG = "CONNECTION";

		@Override
		protected ArrayList<EighthActivityItem> doInBackground(String... urls) {
			assert(urls.length == 1);
			HttpURLConnection urlConnection;
			ArrayList<EighthActivityItem> response = null;
			List<Cookie> cookies = mCookieStore.getCookies();
			try {
				urlConnection = (HttpURLConnection) new URL(urls[0]).openConnection();
				// Add cookies to header
				for(Cookie cookie : cookies) {
					System.out.println(cookie.getName() + "=" + cookie.getValue());
					urlConnection.setRequestProperty("Cookie", cookie.getName() + "=" + cookie.getValue());
				}
				// Begin connection
				urlConnection.connect();
				// Parse xml from server
				response = new EighthActivityXmlParser().parse(urlConnection.getInputStream());
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
		protected void onPostExecute(ArrayList<EighthActivityItem> result) {
			super.onPostExecute(result);
			// Add ArrayList to the ListView in SignupFragment
			mSignupFragment.addAll(result);
		}
	}

	// AsyncTask to handle contacting the server
	private class SignupRequest extends AsyncTask<String, Void, Boolean> {
		private static final String TAG = "CONNECTION";

		@Override
		protected Boolean doInBackground(String... urls) {
			assert(urls.length == 1);
			HttpURLConnection urlConnection;
			List<Cookie> cookies = mCookieStore.getCookies();
			boolean response;
			try {
				urlConnection = (HttpURLConnection) new URL(urls[0]).openConnection();
				// Add cookies
				for(Cookie cookie : cookies) {
					System.out.println(cookie.getName() + "=" + cookie.getValue());
					urlConnection.setRequestProperty("Cookie", cookie.getName() + "=" + cookie.getValue());
				}
				// Add parameters
				urlConnection.addRequestProperty("Content-Type", "xml");
				urlConnection.setRequestProperty("aid", AID);
				urlConnection.setRequestProperty("bid", BID);

				urlConnection.connect();
				response = new EighthActivityXmlParser().parseSuccess(urlConnection.getInputStream());
				urlConnection.disconnect();
				return response;
			} catch (XmlPullParserException e) {
				Log.e(TAG, "XML error.", e);
			} catch (Exception e) {
				Log.e(TAG, "Connection error.", e);
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			postSubmit(result);
		}
	}
}
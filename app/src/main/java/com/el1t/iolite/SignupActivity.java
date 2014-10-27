package com.el1t.iolite;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParserException;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupActivity extends Activity implements SignupFragment.OnFragmentInteractionListener
{
	private final String TAG = "Signup Activity";

	private SignupFragment mSignupFragment;
	private String AID;
	private String BID;
	private ArrayList<SerializedCookie> mCookies;

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
			}
		}

		// Retrieve cookies from previous activity
		mCookies = (ArrayList<SerializedCookie>) intent.getSerializableExtra("cookies");

		// Retrieve list for bid using cookies
		if (!intent.getBooleanExtra("fake", false)) {
			new ActivityListRequest().execute("https://iodine.tjhsst.edu/api/eighth/list_activities/" + intent.getStringExtra("BID"));
		}
	}

	// Try signing up for an activity
	public void submit(int AID, int BID) {
		this.BID = Integer.toString(BID);
		this.AID = Integer.toString(AID);
		new SignupRequest().execute("https://iodine.tjhsst.edu/api/eighth/signup_activity");
	}

	// Notify the user after submission
	protected void postSubmit(boolean result) {
		if(result) {
			Toast.makeText(getApplicationContext(), "Signed up!", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Sign up success");
		} else {
			Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
			Log.w(TAG, "Sign up failure");
		}
	}

	protected void postRequest(ArrayList<EighthActivityItem> result) {
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

	// Get a fake list of activities for debugging
	private ArrayList<EighthActivityItem> getList() {
		try {
			return EighthActivityXmlParser.parse(getAssets().open("testActivityList.xml"));
		} catch(Exception e) {
			Log.e(TAG, "Error parsing activity xml", e);
		}
		return null;
	}

	// Retrieve activity list for BID from server
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
			} catch (Exception e) {
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

		@Override
		protected Boolean doInBackground(String... urls) {
			assert(urls.length == 1);
			DefaultHttpClient client = new DefaultHttpClient();
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
				System.out.println(AID + BID);
				data.add(new BasicNameValuePair("aid", AID));
				data.add(new BasicNameValuePair("bid", BID));
				post.setEntity(new UrlEncodedFormEntity(data));

				// Send request
				HttpResponse response = client.execute(post);

				// Parse response
				HttpEntity entity = response.getEntity();
				return EighthActivityXmlParser.parseSuccess(entity.getContent());
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

// Sort by favorites, alphabetically
class DefaultSortComp implements Comparator<EighthActivityItem>
{
	@Override
	public int compare(EighthActivityItem e1, EighthActivityItem e2) {
		// Compare by name if both or neither are favorites, or return the favorite
		if (e1.isFavorite()) {
			if (e2.isFavorite()) {
				return e1.getName().compareTo(e2.getName());
			}
			return -1;
		}
		if (e2.isFavorite()) {
			return 1;
		} else {
			return e1.getName().compareTo(e2.getName());
		}
	}
}
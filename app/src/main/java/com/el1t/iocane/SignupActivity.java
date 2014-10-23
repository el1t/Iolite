package com.el1t.iocane;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
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

		mSignupFragment = new SignupFragment();
//		Bundle args = new Bundle();
//		args.putSerializable("list", getList());
//		mSignupFragment.setArguments(args);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, mSignupFragment)
					.commit();
		}

		Intent intent = getIntent();
		mCookieStore = new BasicCookieStore();
		ArrayList<SerializedCookie> cookieArray = (ArrayList<SerializedCookie>) intent.getSerializableExtra("cookies");
		for(SerializedCookie c : cookieArray) {
			mCookieStore.addCookie(c.toCookie());
		}
		new WebConnection().execute("https://iodine.tjhsst.edu/api/eighth/list_activities/2829");
	}

	public void submit(int AID, int BID) {
		this.BID = Integer.toString(BID);
		this.AID = Integer.toString(AID);
		new SignupRequest().execute("https://iodine.tjhsst.edu/api/eighth/signup_activity");
	}

	public void postSubmit(boolean result) {
		if(result) {
			Toast.makeText(getApplicationContext(), "Signed up!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
		}
	}

	private ArrayList<EighthActivityItem> getList() {
		try {
			return new EighthActivityXmlParser().parse(getAssets().open("testActivityList.xml"));
		} catch(Exception e) {
			Log.e("hi", e.toString());
		}
		return null;
	}

	// AsyncTask to handle contacting the server
	private class WebConnection extends AsyncTask<String, Void, ArrayList<EighthActivityItem>> {
		private static final String TAG = "CONNECTION";

		@Override
		protected ArrayList<EighthActivityItem> doInBackground(String... urls) {
			assert(urls.length == 1);
			HttpURLConnection urlConnection;
			ArrayList<EighthActivityItem> response = null;
			List<Cookie> cookies = mCookieStore.getCookies();
			try {
				urlConnection = (HttpURLConnection) new URL(urls[0]).openConnection();
				for(Cookie cookie : cookies) {
					System.out.println(cookie.getName() + "=" + cookie.getValue());
					urlConnection.setRequestProperty("Cookie", cookie.getName() + "=" + cookie.getValue());
				}
				urlConnection.connect();
				response = new EighthActivityXmlParser().parse(urlConnection.getInputStream());
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
				urlConnection.setRequestProperty("bid", BID);
				urlConnection.setRequestProperty("aid", AID);

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
package com.el1t.iocane;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

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

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
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
		Bundle args = new Bundle();
		args.putSerializable("list", getList());
		mSignupFragment.setArguments(args);

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

	}

	public void submit(int AID, int BID) {

	}

	private ArrayList<EighthActivityItem> getList() {
		try {
			return new EighthActivityXmlParser().parse(getApplicationContext().getAssets().open("testActivityList.xml"));
		} catch(Exception e) {
			Log.e("hi", e.toString());
		}
		return null;
	}

	// AsyncTask to handle contacting the server
	private class WebConnection extends AsyncTask<String, Void, CookieStore> {
		private static final String TAG = "CONNECTION";

		@Override
		protected CookieStore doInBackground(String... urls) {
			System.out.println("sending");
			assert(urls.length == 1);
			DefaultHttpClient client = new DefaultHttpClient();
			try {

				HttpPost post = new HttpPost(new URI(urls[0]));

				HttpResponse response = client.execute(post);
			} catch (Exception e) {
				Log.e(TAG, "Connection error.", e);
			}
			return client.getCookieStore();
		}

		@Override
		protected void onPostExecute(CookieStore result) {
			super.onPostExecute(result);
			List<Cookie> cookies = result.getCookies();

		}
	}
}
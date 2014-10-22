package com.el1t.iocane;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends Activity implements LoginFragment.OnFragmentInteractionListener
{
	private LoginFragment mLoginFragment;
	private String login_username;
	private String login_password;
	private CookieStore mCookieStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
	    mLoginFragment = new LoginFragment();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mLoginFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	public void submit(String username, String pass) {
		login_username = username;
		login_password = pass;
		new WebConnection().execute("https://iodine.tjhsst.edu");
	}

	public void next() {
		Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
		ArrayList<SerializedCookie> list = new ArrayList<SerializedCookie>();
		for(Cookie c : mCookieStore.getCookies()) {
			list.add(new SerializedCookie(c));
		}
		Intent intent = new Intent(this, SignupActivity.class);
		intent.putExtra("cookies", list);
		startActivity(intent);
	}

	public void failed() {
		Toast.makeText(getApplicationContext(), "Invalid Login Credentials", Toast.LENGTH_SHORT).show();
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
				client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
				HttpPost post = new HttpPost(new URI(urls[0]));
				List<NameValuePair> data = new ArrayList<NameValuePair>(2);
				data.add(new BasicNameValuePair("login_username", login_username));
				data.add(new BasicNameValuePair("login_password", login_password));
				post.setEntity(new UrlEncodedFormEntity(data));

				// Create local HTTP context
				HttpContext context = new BasicHttpContext();
				// Bind custom cookie store to the local context
				context.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);
			} catch (Exception e) {
				Log.e(TAG, "Connection error.", e);
			}
			return client.getCookieStore();
		}

		@Override
		protected void onPostExecute(CookieStore result) {
			super.onPostExecute(result);
			List<Cookie> cookies = result.getCookies();
			System.out.println(cookies);
			if (cookies.size() >= 2) {
				mCookieStore = result;
				next();
			} else {
				failed();
			}
		}
	}
}

package com.el1t.iocane;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends Activity implements LoginFragment.OnFragmentInteractionListener
{
	private final String FAKE_LOGIN = "fake";
	private final String TAG = "Login Activity";

	private LoginFragment mLoginFragment;
	private String login_username;
	private String login_password;
	private ProgressDialog mProgressDialog;

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

	// Submit the login request
	public void submit(String username, String pass) {
		login_username = username.trim();
		login_password = pass;
		if (isFakeLogin()) {
			postSubmit(new ArrayList<Cookie>());
		} else {
			new LoginRequest().execute("https://iodine.tjhsst.edu");
		}
	}

	// Do after submission
	public void postSubmit(List<Cookie> cookies) {
		if (isFakeLogin()) {
			Toast.makeText(getApplicationContext(), "Loading faked data", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
		}
		ArrayList<SerializedCookie> list = new ArrayList<SerializedCookie>();
		for(Cookie c : cookies) {
			list.add(new SerializedCookie(c));
		}
		Intent intent = new Intent(this, BlockActivity.class);
		intent.putExtra("cookies", list);
		intent.putExtra("fake", isFakeLogin());
		startActivity(intent);
	}

	public void failed() {
		Toast.makeText(getApplicationContext(), "Invalid Login Credentials", Toast.LENGTH_SHORT).show();
		mLoginFragment.clearPassword();
		Log.d(TAG, "Login failed");
	}

	// Checks if fake offline cache should be used
	private boolean isFakeLogin() {
		return login_username.toLowerCase().equals(FAKE_LOGIN);
	}

	// AsyncTask to handle login POST request
	private class LoginRequest extends AsyncTask<String, Void, CookieStore> {
		private static final String TAG = "Login Connection";
		private HttpPost mPost;

		@Override
		protected CookieStore doInBackground(String... urls) {
			Log.d(TAG, "Logging in...");
			assert(urls.length == 1);
			if (mProgressDialog == null || !mProgressDialog.isShowing()) {
				mProgressDialog = new ProgressDialog(getApplicationContext());
				mProgressDialog.setTitle("Logging in");
				mProgressDialog.setMessage("Please wait...");
				mProgressDialog.setCancelable(true);
				mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
					@Override
					public void onCancel(DialogInterface dialog) {
						cancel(true);
						finish();
					}
				});
				mProgressDialog.show();
			}
			DefaultHttpClient client = new DefaultHttpClient();
			try {
				client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);

				// Create local HTTP context
				HttpContext context = new BasicHttpContext();
				CookieStore temp = new BasicCookieStore();
				// Bind custom cookie store to the local context
				context.setAttribute(ClientContext.COOKIE_STORE, temp);

				mPost = new HttpPost(new URI(urls[0]));
				List<NameValuePair> data = new ArrayList<NameValuePair>(2);
				data.add(new BasicNameValuePair("login_username", login_username));
				data.add(new BasicNameValuePair("login_password", login_password));
				mPost.setEntity(new UrlEncodedFormEntity(data));

				client.execute(mPost);
			} catch (IOException e) {
				Log.e(TAG, "Connection error, aborted?", e);
			} catch (Exception e) {
				Log.e(TAG, "URL error.", e);
			}
			return client.getCookieStore();
		}

		@Override
		protected void onPostExecute(CookieStore result) {
			super.onPostExecute(result);
			List<Cookie> cookies = result.getCookies();
			mProgressDialog.dismiss();
			if (cookies.size() >= 2) {
				postSubmit(cookies);
			} else {
				failed();
			}
		}

		@Override
		protected void onCancelled() {
			Log.d(TAG, "Connection aborted!");
			mPost.abort();
		}
	}
}

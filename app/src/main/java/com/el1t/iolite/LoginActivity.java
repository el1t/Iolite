package com.el1t.iolite;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.el1t.iolite.item.User;
import com.el1t.iolite.parser.StudentInfoXmlParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// Login request -> Authentication (Grab info) -> Start activity
public class LoginActivity extends ActionBarActivity implements LoginFragment.OnFragmentInteractionListener
{
	public static final String FAKE_LOGIN = "fake";
	public static final String PREFS_NAME = "LOGIN";
	private static final String TAG = "Login Activity";
	private static final String[] COOKIE_NAMES = {"IODINE_PASS_VECTOR", "PHPSESSID"};


	private LoginFragment mLoginFragment;
	private String login_username;
	private String login_password;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		final SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		final Cookie[] cookies = getCookies(preferences);
		final boolean remember = preferences.getBoolean("remember", false);
		final String username = preferences.getString("username", "");
        mLoginFragment = new LoginFragment();
		if (savedInstanceState == null) {
			// Restore saved username
			final Bundle args = new Bundle();
			args.putBoolean("remember", remember);
			args.putString("username", username);
			mLoginFragment.setArguments(args);
			getFragmentManager().beginTransaction()
					.add(R.id.container, mLoginFragment)
					.commit();
		} else {
			mLoginFragment.setUsername(username);
		}

		// This is identical to checkAuthentication except for intent checking
		if (cookies != null) {
			if (getIntent().getBooleanExtra("logout", false)) {
				// Send logout request
				logout(cookies);
			} else {
				// Check authentication
				new Authentication(cookies).execute("https://iodine.tjhsst.edu/api/studentdirectory/info");
			}
		} else if (remember) {
			submit(username, preferences.getString("password", null));
		}

		// Use material design toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if(toolbar != null) {
			setSupportActionBar(toolbar);
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
		switch(item.getItemId()) {
			case R.id.action_settings:
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void storeCookies(List<Cookie> cookies) {
		final SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(COOKIE_NAMES[0])) {
				editor.putString("COOKIE_" + COOKIE_NAMES[0], cookie.getValue());
			} else if (cookie.getName().equals(COOKIE_NAMES[1])) {
				editor.putString("COOKIE_" + COOKIE_NAMES[1], cookie.getValue());
			}
		}
		editor.commit();
	}

	public static Cookie[] getCookies(SharedPreferences preferences) {
		final Cookie[] cookies = new Cookie[COOKIE_NAMES.length];
		for (int i = 0; i < cookies.length; i++) {
			cookies[i] = new BasicClientCookie(COOKIE_NAMES[i], preferences.getString("COOKIE_" + COOKIE_NAMES[i], null));
			if (cookies[i].getValue() == null) {
				return null;
			}
		}
		return cookies;
	}

	private void clearCookies() {
		getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
				.clear()
				.commit();
	}

	private void checkAuthentication() {
		final Cookie[] cookies = getCookies(getSharedPreferences(PREFS_NAME, MODE_PRIVATE));
		if (cookies != null) {
			// Check authentication
			new Authentication(cookies).execute("https://iodine.tjhsst.edu/api/studentdirectory/info");
		}
	}

	// Submit the login request
	public void submit(String username, String pass) {
		if (username == null || pass == null) {
			Log.d(TAG, "Null username or password");
			return;
		}
		login_username = username.trim();
		login_password = pass;
		if (!mLoginFragment.isChecked()) {
			getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
					.putBoolean("remember", false)
					.apply();
		}
		if (isFakeLogin()) {
			postRequest(getList(), true);
		} else if (login_username.isEmpty()) {
			Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
		} else if (login_password.isEmpty()) {
			Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
		} else {
			new LoginRequest().execute("https://iodine.tjhsst.edu/api");
		}
	}

	// Do after authentication request
	void postRequest(User user, boolean fake) {
		if (user != null) {
			if (fake) {
				Toast.makeText(getApplicationContext(), "Loading faked data", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
				final SharedPreferences.Editor preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
				if (login_username != null) {
					preferences.putString("username", login_username);
				}
				if (mLoginFragment.isChecked()) {
					preferences.putBoolean("remember", true);
					if (login_password != null) {
						preferences.putString("password", login_password);
					}
				} else {
					preferences.putBoolean("remember", false);
				}
				preferences.apply();
			}
			clearPassword();
			final Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra("fake", fake);
			intent.putExtra("user", user);
			startActivity(intent);
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "Session Expired", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Session expired");
			clearCookies();
			final SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
			if (preferences.getBoolean("remember", false)) {
				submit(preferences.getString("username", null), preferences.getString("password", null));
			}
		}
	}

	private void logout(Cookie[] cookies) {
		new LogoutRequest(cookies).execute("https://iodine.tjhsst.edu/logout");
		getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
				.remove("password")
				.apply();
		Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
	}

	void failed(boolean isAborted) {
		if (!isAborted) {
			Toast.makeText(getApplicationContext(), "Invalid Login Credentials", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Login failed");
		}
		clearPassword();
	}

	private void clearPassword() {
		login_password = "";
		mLoginFragment.clearPassword();
	}

	public void displayWarning() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_login_disclaimer_title)
				.setMessage(R.string.dialog_login_disclaimer_body)
				.setPositiveButton(R.string.dialog_continue, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// Do nothing
					}
				})
				.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						mLoginFragment.setChecked(false);
					}
				})
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialogInterface) {
						mLoginFragment.setChecked(false);
					}
				})
				.create()
				.show();
	}

	// Checks if fake offline cache should be used
	private boolean isFakeLogin() {
		return login_username != null && login_username.toLowerCase().equals(FAKE_LOGIN);
	}

	// Get a fake list of blocks for debugging
	private User getList() {
		try {
			return StudentInfoXmlParser.parse(getAssets().open("testStudentInfo.xml"));
		} catch(Exception e) {
			Log.e(TAG, "Error parsing block xml", e);
		}
		// Don't die?
		return new User();
	}

	// Login request using HttpPost
	private class LoginRequest extends AsyncTask<String, Void, List<Cookie>> {
		private static final String TAG = "Login Connection";
		private HttpPost mPost;

		@Override
		protected void onPreExecute() {
			if (mProgressDialog == null || !mProgressDialog.isShowing()) {
				mProgressDialog = new ProgressDialog(LoginActivity.this);
				mProgressDialog.setMessage("Logging in");
				mProgressDialog.setCancelable(true);
				mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						Log.d(TAG, "Connection aborted!");
						// Abort connection on background thread
						new Thread(new Runnable() {
							@Override
							public void run() {
								mPost.abort();
							}
						}).start();
						cancel(true);
					}
				});
				mProgressDialog.show();
			}
		}

		@Override
		protected List<Cookie> doInBackground(String... urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			try {
				client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);

				// Create local HTTP context
				HttpContext context = new BasicHttpContext();
				CookieStore temp = new BasicCookieStore();
				// Bind custom cookie store to the local context
				context.setAttribute(ClientContext.COOKIE_STORE, temp);

				mPost = new HttpPost(new URI(urls[0]));
				List<NameValuePair> data = new ArrayList<>(2);
				data.add(new BasicNameValuePair("login_username", login_username));
				data.add(new BasicNameValuePair("login_password", login_password));
				mPost.setEntity(new UrlEncodedFormEntity(data));

				// Check for the message "login failed"
				// Currently, intranet does not return a useful response if login is successful
				final boolean result = client.execute(mPost, new ResponseHandler<Boolean>() {
					@Override
					public Boolean handleResponse(HttpResponse response) throws IOException {
						final HttpEntity entity = response.getEntity();
						return entity != null && !EntityUtils.toString(entity).contains("Login failed");
					}
				});
				return result ? client.getCookieStore().getCookies() : null;
			} catch (IOException e) {
				if (!mPost.isAborted()) {
					Log.e(TAG, "Connection error.", e);
				}
			} catch (Exception e) {
				Log.e(TAG, "URL error.", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Cookie> result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if (result == null) {
				failed(false);
			} else {
				storeCookies(result);
				checkAuthentication();
			}
		}

		@Override
		protected void onCancelled() {
			failed(true);
		}
	}

	// Test if cookies are functional
	private class Authentication extends AsyncTask<String, Void, User> {
		private static final String TAG = "Authentication Connection";
		private HttpURLConnection mConnection;
		private final Cookie[] mCookies;

		public Authentication (Cookie[] cookies) {
			mCookies = cookies;
		}

		@Override
		protected void onPreExecute() {
			if (mProgressDialog == null || !mProgressDialog.isShowing()) {
				mProgressDialog = new ProgressDialog(LoginActivity.this);
				mProgressDialog.setMessage("Authenticating");
				mProgressDialog.setCancelable(true);
				mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						Log.d(TAG, "Connection aborted!");
						// Abort connection on background thread
						new Thread(new Runnable() {
							@Override
							public void run() {
								mConnection.disconnect();
							}
						}).start();
						cancel(true);
					}
				});
				mProgressDialog.show();
			}
		}

		@Override
		protected User doInBackground(String... urls) {
			User response = null;
			try {
				mConnection = (HttpURLConnection) new URL(urls[0]).openConnection();
				// Add cookies to header
				for (Cookie cookie : mCookies) {
					mConnection.setRequestProperty("Cookie", cookie.getName() + "=" + cookie.getValue());
				}
				// Begin connection
				mConnection.connect();
				// Parse xml from server
				response = StudentInfoXmlParser.parse(mConnection.getInputStream());
				// Close connection
				mConnection.disconnect();
			} catch(IOException e) {
				Log.e(TAG, "Parse error.", e);
			} catch (Exception e) {
				Log.e(TAG, "Connection error.", e);
			}
			return response;
		}

		@Override
		protected void onPostExecute(User result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			postRequest(result, false);
		}
	}

	// Logs the user out
	private class LogoutRequest extends AsyncTask<String, Void, Void> {
		private static final String TAG = "Logout Request";
		private final Cookie[] mCookies;

		public LogoutRequest (Cookie[] cookies) {
			mCookies = cookies;
		}

		@Override
		protected Void doInBackground(String... urls) {
			HttpURLConnection urlConnection;
			try {
				urlConnection = (HttpURLConnection) new URL(urls[0]).openConnection();
				// Add cookies to header
				for(Cookie cookie : mCookies) {
					urlConnection.setRequestProperty("Cookie", cookie.getName() + "=" + cookie.getValue());
				}
				// Begin connection
				urlConnection.connect();
				// Get response
				urlConnection.getInputStream();
				// Close connection
				urlConnection.disconnect();
			} catch (Exception e) {
				Log.e(TAG, "Connection error.", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			clearCookies();
		}
	}
}
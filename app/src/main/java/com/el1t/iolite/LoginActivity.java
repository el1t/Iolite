package com.el1t.iolite;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.el1t.iolite.item.User;
import com.el1t.iolite.parser.ProfileJsonParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

// Login request -> Authentication (Grab info) -> Start activity
public class LoginActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener
{
	public static final String FAKE_LOGIN = "fake";
	public static final String PREFS_NAME = "LOGIN";
	private static final String TAG = "Login Activity";

	static java.net.CookieManager mCookieManager = new java.net.CookieManager();
	private LoginFragment mLoginFragment;
	private ProgressDialog mProgressDialog;
	private String login_username;
	private String login_password;
	private String mAuthKey;
	private int attempt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		final SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		final boolean remember = preferences.getBoolean("remember", false);
		final String username = preferences.getString("username", null);
		if (savedInstanceState == null) {
			// Restore saved username
			mLoginFragment = new LoginFragment();
			final Bundle args = new Bundle();
			args.putBoolean("remember", remember);
			args.putString("username", username);
			mLoginFragment.setArguments(args);
			getFragmentManager().beginTransaction()
					.add(R.id.container, mLoginFragment)
					.commit();
		} else {
			mLoginFragment = (LoginFragment) getFragmentManager().getFragment(savedInstanceState, "loginFragment");
		}

		attempt = 0;
		// This is identical to checkAuthentication except for intent checking
		if ((mAuthKey = Utils.getAuthKey(preferences)) != null) {
			if (getIntent().getBooleanExtra("logout", false)) {
				// Send logout request
				logout();
			} else if (getIntent().getBooleanExtra("expired", false)) {
				postRequest(null, isFakeLogin());
			} else {
				// Check authentication
				new Authentication().execute("https://ion.tjhsst.edu/api/profile?format=json");
			}
		} else if (remember) {
			submit(username, preferences.getString("password", null));
		}

		// Use material design toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		getFragmentManager().putFragment(savedInstanceState, "loginFragment", mLoginFragment);
	}

	private static void storeCookies(List<String> cookies) {
		for (String cookie : cookies) {
			mCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
		}
	}

	public static String getCookies() {
		if(mCookieManager.getCookieStore().getCookies().size() > 0) {
			return TextUtils.join(",", mCookieManager.getCookieStore().getCookies());
		}
		return "";
	}

	private void clearCookies() {
		mCookieManager.getCookieStore().removeAll();
	}

	private void checkAuthentication() {
		new Authentication().execute("https://ion.tjhsst.edu/api/profile?format=json");
	}

	// Submit the login request
	public void submit(String username, String pass) {
		// Hide soft keyboard
		final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		final View view = getCurrentFocus();

		if (view != null && imm.isAcceptingText()) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}

		if (username == null || pass == null) {
			Log.d(TAG, "Null Username or Password");
			return;
		}
		login_username = username.trim();
		login_password = pass;
		// Check that the fragment is instantiated, since submit can be called before that
		if (mLoginFragment.isCreated() && !mLoginFragment.isChecked()) {
			getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
					.remove("remember")
					.apply();
		}
		if (isFakeLogin()) {
			postRequest(getList(), true);
		} else if (login_username.isEmpty()) {
			Snackbar.make(findViewById(R.id.container), "Username empty", Snackbar.LENGTH_SHORT).show();
		} else if (login_password.isEmpty()) {
			Snackbar.make(findViewById(R.id.container), "Password empty", Snackbar.LENGTH_SHORT).show();
		} else {
			new LoginRequest().execute("https://ion.tjhsst.edu/api");
		}
	}

	// Do after authentication request
	void postRequest(User user, boolean fake) {
		if (user != null) {
			if (fake) {
				Log.d(TAG, "Loading test data");
			} else {
				final SharedPreferences.Editor preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
				if (login_username != null) {
					preferences.putString("username", login_username);
				}
				if (login_password != null) {
					preferences.putString("password", login_password);
				}
				preferences.putBoolean("remember", mLoginFragment.isChecked());
				preferences.apply();
			}
			attempt = 0;
			final Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra("fake", fake);
			intent.putExtra("user", user);
			startActivity(intent);
			finish();
		} else {
			Snackbar.make(findViewById(R.id.container), "Session Expired", Snackbar.LENGTH_SHORT).show();
			Log.d(TAG, "Session Expired");
			clearCookies();
			final SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
			if (preferences.getBoolean("remember", false) && attempt < 1) {
				submit(preferences.getString("username", null), preferences.getString("password", null));
			}
		}
	}

	private void logout() {
		getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
				.remove("password")
				.apply();
		login_password = null;
		mAuthKey = null;
		Snackbar.make(findViewById(R.id.container), "Logged out", Snackbar.LENGTH_SHORT).show();
	}

//	private void clearPassword() {
//		login_password = "";
//		mLoginFragment.clearPassword();
//	}

	// Checks if fake offline cache should be used
	private boolean isFakeLogin() {
		return login_username != null && login_username.toLowerCase().equals(FAKE_LOGIN);
	}

	// Get a test info for debugging
	private User getList() {
		try {
			return ProfileJsonParser.parse(getAssets().open("testProfile.json"));
		} catch(Exception e) {
			Log.e(TAG, "Error Parsing Block XML", e);
		}
		return null;
	}

	// Login request using HttpPost
	private class LoginRequest extends AsyncTask<String, Void, List<String>> {
		private static final String TAG = "Login Connection";
		private HttpsURLConnection mConnection;

		@Override
		protected void onPreExecute() {
			attempt++;
			if (mProgressDialog == null || !mProgressDialog.isShowing()) {
				mProgressDialog = new ProgressDialog(LoginActivity.this);
				mProgressDialog.setMessage("Logging In");
				mProgressDialog.setCancelable(true);
				mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						Log.d(TAG, "Connection cancelled!");
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
		protected List<String> doInBackground(String... urls) {
			try {
				mConnection = (HttpsURLConnection) new URL(urls[0]).openConnection();
				mAuthKey = "Basic " + Base64.encodeToString((login_username + ":" + login_password).getBytes(), Base64.NO_WRAP);
				mConnection.setRequestProperty("Authorization", mAuthKey);
				mConnection.setUseCaches(false);
				mConnection.connect();
				return mConnection.getHeaderFields().get("Set-Cookie");
			} catch (FileNotFoundException e) {
				try {
					if (mConnection.getResponseCode() == 401) {
						Snackbar.make(findViewById(R.id.container), "Invalid login credentials", Snackbar.LENGTH_SHORT).show();
					}
				} catch (IOException err) {
					Log.e(TAG, "Cannot read response code", err);
				}
			} catch (IOException e) {
				if (isCancelled()) {
					Snackbar.make(findViewById(R.id.container), "Cancelled", Snackbar.LENGTH_SHORT)
							.setAction("Retry", new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									submit(login_username, login_password);
								}
							}).show();
				} else {
					Snackbar.make(findViewById(R.id.container), "Cannot connect to server", Snackbar.LENGTH_SHORT)
							.setAction("Retry", new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									submit(login_username, login_password);
								}
							}).show();
				}
			} catch (Exception e) {
				Log.e(TAG, "Login error", e);
			} finally {
				mConnection.disconnect();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if (result != null) {
				storeCookies(result);
				checkAuthentication();
			}
		}

		@Override
		protected void onCancelled() {
			Log.d(TAG, "Login cancelled");
		}
	}

	// Test if cookies are functional
	private class Authentication extends AsyncTask<String, Void, User> {
		private static final String TAG = "Authentication";
		private HttpsURLConnection mConnection;

		@Override
		protected void onPreExecute() {
			if (mProgressDialog == null || !mProgressDialog.isShowing()) {
				mProgressDialog = new ProgressDialog(LoginActivity.this);
				mProgressDialog.setMessage("Authenticating");
				mProgressDialog.setCancelable(true);
				mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						Log.d(TAG, "Connection Aborted!");
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
			User response;
			try {
				mConnection = (HttpsURLConnection) new URL(urls[0]).openConnection();
				// Attach authentication
				mConnection.setRequestProperty("Authorization", mAuthKey);
				mConnection.setUseCaches(false);
				// Begin connection
				mConnection.connect();
				// Parse xml from server
				response = ProfileJsonParser.parse(mConnection.getInputStream());
				// Close connection
				mConnection.disconnect();
				return response;
			} catch(IOException e) {
				Log.e(TAG, "Parse error.", e);
			} catch (Exception e) {
				Snackbar.make(findViewById(R.id.container), "Connection error", Snackbar.LENGTH_SHORT).show();
				Log.e(TAG, "Connection error.", e);
			}
			try {
				Log.d(TAG, "Response: " + mConnection.getResponseCode());
			} catch (Exception e) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(User result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();

			postRequest(result, false);
		}
	}
}
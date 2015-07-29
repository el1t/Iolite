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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.el1t.iolite.item.User;
import com.el1t.iolite.parser.ProfileJsonParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

// Login request -> Authentication (Grab info) -> Start activity
public class LoginActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener
{
	public static final String FAKE_LOGIN = "fake";
	public static final String PREFS_NAME = "LOGIN";
	private static final String TAG = "Login Activity";

	private LoginFragment mLoginFragment;
	private ProgressDialog mProgressDialog;
	private String login_username;
	private String login_password;
	private String mAuthKey;

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

		// This is identical to checkAuthentication except for intent checking
		if ((mAuthKey = Utils.getAuthKey(preferences)) != null) {
			if (getIntent().getBooleanExtra("logout", false)) {
				// Send logout request
				logout();
			} else {
				// Check authentication
				new Authentication().execute();
			}
		} else {
			// Show keyboard
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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

	private void checkAuthentication() {
		new Authentication().execute();
	}

	// Submit the login request
	public void submit(String username, String pass) {
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
		if (login_username.toLowerCase().equals(FAKE_LOGIN)) {
			postRequest(getList(), true);
		} else if (login_username.isEmpty()) {
			if (login_password.isEmpty()) {
				mLoginFragment.showError(LoginFragment.ErrorType.EMPTY_BOTH);
			} else {
				mLoginFragment.showError(LoginFragment.ErrorType.EMPTY_USERNAME);
			}
		} else if (login_password.isEmpty()) {
			mLoginFragment.showError(LoginFragment.ErrorType.EMPTY_PASSWORD);
		} else {
			new LoginRequest().execute();
		}
	}

	// Do after authentication request
	void postRequest(User user, boolean fake) {
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
		hideKeyboard();
		final Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("fake", fake);
		intent.putExtra("user", user);
		startActivity(intent);
		finish();
	}

	private void logout() {
		getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
				.remove("password")
				.apply();
		login_password = null;
		mAuthKey = null;
		Snackbar.make(findViewById(R.id.container), "Logged out", Snackbar.LENGTH_SHORT).show();
	}

	private void hideKeyboard() {
		final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		final View view = getCurrentFocus();
		if (view != null && imm.isAcceptingText()) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
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
	private class LoginRequest extends AsyncTask<Void, Void, Boolean> {
		private static final String TAG = "Login Connection";
		private static final String URL = "https://ion.tjhsst.edu/api";
		private HttpsURLConnection mConnection;

		@Override
		protected void onPreExecute() {
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
		protected Boolean doInBackground(Void... params) {
			try {
				mConnection = (HttpsURLConnection) new URL(URL).openConnection();
				mAuthKey = "Basic " + Base64.encodeToString((login_username + ":" + login_password).getBytes(), Base64.NO_WRAP);
				mConnection.setRequestProperty("Authorization", mAuthKey);
				mConnection.setUseCaches(false);
				mConnection.connect();
				return true;
			} catch (FileNotFoundException e) {
				try {
					if (mConnection.getResponseCode() == 401) {
						mLoginFragment.showError(LoginFragment.ErrorType.INVALID);
					}
				} catch (IOException err) {
					Log.e(TAG, "Cannot read response code", err);
				}
			} catch (IOException e) {
				hideKeyboard();
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
				Snackbar.make(findViewById(R.id.container), "Connection error", Snackbar.LENGTH_SHORT).show();
				Log.e(TAG, "Login error", e);
			} finally {
				mConnection.disconnect();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if (result) {
				checkAuthentication();
			}
		}

		@Override
		protected void onCancelled() {
			Log.d(TAG, "Login cancelled");
		}
	}

	// Load student profile data
	private class Authentication extends AsyncTask<Void, Void, User> {
		private static final String TAG = "Authentication";
		private static final String URL = "https://ion.tjhsst.edu/api/profile?format=json";
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
		protected User doInBackground(Void... params) {
			final User response;
			try {
				mConnection = (HttpsURLConnection) new URL(URL).openConnection();
				// Attach authentication
				mConnection.setRequestProperty("Authorization", mAuthKey);
				mConnection.setUseCaches(false);
				// Begin connection
				mConnection.connect();
				// Parse JSON from server
				response = ProfileJsonParser.parse(mConnection.getInputStream());
				// Close connection
				mConnection.disconnect();
				return response;
			} catch(IOException e) {
				hideKeyboard();
				if (isCancelled()) {
					Snackbar.make(findViewById(R.id.container), "Cancelled", Snackbar.LENGTH_SHORT)
							.setAction("Retry", new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									checkAuthentication();
								}
							}).show();
				} else {
					Snackbar.make(findViewById(R.id.container), "Cannot connect to server", Snackbar.LENGTH_SHORT)
							.setAction("Retry", new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									checkAuthentication();
								}
							}).show();
				}
			} catch (Exception e) {
				Snackbar.make(findViewById(R.id.container), "Connection error", Snackbar.LENGTH_SHORT).show();
				Log.e(TAG, "Connection error.", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(User result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if (result != null) {
				postRequest(result, false);
			}
		}
	}
}
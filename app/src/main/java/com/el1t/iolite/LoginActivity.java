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

import com.el1t.iolite.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

// Login request -> Check response -> Start HomeActivity
public class LoginActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener
{
	public static final String FAKE_LOGIN = "fake";
	public static final String PREFS_NAME = "LOGIN";
	private static final String TAG = "Login Activity";

	private LoginFragment mLoginFragment;
	private ProgressDialog mProgressDialog;
	private String mUsername;
	private String mPassword;
	private String mAuthKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		final SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		final boolean remember = preferences.getBoolean("remember", false);
		mUsername = preferences.getString("username", null);
		if (savedInstanceState == null) {
			mLoginFragment = LoginFragment.newInstance(remember, mUsername);
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
			} else if (remember) {
				startHome();
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

	// Submit the login request
	public void submit(String username, String pass) {
		if (username == null || pass == null) {
			Log.d(TAG, "Null username or password");
			return;
		}
		mUsername = username;
		mPassword = pass;
		// Check that the fragment is instantiated, since submit can be called before that
		if (!mLoginFragment.isChecked()) {
			getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
					.remove("remember")
					.apply();
		}
		if (mUsername.toLowerCase().equals(FAKE_LOGIN)) {
			postRequest();
		} else if (mUsername.isEmpty()) {
			if (mPassword.isEmpty()) {
				mLoginFragment.showError(LoginFragment.ErrorType.EMPTY_BOTH);
			} else {
				mLoginFragment.showError(LoginFragment.ErrorType.EMPTY_USERNAME);
			}
		} else if (mPassword.isEmpty()) {
			mLoginFragment.showError(LoginFragment.ErrorType.EMPTY_PASSWORD);
		} else {
			new LoginRequest().execute();
		}
	}

	// Do after login request
	void postRequest() {
		final SharedPreferences.Editor preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
		if (mUsername != null) {
			preferences.putString("username", mUsername);
		}
		if (mPassword != null) {
			preferences.putString("password", mPassword);
		}
		preferences.putBoolean("remember", mLoginFragment.isChecked());
		preferences.apply();
		startHome();
	}

	private void startHome() {
		hideKeyboard();
		final Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("fake", mUsername.toLowerCase().equals(FAKE_LOGIN));
		startActivity(intent);
		finish();
	}

	private void logout() {
		getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
				.remove("password")
				.apply();
		mPassword = null;
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

	// Login request using HttpPost
	private class LoginRequest extends AsyncTask<Void, Void, Boolean> {
		private static final String TAG = "Login Connection";
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
				mConnection = (HttpsURLConnection) new URL(Utils.API.LOGIN).openConnection();
				mAuthKey = "Basic " + Base64.encodeToString((mUsername + ":" + mPassword).getBytes(), Base64.NO_WRAP);
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
									submit(mUsername, mPassword);
								}
							}).show();
				} else {
					Snackbar.make(findViewById(R.id.container), "Cannot connect to server", Snackbar.LENGTH_SHORT)
							.setAction("Retry", new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									submit(mUsername, mPassword);
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
				postRequest();
			}
		}

		@Override
		protected void onCancelled() {
			Log.d(TAG, "Login cancelled");
		}
	}
}
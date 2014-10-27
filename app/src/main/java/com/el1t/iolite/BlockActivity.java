package com.el1t.iolite;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by El1t on 10/24/14.
 */

public class BlockActivity extends Activity implements BlockFragment.OnFragmentInteractionListener
{
	private final String TAG = "Block Activity";

	private BlockFragment mBlockFragment;
	private ArrayList<SerializedCookie> mCookies;
	private boolean fake;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_block);
		Intent intent = getIntent();

		// Check if restoring from previously destroyed instance
		if (savedInstanceState == null) {
			// Check if fake information should be used
			if (fake = intent.getBooleanExtra("fake", false)) {
				Log.d(TAG, "Loading fake info");
				// Pretend fake list was received
				postRequest(getList());
			} else {
				// Set loading fragment
				getFragmentManager().beginTransaction()
						.add(R.id.container, new LoadingFragment());
			}
		}

		// Retrieve cookies from previous activity
		mCookies = (ArrayList<SerializedCookie>) intent.getSerializableExtra("cookies");

		// Retrieve list of bids using cookies
		if (!intent.getBooleanExtra("fake", false)) {
			new BlockListRequest().execute("https://iodine.tjhsst.edu/api/eighth/list_blocks");
		}
	}

	// Select a BID to display activities for
	public void select(int BID) {
		// Send data to SignupActivity
		Intent intent = new Intent(this, SignupActivity.class);
		intent.putExtra("BID", Integer.toString(BID));
		intent.putExtra("cookies", mCookies);
		intent.putExtra("fake", fake);
		startActivity(intent);
	}

	// Get a fake list of blocks for debugging
	private ArrayList<EighthBlockItem> getList() {
		try {
			return EighthBlockXmlParser.parse(getAssets().open("testBlockList.xml"));
		} catch(Exception e) {
			Log.e(TAG, "Error parsing block xml", e);
		}
		return null;
	}

	private void postRequest(ArrayList<EighthBlockItem> result) {
		// Sort the array by date (for now)
		Collections.sort(result, new DateSortComp());
		// Create the content view
		mBlockFragment = new BlockFragment();
		// Add ArrayList to the ListView in BlockFragment
		Bundle args = new Bundle();
		args.putSerializable("list", result);
		mBlockFragment.setArguments(args);
		// Switch to BlockFragment view, remove LoadingFragment
		getFragmentManager().beginTransaction()
				.replace(R.id.container, mBlockFragment)
				.commit();
	}

	// Get list of blocks
	private class BlockListRequest extends AsyncTask<String, Void, ArrayList<EighthBlockItem>> {
		private static final String TAG = "Block List Connection";

		@Override
		protected ArrayList<EighthBlockItem> doInBackground(String... urls) {
			assert(urls.length == 1);

			HttpURLConnection urlConnection;
			ArrayList<EighthBlockItem> response = null;
			try {
				urlConnection = (HttpURLConnection) new URL(urls[0]).openConnection();
				// Add cookies to header
				for(SerializedCookie cookie : mCookies) {
					urlConnection.setRequestProperty("Cookie", cookie.getName() + "=" + cookie.getValue());
				}
				// Begin connection
				urlConnection.connect();
				// Parse xml from server
				response = EighthBlockXmlParser.parse(urlConnection.getInputStream());
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
		protected void onPostExecute(ArrayList<EighthBlockItem> result) {
			super.onPostExecute(result);
			postRequest(result);
		}
	}
}

// Sort by date
class DateSortComp implements Comparator<EighthBlockItem>
{
	@Override
	public int compare(EighthBlockItem e1, EighthBlockItem e2) {
		// Sort by block if dates are concurrent
		if (e1.getDate().equals(e2.getDate())) {
			return e1.getBlock().compareTo(e2.getBlock());
		} else {
			return e1.getDate().compareTo(e2.getDate());
		}
	}
}
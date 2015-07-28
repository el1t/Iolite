package com.el1t.iolite;

import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by El1t on 7/21/15.
 */
public class Utils {
	public static final String TAG = "Utils";

	public static JSONObject inputStreamToJSON(InputStream inputStream) {
		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			final StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return new JSONObject(sb.toString());
		} catch (IOException | JSONException e) {
			Log.e(TAG, "Exception", e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String inputStreamToString(InputStream inputStream) {
		Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
		return s.hasNext() ? s.next() : null;
	}

	public static String[] JSONArrayToStringArray(JSONArray array) throws JSONException{
		final String[] generic = new String[array.length()];
		for (int i = 0; i < generic.length; i++) {
			generic[i] = (String) array.get(i);
		}
		return generic;
	}

	public static String getAuthKey(SharedPreferences preferences) {
		final String username;
		final String password;
		if ((username = preferences.getString("username", null)) != null &&
				(password = preferences.getString("password", null)) != null) {
			return "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
		}
		return null;
	}

	public static String join(String[] array) {
		final StringBuilder sb = new StringBuilder();
		for (String s : array) {
			if (sb.length() > 0) {
				sb.append(", ").append(s);
			} else {
				sb.append(s);
			}
		}
		return sb.toString();
	}
}
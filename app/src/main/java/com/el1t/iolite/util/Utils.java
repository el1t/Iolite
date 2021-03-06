package com.el1t.iolite.util;

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
	public static final String PREFS_NAME = "LOGIN";

	public static class API {
		private static final String PREFIX = "https://ion.tjhsst.edu/api";
		private static final String POSTFIX = "?format=json";
		private static final String BLOCK = PREFIX + "/blocks";
		private static final String ACTIVITY = PREFIX + "/activities";

		public static final String LOGIN = PREFIX;
		public static final String PROFILE = PREFIX + "/profile" + POSTFIX;
		public static final String BLOCKS = BLOCK + POSTFIX;
		public static final String SCHEDULE = PREFIX + "/schedule" + POSTFIX;
		public static final String SIGNUP = PREFIX + "/signups/user" + POSTFIX;
		public static final String ACTIVITIES = ACTIVITY + POSTFIX;
		public static final String NEWS = PREFIX + "/announcements" + POSTFIX;

		public static String activity(int activity) {
			return ACTIVITY + "/" + activity + POSTFIX;
		}

		public static String block(int block) {
			return BLOCK + "/" + block + POSTFIX;
		}

		public static String schedule(int page, int size) {
			return SCHEDULE + "&page=" + page + "&page_size=" + size;
		}

		public static String profilePic(String uid) {
			return PREFIX + "/profile/" + uid + "/picture";
		}
	}

	public static JSONObject inputStreamToJSON(InputStream inputStream) {
		try {
			final String temp = inputStreamToBufferedString(inputStream);
			if (temp == null) {
				return null;
			}
			return new JSONObject(temp);
		} catch (JSONException e) {
			Log.e(TAG, "Exception", e);
		}
		return null;
	}

	public static JSONArray inputStreamToJSONArray(InputStream inputStream) {
		try {
			final String temp = inputStreamToBufferedString(inputStream);
			if (temp == null) {
				return null;
			}
			return new JSONArray(temp);
		} catch (JSONException e) {
			Log.e(TAG, "Exception", e);
		}
		return null;
	}

	private static String inputStreamToBufferedString(InputStream inputStream) {
		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			final StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			reader.close();
			return sb.toString();
		} catch (IOException e) {
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
		final Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
		return s.hasNext() ? s.next() : null;
	}

	public static String[] JSONArrayToStringArray(JSONArray array) throws JSONException {
		final String[] generic = new String[array.length()];
		for (int i = 0; i < generic.length; i++) {
			generic[i] = array.getString(i);
		}
		return generic;
	}

	public static boolean[] JSONArrayToBooleanArray(JSONArray array) throws JSONException {
		final boolean[] generic = new boolean[array.length()];
		for (int i = 0; i < generic.length; i++) {
			generic[i] = array.getBoolean(i);
		}
		return generic;
	}

	public static String getAuthKey(SharedPreferences preferences) {
		final String username, password;
		if ((username = preferences.getString("username", null)) != null &&
				(password = preferences.getString("password", null)) != null) {
			return "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
		}
		return null;
	}

	public static <T> String join(T[] array) {
		return join(array, ", ");
	}

	public static <T> String join(T[] array, String delim) {
		final StringBuilder sb = new StringBuilder();
		for (T t : array) {
			if (sb.length() > 0) {
				sb.append(delim);
			}
			sb.append(t);
		}
		return sb.toString();
	}
}

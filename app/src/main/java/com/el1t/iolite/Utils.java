package com.el1t.iolite;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}

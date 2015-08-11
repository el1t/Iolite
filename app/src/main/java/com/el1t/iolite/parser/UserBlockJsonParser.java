package com.el1t.iolite.parser;

import com.el1t.iolite.item.EighthActivity;
import com.el1t.iolite.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;

/**
 * Created by El1t on 8/11/15.
 */
public class UserBlockJsonParser {
	private static final String TAG = "Student Info JSON Parser";

	public static EighthActivity[] parse(InputStream in) throws JSONException, ParseException {
		return parse(Utils.inputStreamToJSONArray(in));
	}

	public static EighthActivity[] parse(JSONArray items) throws JSONException, ParseException {
		final EighthActivity[] activities = new EighthActivity[items.length()];
		JSONObject item, block, activity;

		for (int i = 0; i < items.length(); i++) {
			item = (JSONObject) items.get(i);
			block = (JSONObject) item.get("block");
			activity = (JSONObject) item.get("activity");
			activities[i] = new EighthActivity.EighthActivityBuilder()
					.BID(block.getInt("id"))
					.AID(activity.getInt("id"))
					.name(activity.getString("title"))
					.URL(activity.getString("url"))
					.build();
		}

		return activities;
	}
}

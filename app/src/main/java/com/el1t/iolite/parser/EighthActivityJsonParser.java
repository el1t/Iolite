package com.el1t.iolite.parser;

import com.el1t.iolite.item.EighthActivity;
import com.el1t.iolite.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.util.Iterator;

/**
 * Created by El1t on 7/25/15.
 */
public class EighthActivityJsonParser {
	private static final String TAG = "Activity JSON Parser";
	private final int BID;

	public EighthActivityJsonParser(int BID) {
		this.BID = BID;
	}

	public static EighthActivity[] parseAll(InputStream in) throws JSONException, ParseException {
		return parseAll(Utils.inputStreamToJSON(in));
	}

	public static EighthActivity[] parseAll(JSONObject input) throws JSONException, ParseException {
		final JSONObject activityList = input.getJSONObject("activities");
		final EighthActivityJsonParser parser = new EighthActivityJsonParser(input.getInt("id"));
		final EighthActivity[] activities = new EighthActivity[activityList.length()];
		int index = 0;
		for (Iterator<String> keys = activityList.keys(); keys.hasNext(); index++) {
			activities[index] = parser.parse(activityList.getJSONObject(keys.next()));
		}
		return activities;
	}

	public EighthActivity parse(JSONObject activity) throws JSONException, ParseException {
		final JSONObject roster = activity.getJSONObject("roster");
		return new EighthActivity.EighthActivityBuilder()
				.AID(activity.getInt("id"))
				.BID(BID)
				.memberCount(roster.getInt("count"))
				.capacity(roster.getInt("capacity"))
				.name(activity.getString("name"))
				.description(activity.getString("description"))
				.URL(activity.getString("url"))
				.rooms(Utils.JSONArrayToStringArray(activity.getJSONArray("rooms")))
				.sponsors(Utils.JSONArrayToStringArray(activity.getJSONArray("sponsors")))
				.restricted(activity.getBoolean("restricted_for_user"))
				.administrative(activity.getBoolean("administrative"))
				.presign(activity.getBoolean("presign"))
				.bothblocks(activity.getBoolean("both_blocks"))
				.sticky(activity.getBoolean("sticky"))
				.special(activity.getBoolean("special"))
				.cancelled(activity.getBoolean("cancelled"))
				.favorite(activity.getBoolean("favorited"))
				.build();
	}
}

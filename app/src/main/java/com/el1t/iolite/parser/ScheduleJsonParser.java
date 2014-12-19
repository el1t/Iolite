package com.el1t.iolite.parser;

import com.el1t.iolite.item.Schedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by El1t on 12/11/14.
 */
public class ScheduleJsonParser
{
	private static final String TAG = "Schedule JSON Parser";

	public static Schedule parse(JSONObject schedule) throws JSONException {
		JSONObject date = schedule.getJSONObject("date");
		final String[] items = getItems(schedule.getJSONObject("schedule").getJSONArray("period"));
		return new Schedule.ScheduleBuilder()
				.day(schedule.getString("dayname"))
				.type(schedule.getString("summary"))
				.yesterday(date.getString("yesterday"))
				.tomorrow(date.getString("tomorrow"))
				.blocks(items[0])
				.times(items[1])
				.build();
	}

	private static String[] getItems(JSONArray blocks) throws JSONException {
		final String[] items = {"", ""};
		for (int i = 0; i < blocks.length(); i++) {
			JSONObject block = blocks.getJSONObject(i);
			items[0] += "\n" + block.getString("name");
			items[1] += "\n" + block.getJSONObject("times").getString("times");
		}
		return items;
	}
}
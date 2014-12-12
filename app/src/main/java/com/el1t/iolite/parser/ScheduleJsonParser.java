package com.el1t.iolite.parser;

import com.el1t.iolite.item.Schedule;
import com.el1t.iolite.item.ScheduleItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by El1t on 12/11/14.
 */
public class ScheduleJsonParser
{
	private static final String TAG = "Schedule JSON Parser";

	public static Schedule parse(JSONObject schedule) throws JSONException {
		JSONObject date = schedule.getJSONObject("date");
		return new Schedule.ScheduleBuilder()
				.day(schedule.getString("dayname"))
				.type(schedule.getString("summary"))
				.yesterday(date.getString("yesterday"))
				.tomorrow(date.getString("tomorrow"))
				.items(getItems(schedule.getJSONObject("schedule").getJSONArray("period")))
				.build();
	}

	private static ArrayList<ScheduleItem> getItems(JSONArray blocks) throws JSONException {
		ArrayList<ScheduleItem> items = new ArrayList<>();
		for (int i = 0; i < blocks.length(); i++) {
			JSONObject block = blocks.getJSONObject(i);
			items.add(new ScheduleItem(block.getInt("num"),
					block.getString("name"),
					block.getJSONObject("times").getString("times")));
		}
		return items;
	}
}

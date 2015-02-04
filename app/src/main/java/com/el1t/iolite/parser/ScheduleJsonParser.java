package com.el1t.iolite.parser;

import com.el1t.iolite.item.Schedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by El1t on 12/11/14.
 */
public class ScheduleJsonParser
{
	private static final String TAG = "Schedule JSON Parser";
	private static final DateFormat mFormat = new SimpleDateFormat("yyyyMMdd");

	public static Schedule[] parseSchedules(JSONObject schedule) throws JSONException, ParseException {
		final Schedule[] schedules = new Schedule[schedule.length()];
		int index = 0;
		for (Iterator<String> keys = schedule.keys(); keys.hasNext(); index++) {
			schedules[index] = parseSchedule(schedule.getJSONObject(keys.next()));
		}
		Arrays.sort(schedules);
		return schedules;
	}

	private static Schedule parseSchedule(JSONObject schedule) throws JSONException, ParseException {
		final JSONObject date = schedule.getJSONObject("date");
		final String[] items = getItems(schedule.getJSONObject("schedule").getJSONArray("period"));
		return new Schedule.ScheduleBuilder()
				.day(schedule.getString("dayname"))
				.date(mFormat.parse(date.getString("today")))
				.type(schedule.getString("summary"))
				.yesterday(date.getString("yesterday"))
				.tomorrow(date.getString("tomorrow"))
				.blocks(items[0])
				.times(items[1])
				.build();
	}

	private static String[] getItems(JSONArray blocks) throws JSONException {
		final String[] items = {"", ""};
		JSONObject block;
		for (int i = 0; i < blocks.length(); i++) {
			block = blocks.getJSONObject(i);
			items[0] += "\n" + block.getString("name");
			items[1] += "\n" + block.getJSONObject("times").getString("times");
		}
		return items;
	}
}
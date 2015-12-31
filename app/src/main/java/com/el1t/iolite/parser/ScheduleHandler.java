package com.el1t.iolite.parser;

import com.el1t.iolite.model.Schedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by El1t on 12/11/14.
 */
public class ScheduleHandler {
	private static final String TAG = "ScheduleHandler";
	private static final DateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat mFormat2 = new SimpleDateFormat("EEE, MMM d");

	public static Schedule[] parseAll(JSONObject schedule) throws JSONException, ParseException {
		final JSONArray scheduleArray = schedule.getJSONArray("results");
		final Schedule[] schedules = new Schedule[scheduleArray.length()];
		for (int index = 0; index < scheduleArray.length(); index++) {
			schedules[index] = parseSchedule(scheduleArray.getJSONObject(index));
		}
		Arrays.sort(schedules);
		return schedules;
	}

	public static Schedule parseSchedule(JSONObject schedule) throws JSONException, ParseException {
		final String[] items = getItems(schedule.getJSONObject("day_type").getJSONArray("blocks"));
		final Date date = mFormat.parse(schedule.getString("date"));
		return new Schedule.Builder()
				.day(mFormat2.format(date))
				.date(date)
				.type(schedule.getJSONObject("day_type").getString("name"))
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
			items[1] += "\n" + block.getString("start") + " - " + block.getString("end");
		}
		return items;
	}
}
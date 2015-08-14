package com.el1t.iolite.parser;

import com.el1t.iolite.model.NewsPost;
import com.el1t.iolite.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by El1t on 8/4/15.
 */
public class NewsHandler {
	private static final String TAG = "NewsHandler";
	private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	public static NewsPost[] parse(InputStream in) throws JSONException, ParseException {
		return parse(Utils.inputStreamToJSON(in));
	}

	public static NewsPost[] parse(JSONObject item) throws JSONException, ParseException {
		final JSONArray results = item.getJSONArray("results");
		final NewsPost[] posts = new NewsPost[results.length()];
		JSONObject post;

		for (int i = 0; i < results.length(); i++) {
			post = (JSONObject) results.get(i);
			posts[i] = new NewsPost.Builder()
					.URL(post.getString("url"))
					.ID(post.getInt("id"))
					.title(post.getString("title"))
					.content(post.getString("content"))
					.author(post.getString("author"))
					.user(post.isNull("user") ? -1 : post.getInt("user"))
					.added(parseDate(post.getString("added")))
					.updated(parseDate(post.getString("updated")))
					.build();
		}
		return posts;
	}

	// Drop the milliseconds due to API producing different date formats
	private static Date parseDate(String date) throws ParseException {
		if (date == null) {
			return null;
		}
		if (date.indexOf('.') < 0) {
			return FORMAT.parse(date.substring(0, date.length() - 1));
		}
		return FORMAT.parse(date.substring(0, date.lastIndexOf('.')));
	}
}

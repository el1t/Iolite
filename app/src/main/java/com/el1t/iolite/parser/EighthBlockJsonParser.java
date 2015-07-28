package com.el1t.iolite.parser;

import com.el1t.iolite.Utils;
import com.el1t.iolite.item.EighthBlock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by El1t on 7/21/15.
 */
public class EighthBlockJsonParser {
	private static final String TAG = "Student Info JSON Parser";
	private static final DateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static ArrayList<EighthBlock> parse(InputStream in) throws JSONException, ParseException {
		return parse(Utils.inputStreamToJSON(in));
	}

	public static ArrayList<EighthBlock> parse(JSONObject item) throws JSONException, ParseException {
		final JSONArray results = item.getJSONArray("results");
		final ArrayList<EighthBlock> blocks = new ArrayList<>(item.getInt("count"));
		JSONObject block;

		for (int i = 0; i < results.length(); i++) {
			block = (JSONObject) results.get(i);
			blocks.add(new EighthBlock.ItemBuilder()
					.date(mFormat.parse(block.getString("date")))
					.BID(block.getInt("id"))
					.type(block.getString("block_letter").charAt(0))
					.locked(block.getBoolean("locked"))
					.build());
		}

		return blocks;
	}
}

package com.el1t.iolite.parser;

import com.el1t.iolite.model.EighthBlock;
import com.el1t.iolite.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by El1t on 7/21/15.
 */
public class BlockHandler {
	private static final String TAG = "BlockHandler";
	private static final DateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static EighthBlock[] parse(InputStream in) throws JSONException, ParseException {
		return parse(Utils.inputStreamToJSON(in));
	}

	public static EighthBlock[] parse(JSONObject item) throws JSONException, ParseException {
		final JSONArray results = item.getJSONArray("results");
		final EighthBlock[] blocks = new EighthBlock[results.length()];
		JSONObject block;

		for (int i = 0; i < results.length(); i++) {
			block = (JSONObject) results.get(i);
			blocks[i] = new EighthBlock.Builder()
					.date(mFormat.parse(block.getString("date")))
					.BID(block.getInt("id"))
					.type(block.getString("block_letter").charAt(0))
					.locked(block.getBoolean("locked"))
					.build();
		}

		return blocks;
	}
}

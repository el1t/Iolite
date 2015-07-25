package com.el1t.iolite.parser;

import com.el1t.iolite.Utils;
import com.el1t.iolite.item.Detail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by El1t on 7/25/15.
 */
public class DetailJsonParser
{
	private static final String TAG = "Detail JSON Parser";

	public static void parse(InputStream in) throws JSONException, ParseException {
		parse(in, null);
	}

	public static void parse(InputStream in, ArrayList<Detail> details) throws JSONException, ParseException {
		parse(Utils.inputStreamToJSON(in), details);
	}

	public static void parse(JSONObject detail, ArrayList<Detail> details) throws JSONException, ParseException {
		new Detail.DetailBuilder(details)
				.rooms(Utils.JSONArrayToStringArray(detail.getJSONArray("rooms")))
				.sponsors(Utils.JSONArrayToStringArray(detail.getJSONArray("sponsors")))
				.description(detail.getString("description"))
				.administrative(detail.getBoolean("administrative"))
				.restricted(detail.getBoolean("restricted_for_user"))
				.presign(detail.getBoolean("presign"))
				.bothblocks(detail.getBoolean("both_blocks"))
				.sticky(detail.getBoolean("sticky"))
				.special(detail.getBoolean("special"))
				.cancelled(detail.getBoolean("cancelled"))
				.build();
	}
}

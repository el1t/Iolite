package com.el1t.iolite.parser;

import com.el1t.iolite.utils.Utils;
import com.el1t.iolite.item.EighthActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;

/**
 * Created by El1t on 7/25/15.
 */
public class DetailJsonParser
{
	private static final String TAG = "Detail JSON Parser";

	public static EighthActivity parse(InputStream in, EighthActivity activity) throws JSONException, ParseException {
		return parse(Utils.inputStreamToJSON(in), activity);
	}

	public static EighthActivity parse(JSONObject detail, EighthActivity activity) throws JSONException, ParseException {
		return new EighthActivity.EighthActivityBuilder(activity)
//				.rooms(Utils.JSONArrayToStringArray(detail.getJSONArray("rooms")))
//				.sponsors(Utils.JSONArrayToStringArray(detail.getJSONArray("sponsors")))
				.description(detail.getString("description"))
				.administrative(detail.getBoolean("administrative"))
//				.restricted(detail.getBoolean("restricted_for_user"))
				.presign(detail.getBoolean("presign"))
				.bothblocks(detail.getBoolean("both_blocks"))
				.sticky(detail.getBoolean("sticky"))
				.special(detail.getBoolean("special"))
//				.cancelled(detail.getBoolean("cancelled"))
				.build();
	}
}

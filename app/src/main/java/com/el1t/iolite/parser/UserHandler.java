package com.el1t.iolite.parser;

import com.el1t.iolite.model.User;
import com.el1t.iolite.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;

/**
 * Created by El1t on 12/11/14.
 */
public class UserHandler {
	private static final String TAG = "ProfileHandler";

	public static User parse(InputStream in) throws JSONException, ParseException {
		return parse(Utils.inputStreamToJSON(in));
	}

	public static User parse(JSONObject user) throws JSONException, ParseException {
//		final JSONObject address = user.getJSONObject("address");

		return new User.Builder()
				.UID(user.getString("id"))
				.username(user.getString("ion_username"))
				.firstName(user.getString("first_name"))
				.middleName(user.getString("middle_name"))
				.lastName(user.getString("last_name"))
//				.street(address.getString("street"))
//				.city(address.getString("city"))
//				.state(address.getString("state"))
//				.postalCode(address.getString("postal_code"))
				.emails(Utils.JSONArrayToStringArray(user.getJSONArray("emails")))
				.phone(user.getString("home_phone"))
				.mobile(user.getString("mobile_phone"))
				.gradYear(user.getInt("graduation_year"))
				.build();
	}
}
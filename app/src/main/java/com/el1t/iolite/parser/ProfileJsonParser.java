package com.el1t.iolite.parser;

import com.el1t.iolite.Utils;
import com.el1t.iolite.item.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

/**
 * Created by El1t on 12/11/14.
 */
public class ProfileJsonParser
{
	private static final String TAG = "Student Info JSON Parser";

	public static User parse(InputStream in) throws JSONException, ParseException {
		return parse(Utils.inputStreamToJSON(in));
	}

	public static User parse(JSONObject user) throws JSONException, ParseException {
		final JSONObject address = user.getJSONObject("address");
		final JSONArray emailArray = user.getJSONArray("emails");
		final String[] emails = new String[emailArray.length()];

		for(int i = 0; i < emails.length; i++) {
			emails[i] = (String) emailArray.get(i);
		}

		return new User.UserBuilder()
				.UID(user.getString("id"))
				.username(user.getString("ion_username"))
				.firstName(user.getString("first_name"))
				.middleName(user.getString("middle_name"))
				.lastName(user.getString("last_name"))
				.street(address.getString("street"))
				.city(address.getString("city"))
				.state(address.getString("state"))
				.postalCode(address.getString("postal_code"))
				.emails(emails)
				.phone(user.getString("home_phone"))
				.mobile(user.getString("mobile_phone"))
				.gradYear(user.getInt("graduation_year"))
				.build();
	}
}
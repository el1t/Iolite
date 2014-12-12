package com.el1t.iolite.parser;

import android.util.Log;
import android.util.Xml;

import com.el1t.iolite.item.EighthActivityItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by El1t on 10/21/14.
 */
public class EighthActivityXmlParser
{
	private static final String TAG = "Activity List XML Parser";

	public static boolean parseSuccess(InputStream in) throws XmlPullParserException, IOException {
		// Initialize parser and jump to first tag
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, "UTF-8");
			parser.nextTag();
			return readResponse(parser);
		} finally {
			in.close();
		}
	}

	private static boolean readResponse(XmlPullParser parser) throws XmlPullParserException, IOException {
		boolean response = false;
		parser.require(XmlPullParser.START_TAG, null, "eighth");
		// Consume the eighth AND signup tags (no need for while loop)
		parser.next();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			switch (name) {
				case "auth":
					parser.next();
					// Consume the auth AND error tags
					parser.next();
					while (parser.next() != XmlPullParser.START_TAG) {
						parser.next();
					}
					// Print debug message of error content
					while (parser.next() != XmlPullParser.END_TAG) {
						// Skip whitespace until a tag is reached
						if (parser.getEventType() != XmlPullParser.START_TAG) {
							continue;
						}
						String tagName = parser.getName();

						if (tagName.equals("message")) {
							Log.d(TAG, readString(parser, "message"));
						} else {
							skip(parser);
						}
					}
					break;
				case "signup":
					// Look for success tag
					parser.require(XmlPullParser.START_TAG, null, "signup");
					// Loop through tags under signup
					while (parser.next() != XmlPullParser.END_TAG) {
						if (parser.getEventType() != XmlPullParser.START_TAG) {
							continue;
						}
						// Look for success tag
						if (parser.getName().equals("success")) {
							if (parser.next() == XmlPullParser.TEXT) {
								response = parser.getText().equals("1");
								parser.nextTag();
							}
						} else {
							skip(parser);
						}
					}
					parser.require(XmlPullParser.END_TAG, null, "signup");
					// Look for error tag and print any error
					break;
				case "error":
					parser.require(XmlPullParser.START_TAG, null, "error");
					if (parser.next() == XmlPullParser.TEXT) {
						Log.e(TAG, parser.getText());
						parser.nextTag();
					}
					parser.require(XmlPullParser.END_TAG, null, "error");
					break;
				default:
					skip(parser);
					break;
			}
		}
		return response;
	}

// ============ Parse activity list =============

	public static ArrayList<EighthActivityItem> parse(InputStream in) throws XmlPullParserException, IOException {
		// Initialize parser and jump to first tag
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, "UTF-8");
			parser.nextTag();
			return readEighth(parser);
		} finally {
			in.close();
		}
	}

	private static ArrayList<EighthActivityItem> readEighth(XmlPullParser parser) throws XmlPullParserException, IOException {
		ArrayList<EighthActivityItem> entries = new ArrayList<>();
		parser.require(XmlPullParser.START_TAG, null, "eighth");
		// Consume the eighth AND activities tags
		parser.next();
		while(parser.next() != XmlPullParser.START_TAG) {
			parser.next();
		}
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the activity tag
			if (name.equals("activity")) {
				entries.add(readActivity(parser));
			} else {
				skip(parser);
			}
		}
		return entries;
	}

	private static EighthActivityItem readActivity(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "activity");

		final EighthActivityItem temp = new EighthActivityItem();

		while (parser.next() != XmlPullParser.END_TAG) {
			// Skip whitespace until a tag is reached
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tagName = parser.getName();

			switch (tagName) {
				case "aid":
					temp.setAID(readInt(parser, "aid"));
					break;
				case "name":
					temp.setName(readString(parser, "name"));
					break;
				case "description":
					temp.setDescription(readString(parser, "description"));
					break;
				case "restricted":
					temp.setRestricted(readBool(parser, "restricted"));
					break;
				case "presign":
					temp.setPresign(readBool(parser, "presign"));
					break;
				case "oneaday":
					temp.setOneaday(readBool(parser, "oneaday"));
					break;
				case "bothblocks":
					temp.setBothblocks(readBool(parser, "bothblocks"));
					break;
				case "sticky":
					temp.setSticky(readBool(parser, "sticky"));
					break;
				case "special":
					temp.setSpecial(readBool(parser, "special"));
					break;
				case "calendar":
					temp.setCalendar(readBool(parser, "calendar"));
					break;
				case "room_changed":
					temp.setRoomChanged(readBool(parser, "room_changed"));
					break;
				case "block_rooms_comma":
					temp.setBlockRoomString(readString(parser, "block_rooms_comma"));
					break;
				case "bid":
					temp.setBID(readInt(parser, "bid"));
					break;
				case "cancelled":
					temp.setCancelled(readBool(parser, "cancelled"));
					break;
				case "attendancetaken":
					temp.setAttendanceTaken(readBool(parser, "attendancetaken"));
					break;
				case "favorite":
					temp.setFavorite(readBool(parser, "favorite"));
					break;
				case "member_count":
					temp.setMemberCount(readInt(parser, "member_count"));
					break;
				case "capacity":
					temp.setCapacity(readInt(parser, "capacity"));
					break;
				default:
					skip(parser);
					break;
			}
		}
		parser.require(XmlPullParser.END_TAG, null, "activity");

		return temp;
	}

	private static String readString(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, tagName);
		// Note: this cannot be null, because some fields are empty! (empty fields would have to be set to "", anyways)
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		parser.require(XmlPullParser.END_TAG, null, tagName);
		return result;
	}

	private static int readInt(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, tagName);
		int result = 0;
		if (parser.next() == XmlPullParser.TEXT) {
			result = Integer.parseInt(parser.getText());
			parser.nextTag();
		}
		parser.require(XmlPullParser.END_TAG, null, tagName);
		return result;
	}

	private static boolean readBool(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, tagName);
		boolean result = false;
		if (parser.next() == XmlPullParser.TEXT) {
			result = Integer.parseInt(parser.getText()) == 1;
			parser.nextTag();
		}
		parser.require(XmlPullParser.END_TAG, null, tagName);
		return result;
	}

	private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
			}
		}
	}
}

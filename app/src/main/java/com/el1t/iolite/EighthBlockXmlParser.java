package com.el1t.iolite;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by El1t on 10/24/14.
 */
public class EighthBlockXmlParser
{
	private static final String TAG = "Block List XML Parser";

	public static ArrayList<EighthBlockItem> parse(InputStream in) throws XmlPullParserException, IOException {
		// Initialize parser and jump to first tag
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readEighth(parser);
		} finally {
			in.close();
		}
	}

	private static ArrayList<EighthBlockItem> readEighth(XmlPullParser parser) throws XmlPullParserException, IOException {
		ArrayList<EighthBlockItem> entries = new ArrayList<EighthBlockItem>();
		parser.require(XmlPullParser.START_TAG, null, "eighth");
		// Consume the "eighth" AND "blocks" tags
		parser.next();
		while(parser.next() != XmlPullParser.START_TAG) {
			parser.next();
		}
		try {
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				// Starts by looking for the block tag
				if (name.equals("block")) {
					entries.add(readBlock(parser));
				} else {
					skip(parser);
				}
			}
		} catch (ParseException e) {
			Log.e(TAG, "Block parser", e);
		}
		return entries;
	}

	private static EighthBlockItem readBlock(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
		parser.require(XmlPullParser.START_TAG, null, "block");

		EighthActivityItem activity = null;
		Date date = null;
		int BID = 0;
		String type = null;
		boolean locked = false;
		String disp = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			// Skip whitespace until a tag is reached
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tagName = parser.getName();
			if (tagName.equals("activity")) {
				activity = readActivity(parser);
			} else if (tagName.equals("date")) {
				date = readDate(parser, "date");
			} else if (tagName.equals("bid")) {
				BID = readInt(parser, "bid");
			} else if (tagName.equals("type")) {
				type = readString(parser, "type");
			} else if (tagName.equals("locked")) {
				locked = readBool(parser, "locked");
			} else if (tagName.equals("disp")) {
				disp = readString(parser, "disp");
			} else {
				skip(parser);
			}
		}
		if (activity == null) {
			throw new NullPointerException();
		}
		return new EighthBlockItem(activity, date, BID, type, locked, disp);
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

	// Read the <str> tag under <date>
	private static Date readDate(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException, ParseException {
		Date result = null;
		parser.require(XmlPullParser.START_TAG, null, tagName);
		while(parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			if (parser.getName().equals("str")) {
				if (parser.next() == XmlPullParser.TEXT) {
					result = new SimpleDateFormat("yyyy-MM-dd").parse(parser.getText());
					parser.nextTag();
				}
			} else {
				skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, null, tagName);
		return result;
	}

	// A different method for parsing the inexplicably different tags inside the block xml
	public static EighthActivityItem readActivity(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "activity");

		int AID = 0;
		String name = null;
		String description = null;
		boolean restricted = false;
		boolean presign = false;
		boolean oneaday = false;
		boolean bothblocks = false;
		boolean sticky = false;
		boolean special = false;
		boolean calendar = false;
		ArrayList<Integer> blockSponsors = null;
		ArrayList<Integer> blockRooms = null;
		int BID = 0;
		boolean cancelled = false;
		String comment = null;
		String advertisement = null;
		boolean attendanceTaken = false;

		while (parser.next() != XmlPullParser.END_TAG) {
			// Skip whitespace until a tag is reached
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tagName = parser.getName();
			if (tagName.equals("aid")) {
				AID = readInt(parser, "aid");
			} else if (tagName.equals("name")) {
				name = readString(parser, "name");
			} else if (tagName.equals("description")) {
				description = readString(parser, "description");
			} else if (tagName.equals("restricted")) {
				restricted = readBool(parser, "restricted");
			} else if (tagName.equals("presign")) {
				presign = readBool(parser, "presign");
			} else if (tagName.equals("oneaday")) {
				oneaday = readBool(parser, "oneaday");
			} else if (tagName.equals("bothblocks")) {
				bothblocks = readBool(parser, "bothblocks");
			} else if (tagName.equals("sticky")) {
				sticky = readBool(parser, "sticky");
			} else if (tagName.equals("special")) {
				special = readBool(parser, "special");
			} else if (tagName.equals("calendar")) {
				calendar = readBool(parser, "calendar");
			} else if (tagName.equals("block_sponsors")) {
				blockSponsors = EighthActivityXmlParser.readNestedInts(parser, "block_sponsors");
			} else if (tagName.equals("block_rooms")) {
				blockRooms = EighthActivityXmlParser.readNestedInts(parser, "block_rooms");
			} else if (tagName.equals("bid")) {
				BID = readInt(parser, "bid");
			} else if (tagName.equals("cancelled")) {
				cancelled = readBool(parser, "cancelled");
			} else if (tagName.equals("comment")) {
				comment = readString(parser, "comment");
			} else if (tagName.equals("advertisement")) {
				advertisement = readString(parser, "advertisement");
			} else if (tagName.equals("attendancetaken")) {
				attendanceTaken = readBool(parser, "attendancetaken");
			} else {
				skip(parser);
			}
		}
		if (AID * BID == 0) {
			Log.e(TAG, "Malformed integer in fields for activity " + name);
		}
		parser.require(XmlPullParser.END_TAG, null, "activity");

		return new EighthActivityItem(AID, name, description, restricted, presign, oneaday,
				bothblocks, sticky, special, calendar, blockSponsors, blockRooms, BID, cancelled, comment,
				advertisement, attendanceTaken);
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

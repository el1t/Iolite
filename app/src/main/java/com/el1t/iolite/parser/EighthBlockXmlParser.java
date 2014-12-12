package com.el1t.iolite.parser;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.el1t.iolite.item.EighthActivityItem;
import com.el1t.iolite.item.EighthBlockItem;

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
	private static Context mContext;

	public static ArrayList<EighthBlockItem> parse(InputStream in, Context context) throws XmlPullParserException, IOException {
		mContext = context;
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

	private static ArrayList<EighthBlockItem> readEighth(XmlPullParser parser) throws XmlPullParserException, IOException {
		ArrayList<EighthBlockItem> entries = new ArrayList<>();
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
					// Starts by looking for the block tag
					case "block":
						entries.add(readBlock(parser));
						break;
					default:
						skip(parser);
						break;
				}
			}
		} catch (ParseException e) {
			Log.e(TAG, "Block parser", e);
			Toast.makeText(mContext, "Some blocks failed to load.", Toast.LENGTH_SHORT).show();
		}
		return entries;
	}

	private static EighthBlockItem readBlock(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
		parser.require(XmlPullParser.START_TAG, null, "block");

		final EighthBlockItem temp = new EighthBlockItem();

		while (parser.next() != XmlPullParser.END_TAG) {
			// Skip whitespace until a tag is reached
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tagName = parser.getName();
			switch (tagName) {
				case "activity":
					readActivity(parser, temp.getEighth());
					break;
				case "date":
					temp.setDate(readDate(parser));
					break;
				case "bid":
					temp.setBID(readInt(parser, "bid"));
					break;
				case "type":
					temp.setBlock(readString(parser, "type"));
					break;
				case "locked":
					temp.setLocked(readBool(parser, "locked"));
					break;
				default:
					skip(parser);
					break;
			}
		}
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

	// Read the <str> tag under <date>
	private static Date readDate(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException {
		Date result = null;
		parser.require(XmlPullParser.START_TAG, null, "date");
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
		parser.require(XmlPullParser.END_TAG, null, "date");
		return result;
	}

	// A different method for parsing the inexplicably different tags inside the block xml
	private static void readActivity(XmlPullParser parser, EighthActivityItem temp) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "activity");

		label:
		while (parser.next() != XmlPullParser.END_TAG) {
			// Skip whitespace until a tag is reached
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tagName = parser.getName();
			switch (tagName) {
				case "aid":
					temp.setAID(readInt(parser, "aid"));
					// Detect "No activity selected" activity
					if (temp.getAID() == 999) {
						temp.setName("No Activity Selected");
						temp.setDescription("Please select an activity");
						// Skip the rest of the activity
						while (parser.next() != XmlPullParser.END_TAG || !parser.getName().equals("activity"))
							;
						break label;
					}
					break;
				case "name":
					temp.setName(readString(parser, "name"));
					break;
				case "description":
					temp.setDescription(readString(parser, "description"));
					break;
				case "cancelled":
					temp.setCancelled(readBool(parser, "cancelled"));
					break;
				case "attendancetaken":
					temp.setAttendanceTaken(readBool(parser, "attendancetaken"));
					break;
				default:
					skip(parser);
					break;
			}
		}
		parser.require(XmlPullParser.END_TAG, null, "activity");
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

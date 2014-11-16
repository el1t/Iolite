package com.el1t.iolite;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

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
			if (tagName.equals("activity")) {
				readActivity(parser, temp.getEighth());
			} else if (tagName.equals("date")) {
				temp.setDate(readDate(parser, "date"));
			} else if (tagName.equals("bid")) {
				temp.setBID(readInt(parser, "bid"));
			} else if (tagName.equals("type")) {
				temp.setBlock(readString(parser, "type"));
			} else if (tagName.equals("locked")) {
				temp.setLocked(readBool(parser, "locked"));
			} else {
				skip(parser);
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
	public static EighthActivityItem readActivity(
			XmlPullParser parser, EighthActivityItem temp) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "activity");

		while (parser.next() != XmlPullParser.END_TAG) {
			// Skip whitespace until a tag is reached
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tagName = parser.getName();
			if (tagName.equals("aid")) {
				temp.setAID(readInt(parser, "aid"));
			} else if (tagName.equals("name")) {
				temp.setName(readString(parser, "name"));
			} else if (tagName.equals("description")) {
				temp.setDescription(readString(parser, "description"));
			} else if (tagName.equals("restricted")) {
				temp.setRestricted(readBool(parser, "restricted"));
			} else if (tagName.equals("presign")) {
				temp.setPresign(readBool(parser, "presign"));
			} else if (tagName.equals("oneaday")) {
				temp.setOneaday(readBool(parser, "oneaday"));
			} else if (tagName.equals("bothblocks")) {
				temp.setBothblocks(readBool(parser, "bothblocks"));
			} else if (tagName.equals("sticky")) {
				temp.setSticky(readBool(parser, "sticky"));
			} else if (tagName.equals("special")) {
				temp.setSpecial(readBool(parser, "special"));
			} else if (tagName.equals("calendar")) {
				temp.setCalendar(readBool(parser, "calendar"));
			} else if (tagName.equals("bid")) {
				temp.setBID(readInt(parser, "bid"));
			} else if (tagName.equals("cancelled")) {
				temp.setCancelled(readBool(parser, "cancelled"));
			} else if (tagName.equals("attendancetaken")) {
				temp.setAttendanceTaken(readBool(parser, "attendancetaken"));
			} else {
				skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, null, "activity");

		return temp;
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

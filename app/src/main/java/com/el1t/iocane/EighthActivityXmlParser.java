package com.el1t.iocane;

import android.util.Xml;

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
	public ArrayList<EighthActivityItem> parse(InputStream in) throws XmlPullParserException, IOException {
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

	public boolean parseSuccess(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readResponse(parser);
		} finally {
			in.close();
		}
	}

	private boolean readResponse(XmlPullParser parser) throws XmlPullParserException, IOException {
		boolean response = false;
		parser.require(XmlPullParser.START_TAG, null, "eighth");
		// Consume the eighth AND signup tags
		parser.next();
		while(parser.next() != XmlPullParser.START_TAG) {
			parser.next();
		}
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			System.out.println(name);
			// Look for success tag
			if (name.equals("success")) {
				if (parser.next() == XmlPullParser.TEXT) {
					return parser.getText().equals("1");
				}
			} else {
				skip(parser);
			}
		}
		return response;
	}

	private ArrayList<EighthActivityItem> readEighth(XmlPullParser parser) throws XmlPullParserException, IOException {
		ArrayList<EighthActivityItem> entries = new ArrayList<EighthActivityItem>();
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

	private EighthActivityItem readActivity(XmlPullParser parser) throws XmlPullParserException, IOException {
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
		boolean roomChanged = false;
		ArrayList<Integer> blockSponsors = null;
		ArrayList<Integer> blockRooms = null;
		String blockRoomString = null;
		int bid = 0;
		boolean cancelled = false;
		String comment = null;
		String advertisement = null;
		boolean attendanceTaken = false;
		boolean favorite = false;
		int memberCount = 0;
		int capacity = 0;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tagName = parser.getName();
			if (tagName.equals("AID")) {
				AID = readInt(parser, "AID");
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
			} else if (tagName.equals("room_changed")) {
				roomChanged = readBool(parser, "room_changed");
			} else if (tagName.equals("block_sponsor")) {
				blockSponsors = readNestedInts(parser, "block_sponsor");
			} else if (tagName.equals("block_room")) {
				blockRooms = readNestedInts(parser, "block_room");
			} else if (tagName.equals("block_rooms_comma")) {
				blockRoomString = readString(parser, "block_rooms_comma");
			} else if (tagName.equals("bid")) {
				bid = readInt(parser, "bid");
			} else if (tagName.equals("cancelled")) {
				cancelled = readBool(parser, "cancelled");
			} else if (tagName.equals("comment")) {
				comment = readString(parser, "comment");
			} else if (tagName.equals("advertisement")) {
				advertisement = readString(parser, "advertisement");
			} else if (tagName.equals("attendancetaken")) {
				attendanceTaken = readBool(parser, "attendancetaken");
			} else if (tagName.equals("favorite")) {
				favorite = readBool(parser, "favorite");
			} else if (tagName.equals("member_count")) {
				memberCount = readInt(parser, "member_count");
			} else if (tagName.equals("capacity")) {
				capacity = readInt(parser, "capacity");
			} else {
				skip(parser);
			}
		}
		return new EighthActivityItem(AID, name, description, restricted, presign, oneaday,
				bothblocks, sticky, special, calendar, roomChanged, blockSponsors,
				blockRooms, blockRoomString, bid, cancelled, comment,
				advertisement, attendanceTaken, favorite, memberCount,
				capacity);
	}

	private String readString(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, tagName);
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		parser.require(XmlPullParser.END_TAG, null, tagName);
		return result;
	}

	private int readInt(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, tagName);
		int result = 0;
		if (parser.next() == XmlPullParser.TEXT) {
			result = Integer.parseInt(parser.getText());
			parser.nextTag();
		}
		parser.require(XmlPullParser.END_TAG, null, tagName);
		return result;
	}

	private boolean readBool(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, tagName);
		boolean result = false;
		if (parser.next() == XmlPullParser.TEXT) {
			result = Integer.parseInt(parser.getText()) == 1;
			parser.nextTag();
		}
		parser.require(XmlPullParser.END_TAG, null, tagName);
		return result;
	}

	private ArrayList<Integer> readNestedInts(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
		ArrayList<Integer> result = new ArrayList<Integer>();
		parser.require(XmlPullParser.START_TAG, null, tagName);
		while(parser.next() != XmlPullParser.END_TAG) {
			if (parser.getName().equals(tagName.substring(0, tagName.length() - 1))) {
				result.add(Integer.parseInt(parser.getText()));
			}
		}
		parser.require(XmlPullParser.END_TAG, null, tagName);
		return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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

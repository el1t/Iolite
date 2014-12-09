package com.el1t.iolite.parser;

import android.util.Log;
import android.util.Xml;

import com.el1t.iolite.item.User;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by El1t on 10/21/14.
 */
public class StudentInfoXmlParser
{
	private static final String TAG = "Student Info XML Parser";

	public static User parse(InputStream in) throws XmlPullParserException, IOException {
		// Initialize parser and jump to first tag
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.next();
			return readInfo(parser);
		} finally {
			in.close();
		}
	}

	private static User readInfo(XmlPullParser parser) throws XmlPullParserException, IOException {
		final String tag = parser.getName();
		// Not logged in, return null
		if (tag.equals("auth")) {
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
		} else if (tag.equals("studentdirectory")) {
			// Consume the studentdirectory AND info tags
			parser.next();
			while (parser.next() != XmlPullParser.START_TAG) {
				parser.next();
			}
			final User temp = new User();

			while (parser.next() != XmlPullParser.END_TAG) {
				// Skip whitespace until a tag is reached
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String tagName = parser.getName();

				if (tagName.equals("iodineuidnumber")) {
					temp.setUID(readString(parser, "iodineuidnumber"));
				} else if (tagName.equals("iodineuid")) {
					temp.setUsername(readString(parser, "iodineuid"));
				} else if (tagName.equals("givenname")) {
					temp.setFirstName(readString(parser, "givenname"));
				} else if (tagName.equals("middlename")) {
					temp.setMiddleName(readString(parser, "middlename"));
				} else if (tagName.equals("sn")) {
					temp.setLastName(readString(parser, "sn"));
				} else if (tagName.equals("street")) {
					temp.setStreet(readString(parser, "street"));
				} else if (tagName.equals("l")) {
					temp.setCity(readString(parser, "l"));
				} else if (tagName.equals("st")) {
					temp.setState(readString(parser, "st"));
				} else if (tagName.equals("postalcode")) {
					temp.setPostalCode(readString(parser, "postalcode"));
				} else if (tagName.equals("mobile")) {
					temp.setMobile(readString(parser, "mobile"));
				} else if (tagName.equals("mail")) {
					readEmails(parser, "mail", temp);
				} else if (tagName.equals("graduationyear")) {
					temp.setGradYear(readInt(parser, "graduationyear"));
				} else {
					skip(parser);
				}
			}
			parser.require(XmlPullParser.END_TAG, null, "info");

			return temp;
		} else {
			Log.e(TAG, "Parser skipped all content");
		}
		return null;
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

	// Read integers inside another tag
	private static void readEmails(XmlPullParser parser, String tagName, User user) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, tagName);
		while(parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			// Eliminates the last char (e.g. mail --> mai)
			if (parser.getName().equals(tagName.substring(0, tagName.length() - 1))) {
				if (parser.next() == XmlPullParser.TEXT) {
					user.addEmail(parser.getText());
					parser.nextTag();
				}
			} else {
				skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, null, tagName);
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

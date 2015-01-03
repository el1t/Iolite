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
		// Not logged in, return null
		switch (parser.getName()) {
			case "auth":
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

					if (parser.getName().equals("message")) {
						Log.d(TAG, readString(parser, "message"));
					} else {
						skip(parser);
					}
				}
				break;
			case "studentdirectory":
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

					switch (tagName) {
						case "iodineuidnumber":
							temp.setUID(readString(parser, "iodineuidnumber"));
							break;
						case "iodineuid":
							temp.setUsername(readString(parser, "iodineuid"));
							break;
						case "givenname":
							temp.setFirstName(readString(parser, "givenname"));
							break;
						case "middlename":
							temp.setMiddleName(readString(parser, "middlename"));
							break;
						case "sn":
							temp.setLastName(readString(parser, "sn"));
							break;
						case "street":
							temp.setStreet(readString(parser, "street"));
							break;
						case "l":
							temp.setCity(readString(parser, "l"));
							break;
						case "st":
							temp.setState(readString(parser, "st"));
							break;
						case "postalcode":
							temp.setPostalCode(readString(parser, "postalcode"));
							break;
						case "mobile":
							temp.setMobile(readString(parser, "mobile"));
							break;
						case "mail":
							readEmails(parser, "mail", temp);
							break;
						case "graduationyear":
							temp.setGradYear(readInt(parser, "graduationyear"));
							break;
						default:
							skip(parser);
							break;
					}
				}
				parser.require(XmlPullParser.END_TAG, null, "info");

				return temp;
			default:
				Log.e(TAG, "Parser skipped all content");
				break;
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


package com.test.flickr;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StackOverflowXmlParser {
	private static final String ns = null;

	private static final String RESPONSE_TAG_Link = "link";

	public List<Entry> parse(InputStream in) throws XmlPullParserException,
			IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			in.close();
		}
	}

	private List<Entry> readFeed(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		List<Entry> entries = new ArrayList<Entry>();

		parser.require(XmlPullParser.START_TAG, ns, "feed");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("entry")) {
				// entries.add(readEntry(parser));
				entries.add(parseEntryInfo(parser));
			} else {
				skip(parser);
			}
		}
		return entries;
	}

	private Entry parseEntryInfo(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		int type;
		String name, link = "", linkWithJpg = "", title = "";
		final int depth = parser.getDepth();

		while (((type = parser.next()) != XmlPullParser.END_TAG || parser
				.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
			if (type != XmlPullParser.START_TAG) {
				continue;
			}
			name = parser.getName();
			if (name.equals("title")) {
				title = readTitle(parser);
			} 
			else if (RESPONSE_TAG_Link.equals(name)) {
				link = readLink(parser);
				if (link.endsWith(".jpg")) {
					linkWithJpg = link;
				}
				Log.i("Anoop", linkWithJpg);
			}
		}
		
		return new Entry(title, linkWithJpg);
	}

	public static class Entry {
		public final String title;
		public final String link;

		private Entry(String title, String link) {
			this.title = title;
			this.link = link;
		}
	}

	private Entry readEntry(XmlPullParser parser) throws IOException {

		String title = null;
		String summary = null;
		String link = null;
		try {
			parser.require(XmlPullParser.START_TAG, ns, "entry");

			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				if (name.equals("title")) {
					title = readTitle(parser);
				} /*
				 * else if (name.equals("content")) { summary =
				 * readSummary(parser); }
				 */else if (name.equals("link")) {
					link = readLink(parser);
				} else {
					skip(parser);
				}
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return new Entry(title, summary, link);
		return new Entry(title, link);
	}

	// Processes title tags in the feed.
	private String readTitle(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "title");
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "title");
		return title;
	}

	// Processes link tags in the feed.
	private String readLink(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String link = "";
		parser.require(XmlPullParser.START_TAG, ns, "link");
		String tag = parser.getName();
		String relType = parser.getAttributeValue(null, "rel");
		if (tag.equals("link")) {
			/*
			 * if (relType.equals("alternate")) { link =
			 * parser.getAttributeValue(null, "href"); parser.nextTag(); } else
			 */if (relType.equals("enclosure")) {
				link = parser.getAttributeValue(null, "href");
				parser.nextTag();
			}

		}
		// parser.require(XmlPullParser.END_TAG, ns, "link");
		return link;
	}

	/*// Processes summary tags in the feed.
	private String readSummary(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		// parser.require(XmlPullParser.START_TAG, ns, "summary");
		// String summary = readText(parser);
		// parser.require(XmlPullParser.END_TAG, ns, "summary");
		String summary = "summary";
		return summary;
	}*/

	// For the tags title and summary, extracts their text values.
	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException,
			IOException {
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

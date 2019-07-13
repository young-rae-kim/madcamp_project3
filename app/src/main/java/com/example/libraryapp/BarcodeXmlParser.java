package com.example.libraryapp;

import android.util.Xml;
import com.example.libraryapp.BookItem;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BarcodeXmlParser {
    private static final String ns = null;

    public ArrayList<BookItem> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<BookItem> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<BookItem> entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("item")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private BookItem readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String image = null;
        String author = null;
        String publisher = null;
        String pubdate = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readItem(parser, "title");
            } else if (name.equals("image")) {
                image = readItem(parser, "image");
            } else if (name.equals("author")) {
                author = readItem(parser, "author");
            } else if (name.equals("publisher")) {
                publisher = readItem(parser, "publisher");
            } else if (name.equals("pubdate")){
                pubdate = readItem(parser, "pubdate");
            } else {
                skip(parser);
            }
        }
        return new BookItem(image, title, author, publisher, pubdate);
    }

    // Processes title tags in the feed.
    private String readItem(XmlPullParser parser, String item) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, item);
        String entry = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, item);
        return entry;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
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

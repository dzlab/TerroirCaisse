package com.terroir.caisse.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.terroir.caisse.data.DBAdapter;
import com.terroir.caisse.data.Producer;

/**
 * This class parses XML feeds from stackoverflow.com.
 * Given an InputStream representation of a feed, it returns a List of entries,
 * where each list element represents a single entry (post) in the XML feed.
 */
public class OpenDataXmlParser {
	public static final String TAG = OpenDataXmlParser.class.getSimpleName();
    private static final String ns = null;
    
    protected Context context;
    protected DBAdapter db;

    public OpenDataXmlParser(Context context) {
    	this.context = context;
    	db = new DBAdapter(context);        	
    }
    
    // Given a string representation of a URL, sets up a connection and gets an input stream.
    public InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //conn.setReadTimeout(10000 /* milliseconds */);
        //conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }
    
    // We don't use namespaces

    public List<Producer> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
        	db.open();
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
            db.close();
        }
    }
    
    private List<Producer> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Producer> entries = new ArrayList<Producer>();
        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
        	//Log.i(TAG, "readFeed- parser name "+parser.getName()+" parser text: "+parser.getText());
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                //entries.add(readEntry(parser));
            } else if(name.equals("content")) {            	
            	Producer producer = readContent(parser); 
            	// Skip alcohol
            	if(!(producer.sous_type!=null && producer.sous_type.toUpperCase().contains("VIN"))) {
            		db.insert(producer);
                	entries.add(producer);
            	}
            	
            } else {
                skip(parser);
            }
        }
        return entries;
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
    
 // Processes summary tags in the feed.
    private Producer readContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "content");
        parser.nextTag(); // jump to the child element
        
        Producer producer = new Producer();
        String numro = "";
        String type_voie = "";
        String voie = "";
        while (parser.next() != XmlPullParser.END_TAG) {        
        	//Log.i(TAG, "readContent- parser name "+parser.getName()+" parser text: "+parser.getText());
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("d:raisonsociale")) {
                producer.raison_social = readText(parser);
            } else if (name.equals("d:soustype")) {
            	producer.sous_type = readText(parser);
            } else if (name.equals("d:tlphone")) {
            	producer.telephone = readText(parser).replace(" ", "");
            } else if(name.equals("d:mail")) {
            	producer.mail = readText(parser);            	
            } else if(name.equals("d:numro")) {
            	numro = readText(parser);            	
            } else if(name.equals("d:typedevoie")) {
            	type_voie = readText(parser);            	
            } else if(name.equals("d:voie")) {
            	voie = readText(parser);            	
            } else if(name.equals("d:ville")) {
            	producer.ville = readText(parser);            	
            } else if(name.equals("d:codepostal")) {
            	producer.code_postal = readText(parser);            	
            } else if(name.equals("d:latitude")) {
            	producer.latitude = Double.parseDouble(readText(parser));            	
            } else if(name.equals("d:longitude")) {
            	producer.longitude = Double.parseDouble(readText(parser));            	
            } else {
            	//Log.i(TAG, "Skiping a tag"+parser.toString());
            	skip(parser);
            }
        }
        if(!numro.equals("") && !type_voie.equals("") && !voie.equals(""))
        	producer.address = numro + " " + type_voie + " " + voie;
        //Log.i(TAG, producer.toString());
        parser.nextTag(); // jump to the child element
        parser.require(XmlPullParser.END_TAG, ns, "content");        
        parser.nextTag();
        return producer;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
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
    /*
    private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Entry> entries = new ArrayList<Entry>();

        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }
*/
    // This class represents a single entry (post) in the XML feed.
    // It includes the data members "title," "link," and "summary."
/*
    public static class Entry {
        public final String title;
        public final String link;
        public final String summary;

        private Entry(String title, String summary, String link) {
            this.title = title;
            this.summary = summary;
            this.link = link;
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
    // off
    // to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String title = null;
        String summary = null;
        String link = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("summary")) {
                summary = readSummary(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
            } else {
                skip(parser);
            }
        }
        return new Entry(title, summary, link);
    }
*/
}

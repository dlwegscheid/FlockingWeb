package edu.msu.kingfisher.flocking;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Cloud {
    private final static String LOGIN_URL = "http://webdev.cse.msu.edu/~merril51/cse476/project2/birdgame-login.php";
    private final static String REGISTER_URL = "http://webdev.cse.msu.edu/~merril51/cse476/project2/birdgame-createuser.php";
    private final static String SAVE_URL = "";
    private final static String LOAD_URL = "";
    private final static String POLL_URL = "http://webdev.cse.msu.edu/~merril51/cse476/project2/birdgame-poll.php";
    private final static String MAGIC = "18qu27wy36et45r";
    private static final String UTF8 = "UTF-8";

    public enum Status {
        GOOD, BAD_LOGIN, BAD_CONNECTION, DUPLICATE_LOGIN, UPDATE
    }

    public Status loginRegister(String user, String password, boolean register) {
        // Create a get query
        String query;
        if (register) {
            query = REGISTER_URL;
        } else {
            query = LOGIN_URL;
        }
        query += "?user=" + user + "&password=" + password + "&magic=" + MAGIC;

        InputStream stream = null;
        Status status;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return Status.BAD_CONNECTION;
            }

            stream = new InputStreamIntercept(conn.getInputStream());
            //logStream(stream);
            status = isGoodResult(stream);

        } catch (MalformedURLException e) {
            // Should never happen
            return Status.BAD_CONNECTION;
        } catch (IOException ex) {
            return Status.BAD_CONNECTION;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ex) {
                    // Fail silently
                }
            }
        }

        return status;
    }

    public InputStream pollLoad(Game game, String user, String pass, boolean gameOver) {
        // Create an XML packet with the information about the current game
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            xml.setOutput(writer);

            xml.startDocument("UTF-8", true);
            game.saveXml(xml);
            xml.endDocument();

        } catch (IOException e) {
            // This won't occur when writing to a string
            return null;
        }

        final String xmlStr = writer.toString();

        /*
         * Convert the XML into HTTP POST data
         */
        String postDataStr;
        postDataStr = "user=&pass=&winner=null&xml=test";

        /*try {
            postDataStr = "xml=" + URLEncoder.encode(xmlStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }*/

        /*
         * Send the data to the server
         */
        byte[] postData = postDataStr.getBytes();

        InputStream stream = null;
        try {
            URL url = new URL(POLL_URL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            OutputStream out = conn.getOutputStream();
            out.write(postData);
            out.close();

            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            stream = conn.getInputStream();

        } catch (MalformedURLException e) {
            return null;
        } catch (IOException ex) {
            return null;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ex) {
                    // Fail silently
                }
            }
        }
        return stream;
    }

    public Status isGoodResult(InputStream stream) {
        /**
         * Create an XML parser for the result
         */
        try {
            XmlPullParser xmlR = Xml.newPullParser();
            xmlR.setInput(stream, UTF8);

            xmlR.nextTag();      // Advance to first tag
            xmlR.require(XmlPullParser.START_TAG, null, "birdgame");

            String status = xmlR.getAttributeValue(null, "status");
            if(status.equals("yes")) {
                return Status.GOOD;
            } else if(status.equals("no")) {
                if (xmlR.getAttributeValue(null, "msg").equals("duplicateusername")) {
                    return Status.DUPLICATE_LOGIN;
                }
                return Status.BAD_LOGIN;
            } else if(status.equals("update")) {
                return Status.UPDATE;
            } else {
                return Status.BAD_CONNECTION;
            }

        } catch(XmlPullParserException ex) {
            return Status.BAD_CONNECTION;
        } catch(IOException ex) {
            return Status.BAD_CONNECTION;
        }
    }

    public static void logStream(InputStream stream) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream));

        Log.e("476", "logStream: If you leave this in, code after will not work!");
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                Log.e("476", line);
            }
        } catch (IOException ex) {
            return;
        }
    }

    /**
     * Skip the XML parser to the end tag for whatever
     * tag we are currently within.
     * @param xml the parser
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static void skipToEndTag(XmlPullParser xml)
            throws IOException, XmlPullParserException {
        int tag;
        do
        {
            tag = xml.next();
            if(tag == XmlPullParser.START_TAG) {
                // Recurse over any start tag
                skipToEndTag(xml);
            }
        } while(tag != XmlPullParser.END_TAG &&
                tag != XmlPullParser.END_DOCUMENT);
    }
}

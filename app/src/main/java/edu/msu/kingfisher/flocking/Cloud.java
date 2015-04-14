package edu.msu.kingfisher.flocking;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Cloud {
    private final static String LOGIN_URL = "http://webdev.cse.msu.edu/~merril51/cse476/project2/birdgame-login.php";
    private final static String REGISTER_URL = "http://webdev.cse.msu.edu/~merril51/cse476/project2/birdgame-createuser.php";
    private final static String SAVE_URL = "";
    private final static String LOAD_URL = "";
    private final static String MAGIC = "18qu27wy36et45r";
    private static final String UTF8 = "UTF-8";

    public enum Status {
        GOOD, BAD_LOGIN, BAD_CONNECTION, DUPLICATE_LOGIN
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

            stream = conn.getInputStream();
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

    public Status register(String user, String password) {
        // Create a get query
        String query = REGISTER_URL + "?user=" + user + "&password=" + password + "&magic=" + MAGIC;
        InputStream stream = null;
        Status status;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return Status.BAD_CONNECTION;
            }

            stream = conn.getInputStream();
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

    public static Status isGoodResult(InputStream stream) {
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
                if(xmlR.getAttributeValue(null, "msg").equals("duplicateusername")) {
                    return Status.DUPLICATE_LOGIN;
                }
                return Status.BAD_LOGIN;
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
}

package edu.msu.kingfisher.flocking;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

/**
 * This custom stream filter can intercept data being
 * sent to another stream and send it to LogCat. It is
 * useful as a way to tell what is being sent to an
 * Xml parser.
 */
public class InputStreamIntercept extends FilterInputStream {

    /**
     * The string we build from data we input.
     */
    private StringBuilder sb = new StringBuilder();

    /**
     * Constructor.
     * @param in the stream we are intercepting
     */
    public InputStreamIntercept(InputStream in) {
        super(in);
    }

    /**
     * Intercept read() and store the character for output
     * @return -1 if nothing read or the character read
     */
    @Override
    public int read() throws IOException {
        int ret = super.read();
        if(ret >= 0) {
            newChar((byte)ret);
        }
        return ret;
    }

    /**
     * Intercept read and output the content to Log.i.
     */
    @Override
    public int read(byte[] buffer, int offset, int count) throws IOException {
        int ret = super.read(buffer, offset, count);
        for(int i=0;  i<ret;  i++) {
            newChar(buffer[i]);
        }
        return ret;
    }


    /**
     * Handle a new character. We output whenever we get a newline
     * @param ch char
     */
    private void newChar(byte ch) {
        if(ch == 10) {
            String str = sb.toString();
            if(str.equals("")) {
                Log.i("476", "--blank line--");
            } else {
                Log.i("476", str);
            }

            sb = new StringBuilder();
        } else if(ch == 13) {
            // Skip this character
        } else {
            sb.append((char)ch);
        }
    }

    /**
     * Close the stream
     */
    @Override
    public void close() throws IOException {
        super.close();
        if(sb.length() > 0) {
            Log.i("476", sb.toString());
        }
    }

}

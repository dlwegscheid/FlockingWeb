package edu.msu.kingfisher.flocking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class PollingDlg extends DialogFragment {
    private Game game;
    private volatile boolean close = false;

    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        close = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the title
        builder.setTitle(R.string.polling);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.polling_dlg, null);
        builder.setView(view);

        builder.setNegativeButton(R.string.quit_game, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                close = true;
            }
        });

        final AlertDialog dlg = builder.create();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                while(!close) {
                    InputStream stream = cloud.pollLoad();

                    if(close) {
                        return;
                    }

                    // Test for an error
                    boolean fail = stream == null;
                    if(!fail) {
                        try {
                            XmlPullParser xml = Xml.newPullParser();
                            xml.setInput(stream, "UTF-8");

                            //Cloud.logStream(stream);

                            xml.nextTag();      // Advance to first tag
                            xml.require(XmlPullParser.START_TAG, null, "birdgame");
                            String status = xml.getAttributeValue(null, "status");
                            if(status.equals("yes")) {
                                close = true;
                            } else if(status.equals("update")) {
                                if (xml.nextTag() == XmlPullParser.START_TAG) {
                                    if (close) {
                                        return;
                                    }

                                    // do something with the hatting tag...
                                    game.loadXml(xml);
                                    break;
                                }
                            } else if(status.equals("no")) {
                                try {
                                    Thread.sleep(5000);
                                } catch(InterruptedException e) {
                                    close = true;
                                }
                            } else {
                                fail = true;
                            }
                        } catch(IOException ex) {
                            fail = true;
                        } catch(XmlPullParserException ex) {
                            fail = true;
                        } finally {
                            try {
                                stream.close();
                            } catch(IOException ex) {
                                //fail silently
                            }
                        }
                    }
                }
            }
        }).start();

        return dlg;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}


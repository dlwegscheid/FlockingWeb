package edu.msu.kingfisher.flocking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class PollingDlg extends DialogFragment {
    private volatile Game game = null;
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
        GameActivity activity = (GameActivity)getActivity();
        game = activity.getGame();
        final String userName = activity.getUserName();

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean fail = false;
                Cloud cloud = new Cloud();
                while(!close) {
                    InputStream stream = cloud.pollLoad(userName);

                    if(close) {
                        return;
                    }

                    // Test for an error
                    fail = stream == null;
                    if(!fail) {
                        try {
                            XmlPullParser xml = Xml.newPullParser();
                            //Cloud.logStream(stream);

                            xml.setInput(stream, "UTF-8");

                            xml.nextTag();      // Advance to first tag
                            xml.require(XmlPullParser.START_TAG, null, "birdgame");
                            String status = xml.getAttributeValue(null, "status");
                            if(status.equals("yes")) {
                                game.setLastPlace(true);
                                close = true;
                                final Activity activity = getActivity();
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(activity, SelectionActivity.class);
                                        activity.startActivityForResult(intent, 1);
                                    }
                                });
                            } else if(status.equals("update")) {
                                if (xml.nextTag() == XmlPullParser.START_TAG) {
                                    if (close) {
                                        return;
                                    }

                                    // do something with the game xml
                                    game.loadXml(xml);

                                    final Activity activity = getActivity();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(activity, SelectionActivity.class);
                                            activity.startActivityForResult(intent, 1);
                                        }
                                    });
                                    close = true;
                                    break;
                                }
                            } else if(status.equals("no")) {
                                try {
                                    Thread.sleep(5000);
                                } catch(InterruptedException e) {
                                    close = true;
                                    fail = true;
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
                dlg.dismiss();

            }
        }).start();

        return dlg;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}


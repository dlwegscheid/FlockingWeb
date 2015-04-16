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
    private volatile Game game = null;
    private volatile boolean close = false;

    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        close = false;
        GameActivity activity = (GameActivity)getActivity();
        game = activity.getGame();

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
                game.end();
                close = true;
            }
        });

        final AlertDialog dlg = builder.create();
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
                                game.advanceGame(-1);
                                close = true;
                            } else if(status.equals("update")) {
                                if (xml.nextTag() == XmlPullParser.START_TAG) {
                                    if (close) {
                                        return;
                                    }

                                    // do something with the game xml
                                    game.setState(Game.State.POLLING);
                                    game.loadXml(xml);
                                    game.advanceGame(-1);
                                    close = true;
                                }
                            } else if(status.equals("gameover")) {
                                String user = xml.getAttributeValue(null, "winner");
                                if (xml.nextTag() == XmlPullParser.START_TAG) {
                                    if (close) {
                                        return;
                                    }

                                    // do something with the game xml
                                    game.loadXml(xml);
                                    game.setWinner(user.equals(game.getUserName()));
                                    game.end();
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
                    if(fail) {
                        try {
                            Thread.sleep(5000);
                        } catch(InterruptedException ex) {
                            close = true;
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


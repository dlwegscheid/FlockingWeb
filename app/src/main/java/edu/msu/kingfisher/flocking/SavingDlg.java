package edu.msu.kingfisher.flocking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class SavingDlg extends DialogFragment {
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
        builder.setTitle(R.string.saving);

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
                Cloud cloud = new Cloud();

                while(cloud.pollSave(game, userName) != Cloud.Status.GOOD) {
                    if(close) {
                        return;
                    }
                    try {
                        Thread.sleep(5000);
                    } catch(InterruptedException ex) {
                        close = true;
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((GameActivity)getActivity()).startPolling();
                    }
                });

                dlg.dismiss();
            }
        }).start();

        return dlg;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}


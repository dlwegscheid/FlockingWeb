package edu.msu.wegschei.flocking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

public class LoginDlg extends DialogFragment {
    private String user;
    private String password;
    private boolean register = false;

    private final static String USER_NAME = "LoginDlg.userName";
    private final static String PASSWORD = "LoginDlg.password";
    private final static String REGISTER = "LoginDlg.register";

    /**
     * Set true if we want to cancel
     */
    private volatile boolean cancel = false;

    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        if (bundle != null) {
            user = bundle.getString(USER_NAME);
            password = bundle.getString(PASSWORD);
            register = bundle.getBoolean(REGISTER);
        }
        cancel = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        if(register) {
            builder.setTitle(R.string.registering);
        } else {
            builder.setTitle(R.string.logging_in);
        }

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                cancel = true;
            }
        });

        final AlertDialog dlg = builder.create();
        final boolean reg = register;

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Create a cloud object and get the XML
                Cloud cloud = new Cloud();
                final Cloud.Status status = cloud.loginRegister(user, password, register);

                if(cancel) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (status) {
                            case BAD_LOGIN:
                                Toast.makeText(getActivity(), R.string.login_failure, Toast.LENGTH_SHORT).show();
                                break;
                            case BAD_CONNECTION:
                                Toast.makeText(getActivity(), R.string.server_failure, Toast.LENGTH_SHORT).show();
                                break;
                            case DUPLICATE_LOGIN:
                                Toast.makeText(getActivity(), R.string.duplicate_username, Toast.LENGTH_SHORT).show();
                                break;
                            case GOOD:
                                if(reg) {
                                    Intent intent = new Intent(getActivity(), MainActivity.class);

                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(getActivity(), GameActivity.class);
                                    intent.putExtra(USER_NAME, user);

                                    startActivity(intent);
                                }
                                break;
                        }
                    }
                });
                dlg.dismiss();
            }
        }).start();

        return dlg;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putString(USER_NAME, user);
        bundle.putString(PASSWORD, password);
        bundle.putBoolean(REGISTER, register);
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRegister(boolean register) {
        this.register = register;
    }
}

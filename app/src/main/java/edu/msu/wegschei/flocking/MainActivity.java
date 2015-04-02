package edu.msu.wegschei.flocking;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
// magic = 18qu27wy36et45r

public class MainActivity extends ActionBarActivity {
    private EditText textUserName;
    private EditText textPassword;

    private final static String USER_NAME = "MainActivity.userName";
    private final static String PASSWORD = "MainActivity.password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.textUserName = (EditText)findViewById(R.id.textUserName);
        this.textPassword = (EditText)findViewById(R.id.textPassword);

        if(savedInstanceState != null) {
            // We have saved state
            loadInstanceState(savedInstanceState);
        }
    }

    public void loadInstanceState(Bundle bundle) {
        String userName = bundle.getString(USER_NAME);
        String password = bundle.getString(PASSWORD);

        textUserName.setText(userName, TextView.BufferType.EDITABLE);
        textPassword.setText(password, TextView.BufferType.EDITABLE);

    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        String userName = textUserName.getText().toString();
        String password = textPassword.getText().toString();

        bundle.putString(USER_NAME, userName);
        bundle.putString(PASSWORD, password);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_rules:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // Parameterize the builder
                builder.setTitle(R.string.rules);
                builder.setMessage(R.string.rules_text);
                builder.setPositiveButton(android.R.string.ok, null);

                // Create the dialog box and show it
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onLogin(View view){
        LoginDlg loginDlg = new LoginDlg();
        loginDlg.setUser(textUserName.getText().toString());
        loginDlg.setPassword(textPassword.getText().toString());
        loginDlg.setRegister(false);
        loginDlg.show(this.getFragmentManager(), "logging_in");
    }
    public void onRegister(View view){
        LoginDlg loginDlg = new LoginDlg();
        loginDlg.setUser(textUserName.getText().toString());
        loginDlg.setPassword(textPassword.getText().toString());
        loginDlg.setRegister(true);
        loginDlg.show(this.getFragmentManager(), "registering");
    }
}

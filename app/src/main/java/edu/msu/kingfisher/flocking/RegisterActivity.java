package edu.msu.kingfisher.flocking;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends ActionBarActivity {
    private EditText textUserName;
    private EditText textPassword;
    private EditText textPasswordConfirm;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String PASSWORD_CONFIRM = "passwordConfirm";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_register);

        textUserName = (EditText)findViewById(R.id.textUserName);
        textPassword = (EditText)findViewById(R.id.textPassword);
        textPasswordConfirm = (EditText)findViewById(R.id.textConfirmPassword);

        if(bundle != null) {
            textUserName.setText(bundle.getString(USERNAME));
            textPassword.setText(bundle.getString(PASSWORD));
            textPasswordConfirm.setText(bundle.getString(PASSWORD_CONFIRM));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putString(textUserName.getText().toString(), USERNAME);
        bundle.putString(textPassword.getText().toString(), PASSWORD);
        bundle.putString(textPasswordConfirm.getText().toString(), PASSWORD_CONFIRM);
    }

    public void onRegister(View view){
        String password = textPassword.getText().toString();
        String passwordConfirm = textPasswordConfirm.getText().toString();

        if(password.equals(passwordConfirm)) {
            LoginDlg loginDlg = new LoginDlg();
            loginDlg.setUser(textUserName.getText().toString());
            loginDlg.setPassword(password);
            loginDlg.setRegister(true);
            loginDlg.show(this.getFragmentManager(), "registering");
        } else {
            Toast.makeText(this, R.string.passwords_dont_match, Toast.LENGTH_SHORT).show();
        }
    }
}

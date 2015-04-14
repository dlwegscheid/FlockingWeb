package edu.msu.kingfisher.flocking;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends ActionBarActivity {

    /**
     * The game view in this activity's view
     */
    private GameView gameView;
    private Game game;
    private TextView textView;
    private Button placeButton;

    public final static String MESSAGE_TEXT = "GameActivity.messageText";
    public final static String BUTTON_TEXT = "GameActivity.buttonText";

    private String playerNameOne;
    private String playerNameTwo;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_game);

        gameView = (GameView)this.findViewById(R.id.gameView);
        game = gameView.getGame();
        placeButton = (Button) findViewById(R.id.buttonPlace);
        textView = (TextView)findViewById(R.id.textPlayer);

        Bundle extras = getIntent().getExtras();
        playerNameOne = extras.getString(LoginDlg.USER_NAME);
        playerNameTwo = extras.getString(LoginDlg.PASSWORD);
        game.setUser(playerNameOne, playerNameTwo);

        if(bundle != null) {
            gameView.loadInstanceState(bundle);
        } else {
            //gameView.advanceGame(-1);
            startPolling();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPlace(View view) {
        gameView.onPlace();

        Game.State newState = game.getState();
        if(newState ==  Game.State.PLAYER_ONE_WON) {
            textView.setText(playerNameOne + " wins!");
            placeButton.setText("Continue");
        } else if (newState ==  Game.State.PLAYER_TWO_WON) {
            textView.setText(playerNameTwo + " wins!");
            placeButton.setText("Continue");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putString(MESSAGE_TEXT, textView.getText().toString());
        bundle.putString(BUTTON_TEXT, placeButton.getText().toString());
        super.onSaveInstanceState(bundle);
        gameView.saveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        textView.setText(savedInstanceState.getString(MESSAGE_TEXT));
        placeButton.setText(savedInstanceState.getString(BUTTON_TEXT));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPolling() {
        PollingDlg pollDlg = new PollingDlg();
        pollDlg.setGame(gameView.getGame());
        pollDlg.show(this.getFragmentManager(), "polling");
    }
}
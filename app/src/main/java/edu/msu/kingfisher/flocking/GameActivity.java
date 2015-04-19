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
import android.widget.Toast;

public class GameActivity extends ActionBarActivity {

    /**
     * The game view in this activity's view
     */
    private GameView gameView;
    private Game game;
    private TextView textView;
    private Button placeButton;

    private final static String MESSAGE_TEXT = "GameActivity.messageText";
    private final static String BUTTON_TEXT = "GameActivity.buttonText";

    private String userName;
    private String password;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_game);

        gameView = (GameView)this.findViewById(R.id.gameView);
        game = gameView.getGame();
        placeButton = (Button) findViewById(R.id.buttonPlace);
        textView = (TextView)findViewById(R.id.textPlayer);

        Bundle extras = getIntent().getExtras();
        userName = extras.getString(LoginDlg.USER_NAME);
        password = extras.getString(LoginDlg.PASSWORD);
        game.setUser(userName, password);

        if(bundle != null) {
            gameView.loadInstanceState(bundle);
        } else {
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
        if (id == R.id.action_quit) {
            game.setWinner(false);

            SavingDlg saveDlg = new SavingDlg();
            saveDlg.setGame(game);
            saveDlg.show(this.getFragmentManager(), "saving");

            game.end();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPlace(View view) {
        if(game.getState() == Game.State.END) {
            game.end();
        }

        CharSequence text;

        if(game.canPlace()) {
            text = "Bird Placed";
            game.advanceGame(-1);
        } else {
            text = "Invalid Placement";
            game.setWinner(false);

            SavingDlg saveDlg = new SavingDlg();
            saveDlg.setGame(game);
            saveDlg.show(this.getFragmentManager(), "saving");

            game.end();
        }
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
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

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
            int birdID = extras.getInt("BirdImageID");
            textView.setText("Place your bird!");
            game.advanceGame(birdID);
        }
    }

    public void startPolling() {
        PollingDlg pollDlg = new PollingDlg();
        pollDlg.setGame(game);
        pollDlg.show(this.getFragmentManager(), "polling");
    }

    public Game getGame() {
        return game;
    }

    public String getUserName() {
        return userName;
    }
}
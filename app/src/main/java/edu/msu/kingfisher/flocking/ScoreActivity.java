package edu.msu.kingfisher.flocking;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class ScoreActivity extends ActionBarActivity {

    private final static String SCORE = "ScoreActivity.score";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int birdsPlaced;

        TextView textBirdsPlaced;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Bundle extras = getIntent().getExtras();
        birdsPlaced = extras.getInt(SCORE);

        textBirdsPlaced = (TextView)findViewById(R.id.textBirdsPlaced);
        textBirdsPlaced.setText("Birds placed: " + birdsPlaced);
    }

    public void onReset(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

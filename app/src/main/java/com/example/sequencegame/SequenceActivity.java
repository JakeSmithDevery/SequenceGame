package com.example.sequencegame;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SequenceActivity extends AppCompatActivity {
    private TextView sequenceText;
    private String[] colors = {"Red", "Blue", "Green", "Yellow"};
    private List<String> currentSequence = new ArrayList<>();
    private int sequenceLength = 4; // length of the sequence

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_sequence);

        sequenceText = findViewById(R.id.tvSequence);

        // receive the sequence from GameActivity
        ArrayList<String> receivedSequence = getIntent().getStringArrayListExtra("sequence");

        // if sequence was passed from GameActivity use it
        if (receivedSequence != null && !receivedSequence.isEmpty()) {
            currentSequence = receivedSequence; // Set current sequence to received sequence
            // extend the sequence
            extendSequence(2); // add 2 colors to the sequence
        } else {
            // no sequence was received
            generateSequence(sequenceLength); //initial sequence
        }

        // display the sequence
        displaySequence();

        // wait for 3 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SequenceActivity.this, GameActivity.class);
            intent.putExtra("score", getIntent().getIntExtra("score", 0)); //score to GameActivity
            intent.putStringArrayListExtra("sequence", (ArrayList<String>) currentSequence); //sequence to GameActivity
            startActivity(intent);
            finish();
        }, 3000);
    }

    //generate the initial sequence
    private void generateSequence(int length) {
        Random random = new Random();
        currentSequence.clear(); // clear previous sequence
        for (int i = 0; i < length; i++) {
            // add a random color to the sequence
            currentSequence.add(colors[random.nextInt(colors.length)]);
        }
    }

    //extend the sequence
    private void extendSequence(int length) {
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // add new random colors to the sequence
            currentSequence.add(colors[random.nextInt(colors.length)]);
        }
    }

    //display the sequence in the TextView
    private void displaySequence() {
        StringBuilder sequenceDisplay = new StringBuilder("Sequence: ");
        for (String color : currentSequence) {
            sequenceDisplay.append(color).append(", ");
        }
        sequenceText.setText(sequenceDisplay.toString().trim());
    }
}

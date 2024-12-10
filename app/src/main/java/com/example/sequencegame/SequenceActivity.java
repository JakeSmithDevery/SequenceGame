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
    public int SequenceLenght = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_sequence);



        sequenceText = findViewById(R.id.tvSequence);
        generateSequence(SequenceLenght); // Initial sequence of 4 colors
        displaySequence();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SequenceActivity.this, GameActivity.class);
            //intent.putExtra("score", getIntent().getIntExtra("score", 0)); // Pass the score back
            //SequenceLenght = getIntent().getIntExtra("SequenceLength", 0);
            intent.putStringArrayListExtra("sequence", (ArrayList<String>) currentSequence);
            startActivity(intent);
        }, 3000); // Show sequence for 3 seconds
    }

    private void generateSequence(int length) {
        Random random = new Random();
        currentSequence.clear();
        for (int i = 0; i < length; i++) {
            currentSequence.add(colors[random.nextInt(colors.length)]);
        }
    }

    private void displaySequence() {
        StringBuilder sequenceDisplay = new StringBuilder("Sequence: ");
        for (String color : currentSequence) {
            sequenceDisplay.append(color).append(", ");
        }
        sequenceText.setText(sequenceDisplay.toString().trim());
    }
}

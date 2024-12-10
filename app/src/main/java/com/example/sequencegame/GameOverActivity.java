package com.example.sequencegame;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameOverActivity extends AppCompatActivity {
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_game_over);

        Button playButton = findViewById(R.id.btnPlayAgain);
        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
            startActivity(intent);
        });

        int score = getIntent().getIntExtra("score", 0);
        TextView scoreText = findViewById(R.id.tvFinalScore);
        scoreText.setText("Score: " + score);

        dbHelper = new DBHelper(this);

        // check if score is in top 5
        if (isTop5Score(score)) {
            promptForNameAndSaveScore(score);
        }

        Button viewHighScoresButton = findViewById(R.id.btnViewHiScores);
        viewHighScoresButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameOverActivity.this, HighScoresActivity.class);
            startActivity(intent);
        });
    }

    private boolean isTop5Score(int score) {
        Cursor cursor = dbHelper.getHighScores();
        boolean isTop5 = false;

        try {
            if (cursor != null) {
                List<Integer> highScores = new ArrayList<>();

                // ensure columns exist
                int scoreIndex = cursor.getColumnIndex("score");
                if (scoreIndex == -1) {
                    throw new IllegalArgumentException("Column 'score' not found in the database.");
                }

                // get scores
                while (cursor.moveToNext()) {
                    highScores.add(cursor.getInt(scoreIndex));
                }

                // check if the current score is in top 5
                Collections.sort(highScores, Collections.reverseOrder());
                isTop5 = highScores.size() < 5 || highScores.get(4) < score;
            }
        } finally {
            if (cursor != null) {
                cursor.close(); //close the cursor to prevent memory leaks
            }
        }

        return isTop5;
    }

    private void promptForNameAndSaveScore(int score) {
        // ask for player name
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Your Name");

        //EditText to enter the name
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String playerName = input.getText().toString();
            if (!playerName.isEmpty()) {
                dbHelper.addHighScore(playerName, score);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}

package com.example.sequencegame;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_game_over);

        int score = getIntent().getIntExtra("score", 0);
        TextView scoreText = findViewById(R.id.tvFinalScore);
        scoreText.setText("Score: " + score);

        dbHelper = new DBHelper(this);
        dbHelper.addHighScore("Player", score);

        Button viewHighScoresButton = findViewById(R.id.btnViewHiScores);
        viewHighScoresButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameOverActivity.this, HighScoresActivity.class);
            startActivity(intent);
        });
    }
}

package com.example.sequencegame;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class HighScoresActivity extends AppCompatActivity {
    private ListView highScoresListView;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_high_scores);

        Button playButton = findViewById(R.id.btnBackToMain);
        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(HighScoresActivity.this, MainActivity.class);
            startActivity(intent);
        });

        highScoresListView = findViewById(R.id.lvHighScores); // ensure the ID matches the XML layout
        dbHelper = new DBHelper(this);

        // retrieve and display high scores
        Cursor cursor = dbHelper.getHighScores();

        if (cursor != null && cursor.getCount() > 0) {
            List<String> highScores = new ArrayList<>();

            //column index fetching
            int nameIndex = cursor.getColumnIndex("name");
            int scoreIndex = cursor.getColumnIndex("score");

            // validate column indexes
            if (nameIndex >= 0 && scoreIndex >= 0) {
                cursor.moveToFirst(); // move to the first row

                do {
                    String name = cursor.getString(nameIndex);
                    int score = cursor.getInt(scoreIndex);
                    highScores.add(name + " - " + score);
                } while (cursor.moveToNext()); // move to the next row

                //adapter for the ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, highScores);
                highScoresListView.setAdapter(adapter);
            } else {
                Log.e("HighScoresActivity", "Invalid column indexes. Check column names in database query.");
            }

            cursor.close(); // close the cursor
        } else {
            Log.d("HighScoresActivity", "No high scores found.");

        }
    }
}

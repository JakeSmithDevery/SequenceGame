package com.example.sequencegame;


import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
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
        setContentView(R.layout.activity_high_scores);

        highScoresListView = findViewById(R.id.lvHighScores); // Ensure the ID matches your XML layout
        dbHelper = new DBHelper(this);

        // Retrieve and display high scores
        Cursor cursor = dbHelper.getHighScores();

        if (cursor != null && cursor.getCount() > 0) {
            List<String> highScores = new ArrayList<>();

            // Safe column index fetching
            int nameIndex = cursor.getColumnIndex("name");
            int scoreIndex = cursor.getColumnIndex("score");

            // Validate column indexes
            if (nameIndex >= 0 && scoreIndex >= 0) {
                cursor.moveToFirst(); // Move to the first row

                do {
                    String name = cursor.getString(nameIndex);
                    int score = cursor.getInt(scoreIndex);
                    highScores.add(name + " - " + score);
                } while (cursor.moveToNext()); // Move to the next row

                // Set up the adapter for the ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, highScores);
                highScoresListView.setAdapter(adapter);
            } else {
                Log.e("HighScoresActivity", "Invalid column indexes. Check column names in database query.");
            }

            cursor.close(); // Close the cursor after use
        } else {
            Log.d("HighScoresActivity", "No high scores found.");
            // Optionally show a message on the screen or handle the empty list
        }
    }
}

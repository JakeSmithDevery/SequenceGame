package com.example.sequencegame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "highScoresDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS scores (id INTEGER PRIMARY KEY, name TEXT, score INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS scores");
        onCreate(db);
    }


    // add a high score to the database
    public void addHighScore(String name, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("score", score);
        db.insert("scores", null, values);

    }

    public Cursor getHighScores() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("scores", new String[]{"name", "score"}, null, null, null, null, "score DESC", "5");

        // check if cursor is null
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }
}

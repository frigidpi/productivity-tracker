package com.example.android.productivitytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.productivitytracker.TaskContract.TaskEntry;

/**
 * Created by duncan on 28/11/15.
 */
public class TaskDbHelper extends AbstractDbHelper {
    static {
        SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                        TaskEntry._ID + " INTEGER PRIMARY KEY," +
                        TaskEntry.COLUMN_NAME_CATEGORY_ID + TEXT_TYPE + COMMA_SEP +
                        TaskEntry.COLUMN_NAME_DURATION + INT_TYPE +
                        " )";
        SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;
    }


    public TaskDbHelper(Context context) {
        super(context);
    }

    public void insertTask(int categoryId, int duration) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_CATEGORY_ID, categoryId);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DURATION, duration);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(
                TaskContract.TaskEntry.TABLE_NAME,
                null,
                values);
    }

    /**
     * Calculate the total score for all tasks.
     */
    public int calculateScore() {
        // Gets the data repository in read mode
        SQLiteDatabase db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {TaskContract.TaskEntry.COLUMN_NAME_DURATION};

        String selection = null;
        String[] selectionArgs = null;

        Cursor c = db.query(
                TaskContract.TaskEntry.TABLE_NAME, projection,
                selection,     // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, null, null
        );

        Integer sum = 0;
        // Cursors start at position -1.
        while (c.moveToNext()) {
            int duration = c.getInt(c.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_DURATION));
            sum += duration;
        }

        return sum;
    }

    public void deleteAll(SQLiteDatabase db) {
        //db.execSQL(SQL_DELETE_ENTRIES);
        db.delete(TaskEntry.TABLE_NAME,null,null);  
    }
}

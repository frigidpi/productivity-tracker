package com.example.android.productivitytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by duncan on 28/11/15.
 */
public class CategoryDbHelper extends AbstractDbHelper {
    static {
        SQL_CREATE_ENTRIES =
                "CREATE TABLE " + CategoryContract.CategoryEntry.TABLE_NAME + " (" +
                        CategoryContract.CategoryEntry._ID + " INTEGER PRIMARY KEY," +
                        CategoryContract.CategoryEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                        CategoryContract.CategoryEntry.COLUMN_NAME_WEIGHT + INT_TYPE + COMMA_SEP +
                        CategoryContract.CategoryEntry.COLUMN_NAME_GOAL + INT_TYPE + COMMA_SEP +
                        CategoryContract.CategoryEntry.COLUMN_NAME_LIMIT + INT_TYPE +
                        " )";
        SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + CategoryContract.CategoryEntry.TABLE_NAME;

    }

    public CategoryDbHelper(Context context) {
        super(context);
    }

    /**
     * Insert a new category.
     */
    public void insertCategory(String name) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CategoryContract.CategoryEntry.COLUMN_NAME_NAME, name);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(
                TaskContract.TaskEntry.TABLE_NAME,
                null,
                values);
    }

    /**
     * Gets all the categories in the database as an ArrayList.
     */
    public ArrayList<String> getAllCategories(String name) {
        // Gets the data repository in read mode
        SQLiteDatabase db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {CategoryContract.CategoryEntry.COLUMN_NAME_NAME};

        String selection = null;
        String[] selectionArgs = null;

        Cursor c = db.query(
                CategoryContract.CategoryEntry.COLUMN_NAME_NAME, projection,
                selection,     // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, null, null
        );

        ArrayList<String> categories = new ArrayList<>();
        // Cursors start at position -1.
        while (c.moveToNext()) {
            String category = c.getString(c.getColumnIndex(CategoryContract.CategoryEntry.COLUMN_NAME_NAME));
            categories.add(category);
        }
        return categories;
    }
}

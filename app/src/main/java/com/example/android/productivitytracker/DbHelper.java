package com.example.android.productivitytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by duncan on 29/11/15.
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "ProductivityTracker.db";

    protected static final String TEXT_TYPE = " TEXT";
    protected static final String INT_TYPE = " INT";
    protected static final String COMMA_SEP = ",";


    // Create Categories table.
    private static final String CATEGORY_CREATE_ENTRIES =
            "CREATE TABLE " + CategoryContract.CategoryEntry.TABLE_NAME + " (" +
    CategoryContract.CategoryEntry._ID + " INTEGER PRIMARY KEY," +
    CategoryContract.CategoryEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
    CategoryContract.CategoryEntry.COLUMN_NAME_WEIGHT + INT_TYPE + COMMA_SEP +
    CategoryContract.CategoryEntry.COLUMN_NAME_GOAL + INT_TYPE + COMMA_SEP +
    CategoryContract.CategoryEntry.COLUMN_NAME_LIMIT + INT_TYPE +
            " )";


    private static final String TASK_CREATE_ENTRIES =
            "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" +
    TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY," +
    TaskContract.TaskEntry.COLUMN_NAME_CATEGORY_ID + TEXT_TYPE + COMMA_SEP +
    TaskContract.TaskEntry.COLUMN_NAME_DURATION + INT_TYPE +
            " )";

    private static final String TASK_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME;

    private static final String CATEGORY_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CategoryContract.CategoryEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CATEGORY_CREATE_ENTRIES);
        db.execSQL(TASK_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(CATEGORY_DELETE_ENTRIES);
        db.execSQL(TASK_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void dropAndCreateTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(TASK_DELETE_ENTRIES);
        onCreate(getWritableDatabase());
    }


    /**
     * Insert a new category.
     */
    public void insertCategory(String name, int weight, int goal, int threshold) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CategoryContract.CategoryEntry.COLUMN_NAME_NAME, name);
        values.put(CategoryContract.CategoryEntry.COLUMN_NAME_GOAL, goal);
        values.put(CategoryContract.CategoryEntry.COLUMN_NAME_LIMIT, threshold);
        values.put(CategoryContract.CategoryEntry.COLUMN_NAME_WEIGHT, weight);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(
                CategoryContract.CategoryEntry.TABLE_NAME,
                null,
                values);
    }

    /**
     * Gets all the categories in the database as an ArrayList.
     */
    public ArrayList<String> getAllCategories() {
        // Gets the data repository in read mode
        SQLiteDatabase db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {CategoryContract.CategoryEntry.COLUMN_NAME_NAME};

        String selection = null;
        String[] selectionArgs = null;

        Cursor c = db.query(
                CategoryContract.CategoryEntry.TABLE_NAME, projection,
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
}

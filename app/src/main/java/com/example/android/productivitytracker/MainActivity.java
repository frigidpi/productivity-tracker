package com.example.android.productivitytracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mScoreTextView;
    private TaskDbHelper mDbHelper;
    private int currentSum = 0;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the task DB helper.
        mDbHelper = new TaskDbHelper(getApplicationContext());
        mScoreTextView = (TextView) findViewById(R.id.score);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrieve the data from the database, if any.
        SQLSelectTask task = new SQLSelectTask();
        task.execute();

        // Add listener to the button.
        findViewById(R.id.add_task_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) findViewById(R.id.edit_text);
                Integer duration = Integer.parseInt(editText.getText().toString());
                currentSum += duration;

                Spinner categorySpinner = (Spinner) findViewById(R.id.spinner);
                String category = categorySpinner.getSelectedItem().toString();

                SQLInsertTask task = new SQLInsertTask();
                task.execute(category, String.valueOf(duration));

                mScoreTextView.setText(String.valueOf(currentSum));
                Snackbar.make(view,
                        String.format("Added %d minutes of %s.", duration, category),
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Async task that inserts a task into the database.
     */
    public class SQLInsertTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            // Gets the data repository in write mode
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(TaskContract.TaskEntry.COLUMN_NAME_CATEGORY, params[0]);
            values.put(TaskContract.TaskEntry.COLUMN_NAME_DURATION, Integer.parseInt(params[1]));

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(
                    TaskContract.TaskEntry.TABLE_NAME,
                    null,
                    values);
            return null;
        }
    }

    /**
     * Async task that aggregates tasks from the database.
     */
    public class SQLSelectTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            // Gets the data repository in read mode
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    TaskContract.TaskEntry._ID,
                    TaskContract.TaskEntry.COLUMN_NAME_CATEGORY,
                    TaskContract.TaskEntry.COLUMN_NAME_DURATION};

            String selection = null;
            String[] selectionArgs = null;

            Cursor c = db.query(
                    TaskContract.TaskEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // No sort order
            );

            Integer sum = 0;
            // Cursors start at position -1.
            while (c.moveToNext()) {
                int duration = c.getInt(c.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_DURATION));
                sum += duration;
            }

            return sum;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            currentSum = result;
            mScoreTextView.setText(String.valueOf(result));
        }
    }
}

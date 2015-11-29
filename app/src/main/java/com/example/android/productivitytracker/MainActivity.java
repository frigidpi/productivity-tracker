package com.example.android.productivitytracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Map<String, Integer> categoryIds = new HashMap<>();
    private TextView mScoreTextView;
    private DbHelper mDbHelper;
    private ArrayAdapter<String> mSpinnerAdapter;
    private int currentSum = 0;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the task DB helper.
        mDbHelper = new DbHelper(this);
        mScoreTextView = (TextView) findViewById(R.id.score);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrieve the data from the database, if any.
        CalculateScoreTask task = new CalculateScoreTask();
        task.execute();

        // Add listener to the button.
        findViewById(R.id.add_task_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) findViewById(R.id.edit_text);
                try {
                    Integer duration = Integer.parseInt(editText.getText().toString());
                    currentSum += duration;

                    Spinner categorySpinner = (Spinner) findViewById(R.id.spinner);
                    String category = categorySpinner.getSelectedItem().toString();
                    int categoryId = categoryIds.get(category);

                    InsertTaskTask task = new InsertTaskTask();
                    task.execute(categoryId, duration);

                    mScoreTextView.setText(String.valueOf(currentSum));
                    Snackbar.make(view,
                        String.format("Added %d minutes of %s.", duration, category),
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Enter numbers <= 1440 (24 hours)!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        mSpinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, new ArrayList<String>());
        // Apply the adapter to the spinner
        spinner.setAdapter(mSpinnerAdapter);

        // Select categories from the database and populate spinner.
        SelectAllCategoriesTask categoriesTask = new SelectAllCategoriesTask();
        categoriesTask.execute();
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
        } else if (id == R.id.action_delete){
            mScoreTextView.setText(String.valueOf(0));

            RecreateDbTask task = new RecreateDbTask();
            task.execute();
            currentSum = 0;
            return true;
        } else if (id == R.id.action_add_category) {
            Intent intent = new Intent(this, CategoryActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Async task that inserts a task into the database.
     */
    public class InsertTaskTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            mDbHelper.insertTask(params[0], params[1]);
            return null;
        }
    }

    /**
     * Selects all categories and saves their IDs and names.
     */
    public class SelectAllCategoriesTask extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            // Gets the data repository in read mode
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {CategoryContract.CategoryEntry._ID,
                    CategoryContract.CategoryEntry.COLUMN_NAME_NAME};

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
                int id = c.getInt(c.getColumnIndex(CategoryContract.CategoryEntry._ID));
                String category = c.getString(c.getColumnIndex(CategoryContract.CategoryEntry.COLUMN_NAME_NAME));
                categories.add(category);
                categoryIds.put(category, id);
            }
            return categories;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            mSpinnerAdapter.addAll(result);
        }
    }

    /**
     * Async task that deletes the database.
     */
    public class RecreateDbTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // Gets the data repository in write mode
            //db.execSQL(SQL_CREATE_ENTRIES);
            mDbHelper.dropAndCreateTable();
            return null;
        }
    }

    /**
     * Async task that aggregates tasks from the database.
     */
    public class CalculateScoreTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            return mDbHelper.calculateScore();
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            currentSum = result;
            mScoreTextView.setText(String.valueOf(result));
        }
    }
}

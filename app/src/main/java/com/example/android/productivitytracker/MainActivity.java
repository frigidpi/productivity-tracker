package com.example.android.productivitytracker;

import android.content.ContentValues;
import android.content.Intent;
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
                int categoryId = (int) categorySpinner.getSelectedItemId();

                SQLInsertTask task = new SQLInsertTask();
                task.execute(categoryId, duration);

                mScoreTextView.setText(String.valueOf(currentSum));
                Snackbar.make(view,
                        String.format("Added %d minutes of %s.", duration, categoryId),
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
        } else if (id == R.id.action_delete){
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
    public class SQLInsertTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            mDbHelper.insertTask(params[0], params[1]);
            return null;
        }
    }

    /**
     * Async task that aggregates tasks from the database.
     */
    public class SQLSelectTask extends AsyncTask<Void, Void, Integer> {
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

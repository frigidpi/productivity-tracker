package com.example.android.productivitytracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    private ArrayAdapter<String> mAdapter;
    private DbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbHelper = new DbHelper(this);

        findViewById(R.id.category_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nameInput = (EditText) findViewById(R.id.category_add_name);
                EditText thresholdInput = (EditText) findViewById(R.id.category_add_threshold);
                EditText goalInput = (EditText) findViewById(R.id.category_add_goal);
                EditText weightInput = (EditText) findViewById(R.id.category_add_weight);
                AddCategoryTask task = new AddCategoryTask();
                task.execute(nameInput.getText().toString(),
                        weightInput.getText().toString(),
                        goalInput.getText().toString(),
                        thresholdInput.getText().toString());
            }
        });

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
    }

    public class AddCategoryTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mDbHelper.insertCategory(params[0], Integer.parseInt(params[1]),
                    Integer.parseInt(params[2]), Integer.parseInt(params[3]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}

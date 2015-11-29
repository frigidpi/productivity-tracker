package com.example.android.productivitytracker;

import android.provider.BaseColumns;

/**
 * Created by qedpi on 2015-11-28.
 */
public final class TaskContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TaskContract() {}

    /* Inner class that defines the table contents */
    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "Tasks";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_DURATION = "duration";
    }
}

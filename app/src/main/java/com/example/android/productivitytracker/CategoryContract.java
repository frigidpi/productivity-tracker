package com.example.android.productivitytracker;

import android.provider.BaseColumns;

/**
 * Created by qedpi on 2015-11-28.
 */
public final class CategoryContract {
    public CategoryContract() {}

    /* Inner class that defines the table contents */
    public static abstract class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "Categories";
        public static final String COLUMN_NAME_NAME = "name";

        // Weighting for this activity.
        public static final String COLUMN_NAME_WEIGHT = "weight";

        // Goal for this category.
        public static final String COLUMN_NAME_GOAL = "goal";

        // Limit for this category.
        public static final String COLUMN_NAME_LIMIT = "threshold";
    }
}

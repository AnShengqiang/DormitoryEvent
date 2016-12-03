package com.charger.android.dormtoryevents.database;

/**
 * Created by a1877 on 2016/12/2.
 */

public class EventDbSchema {
    public static final class EventTable{
        public static final String NAME = "events";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }
}

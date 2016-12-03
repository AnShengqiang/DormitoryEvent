package com.charger.android.dormtoryevents.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.charger.android.dormtoryevents.Event;
import com.charger.android.dormtoryevents.database.EventDbSchema.EventTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by a1877 on 2016/12/3.
 */

public class EventCursorWrapper extends CursorWrapper {
    public EventCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Event getEvent(){
        String uuidString = getString(getColumnIndex(EventTable.Cols.UUID));
        String title = getString(getColumnIndex(EventTable.Cols.TITLE));
        long date = getLong(getColumnIndex(EventTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(EventTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(EventTable.Cols.SUSPECT));

        Event event = new Event(UUID.fromString(uuidString));
        event.setTitle(title);
        event.setDate(new Date(date));
        event.setSolved(isSolved != 0);
        event.setSuspect(suspect);

        return event;
    }
}

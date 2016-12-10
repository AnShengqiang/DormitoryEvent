package com.charger.android.dormtoryevents;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.charger.android.dormtoryevents.database.EventBaseHelper;
import com.charger.android.dormtoryevents.database.EventCursorWrapper;
import com.charger.android.dormtoryevents.database.EventDbSchema.EventTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by a1877 on 2016/11/19.
 */

public class EventLab {
    private static EventLab sEventLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static EventLab get(Context context) {
        if (sEventLab == null) {
            sEventLab = new EventLab(context);
        }
        return sEventLab;
    }

    private EventLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new EventBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addEvent(Event e) {
        ContentValues values = getContentValues(e);

        mDatabase.insert(EventTable.NAME, null, values);
    }

    public List<Event> getEvents() {

        List<Event> events = new ArrayList<>();

        EventCursorWrapper cursor = queryEvents(null, null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                events.add(cursor.getEvent());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }

        return events;
    }

    public Event getEvent(UUID id) {
        EventCursorWrapper cursor = queryEvents(
                EventTable.Cols.UUID + "= ?",
                new String[]{id.toString()}
        );

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getEvent();
        }finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Event event){
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null){
            return null;
        }

        return new File(externalFilesDir, event.getPhotoFilename());
    }

    public void updateEvent(Event event) {
        String uuidString = event.getId().toString();
        ContentValues values = getContentValues(event);

        mDatabase.update(EventTable.NAME, values,
                EventTable.Cols.UUID + " = ?",
                new String[]{uuidString});

    }

    private static ContentValues getContentValues(Event event) {
        ContentValues values = new ContentValues();
        values.put(EventTable.Cols.UUID, event.getId().toString());
        values.put(EventTable.Cols.TITLE, event.getTitle());
        values.put(EventTable.Cols.DATE, event.getDate().getTime());
        values.put(EventTable.Cols.SOLVED, event.isSolved() ? 1 : 0);
        values.put(EventTable.Cols.SUSPECT, event.getSuspect());

        return values;
    }

    private EventCursorWrapper queryEvents(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                EventTable.NAME,
                null,       //null select all columns
                whereClause,
                whereArgs,
                null,       //group by
                null,       //having
                null        //order by
        );
        return new EventCursorWrapper(cursor);
    }


}

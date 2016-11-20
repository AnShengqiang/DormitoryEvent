package com.charger.android.dormtoryevents;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by a1877 on 2016/11/19.
 */

public class EventLab {
    private static EventLab sEventLab;

    private List<Event> mEvents;

    public static EventLab get(Context context){
        if(sEventLab == null){
            sEventLab = new EventLab(context);
        }
        return sEventLab;
    }

    private EventLab(Context context){
        mEvents = new ArrayList<>();
        for(int i = 0; i<100; i++){
            Event event = new Event();
            event.setTitle("八卦 ："+ i);
            event.setSolved(i%2 == 0);
            mEvents.add(event);
        }
    };

    public List<Event> getEvents(){
        return mEvents;
    }

    public Event getEvent(UUID id){
        for (Event event: mEvents){
            if(event.getId().equals(id)){
                return event;
            }
        }
        return null;
    }

}

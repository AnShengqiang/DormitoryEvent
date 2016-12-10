package com.charger.android.dormtoryevents;

import java.util.Date;
import java.util.UUID;

/**
 * Created by a1877 on 2016/11/7.
 */

public class Event {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;



    public Event(){
        this(UUID.randomUUID());
    }

    public Event(UUID id){
        mId = id;
        mDate = new Date();
    }



    /*getters & setters*/
    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }
}

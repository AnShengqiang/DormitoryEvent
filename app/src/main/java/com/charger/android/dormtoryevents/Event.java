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



    public Event(){
        mId = UUID.randomUUID();                                                                    //随机生成id
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
}

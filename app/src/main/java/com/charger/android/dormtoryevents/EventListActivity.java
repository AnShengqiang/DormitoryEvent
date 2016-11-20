package com.charger.android.dormtoryevents;

import android.support.v4.app.Fragment;

/**
 * Created by a1877 on 2016/11/19.
 */

public class EventListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new EventListFragment();
    }

}

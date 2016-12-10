package com.charger.android.dormtoryevents;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by a1877 on 2016/11/19.
 */

public class EventListActivity extends SingleFragmentActivity
        implements EventListFragment.Callbacks, EventFragment.Callbacks{

    @Override
    protected Fragment createFragment(){
        return new EventListFragment();
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onEventSelected(Event event){
        if (findViewById(R.id.detail_fragment_container) == null){
            Intent intent = EventPagerActivity.newIntent(this, event.getId());
            startActivity(intent);
        }else{
            Fragment newDetail = EventFragment.newInstance(event.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onEventUpdated(Event event) {
        EventListFragment listFragment = (EventListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}

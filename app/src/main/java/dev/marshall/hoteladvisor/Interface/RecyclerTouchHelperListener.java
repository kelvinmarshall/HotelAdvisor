package dev.marshall.hoteladvisor.Interface;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Marshall on 08/04/2018.
 */

public interface RecyclerTouchHelperListener {
    void onSwipe(RecyclerView.ViewHolder viewHolder,int direction,int position);
}

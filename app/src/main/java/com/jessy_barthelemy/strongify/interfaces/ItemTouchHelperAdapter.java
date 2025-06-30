package com.jessy_barthelemy.strongify.interfaces;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(RecyclerView view, final int position);

    boolean isSwipeEnabled();
}
package com.sharpdroid.registroelettronico.Listeners;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class OnScrollLessonsListener extends RecyclerView.OnScrollListener {
    public static final int SCROLL_STATE_IDLE = 0;      //FERMO
    public static final int SCROLL_STATE_DRAGGING = 1;  //TRASCINANDO
    public static final int SCROLL_STATE_SETTLING = 2;  //RILASCIATO
    private static final String TAG = OnScrollLessonsListener.class.getSimpleName();
    private LinearLayoutManager layoutManager;
    private int firstVisible = 0, lastVisible;

    public OnScrollLessonsListener() {
        super();
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (layoutManager == null) {
            layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        }

        Log.d(TAG, "" + newState);

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (layoutManager != null) {
            firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition();
            lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
        }
    }
}

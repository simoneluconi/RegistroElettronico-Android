package com.sharpdroid.registro.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.sharpdroid.registro.R;

import butterknife.ButterKnife;

public class FragmentAgenda extends Fragment {
    final private String TAG = FragmentAgenda.class.getSimpleName();


    private Context mContext;

    public FragmentAgenda() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        View layout = inflater.inflate(R.layout.fragment_calendar, container, false);

        ButterKnife.bind(this, layout);

        return layout;
    }

}

package com.sharpdroid.registroelettronico.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.Interfaces.API.Event;
import com.sharpdroid.registroelettronico.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     LongClickAgenda.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link LongClickAgenda.Listener}.</p>
 */
public class LongClickAgenda extends BottomSheetDialogFragment {

    private static final int mItemCount = 3;
    private static final int icons[] = {R.drawable.agenda_bsheet_share, R.drawable.agenda_bsheet_calendar, R.drawable.agenda_bsheet_copy};
    private static final String texts[] = {"Condividi", "Calendario", "Copia"};


    private static final String ARG_ITEM_COUNT = "item_count";
    private Listener mListener;
    private Event event;

    public static LongClickAgenda newInstance(/*int itemCount*/) {
        final LongClickAgenda fragment = new LongClickAgenda();
        final Bundle args = new Bundle();
        //args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    public void setEvent(Event e) {
        event = e;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_list_options, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ItemAdapter(/*getArguments().getInt(ARG_ITEM_COUNT)*/));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface Listener {
        void onBottomSheetItemClicked(int position, Event e);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.layout)
        View layout;
        @BindView(R.id.image)
        ImageView image;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            layout.setOnClickListener(v1 -> {
                if (mListener != null) {
                    mListener.onBottomSheetItemClicked(getAdapterPosition(), event);
                    dismiss();
                }
            });
        }

    }

    private class ItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        ItemAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_agenda_options, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(texts[position]);
            holder.image.setImageResource(icons[position]);
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }

    }

}

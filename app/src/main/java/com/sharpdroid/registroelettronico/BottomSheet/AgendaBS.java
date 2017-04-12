package com.sharpdroid.registroelettronico.BottomSheet;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.Interfaces.Client.AdvancedEvent;
import com.sharpdroid.registroelettronico.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     AgendaBS.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link AgendaBS.Listener}.</p>
 */
public class AgendaBS extends BottomSheetDialogFragment {

    private static final int mItemCount = 5;
    private static final int icons[] = {R.drawable.agenda_bsheet_share, R.drawable.agenda_bsheet_calendar, R.drawable.agenda_bsheet_copy, R.drawable.agenda_bsheet_archive};
    private static final String texts[] = {"Condividi", "Inserisci nel calendario", "Copia", "Archivia"};


    private static final String ARG_ITEM_COUNT = "item_count";
    private Listener mListener;
    private AdvancedEvent event;

    public static AgendaBS newInstance(/*int itemCount*/) {
        final AgendaBS fragment = new AgendaBS();
        final Bundle args = new Bundle();
        //args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    public void setEvent(AdvancedEvent e) {
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
        void onBottomSheetItemClicked(int position, AdvancedEvent e);
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
            if (position != 0) {
                holder.text.setText(texts[position - 1]);
                holder.text.setTextColor(Color.BLACK);
                holder.image.setImageResource(icons[position - 1]);
            } else {
                holder.text.setText(event.isCompleted() ? "Non completato" : "Completato");
                holder.text.setTextColor(ContextCompat.getColor(getContext(), R.color.intro_blue_dark));
                holder.image.setImageResource(event.isCompleted() ? R.drawable.agenda_uncomplete : R.drawable.agenda_complete);
            }
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }

    }

}

package com.sharpdroid.registroelettronico.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.API.V2.APIClient;
import com.sharpdroid.registroelettronico.Databases.Entities.Communication;
import com.sharpdroid.registroelettronico.Fragments.FragmentCommunications;
import com.sharpdroid.registroelettronico.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommunicationAdapter extends RecyclerView.Adapter<CommunicationAdapter.CommunicationHolder> implements Filterable {
    private static final String TAG = CommunicationAdapter.class.getSimpleName();
    private final List<Communication> CVDataList = new CopyOnWriteArrayList<>();
    private final List<Communication> filtered = new CopyOnWriteArrayList<>();
    private final Context mContext;
    private final SimpleDateFormat formatter = new SimpleDateFormat("d MMM", Locale.ITALIAN);
    private ItemFilter mFilter = new ItemFilter();
    private DownloadListener listener;

    public CommunicationAdapter(FragmentCommunications fragmentCommunications) {
        this.mContext = fragmentCommunications.getContext();
        listener = fragmentCommunications;
    }

    public void addAll(Collection<Communication> list) {
        CVDataList.addAll(list);
        filtered.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
        filtered.clear();
        notifyDataSetChanged();
    }

    @Override
    public CommunicationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_communications, parent, false);
        return new CommunicationHolder(v);
    }

    @Override
    public void onBindViewHolder(CommunicationHolder ViewHolder, int i) {
        final Communication communication = filtered.get(i);

        ViewHolder.Title.setText(communication.getTitle().trim());
        ViewHolder.Date.setText(formatter.format(communication.getDate()));
        ViewHolder.Type.setText(communication.getCategory());

        ViewHolder.mRelativeLayout.setOnClickListener(v -> {
            listener.onCommunicationClick(communication);
        });


        if (!communication.isRead()) {
            APIClient.Companion.with(mContext).readBacheca(communication.getEvtCode(), communication.getId()).subscribe(readResponse -> {
                communication.setContent(readResponse.getItem().getText());
            }, Throwable::printStackTrace);
        }
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public interface DownloadListener {
        void onCommunicationClick(@NotNull Communication communication);
    }

    class CommunicationHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relative_layout)
        RelativeLayout mRelativeLayout;
        @BindView(R.id.title)
        TextView Title;
        @BindView(R.id.date)
        TextView Date;
        @BindView(R.id.type)
        TextView Type;

        CommunicationHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Communication> list = new ArrayList<>();
            FilterResults filterResults = new FilterResults();

            if (!TextUtils.isEmpty(constraint)) {
                for (Communication c : CVDataList) {
                    if (c.getTitle().toLowerCase().contains(constraint.toString().toLowerCase()))
                        list.add(c);
                }
            } else {
                list.addAll(CVDataList);
            }

            filterResults.values = list;
            filterResults.count = list.size();
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered.clear();
            filtered.addAll((Collection<? extends Communication>) results.values);
            notifyDataSetChanged();
        }
    }
}

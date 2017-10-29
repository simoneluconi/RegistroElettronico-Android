package com.sharpdroid.registroelettronico.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.database.entities.Communication;
import com.sharpdroid.registroelettronico.fragments.FragmentCommunications;

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
    private final SimpleDateFormat formatter = new SimpleDateFormat("d MMM", Locale.ITALIAN);
    private ItemFilter mFilter = new ItemFilter();
    private DownloadListener listener;

    public CommunicationAdapter(FragmentCommunications fragmentCommunications) {
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

        ViewHolder.mRelativeLayout.setOnClickListener(v -> ViewHolder.mRelativeLayout.postDelayed(() -> listener.onCommunicationClick(communication), ViewConfiguration.getTapTimeout()));

        ViewHolder.attachment.setVisibility((communication.getHasAttachment()) ? View.VISIBLE : View.GONE);
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
        @BindView(R.id.attachment)
        View attachment;

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

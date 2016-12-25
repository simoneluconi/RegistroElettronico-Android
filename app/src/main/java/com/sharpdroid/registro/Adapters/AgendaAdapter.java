package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharpdroid.registro.Interfaces.API.Communication;
import com.sharpdroid.registro.R;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.CommunicationHolder> {
    private final List<Communication> CVDataList;
    private final Context mContext;
    private final CoordinatorLayout mCoordinatorLayout;

    private final SimpleDateFormat formatter = new SimpleDateFormat("d MMMM", Locale.ITALIAN);

    public AgendaAdapter(Context mContext, CoordinatorLayout mCoordinatorLayout, List<Communication> CVDataList) {
        this.CVDataList = CVDataList;
        this.mContext = mContext;
        this.mCoordinatorLayout = mCoordinatorLayout;
    }

    public void addAll(Collection<Communication> list) {
        CVDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
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
        final Communication communication = CVDataList.get(ViewHolder.getAdapterPosition());
        ViewHolder.Title.setText(communication.getTitle().trim());
        ViewHolder.Date.setText(formatter.format(communication.getDate()));
        ViewHolder.Type.setText(communication.getType());

        // TODO: 18/11/2016 aggiungere listener solo con allegati presenti
       /* ViewHolder.mRelativeLayout.setOnClickListener(v -> {
            Snackbar DownloadProgressSnak = Snackbar.make(mCoordinatorLayout, R.string.download_in_corso, Snackbar.LENGTH_INDEFINITE);

            File dir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).toString() +
                            File.separator +
                            "Registro Elettronico");

            File file = new File(dir +
                    File.separator +
                    communication.getId() + ".pdf");

            communication.getId();

            if (!file.exists()) {
                DownloadProgressSnak.show();

                if (!dir.exists()) dir.mkdir();
                new SpiaggiariApiClient(mContext).mService.getcommunicationDownload(communication.getId())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(communication_file -> {
                            writeResponseBodyToDisk(communication_file, file);
                            openpdf(file, DownloadProgressSnak);
                        }, error -> {
                            DownloadProgressSnak.setText(mContext.getResources().getString(R.string.download_fallito, error.getCause()));
                            DownloadProgressSnak.setDuration(Snackbar.LENGTH_SHORT).show();
                        });
            } else {
                openpdf(file, DownloadProgressSnak);
            }
        });

        */
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
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
}

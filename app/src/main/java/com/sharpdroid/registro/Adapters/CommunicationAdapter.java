package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.Interfaces.Communication;
import com.sharpdroid.registro.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommunicationAdapter extends RecyclerView.Adapter<CommunicationAdapter.CommunicationHolder> {
    private final List<Communication> CVDataList;
    private final Context mContext;
    private final CoordinatorLayout mCoordinatorLayout;

    public CommunicationAdapter(Context mContext, CoordinatorLayout mCoordinatorLayout, List<Communication> CVDataList) {
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
        String datestring = communication.getDate().split("T")[0];
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
            Date convertedCommitDate = formatter.parse(datestring);
            //formatter = new SimpleDateFormat("dd/MM/YYYY", Locale.ITALIAN);
            formatter = new SimpleDateFormat("d MMMM", Locale.ITALIAN);
            ViewHolder.Date.setText(formatter.format(convertedCommitDate));
        } catch (ParseException e) {
            ViewHolder.Date.setText(datestring);
        }
        ViewHolder.Type.setText(communication.getType());

        ViewHolder.mRelativeLayout.setOnClickListener(v -> {
            Snackbar DownloadProgressSnak;
            DownloadProgressSnak = Snackbar.make(mCoordinatorLayout, R.string.download_in_corso, Snackbar.LENGTH_INDEFINITE);
            DownloadProgressSnak.show();

            File file = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).toString() +
                            File.separator +
                            "Registro Elettronico" +
                            File.separator +
                            communication.getId() + ".pdf");

            String url = new RESTFulAPI().COMMUNICATION_DOWNLOAD_URL(communication.getId());

            Ion.with(mContext)
                    .load(url)
                    .write(file)
                    .withResponse()
                    .setCallback((e, result) -> {
                        if (result.getHeaders().code() == 200) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(FileProvider.getUriForFile(mContext, "com.sharpdroid.registro.fileprovider", file), "application/pdf");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                            DownloadProgressSnak.setText(R.string.click_to_open);
                            DownloadProgressSnak.setAction(R.string.open, v1 -> {
                                mContext.startActivity(intent);
                                DownloadProgressSnak.dismiss();
                            });
                        }
                    });
        });
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

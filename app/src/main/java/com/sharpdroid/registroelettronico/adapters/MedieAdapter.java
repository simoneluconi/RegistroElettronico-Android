package com.sharpdroid.registroelettronico.adapters;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.activities.MarkSubjectDetailActivity;
import com.sharpdroid.registroelettronico.database.entities.Average;
import com.sharpdroid.registroelettronico.views.CircleProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.utils.Metodi.MessaggioVoto;
import static com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach;
import static com.sharpdroid.registroelettronico.utils.Metodi.getMediaColor;
import static com.sharpdroid.registroelettronico.utils.Metodi.getPossibileSubjectTarget;

public class MedieAdapter extends RecyclerView.Adapter<MedieAdapter.MedieHolder> {
    final private String TAG = MedieAdapter.class.getSimpleName();

    private final List<Average> CVDataList = new ArrayList<>();
    private final Context mContext;

    private int period;

    public MedieAdapter(Context context) {
        this.mContext = context;
    }

    public void addAll(List<Average> list, int p) {

        DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return CVDataList.size();
            }

            @Override
            public int getNewListSize() {
                return list.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return CVDataList.get(oldItemPosition).name.equals(list.get(newItemPosition).name);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return CVDataList.get(oldItemPosition).name.equals(list.get(newItemPosition).name) && CVDataList.get(oldItemPosition).avg == list.get(newItemPosition).avg;
            }
        }, true).dispatchUpdatesTo(this);
        CVDataList.clear();
        CVDataList.addAll(list);
        this.period = p;
        //notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public MedieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_medie_grid, parent, false);
        return new MedieHolder(v);
    }


    @Override
    public void onBindViewHolder(MedieHolder ViewHolder, int position) {
        final Average avg = CVDataList.get(position);

        ViewHolder.mTextViewMateria.setText(capitalizeEach(avg.name));

        ViewHolder.mCardViewMedia.setOnClickListener(v ->
                ViewHolder.mCardViewMedia.postDelayed(() -> mContext.startActivity(new Intent(mContext, MarkSubjectDetailActivity.class)
                        .putExtra("subject_id", avg.code)
                        .putExtra("period", period)), ViewConfiguration.getTapTimeout()));

        if (avg.avg != 0f) {
            ViewHolder.mTextViewMedia.setText(String.format(Locale.getDefault(), "%.2f", avg.avg));

            float target = avg.target;

            if (target <= 0) {
                String t = PreferenceManager.getDefaultSharedPreferences(mContext)
                        .getString("voto_obiettivo", "8");

                if (t.equals("Auto")) {
                    target = getPossibileSubjectTarget(avg.avg);

                } else target = Float.parseFloat(t);

            }
            ViewHolder.mCircleProgressBar.setProgress(avg.avg * 10);
            ViewHolder.mCircleProgressBar.setColor(ContextCompat.getColor(mContext, getMediaColor(avg.avg, target)));

            ViewHolder.mTextViewDesc.setText(MessaggioVoto(target, avg.avg, avg.count));
        } else {
            ViewHolder.mCircleProgressBar.setProgress(100);
            ViewHolder.mCircleProgressBar.setColor(ContextCompat.getColor(mContext, R.color.intro_blue));

            ViewHolder.mTextViewMedia.setText("-");
            ViewHolder.mTextViewDesc.setText(mContext.getString(R.string.nessun_voto_numerico));
        }
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    class MedieHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardview_medie)
        CardView mCardViewMedia;
        @BindView(R.id.custom_progressBar)
        CircleProgressBar mCircleProgressBar;
        @BindView(R.id.materia)
        TextView mTextViewMateria;
        @BindView(R.id.media)
        TextView mTextViewMedia;
        @BindView(R.id.descrizione)
        TextView mTextViewDesc;

        MedieHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.sharpdroid.registro.Interfaces.Mark;
import com.sharpdroid.registro.Interfaces.MarkSubject;
import com.sharpdroid.registro.Interfaces.Media;
import com.sharpdroid.registro.Interfaces.Metodi;
import com.sharpdroid.registro.R;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class MedieAdapter extends RecyclerView.Adapter<MedieAdapter.MedieHolder> {
    final private String TAG = MedieAdapter.class.getSimpleName();

    private final List<MarkSubject> CVDataList;
    private final Context mContext;

    public MedieAdapter(Context context, List<MarkSubject> CVDataList) {
        this.mContext = context;
        this.CVDataList = CVDataList;
    }

    public void addAll(Collection<MarkSubject> list) {
        CVDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public MedieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_medie, parent, false);
        return new MedieHolder(v);
    }


    @Override
    public void onBindViewHolder(MedieHolder ViewHolder, int position) {
        final MarkSubject marksubject = CVDataList.get(position);
        final List<Mark> marks = marksubject.getMarks();
        Media media = new Media();
        media.setMateria(marksubject.getName());
        for (Mark mark : marks) {
            if (!mark.isNs()) {
                media.addMark(mark);
            } else {
                Log.d(TAG, "Voto non significativo");
            }
        }
        ViewHolder.mTextViewMateria.setText(media.getMateria());
        ViewHolder.mTextViewMedia.setText(String.format(Locale.getDefault(), "%.2f", media.getMediaGenerale()));
        ViewHolder.mCircularProgressViewMedia.setProgress(media.getMediaGenerale() * 10);
        if (media.isSufficiente()) {
            ViewHolder.mCircularProgressViewMedia.setColor(ContextCompat.getColor(mContext, R.color.greenmaterial));
        } else {
            ViewHolder.mCircularProgressViewMedia.setColor(ContextCompat.getColor(mContext, R.color.redmaterial));
        }

        float obbiettivo_voto = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getFloat("obbiettivo_voto", 8);

        String obbiettivo_string = Metodi.MessaggioVoto(obbiettivo_voto, media.getMediaGenerale(), media.getSommaGenerale(), media.getNumeroVoti());
        ViewHolder.mTextViewDesc.setText(obbiettivo_string);

        ViewHolder.mCardViewMedia.setOnClickListener(v -> {
            // Perform action on click
        });
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    class MedieHolder extends RecyclerView.ViewHolder {
        final CardView mCardViewMedia;
        final CircularProgressView mCircularProgressViewMedia;
        final TextView mTextViewMateria;
        final TextView mTextViewMedia;
        final TextView mTextViewDesc;

        MedieHolder(View itemView) {
            super(itemView);
            mCardViewMedia = (CardView) itemView.findViewById(R.id.cardview_medie);
            mCircularProgressViewMedia = (CircularProgressView) itemView.findViewById(R.id.progressvoti);
            mTextViewMateria = (TextView) itemView.findViewById(R.id.materia);
            mTextViewMedia = (TextView) itemView.findViewById(R.id.media);
            mTextViewDesc = (TextView) itemView.findViewById(R.id.descrizione);
        }
    }
}
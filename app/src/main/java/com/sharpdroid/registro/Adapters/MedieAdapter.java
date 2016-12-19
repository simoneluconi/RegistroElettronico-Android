package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registro.Activities.MarkSubjectDetailActivity;
import com.sharpdroid.registro.Interfaces.Mark;
import com.sharpdroid.registro.Interfaces.MarkSubject;
import com.sharpdroid.registro.Interfaces.Media;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Utils.Metodi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ArcProgressStackView;

import static com.sharpdroid.registro.Utils.Metodi.getMediaColor;

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
                .inflate(R.layout.adapter_medie_grid, parent, false);
        return new MedieHolder(v);
    }


    @Override
    public void onBindViewHolder(MedieHolder ViewHolder, int position) {
        final MarkSubject marksubject = CVDataList.get(position);
        final List<Mark> marks = marksubject.getMarks();
        Media media = new Media();
        media.setMateria(marksubject.getName());

        media.addMarks(marks);

        ViewHolder.mTextViewMateria.setText(media.getMateria());
        ViewHolder.mTextViewMedia.setText(String.format(Locale.getDefault(), "%.2f", media.getMediaGenerale()));

        final float voto_obiettivo = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString("voto_obiettivo", "8"));

        List<ArcProgressStackView.Model> models = new ArrayList<>();
        models.add(new ArcProgressStackView.Model("media", media.getMediaGenerale() * 10, ContextCompat.getColor(mContext, getMediaColor(media, voto_obiettivo))));

        ViewHolder.mArcProgressStackView.setModels(models);

        String obbiettivo_string = Metodi.MessaggioVoto(voto_obiettivo, media.getMediaGenerale(), media.getNumeroVoti());
        ViewHolder.mTextViewDesc.setText(obbiettivo_string);

        ViewHolder.mCardViewMedia.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, MarkSubjectDetailActivity.class).putExtra("data", marksubject)));
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    class MedieHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardview_medie)
        CardView mCardViewMedia;
        @BindView(R.id.progressvoti)
        ArcProgressStackView mArcProgressStackView;
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

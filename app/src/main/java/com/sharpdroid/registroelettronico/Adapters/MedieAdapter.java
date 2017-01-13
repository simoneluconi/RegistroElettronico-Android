package com.sharpdroid.registroelettronico.Adapters;

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

import com.sharpdroid.registroelettronico.Activities.MarkSubjectDetailActivity;
import com.sharpdroid.registroelettronico.Databases.SubjectsDB;
import com.sharpdroid.registroelettronico.Interfaces.API.MarkSubject;
import com.sharpdroid.registroelettronico.Interfaces.Client.Media;
import com.sharpdroid.registroelettronico.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ArcProgressStackView;

import static com.sharpdroid.registroelettronico.Utils.Metodi.MessaggioVoto;
import static com.sharpdroid.registroelettronico.Utils.Metodi.beautifyName;
import static com.sharpdroid.registroelettronico.Utils.Metodi.getMediaColor;
import static com.sharpdroid.registroelettronico.Utils.Metodi.getSubjectName;

public class MedieAdapter extends RecyclerView.Adapter<MedieAdapter.MedieHolder> {
    final private String TAG = MedieAdapter.class.getSimpleName();

    private final List<MarkSubject> CVDataList;
    private final Context mContext;

    private final SubjectsDB db;

    public MedieAdapter(Context context, List<MarkSubject> CVDataList, SubjectsDB db) {
        this.mContext = context;
        this.CVDataList = CVDataList;
        this.db = db;
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
        final String subjectname = getSubjectName(db.getSubject(marksubject.getName().toLowerCase()));

        Media media = new Media();
        media.setMateria(subjectname);
        media.addMarks(marksubject.getMarks());

        ViewHolder.mTextViewMateria.setText(subjectname);

        if (media.containsValidMarks()) {
            ViewHolder.mTextViewMedia.setText(String.format(Locale.getDefault(), "%.2f", media.getMediaGenerale()));

            final float voto_obiettivo = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(mContext)
                    .getString("voto_obiettivo", "8"));

            List<ArcProgressStackView.Model> models = new ArrayList<>();
            models.add(new ArcProgressStackView.Model("media", media.getMediaGenerale() * 10, ContextCompat.getColor(mContext, getMediaColor(media, voto_obiettivo))));

            ViewHolder.mArcProgressStackView.setModels(models);

            String obbiettivo_string = MessaggioVoto(voto_obiettivo, media.getMediaGenerale(), media.getNumeroVoti());
            ViewHolder.mTextViewDesc.setText(obbiettivo_string);

            ViewHolder.mCardViewMedia.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, MarkSubjectDetailActivity.class).putExtra("data", marksubject)));
        } else {
            List<ArcProgressStackView.Model> models = new ArrayList<>();
            models.add(new ArcProgressStackView.Model("media", 100, ContextCompat.getColor(mContext, R.color.intro_blue)));
            ViewHolder.mArcProgressStackView.setModels(models);
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

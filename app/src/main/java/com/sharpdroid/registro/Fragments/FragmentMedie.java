package com.sharpdroid.registro.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.Library.Mark;
import com.sharpdroid.registro.Library.MarkSubject;
import com.sharpdroid.registro.Library.Media;
import com.sharpdroid.registro.Library.Metodi;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Utils.CacheTask;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.sharpdroid.registro.Library.Metodi.isNetworkAvailable;

public class FragmentMedie extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentMedie.class.getSimpleName();

    private CoordinatorLayout mCoordinatorLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RVAdapter mRVAdapter;

    public FragmentMedie() {

    }

    private void addSubjects(List<MarkSubject> markSubjects) {
        if (markSubjects.size() != 0) {
            mRVAdapter.clear();
            mRVAdapter.addAll(markSubjects);

            // Update cache
            new CacheTask(getContext().getCacheDir(), TAG).execute((List) markSubjects);

            mSwipeRefreshLayout.setRefreshing(false);

            Snackbar.make(mCoordinatorLayout, R.string.new_marks, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_voti, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swiperefresh_voti);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);

        mCoordinatorLayout = (CoordinatorLayout) layout.findViewById(R.id.coordinatorlayout_voti);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.cardlist_voti);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<MarkSubject> CVDataList = new LinkedList<>();
        mRVAdapter = new RVAdapter(CVDataList);
        mRecyclerView.setAdapter(mRVAdapter);

        bindVotiCache();

        new Handler().post(new RESTFulAPI.Marks(getContext()) {
            @Override
            public void then(List<MarkSubject> markSubjects) {
                addSubjects(markSubjects);
            }
        });

        return layout;
    }

    @Override
    public void onRefresh() {
        if (isNetworkAvailable(getContext())) {
            new Handler().post(new RESTFulAPI.Marks(getContext()) {
                @Override
                public void then(List<MarkSubject> markSubjects) {
                    addSubjects(markSubjects);
                }
            });
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
        final List<MarkSubject> CVDataList;

        RVAdapter(List<MarkSubject> CVDataList) {
            this.CVDataList = CVDataList;
        }

        void addAll(Collection<MarkSubject> list) {
            CVDataList.addAll(list);
            notifyDataSetChanged();
        }

        void clear() {
            CVDataList.clear();
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_medie, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder ViewHolder, int i) {
            final MarkSubject marksubject = CVDataList.get(ViewHolder.getAdapterPosition());
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
            ViewHolder.Materia.setText(media.getMateria());
            ViewHolder.Media.setText(String.format(Locale.getDefault(), "%.2f", media.getMediaGenerale()));
            ViewHolder.CircularMedia.setProgress(media.getMediaGenerale() * 10);
            if (media.isSufficiente()) {
                ViewHolder.CircularMedia.setColor(ContextCompat.getColor(getContext(), R.color.greenmaterial));
            } else {
                ViewHolder.CircularMedia.setColor(ContextCompat.getColor(getContext(), R.color.redmaterial));
            }

            float obbiettivo_voto = PreferenceManager.getDefaultSharedPreferences(getContext())
                    .getFloat("obbiettivo_voto", 8);

            String obbiettivo_string = Metodi.MessaggioVoto(obbiettivo_voto, media.getMediaGenerale(), media.getSommaGenerale(), media.getNumeroVoti());
            ViewHolder.Desc.setText(obbiettivo_string);
        }

        @Override
        public int getItemCount() {
            return CVDataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView Materia;
            final TextView Media;
            final TextView Desc;
            final CircularProgressView CircularMedia;

            ViewHolder(View itemView) {
                super(itemView);
                Materia = (TextView) itemView.findViewById(R.id.materia);
                Media = (TextView) itemView.findViewById(R.id.media);
                Desc = (TextView) itemView.findViewById(R.id.descrizione);
                CircularMedia = (CircularProgressView) itemView.findViewById(R.id.progressvoti);
            }
        }
    }

    private void bindVotiCache() {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(getContext().getCacheDir(), TAG));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<MarkSubject> cachedData = new LinkedList<>();
            MarkSubject temp;
            while ((temp = (MarkSubject) objectInputStream.readObject()) != null) {
                cachedData.add(temp);
            }
            objectInputStream.close();
            mRVAdapter.clear();
            mRVAdapter.addAll(cachedData);
            Log.d(TAG, "Restored cache");
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Cache not found.");
        } catch (EOFException e) {
            Log.e(TAG, "Error while reading cache! (EOF) ");
        } catch (StreamCorruptedException e) {
            Log.e(TAG, "Corrupted cache!");
        } catch (IOException e) {
            Log.e(TAG, "Error while reading cache!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

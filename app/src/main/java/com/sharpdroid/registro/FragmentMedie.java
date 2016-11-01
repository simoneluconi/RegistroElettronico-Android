package com.sharpdroid.registro;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
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

import static com.sharpdroid.registro.Metodi.isNetworkAvailable;

public class FragmentMedie extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentMedie.class.getSimpleName();

    private CoordinatorLayout mCoordinatorLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RVAdapter mRVAdapter;

    public FragmentMedie() {

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
        List<Materia> CVDataList = new LinkedList<>();
        mRVAdapter = new RVAdapter(CVDataList);
        mRecyclerView.setAdapter(mRVAdapter);

        bindVotiCache();
        new VotiTask().execute();

        return layout;
    }

    @Override
    public void onRefresh() {
        if (isNetworkAvailable(getContext())) {
            new VotiTask().execute();
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
        final List<Materia> CVDataList;

        RVAdapter(List<Materia> CVDataList) {
            this.CVDataList = CVDataList;
        }

        void addAll(Collection<Materia> list) {
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
            final Materia materia = CVDataList.get(ViewHolder.getAdapterPosition());
            final List<Voto> voti = materia.getVoti();
            Media media = new Media();
            media.setMateria(materia.getMateria());
            for (Voto voto : voti) {
                media.addVoto(voto);
            }
            ViewHolder.Materia.setText(media.getMateria());
            ViewHolder.Media.setText(String.valueOf(media.getMediaGenerale()));
            ViewHolder.CircularMedia.setProgress((float) (media.getMediaGenerale() * 10));
            if (media.isSufficiente()) {
                ViewHolder.CircularMedia.setColor(ContextCompat.getColor(getContext(), R.color.greenmaterial));
            } else {
                ViewHolder.CircularMedia.setColor(ContextCompat.getColor(getContext(), R.color.redmaterial));
            }
        }

        @Override
        public int getItemCount() {
            return CVDataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView Materia;
            final TextView Media;
            final CircularProgressView CircularMedia;

            ViewHolder(View itemView) {
                super(itemView);
                Materia = (TextView) itemView.findViewById(R.id.materia);
                Media = (TextView) itemView.findViewById(R.id.media);
                CircularMedia = (CircularProgressView) itemView.findViewById(R.id.progressvoti);
            }
        }
    }

    private void bindVotiCache() {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(getContext().getCacheDir(), "cache"));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<Materia> cachedData = new LinkedList<>();
            Materia temp;
            while ((temp = (Materia) objectInputStream.readObject()) != null) {
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

    private class VotiTask extends AsyncTask<Void, Void, List<Materia>> {
        // Runs on UI thread
        @UiThread
        @Override
        protected void onPreExecute() {
            /* Start refreshing circle animation.
             * Wrap in runnable to workaround SwipeRefreshLayout bug.
             * View: https://code.google.com/p/android/issues/detail?id=77712
             */
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        @WorkerThread
        @Override
        protected List<Materia> doInBackground(Void... params) {
            List<Materia> materie = new LinkedList<>();
            MaterieParser parser = new MaterieParser();
            long time = System.currentTimeMillis();
            String url = "https://gist.githubusercontent.com/luca020400/ee7ecf8b0dac048ae9091ec8256ff303/raw";
            //TODO: String url = "https://api.daniele.ml/marks";
            try {
                materie.addAll(parser.parseJSON(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.v(TAG, "Successfully parsed " + materie.size() + " changes in " + (System.currentTimeMillis() - time) + "ms");
            return materie;
        }

        // Runs on the UI thread
        @UiThread
        @Override
        protected void onPostExecute(List<Materia> materie) {
            if (materie != null && materie.size() != 0) {
                mRVAdapter.clear();
                mRVAdapter.addAll(materie);

                // Update cache
                new CacheTask(getContext().getCacheDir()).execute((List) materie);
            }

            // Delay refreshing animation just for the show
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 300);
        }
    }
}

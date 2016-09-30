package com.sharpdroid.registro;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.sharpdroid.registro.Metodi.isNetworkAvailable;

public class FragmentVoti extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentVoti.class.getSimpleName();

    private CoordinatorLayout mCoordinatorLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RVAdapter mRVAdapter;

    public FragmentVoti() {

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
        List<Voto> CVDataList = new LinkedList<>();
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
        final List<Voto> CVDataList;

        RVAdapter(List<Voto> CVDataList) {
            this.CVDataList = CVDataList;
        }

        void addAll(Collection<Voto> list) {
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
                    .inflate(R.layout.adapter_voti, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder ViewHolder, int i) {
            final Voto voto = CVDataList.get(ViewHolder.getAdapterPosition());
            ViewHolder.Materia.setText(voto.getMateria());
            ViewHolder.Data.setText(voto.getData());
            ViewHolder.Tipo.setText(voto.getTipo());
            ViewHolder.Commento.setText(voto.getCommento());
            ViewHolder.Voto.setText(String.valueOf(voto.getVoto()));
        }

        @Override
        public int getItemCount() {
            return CVDataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView Materia;
            final TextView Voto;
            final TextView Data;
            final TextView Tipo;
            final TextView Commento;

            ViewHolder(View itemView) {
                super(itemView);
                Materia = (TextView) itemView.findViewById(R.id.materia);
                Voto = (TextView) itemView.findViewById(R.id.voto);
                Data = (TextView) itemView.findViewById(R.id.data);
                Commento = (TextView) itemView.findViewById(R.id.commento);
                Tipo = (TextView) itemView.findViewById(R.id.tipo);
            }
        }
    }

    private void bindVotiCache() {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(getContext().getCacheDir(), "cache"));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<Voto> cachedData = new LinkedList<>();
            Voto temp;
            while ((temp = (Voto) objectInputStream.readObject()) != null) {
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

    public class VotiTask extends AsyncTask<Void, Void, List<Voto>> {
        // Runs on UI thread
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

        @Override
        protected List<Voto> doInBackground(Void... params) {
            List<Voto> voti = new LinkedList<>();
            VotiParser parser = new VotiParser();
            long time = System.currentTimeMillis();
            String url = "https://gist.githubusercontent.com/luca020400/ddf60baa2de3d5d4b8ab98f58f21571d/raw";
            try {
                Log.d(TAG, "Sending GET request to \"" + url + "\"");
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                try {
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestMethod("GET");
                    Log.d(TAG, "Response: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
                    // Parse JSON
                    voti.addAll(parser.parseJSON(connection.getInputStream()));
                } catch (ProtocolException e) {
                    Log.e(TAG, e.getMessage());
                    return null;
                } finally {
                    connection.disconnect();
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "Malformed URL!");
                return null;
            } catch (IOException e) {
                Log.e(TAG, "Error while connecting to " + url);
                return null;
            }
            Log.v(TAG, "Successfully parsed " + voti.size() + " changes in " + (System.currentTimeMillis() - time) + "ms");
            return voti;
        }

        // Runs on the UI thread
        @Override
        protected void onPostExecute(List<Voto> voti) {
            if (voti != null) {
                mRVAdapter.clear();
                mRVAdapter.addAll(voti);

                // Update cache
                new CacheTask(getContext().getCacheDir()).execute((List) voti);
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

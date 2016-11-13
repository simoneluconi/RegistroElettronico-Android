package com.sharpdroid.registro.Fragments;

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

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.Library.Communication;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Utils.CacheTask;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.sharpdroid.registro.Library.Metodi.isNetworkAvailable;

public class FragmentCommunications extends Fragment implements RecyclerRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentCommunications.class.getSimpleName();

    private CoordinatorLayout mCoordinatorLayout;
    private RecyclerRefreshLayout mSwipeRefreshLayout;
    private RVAdapter mRVAdapter;

    public FragmentCommunications() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_communications, container, false);
        mSwipeRefreshLayout = (RecyclerRefreshLayout) layout.findViewById(R.id.swiperefresh_communications);
        mSwipeRefreshLayout.setOnRefreshListener(this);

/*        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);
*/
        mCoordinatorLayout = (CoordinatorLayout) layout.findViewById(R.id.coordinatorlayout_communications);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.cardlist_communications);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Communication> CVDataList = new LinkedList<>();
        mRVAdapter = new RVAdapter(CVDataList);
        mRecyclerView.setAdapter(mRVAdapter);

        bindCommunicationsCache();

        mSwipeRefreshLayout.setRefreshing(true);

        new Handler().post(new RESTFulAPI.Communications(getContext()) {
            @Override
            public void then(List<Communication> communications) {
                addCommunications(communications);
            }
        });

        return layout;
    }

    private void addCommunications(List<Communication> communications) {
        if (communications.size() != 0) {
            mRVAdapter.clear();
            mRVAdapter.addAll(communications);

            // Update cache
            new CacheTask(getContext().getCacheDir(), TAG).execute((List) communications);

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        if (isNetworkAvailable(getContext())) {
            new Handler().post(new RESTFulAPI.Communications(getContext()) {
                @Override
                public void then(List<Communication> communications) {
                    addCommunications(communications);
                }
            });
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
        final List<Communication> CVDataList;

        RVAdapter(List<Communication> CVDataList) {
            this.CVDataList = CVDataList;
        }

        void addAll(Collection<Communication> list) {
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
                    .inflate(R.layout.adapter_communications, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder ViewHolder, int i) {
            final Communication communication = CVDataList.get(ViewHolder.getAdapterPosition());
            ViewHolder.Title.setText(communication.getTitle());
            String datestring = communication.getDate().split("T")[0];
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
                Date convertedCommitDate = formatter.parse(datestring);
                formatter = new SimpleDateFormat("dd/MM/YYYY", Locale.ITALIAN);
                ViewHolder.Date.setText(formatter.format(convertedCommitDate));
            } catch (ParseException e) {
                ViewHolder.Date.setText(datestring);
            }
            ViewHolder.Type.setText(communication.getType());
        }

        @Override
        public int getItemCount() {
            return CVDataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView Title;
            final TextView Date;
            final TextView Type;

            ViewHolder(View itemView) {
                super(itemView);
                Title = (TextView) itemView.findViewById(R.id.communication_title);
                Date = (TextView) itemView.findViewById(R.id.communication_date);
                Type = (TextView) itemView.findViewById(R.id.communication_type);
            }
        }
    }

    private void bindCommunicationsCache() {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(getContext().getCacheDir(), TAG));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<Communication> cachedData = new LinkedList<>();
            Communication temp;
            while ((temp = (Communication) objectInputStream.readObject()) != null) {
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

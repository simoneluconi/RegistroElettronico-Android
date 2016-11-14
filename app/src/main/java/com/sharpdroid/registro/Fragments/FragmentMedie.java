package com.sharpdroid.registro.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.user.Entry.Mark;
import com.sharpdroid.registro.user.Entry.MarkSubject;
import com.sharpdroid.registro.user.Entry.Media;
import com.sharpdroid.registro.user.Entry.Metodi;
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

import static com.sharpdroid.registro.user.Entry.Metodi.isNetworkAvailable;

public class FragmentMedie extends Fragment implements RecyclerRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentMedie.class.getSimpleName();

    private CoordinatorLayout mCoordinatorLayout;
    private RecyclerRefreshLayout mSwipeRefreshLayout;
    private  mRVAdapter;

    public FragmentMedie() {

    }

    private void addSubjects(List<MarkSubject> markSubjects) {
        if (markSubjects.size() != 0) {
            mRVAdapter.clear();
            mRVAdapter.addAll(markSubjects);

            // Update cache
            new CacheTask(getContext().getCacheDir(), TAG).execute((List) markSubjects);

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_marks, container, false);
        mSwipeRefreshLayout = (RecyclerRefreshLayout) layout.findViewById(R.id.swiperefresh_voti);
        mSwipeRefreshLayout.setOnRefreshListener(this);

  /*      mSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);
*/
        mCoordinatorLayout = (CoordinatorLayout) layout.findViewById(R.id.coordinatorlayout_voti);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.cardlist_voti);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<MarkSubject> CVDataList = new LinkedList<>();
        mRVAdapter = new RVAdapter(CVDataList);
        mRecyclerView.setAdapter(mRVAdapter);

        bindMarksCache();

        mSwipeRefreshLayout.setRefreshing(true);

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

    private void bindMarksCache() {
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

package com.sharpdroid.registro;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.sharpdroid.registro.ClasseVivaRestAPI.AggiornaVoti;
import static com.sharpdroid.registro.Metodi.isNetworkAvailable;

public class FragmentVoti extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private CoordinatorLayout mCoordinatorLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
        List<Voto> CVDataList = new ArrayList<>();
        RVAdapter mRVAdapter = new RVAdapter(CVDataList);
        mRecyclerView.setAdapter(mRVAdapter);

        CVDataList.add(new Voto("Matematica", "Scritto", "30/10", "", "q2", false, 8.5));
        CVDataList.add(new Voto("Italiano", "Orale", "29/10", "Dovevi studiare di più", "q1", false, 5.5));

        return layout;
    }

    @Override
    public void onRefresh() {
        if (isNetworkAvailable(getContext())) {
            AggiornaVoti();
            mSwipeRefreshLayout.setRefreshing(false);
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

        public void addAll(List<Voto> list) {
            CVDataList.addAll(list);
            notifyItemRangeChanged(0, getItemCount());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_voti, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder ViewHolder, int i) {
            ViewHolder.Materia.setText(CVDataList.get(i).getMateria());
            ViewHolder.Data.setText(CVDataList.get(i).getData());
            ViewHolder.Tipo.setText(CVDataList.get(i).getTipo());
            ViewHolder.Commento.setText(CVDataList.get(i).getCommento());
            ViewHolder.Voto.setText(String.valueOf(CVDataList.get(i).getVoto()));
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
}

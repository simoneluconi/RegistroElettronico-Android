package com.sharpdroid.registro.Views.SubjectDetails;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharpdroid.registro.Interfaces.Subject;
import com.sharpdroid.registro.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoView extends CardView {
    Context mContext;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.edit)
    Button edit;

    InfoAdapter adapter;

    public InfoView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public InfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public InfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    void init() {
        inflate(mContext, R.layout.view_info, this);
        ButterKnife.bind(this);

        adapter = new InfoAdapter(mContext);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext).size(1).marginResId(R.dimen.padding_left_divider1, R.dimen.nav_header_vertical_spacing).build());

        // TODO: 14/12/2016 Start Activity Edit_Details
    }

    public void setSubjectDetails(Subject data) {
        adapter.setData(data);
    }

    public void setEditListener(OnClickListener listener) {
        edit.setOnClickListener(listener);
    }

    class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoHolder> {

        private Context mContext;

        private Subject subject;
        private List<Pair<Integer, String>> data;

        InfoAdapter(Context mContext) {
            this.mContext = mContext;
            data = new ArrayList<>();
        }

        @Override
        public InfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new InfoHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_view_info, parent, false));
        }

        @Override
        public void onBindViewHolder(InfoHolder holder, int position) {
            Pair<Integer, String> pair = data.get(position);

            holder.imageView.setImageResource(pair.first);
            holder.textView.setText(pair.second);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void clear() {
            subject = null;
            data.clear();
            notifyDataSetChanged();
        }

        public void setData(Subject data) {
            subject = data;
            this.data = convertToList(data);
            notifyDataSetChanged();
        }

        private List<Pair<Integer, String>> convertToList(Subject data) {
            List<Pair<Integer, String>> list = new ArrayList<>();

            if (data != null) {
                if (data.getName() != null)
                    list.add(new Pair<>(R.drawable.ic_title, data.getName()));
                else list.add(new Pair<>(R.drawable.ic_title, data.getOriginalName()));
                if (data.getProfessor() != null)
                    list.add(new Pair<>(R.drawable.ic_person, data.getProfessor()));
                if (data.getClassroom() != null)
                    list.add(new Pair<>(R.drawable.ic_room, data.getClassroom()));
                if (data.getNotes() != null)
                    list.add(new Pair<>(R.drawable.ic_description, data.getNotes()));
            }

            return list;
        }

        class InfoHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.image)
            ImageView imageView;
            @BindView(R.id.content)
            TextView textView;

            InfoHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}

package com.sharpdroid.registroelettronico.Views.SubjectDetails;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Utils.Metodi;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.capitalizeEach;


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
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext).colorResId(R.color.divider).size(Metodi.dp(1)).marginResId(R.dimen.padding_left_divider1, R.dimen.nav_header_vertical_spacing).build());
    }

    public void setSubjectDetails(com.sharpdroid.registroelettronico.Databases.Entities.SubjectInfo data) {
        adapter.setData(data);
    }

    public void setEditListener(OnClickListener listener) {
        edit.setOnClickListener(listener);
    }

    class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoHolder> {

        private Context mContext;

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
            data.clear();
            notifyDataSetChanged();
        }

        public void setData(com.sharpdroid.registroelettronico.Databases.Entities.SubjectInfo data) {
            this.data = convertToList(data);
            notifyDataSetChanged();
        }

        private List<Pair<Integer, String>> convertToList(com.sharpdroid.registroelettronico.Databases.Entities.SubjectInfo data) {
            List<Pair<Integer, String>> list = new ArrayList<>();

            if (data != null) {
                list.add(new Pair<>(R.drawable.ic_title,
                        capitalizeEach(
                                (data.getDescription().isEmpty()) ?
                                        data.getSubject().getDescription() :
                                        data.getDescription())));
                if (!data.getSubject().getTeachers().isEmpty())
                    list.add(new Pair<>(R.drawable.ic_person, capitalizeEach(TextUtils.join(" - ", data.getSubject().getTeachers()), true)));
                if (!TextUtils.isEmpty(data.getClassroom()))
                    list.add(new Pair<>(R.drawable.ic_room, data.getClassroom()));
                if (!TextUtils.isEmpty(data.getDetails()))
                    list.add(new Pair<>(R.drawable.ic_description, data.getDetails()));
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

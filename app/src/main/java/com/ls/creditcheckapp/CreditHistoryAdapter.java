package com.ls.creditcheckapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.MessageFormat;
import java.util.List;

public class CreditHistoryAdapter extends RecyclerView.Adapter<CreditHistoryAdapter.MyViewHolder> implements View.OnClickListener {
    private final Context context;
    private final List<CreditScoreHistory> histories;

    @Override
    public void onClick(View view) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtPhoneNo, txtScore, txtDate;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);

            txtDate = view.findViewById(R.id.dateCreated);
            txtScore = view.findViewById(R.id.score);
            txtPhoneNo = view.findViewById(R.id._phone);
            thumbnail = view.findViewById(R.id.thumbnail_);



        }
    }


    public CreditHistoryAdapter(Context context, List<CreditScoreHistory> histories) {
        this.context = context;
        this.histories = histories;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);

        return new CreditHistoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CreditHistoryAdapter.MyViewHolder holder, final int position) {
        final CreditScoreHistory scoreHistory = histories.get(position);
        holder.txtPhoneNo.setText(MessageFormat.format("Phone No:{0}", scoreHistory.getMsisdn1()));
        holder.txtScore.setText(MessageFormat.format("Credit Score:{0}", scoreHistory.getCreditScore()));
        holder.txtDate.setText(MessageFormat.format("Created Date{0}", scoreHistory.getDateCreated1()));


    }

    @Override
    public int getItemCount() {
        return (null != histories ? histories.size() : 0);
    }
    public interface CSHInteractionListener {
        void onItemClick(CreditScoreHistory item);
    }
}

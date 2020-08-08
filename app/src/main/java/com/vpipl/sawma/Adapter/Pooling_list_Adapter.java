package com.vpipl.sawma.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.sawma.MeetingDetailActivity;
import com.vpipl.sawma.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Pooling_list_Adapter extends RecyclerView.Adapter<Pooling_list_Adapter.MyViewHolder> {
    public ArrayList<HashMap<String, String>> array_List;
    LayoutInflater inflater = null;
    Context context;

    public Pooling_list_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        array_List = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.adapter_pooling_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            holder.txt_meeting_title.setText(array_List.get(position).get("PoolingTitle"));
            holder.txt_date_time.setText(array_List.get(position).get("Date_time"));
            holder.txt_status.setText(array_List.get(position).get("PoolingStatus"));

            holder.txt_lead_first_letter.setText(array_List.get(position).get("PoolingTitle").substring(0, 1));

            holder.txt_meeting_title.setSelected(true);
            holder.txt_meeting_title.setSingleLine(true);

            holder.txt_date_time.setSelected(true);
            holder.txt_date_time.setSingleLine(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return array_List.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_lead_first_letter, txt_meeting_title, txt_status ,txt_date_time;
        LinearLayout ll_main;

        public MyViewHolder(View view) {
            super(view);
            txt_lead_first_letter = (TextView) view.findViewById(R.id.txt_lead_first_letter);
            txt_meeting_title = (TextView) view.findViewById(R.id.txt_meeting_title);
            txt_status = (TextView) view.findViewById(R.id.txt_status);
            txt_date_time = (TextView) view.findViewById(R.id.txt_date_time);
            ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        }
    }
}
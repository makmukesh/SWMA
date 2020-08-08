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

public class Upcoming_Meeting_Adapter extends RecyclerView.Adapter<Upcoming_Meeting_Adapter.MyViewHolder> {
    public ArrayList<HashMap<String, String>> array_List;
    LayoutInflater inflater = null;
    Context context;

    public Upcoming_Meeting_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        array_List = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.adapter_recent_meeting, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            holder.txt_meeting_title.setText(array_List.get(position).get("Title"));
            holder.txt_date_time.setText(array_List.get(position).get("Date_time"));

            holder.txt_lead_first_letter.setText(array_List.get(position).get("Title").substring(0 , 1));

            holder.ll_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MeetingDetailActivity.class);
                    intent.putExtra("MeetingID", "" + array_List.get(position).get("MeetingID"));
                    intent.putExtra("Title", "" + array_List.get(position).get("Title"));
                    intent.putExtra("Date_time", "" + array_List.get(position).get("Date_time"));
                    intent.putExtra("Desc", "" + array_List.get(position).get("Desc"));
                    intent.putExtra("Type", "" + array_List.get(position).get("Type"));
                    intent.putExtra("AttendStatus", "" + array_List.get(position).get("AttendStatus"));
                    intent.putExtra("Status", "" + array_List.get(position).get("Status"));
                    intent.putExtra("Suggestion", "" + array_List.get(position).get("Suggestion"));
                    context.startActivity(intent);
                }
            });

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

        TextView txt_lead_first_letter, txt_meeting_title, txt_date_time;
        LinearLayout ll_main;

        public MyViewHolder(View view) {
            super(view);
            txt_lead_first_letter = (TextView) view.findViewById(R.id.txt_lead_first_letter);
            txt_meeting_title = (TextView) view.findViewById(R.id.txt_meeting_title);
            txt_date_time = (TextView) view.findViewById(R.id.txt_date_time);
            ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        }
    }
}
package com.vpipl.sawma.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.sawma.R;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Attendance_History_Adapter extends RecyclerView.Adapter<Attendance_History_Adapter.MyViewHolder> {
    public ArrayList<HashMap<String, String>> array_List;
    LayoutInflater inflater = null;
    Context context;

    public Attendance_History_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        array_List = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.adapter_attendance_history, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            holder.txt_title.setText(array_List.get(position).get("MeetingTitle"));
            holder.txt_time.setText(array_List.get(position).get("AttendTime"));

            holder.txt_status.setText(array_List.get(position).get("AttendStatus"));
            if (array_List.get(position).get("AttendStatus").equalsIgnoreCase("Present")) {
                holder.txt_status.setBackgroundColor(context.getResources().getColor(R.color.color_Present));
            } else if (array_List.get(position).get("AttendStatus").equalsIgnoreCase("Absent")) {
                holder.txt_status.setBackgroundColor(context.getResources().getColor(R.color.color_Absent));
            } else {
                holder.txt_status.setBackgroundColor(context.getResources().getColor(R.color.color_Pending));
            }

            String date = array_List.get(position).get("AttendDate");
            String[] items1 = date.split("/");
            String date1 = items1[0];
            String month = items1[1];
            String year = items1[2];

            holder.txt_event_date.setText(date1);
            holder.txt_event_month.setText(getMonth(Integer.parseInt(month)));

            SimpleDateFormat df = new SimpleDateFormat("EEEE");
            Date current = new Date();
            String dtStart = array_List.get(position).get("AttendDate");
            String day = "";
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyyy");
            try {
                Date date123 = format.parse(dtStart);
                System.out.println(date);
                day = df.format(date123).toString();

            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.txt_event_year.setText(day);
            //holder.txt_event_year.setText(android.text.format.DateFormat.format("EEEE", Long.parseLong(array_List.get(position).get("AttendDate"))));

            holder.txt_title.setSelected(true);
            holder.txt_title.setSingleLine(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMonth(int month) throws ParseException {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    @Override
    public int getItemCount() {
        return array_List.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_title, txt_time, txt_event_year, txt_event_date, txt_event_month, txt_status;
        LinearLayout ll_main;

        public MyViewHolder(View view) {
            super(view);
            txt_title = (TextView) view.findViewById(R.id.txt_title);
            txt_time = (TextView) view.findViewById(R.id.txt_time);
            txt_event_year = (TextView) view.findViewById(R.id.txt_event_year);
            txt_event_date = (TextView) view.findViewById(R.id.txt_event_date);
            txt_event_month = (TextView) view.findViewById(R.id.txt_event_month);
            txt_status = (TextView) view.findViewById(R.id.txt_status);
            ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        }
    }
}
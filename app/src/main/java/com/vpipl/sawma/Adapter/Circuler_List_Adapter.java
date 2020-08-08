package com.vpipl.sawma.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.sawma.CirculerDetailActivity;
import com.vpipl.sawma.R;
import com.vpipl.sawma.RemotePDFActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class Circuler_List_Adapter extends RecyclerView.Adapter<Circuler_List_Adapter.MyViewHolder> {
    public ArrayList<HashMap<String, String>> array_List;
    LayoutInflater inflater = null;
    Context context;

    public Circuler_List_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        array_List = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.adapter_circuler_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            holder.txt_circuler_title.setText(array_List.get(position).get("Title"));
            holder.txt_date_time.setText(array_List.get(position).get("Date_time"));

            holder.txt_circuler_title.setSelected(true);
            holder.txt_circuler_title.setSingleLine(true);

            holder.txt_date_time.setSelected(true);
            holder.txt_date_time.setSingleLine(true);

            holder.ll_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context , RemotePDFActivity.class);
                    intent.putExtra("Title" , array_List.get(position).get("Title"));
                    intent.putExtra("FileURL" , array_List.get(position).get("FileURL"));
                    context.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return array_List.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_circuler_title, txt_date_time;
        LinearLayout ll_main;

        public MyViewHolder(View view) {
            super(view);
            txt_circuler_title = (TextView) view.findViewById(R.id.txt_circuler_title);
            txt_date_time = (TextView) view.findViewById(R.id.txt_date_time);
            ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        }
    }
}
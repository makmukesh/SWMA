package com.vpipl.sawma.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.sawma.PackagePurchaseActivity;
import com.vpipl.sawma.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Package_List_Adapter extends RecyclerView.Adapter<Package_List_Adapter.MyViewHolder> {
    public ArrayList<HashMap<String, String>> array_List;
    LayoutInflater inflater = null;
    Context context;

    public Package_List_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        array_List = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.adapter_packagelist, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            holder.txt_package_title.setText(array_List.get(position).get("PackageName"));
            holder.txt_package_validity.setText("for " + array_List.get(position).get("Validity") + " months ");
            holder.txt_package_amount.setText(array_List.get(position).get("Amount"));

            holder.txt_package_title.setSelected(true);
            holder.txt_package_title.setSingleLine(true);

            holder.txt_get_membership.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //PID
                    Intent intent = new Intent(context, PackagePurchaseActivity.class);
                    intent.putExtra("PID", "" + array_List.get(position).get("PID"));
                    intent.putExtra("Validity", "" + array_List.get(position).get("Validity"));
                    intent.putExtra("Amount", "" + array_List.get(position).get("Amount"));
                    intent.putExtra("PackageName", "" + array_List.get(position).get("PackageName"));
                    intent.putExtra("PackageType", "" + array_List.get(position).get("PackageType"));
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

        TextView txt_package_title, txt_package_amount, txt_package_validity, txt_get_membership;
        LinearLayout ll_main;

        public MyViewHolder(View view) {
            super(view);
            txt_package_title = (TextView) view.findViewById(R.id.txt_package_title);
            txt_package_amount = (TextView) view.findViewById(R.id.txt_package_amount);
            txt_package_validity = (TextView) view.findViewById(R.id.txt_package_validity);
            txt_get_membership = (TextView) view.findViewById(R.id.txt_get_membership);
            ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        }
    }
}
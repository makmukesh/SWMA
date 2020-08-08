package com.vpipl.sawma;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.vpipl.sawma.Utils.AppController;
import com.vpipl.sawma.Utils.AppUtils;
import com.vpipl.sawma.Utils.SPUtils;

public class PackagePurchaseResponseActivity extends Activity {
    private String TAG = "PackagePurchaseResponseActivity";

    Activity act;
    ImageView img_nav_back;
    LottieAnimationView lottie_icon_mobile ;

    TextView txt_txn_id, txt_msg, txt_go_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_package_purchase_response);

            act = PackagePurchaseResponseActivity.this;

            txt_txn_id = findViewById(R.id.txt_txn_id);
            txt_msg = findViewById(R.id.txt_msg);
            txt_go_home = findViewById(R.id.txt_go_home);
            lottie_icon_mobile = findViewById(R.id.lottie_icon_mobile);

            txt_txn_id.setText("Txn ID : " + getIntent().getStringExtra("txn_id"));
            txt_msg.setText("" + getIntent().getStringExtra("Msg"));


            if (getIntent().getStringExtra("status").equalsIgnoreCase("S")) {
                txt_go_home.setBackground(getResources().getDrawable(R.drawable.ic_home));
               // lottie_icon_mobile.setAnimation(R.raw.success);
            } else {
                txt_go_home.setBackground(getResources().getDrawable(R.drawable.ic_back));
                lottie_icon_mobile.setAnimation(R.raw.simple_circle_warning);
                lottie_icon_mobile.playAnimation();
            }

            txt_go_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getIntent().getStringExtra("status").equalsIgnoreCase("S")) {
                        AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();
                        startActivity(new Intent(PackagePurchaseResponseActivity.this, MainActivity.class));
                        finish();
                    } else {
                        AppController.getSpUserInfo().edit().clear().commit();
                        Intent intent = new Intent(act, Login_Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        act.startActivity(intent);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(PackagePurchaseResponseActivity.this);
        }
        System.gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (getIntent().getStringExtra("status").equalsIgnoreCase("S")) {
                AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();
                startActivity(new Intent(PackagePurchaseResponseActivity.this, MainActivity.class));
                finish();
            } else {
                AppController.getSpUserInfo().edit().clear().commit();
                Intent intent = new Intent(act, Login_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                act.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
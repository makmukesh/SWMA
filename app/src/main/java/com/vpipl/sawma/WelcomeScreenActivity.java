package com.vpipl.sawma;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.vpipl.sawma.Utils.AppController;
import com.vpipl.sawma.Utils.SPUtils;


public class WelcomeScreenActivity extends Activity {

    TextView txt_member_name_first, txt_next;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            act = WelcomeScreenActivity.this;
            setContentView(R.layout.activity_welcome_screen);

            txt_member_name_first = findViewById(R.id.txt_member_name_first);
            txt_next = findViewById(R.id.txt_next);

            txt_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, MainActivity.class));
                }
            });
//
            txt_member_name_first.setText("" + AppController.getSpUserInfo().getString(SPUtils.MemberName, "").substring(0, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
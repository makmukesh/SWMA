package com.vpipl.sawma;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Mukesh on 28/07/2020.
 */
public class CloseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        finish();
        overridePendingTransition(0, 0);

    }
}

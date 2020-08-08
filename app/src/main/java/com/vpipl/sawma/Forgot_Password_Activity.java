package com.vpipl.sawma;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.iid.FirebaseInstanceId;
import com.vpipl.sawma.Utils.AppController;
import com.vpipl.sawma.Utils.AppUtils;
import com.vpipl.sawma.Utils.QueryUtils;
import com.vpipl.sawma.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class Forgot_Password_Activity extends Activity {

    private static final String TAG = "Forgot_Password_Activity";
    private EditText edtxt_mobile, edtxt_otp;
    LinearLayout ll_et_mobileno, ll_et_otp;
    RelativeLayout rl_btn_submit, rl_btn_otp_submit;

    String mobileno;
    String user_otp, OTP, Password;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            act = Forgot_Password_Activity.this;
            setContentView(R.layout.activity_forgot_password);
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


            edtxt_mobile = findViewById(R.id.edtxt_mobile);
            edtxt_otp = findViewById(R.id.edtxt_otp);
            ll_et_mobileno = findViewById(R.id.ll_et_mobileno);
            ll_et_otp = findViewById(R.id.ll_et_otp);
            rl_btn_submit = findViewById(R.id.rl_btn_submit);
            rl_btn_otp_submit = findViewById(R.id.rl_btn_otp_submit);

            ShowData1();

            rl_btn_submit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    ValidateData();
                }
            });
            rl_btn_otp_submit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    ValidateDataOTP();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void ValidateData() {
        edtxt_mobile.setError(null);

        mobileno = edtxt_mobile.getText().toString().trim();

        if (TextUtils.isEmpty(mobileno)) {
            Toasty.error(act, "Please Enter Mobile No ", Toast.LENGTH_SHORT, true).show();
            edtxt_mobile.requestFocus();
        } else if (!AppUtils.isValidMobileno(mobileno)) {
            Toasty.error(act, "Please Enter Valid Mobile No", Toast.LENGTH_SHORT, true).show();
            edtxt_mobile.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(act)) {
                executeForgotPasswordOTPRequest();
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void executeForgotPasswordOTPRequest() {
        try {

            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("MobileNo", mobileno));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToUser_ForgotPassword, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("", "" + e.getMessage());
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);
                            if (jsonObject.getString("Status").equalsIgnoreCase("False")) {
                                AppUtils.alertDialog(act, jsonObject.getString("Message"));
                            } else {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                if (jsonArrayData.length() != 0) {
                                    ShowData2();
                                    OTP = jsonArrayData.getJSONObject(0).getString("OTP");
                                    OTP = "123456";
                                    Password = jsonArrayData.getJSONObject(0).getString("Password");
                                } else {
                                    AppUtils.alertDialog(act, jsonObject.getString("Message"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void ValidateDataOTP() {
        edtxt_otp.setError(null);

        user_otp = edtxt_otp.getText().toString().trim();

        if (TextUtils.isEmpty(user_otp)) {
            Toasty.error(act, "Please Enter OTP ", Toast.LENGTH_SHORT, true).show();
            edtxt_otp.requestFocus();
        } else if (!user_otp.equalsIgnoreCase(OTP)) {
            Toasty.error(act, "OTP Not Match ", Toast.LENGTH_SHORT, true).show();
            edtxt_otp.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(act)) {
                executeForgotPasswordOTPSubmit();
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void executeForgotPasswordOTPSubmit() {
        try {

            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Mobile", mobileno));
                            postParameters.add(new BasicNameValuePair("Password", mobileno));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToUser_SendPassword, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("", "" + e.getMessage());
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);
                            if (jsonObject.getString("Status").equalsIgnoreCase("False")) {
                                AppUtils.alertDialog(act, jsonObject.getString("Message"));
                            } else {
                                AppUtils.alertDialogWithFinish(act, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void ShowData1() {
        ll_et_mobileno.setVisibility(View.VISIBLE);
        ll_et_otp.setVisibility(View.GONE);

        rl_btn_submit.setVisibility(View.VISIBLE);
        rl_btn_otp_submit.setVisibility(View.GONE);
    }

    private void ShowData2() {
        ll_et_mobileno.setVisibility(View.GONE);
        ll_et_otp.setVisibility(View.VISIBLE);

        rl_btn_submit.setVisibility(View.GONE);
        rl_btn_otp_submit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (ll_et_otp.getVisibility() == View.VISIBLE) {
            ShowData1();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //  AppUtils.showExceptionDialog(act);
        }
        return super.onOptionsItemSelected(item);
    }
}
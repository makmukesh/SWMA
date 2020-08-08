package com.vpipl.sawma;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
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


public class Register_Activity extends Activity {

    private static final String TAG = "Register_Activity";
    TextView txt_terms_conditon, txt_sign_up, txt_login;
    TextView txt_forgot_password;
    private EditText edtxt_name, edtxt_mobile, edtxt_address, edtxt_mill_name,
            edtxt_department_name, edtxt_password;
    CheckBox cb_terms_and_condition;

    String name, mobileno, address, mill_name, dept_name, password;
    Activity act;
    TextView txt_eye_close, txt_eye_open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            act = Register_Activity.this;
            setContentView(R.layout.activity_register);
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            txt_terms_conditon = findViewById(R.id.txt_terms_conditon);
            edtxt_name = findViewById(R.id.edtxt_name);
            edtxt_mobile = findViewById(R.id.edtxt_mobile);
            edtxt_address = findViewById(R.id.edtxt_address);
            edtxt_mill_name = findViewById(R.id.edtxt_mill_name);
            edtxt_department_name = findViewById(R.id.edtxt_department_name);
            edtxt_password = findViewById(R.id.edtxt_password);
            cb_terms_and_condition = findViewById(R.id.cb_terms_and_condition);

            txt_login = findViewById(R.id.txt_login);

            txt_eye_close = findViewById(R.id.txt_eye_close);
            txt_eye_open = findViewById(R.id.txt_eye_open);
            txt_sign_up = findViewById(R.id.txt_sign_up);

            txt_sign_up.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);

                    if(cb_terms_and_condition.isChecked()) {
                        ValidateData();
                    }
                    else {
                        Toasty.error(act, "Please Checked Terms and Conditions", Toast.LENGTH_SHORT, true).show();
                    }
                }
            });
            txt_login.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    startSplash(new Intent(act, Login_Activity.class));
                }
            });

            edtxt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == 1234 || id == EditorInfo.IME_NULL) {
                        AppUtils.hideKeyboardOnClick(act, textView);
                        if(cb_terms_and_condition.isChecked()) {
                            ValidateData();
                        }
                        else {
                            Toasty.error(act, "Please Checked Terms and Conditions", Toast.LENGTH_SHORT, true).show();
                        }
                        return true;
                    }
                    return false;
                }
            });

            txt_eye_open.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // hide password
                    edtxt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_eye_close.setVisibility(View.VISIBLE);
                    txt_eye_open.setVisibility(View.GONE);
                }
            });
            txt_eye_close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // show password
                    edtxt_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_eye_close.setVisibility(View.GONE);
                    txt_eye_open.setVisibility(View.VISIBLE);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void ValidateData() {
        edtxt_name.setError(null);
        edtxt_mobile.setError(null);
        edtxt_address.setError(null);
        edtxt_mill_name.setError(null);
        edtxt_department_name.setError(null);
        edtxt_password.setError(null);

        name = edtxt_name.getText().toString().trim();
        mobileno = edtxt_mobile.getText().toString().trim();
        address = edtxt_address.getText().toString().trim();
        mill_name = edtxt_mill_name.getText().toString().trim();
        dept_name = edtxt_department_name.getText().toString().trim();
        password = edtxt_password.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toasty.error(act, "Please Enter Your Name ", Toast.LENGTH_SHORT, true).show();
            edtxt_name.requestFocus();
        } else if (TextUtils.isEmpty(mobileno)) {
            Toasty.error(act, "Please Enter Mobile No ", Toast.LENGTH_SHORT, true).show();
            edtxt_mobile.requestFocus();
        } else if (!AppUtils.isValidMobileno(mobileno)) {
            Toasty.error(act, "Please Enter Valid Mobile No", Toast.LENGTH_SHORT, true).show();
            edtxt_mobile.requestFocus();
        } else if (TextUtils.isEmpty(address)) {
            Toasty.error(act, "Please Enter Your Address", Toast.LENGTH_SHORT, true).show();
            edtxt_address.requestFocus();
        } else if (TextUtils.isEmpty(mill_name)) {
            Toasty.error(act, "Please Enter Your Mill Name", Toast.LENGTH_SHORT, true).show();
            edtxt_mill_name.requestFocus();
        } else if (TextUtils.isEmpty(dept_name)) {
            Toasty.error(act, "Please Enter Your Department Name", Toast.LENGTH_SHORT, true).show();
            edtxt_department_name.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            Toasty.error(act, "Please Enter Password", Toast.LENGTH_SHORT, true).show();
            edtxt_password.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(act)) {
                executeRegisterRequest();
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void executeRegisterRequest() {
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
                            postParameters.add(new BasicNameValuePair("Name", "" + name));
                            postParameters.add(new BasicNameValuePair("MobileNo", mobileno));
                            postParameters.add(new BasicNameValuePair("Address", address));
                            postParameters.add(new BasicNameValuePair("MillName", mill_name));
                            postParameters.add(new BasicNameValuePair("DepartName", dept_name));
                            postParameters.add(new BasicNameValuePair("Password", password));
                            // postParameters.add(new BasicNameValuePair("DeviceID", "" + AppUtils.getDeviceID(act)));
                            //postParameters.add(new BasicNameValuePair("FirebaseID", "" + FirebaseInstanceId.getInstance().getToken()));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToUser_Registration, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                if (jsonArrayData.length() != 0) {
                                    AppUtils.alertDialogWithFinish(act, jsonObject.getString("Message"));

                                   /* AppController.getSpUserInfo().edit().clear().commit();

                                    AppController.getSpUserInfo().edit()
                                            .putString(SPUtils.USER_TYPE, "User")
                                            .putString(SPUtils.Member_ID, jsonArrayData.getJSONObject(0).getString("RID"))
                                            .putString(SPUtils.MemberName, jsonArrayData.getJSONObject(0).getString("Name"))
                                            .putString(SPUtils.MemberMobileNo, jsonArrayData.getJSONObject(0).getString("MobileNo"))
                                            .putString(SPUtils.MemberAddress, jsonArrayData.getJSONObject(0).getString("Address"))
                                            .putString(SPUtils.MemberMillName, jsonArrayData.getJSONObject(0).getString("MillName"))
                                            .putString(SPUtils.MemberDepartment, jsonArrayData.getJSONObject(0).getString("DepartName"))
                                            .putString(SPUtils.MemberPasswd, jsonArrayData.getJSONObject(0).getString("Password"))
                                            .putString(SPUtils.DeviceToken, jsonArrayData.getJSONObject(0).getString("DeviceID"))
                                            .putString(SPUtils.MemberActiveStatus, jsonArrayData.getJSONObject(0).getString("ActiveStatus"))
                                            .putString(SPUtils.MemberDOJ, jsonArrayData.getJSONObject(0).getString("RTS"))
                                            .commit();
                                    startActivity(new Intent(act, PackageListActivity.class));
                                    finish();*/
                                } else {
                                    AppUtils.alertDialog(act, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(act, jsonObject.getString("Message"));
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
/*
    private void saveLoginUserInfo(final JSONArray jsonArray) {
        try {

            AppController.getSpUserInfo().edit().clear().commit();

            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.USER_TYPE, "Staff")
                    .putString(SPUtils.Member_ID, jsonArray.getJSONObject(0).getString("EMPID"))
                    .putString(SPUtils.MemberName, jsonArray.getJSONObject(0).getString("FirstName"))
                    .putString(SPUtils.MemberMobileNo, jsonArray.getJSONObject(0).getString("Mobile"))
                    .putString(SPUtils.MemberEMail, jsonArray.getJSONObject(0).getString("Email"))
                    .putString(SPUtils.MemberAddress, jsonArray.getJSONObject(0).getString("Address"))
                    .putString(SPUtils.MemberPasswd, jsonArray.getJSONObject(0).getString("Password"))
                    .putString(SPUtils.MemberDepartment, jsonArray.getJSONObject(0).getString("Department"))
                    .putString(SPUtils.DeviceToken, jsonArray.getJSONObject(0).getString("DeviceID"))
                    .putString(SPUtils.MemberActiveStatus, jsonArray.getJSONObject(0).getString("ActiveStatus"))
                    .putString(SPUtils.USER_profile_pic_byte_code, AppUtils.imageURL() + jsonArray.getJSONObject(0).getString("Images"))
                    //.putString(SPUtils.MemberDOJ, AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("RTS")))
                    .commit();

            startSplash(new Intent(act, MainActivity.class));

            AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    private void startSplash(final Intent intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
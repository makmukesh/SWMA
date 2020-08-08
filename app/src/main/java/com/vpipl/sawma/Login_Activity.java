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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vpipl.sawma.Utils.AppController;
import com.vpipl.sawma.Utils.QueryUtils;
import com.vpipl.sawma.Utils.SPUtils;
import com.vpipl.sawma.R;
import com.vpipl.sawma.Utils.AppUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class Login_Activity extends Activity {

    private static final String TAG = "Login_Activity";
    TextView txt_login;
    TextView txt_forgot_password;
    private EditText edtxt_mobile, edtxt_password;

    String mobileno;
    String password;
    Activity act;
    TextView txt_eye_close, txt_eye_open, txt_sign_up;
    Animation btnAnim ;
    LinearLayout ll_login_btn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            act = Login_Activity.this;
            setContentView(R.layout.activity_login);
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


            ll_login_btn = findViewById(R.id.ll_login_btn);
            edtxt_mobile = findViewById(R.id.edtxt_mobile);
            edtxt_password = findViewById(R.id.edtxt_password);

            txt_login = findViewById(R.id.txt_login);

            txt_forgot_password = findViewById(R.id.txt_forgot_password);
            txt_eye_close = findViewById(R.id.txt_eye_close);
            txt_eye_open = findViewById(R.id.txt_eye_open);
            txt_sign_up = findViewById(R.id.txt_sign_up);

          //  btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
          //  ll_login_btn.setAnimation(btnAnim);

           // ll_login_btn.setAlpha(0.5f);
            txt_login.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    ValidateData();
                  //  ll_login_btn.setAlpha(1f);
                    AppUtils.showProgressDialog(act);
                    // startSplash(new Intent(act, MainActivity.class));
                }
            });

            edtxt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == 1234 || id == EditorInfo.IME_NULL) {
                        AppUtils.hideKeyboardOnClick(act, textView);
                        ValidateData();
                        AppUtils.showProgressDialog(act);
                        return true;
                    }
                    return false;
                }
            });

            txt_forgot_password.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    startActivity(new Intent(act, Forgot_Password_Activity.class));

                }
            });
            txt_sign_up.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    startActivity(new Intent(act, Register_Activity.class));
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

        /*    FirebaseApp.initializeApp(act);
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.e("token", token);*/

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
        edtxt_password.setError(null);

        mobileno = edtxt_mobile.getText().toString().trim();
        password = edtxt_password.getText().toString().trim();

        if (TextUtils.isEmpty(mobileno)) {
            AppUtils.dismissProgressDialog();
            Toasty.error(act, "Please Enter Mobile No ", Toast.LENGTH_SHORT, true).show();
            edtxt_mobile.requestFocus();
        } else if (!AppUtils.isValidMobileno(mobileno)) {
            AppUtils.dismissProgressDialog();
            Toasty.error(act, "Please Enter Valid Mobile No", Toast.LENGTH_SHORT, true).show();
            edtxt_mobile.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            AppUtils.dismissProgressDialog();
            Toasty.error(act, "Please Enter Password", Toast.LENGTH_SHORT, true).show();
            edtxt_password.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(act)) {
                executeLoginRequest(mobileno, password);
            } else {
                AppUtils.dismissProgressDialog();
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void executeLoginRequest(final String MobileNo, final String passwd) {
        try {

            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                       // AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Mobile", MobileNo));
                            postParameters.add(new BasicNameValuePair("Password", passwd));
                            postParameters.add(new BasicNameValuePair("DeviceID", "" + AppUtils.getDeviceID(act)));
                            postParameters.add(new BasicNameValuePair("FireBaseID", "" + FirebaseInstanceId.getInstance().getToken()));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToUser_Login, TAG);
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
                                    saveLoginUserInfo(jsonArrayData);
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

    private void saveLoginUserInfo(final JSONArray jsonArray) {
        try {

            AppController.getSpUserInfo().edit().clear().commit();

            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.USER_TYPE, "User")
                    .putString(SPUtils.Member_ID, jsonArray.getJSONObject(0).getString("RID"))
                    .putString(SPUtils.MemberName, jsonArray.getJSONObject(0).getString("Name"))
                    .putString(SPUtils.MemberMobileNo, jsonArray.getJSONObject(0).getString("MobileNo"))
                    .putString(SPUtils.MemberAddress, jsonArray.getJSONObject(0).getString("Address"))
                    .putString(SPUtils.MemberMillName, jsonArray.getJSONObject(0).getString("MillName"))
                    .putString(SPUtils.MemberDepartment, jsonArray.getJSONObject(0).getString("DepartName"))
                    .putString(SPUtils.MemberPasswd, jsonArray.getJSONObject(0).getString("Password"))
                    .putString(SPUtils.DeviceToken, jsonArray.getJSONObject(0).getString("DeviceID"))
                    .putString(SPUtils.MemberActiveStatus, jsonArray.getJSONObject(0).getString("ActiveStatus"))
                    .putString(SPUtils.USER_profile_pic_byte_code, AppUtils.imageURL() + jsonArray.getJSONObject(0).getString("Image"))
                    .putString(SPUtils.MemberDOJ, jsonArray.getJSONObject(0).getString("RTS"))
                    .commit();

            if (jsonArray.getJSONObject(0).getString("Status").equalsIgnoreCase("M")) {
                startActivity(new Intent(act, PackageListActivity.class).putExtra("Type", "M"));
            } else if (jsonArray.getJSONObject(0).getString("Status").equalsIgnoreCase("R")) {
                startActivity(new Intent(act, PackageListActivity.class).putExtra("Type", "R"));
            } else if (jsonArray.getJSONObject(0).getString("Status").equalsIgnoreCase("True")) {
                startSplash(new Intent(act, WelcomeScreenActivity.class));
                AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();
            } else {
                AppController.getSpUserInfo().edit().clear().commit();
                AppUtils.alertDialog(act, jsonArray.getJSONObject(0).getString("Message"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
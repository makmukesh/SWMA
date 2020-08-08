package com.vpipl.sawma;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.vpipl.sawma.Adapter.Notification_History_Adapter;
import com.vpipl.sawma.Utils.AppController;
import com.vpipl.sawma.Utils.AppUtils;
import com.vpipl.sawma.Utils.QueryUtils;
import com.vpipl.sawma.Utils.SPUtils;
import com.vpipl.sawma.firbase.Config;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ru.nikartm.support.ImageBadgeView;

public class MyProfileActivity extends Activity {
    private String TAG = "MyProfileActivity";

    Activity act;
    ImageView img_nav_back;
    ImageBadgeView mukesh_begview;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ValueAnimator animator;

    private EditText edtxt_name, edtxt_mobile, edtxt_address, edtxt_mill_name,
            edtxt_department_name, edtxt_password;

    String name, mobileno, address, mill_name, dept_name, password;
    TextView txt_edit_profile, txt_eye_close, txt_eye_open;

    LinearLayout ll_home_bo_home, ll_home_bo_attendance, ll_home_bo_profile, ll_home_bo_notification, ll_home_bo_logout;

    /*Profile Image Uplaod*/
    private static final int REQUEST_CODE = 1234;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;
    ImageView user_profile;
    FrameLayout fl_profile_picture;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_profile);

            act = MyProfileActivity.this;
            img_nav_back = findViewById(R.id.img_nav_back);
            mukesh_begview = findViewById(R.id.mukesh_begview);

            img_nav_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) > 0) {
                animator = ValueAnimator.ofFloat(0f, 1f);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mukesh_begview.setAlpha((Float) animation.getAnimatedValue());
                    }
                });
                animator.setDuration(500);
                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.setRepeatCount(-1);
                animator.start();
            }

            mukesh_begview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppController.getSpUserInfo().edit().putString(SPUtils.notification_count, "0").commit();
                    //Toast.makeText(NotificationHistoryActivity.this, AppController.getSpUserInfo().getString(SPUtils.notification_count, "0"), Toast.LENGTH_SHORT).show();
                    // startActivity(new Intent(act, AddCartCheckOut_Activity.class));
                }
            });
            mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));

            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.e("count", AppController.getNotification_count().getString(SPUtils.notification_count, "0"));
                    mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));
                    if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) > 0) {
                        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                mukesh_begview.setAlpha((Float) animation.getAnimatedValue());
                            }
                        });
                        animator.setDuration(500);
                        animator.setRepeatMode(ValueAnimator.REVERSE);
                        animator.setRepeatCount(-1);
                        animator.start();
                    }
                }
            };

            edtxt_name = findViewById(R.id.edtxt_name);
            edtxt_mobile = findViewById(R.id.edtxt_mobile);
            edtxt_address = findViewById(R.id.edtxt_address);
            edtxt_mill_name = findViewById(R.id.edtxt_mill_name);
            edtxt_department_name = findViewById(R.id.edtxt_department_name);
            edtxt_password = findViewById(R.id.edtxt_password);

            txt_eye_close = findViewById(R.id.txt_eye_close);
            txt_eye_open = findViewById(R.id.txt_eye_open);
            txt_edit_profile = findViewById(R.id.txt_edit_profile);

            txt_eye_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // hide password
                    edtxt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_eye_close.setVisibility(View.VISIBLE);
                    txt_eye_open.setVisibility(View.GONE);
                }
            });
            txt_eye_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // show password
                    edtxt_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_eye_close.setVisibility(View.GONE);
                    txt_eye_open.setVisibility(View.VISIBLE);
                }
            });
            txt_edit_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    ValidateData();
                }
            });

            if (AppUtils.isNetworkAvailable(act)) {
                executeLoginRequest();
            } else {
                AppUtils.alertDialogWithFinish(act, getResources().getString(R.string.txt_networkAlert));
            }

            /*Bottom Bar Work Start */

            ll_home_bo_home = findViewById(R.id.ll_home_bo_home);
            ll_home_bo_attendance = findViewById(R.id.ll_home_bo_attendance);
            ll_home_bo_profile = findViewById(R.id.ll_home_bo_profile);
            ll_home_bo_notification = findViewById(R.id.ll_home_bo_notification);
            ll_home_bo_logout = findViewById(R.id.ll_home_bo_logout);
            mukesh_begview = findViewById(R.id.mukesh_begview);

            ll_home_bo_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, MainActivity.class));
                    finish();
                }
            });
            ll_home_bo_attendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, AttendanceHistoryActivity.class));
                    finish();
                }
            });
            ll_home_bo_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  startActivity(new Intent(act, MyProfileActivity.class));
                    //  finish();
                }
            });
            ll_home_bo_notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, NotificationActivity.class));
                }
            });
            ll_home_bo_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.showDialogSignOut(act);
                }
            });

            /*profile picture */
            user_profile = findViewById(R.id.user_profile);
            fl_profile_picture = findViewById(R.id.fl_profile_picture);

            fl_profile_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkAndRequestPermissions())
                        selectImage();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));
        super.onRestart();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
        System.gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeLoginRequest() {
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
                            postParameters.add(new BasicNameValuePair("Mobile", AppController.getSpUserInfo().getString(SPUtils.MemberMobileNo, "")));
                            postParameters.add(new BasicNameValuePair("Password", AppController.getSpUserInfo().getString(SPUtils.MemberPasswd, "")));
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
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                if (jsonArrayData.length() != 0) {
                                    saveLoginUserInfo(jsonArrayData);
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

    private void saveLoginUserInfo(final JSONArray jsonArray) {
        try {
            edtxt_name.setText("" + jsonArray.getJSONObject(0).getString("Name"));
            edtxt_mobile.setText("" + jsonArray.getJSONObject(0).getString("MobileNo"));
            edtxt_address.setText("" + jsonArray.getJSONObject(0).getString("Address"));
            edtxt_mill_name.setText("" + jsonArray.getJSONObject(0).getString("MillName"));
            edtxt_department_name.setText("" + jsonArray.getJSONObject(0).getString("DepartName"));
            edtxt_password.setText("" + jsonArray.getJSONObject(0).getString("Password"));

            AppUtils.loadImage(act, AppUtils.imageURL() + jsonArray.getJSONObject(0).getString("Image"), user_profile);
            AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic_byte_code, (AppUtils.imageURL() + jsonArray.getJSONObject(0).getString("Image"))).commit();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
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
                executeEditProfileRequest(mobileno, password);
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void executeEditProfileRequest(final String MobileNo, final String passwd) {
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
                            /*RID:Name:MobileNo:Address:MillName:DepartName:Password:*/
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("RID", "" + AppController.getSpUserInfo().getString(SPUtils.Member_ID, "")));
                            postParameters.add(new BasicNameValuePair("Name", "" + name));
                            postParameters.add(new BasicNameValuePair("MobileNo", MobileNo));
                            postParameters.add(new BasicNameValuePair("Address", address));
                            postParameters.add(new BasicNameValuePair("MillName", mill_name));
                            postParameters.add(new BasicNameValuePair("DepartName", dept_name));
                            postParameters.add(new BasicNameValuePair("Password", passwd));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToUser_UpdateRegistrationProfile, TAG);
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
                                    AppController.getSpUserInfo().edit().clear().commit();

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

                                    final Dialog dialog = AppUtils.createDialog(act, true);
                                    TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
                                    dialog4all_txt.setText(jsonObject.getString("Message"));
                                    TextView txtsubmit = (TextView) dialog.findViewById(R.id.txt_submit);
                                    txtsubmit.setText("Ok");
                                    txtsubmit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            startActivity(new Intent(act, MainActivity.class));
                                            finish();

                                        }
                                    });
                                    dialog.show();
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

    private boolean checkAndRequestPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int permissionWRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionREAD_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionREAD_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        String imageStoragePath = destination.getAbsolutePath();
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        executePostImageUploadRequest(bitmap);
        user_profile.setImageBitmap(bitmap);

        Log.e("from camera data", imageStoragePath);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        executePostImageUploadRequest(bm);
        user_profile.setImageBitmap(bm);
        String imagepath = bm.toString();
        Log.e("from gallery data", imagepath);
    }

    private void executePostImageUploadRequest(final Bitmap bitmap) {
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
                            postParameters.add(new BasicNameValuePair("RID", AppController.getSpUserInfo().getString(SPUtils.Member_ID, "")));
                            postParameters.add(new BasicNameValuePair("Image", AppUtils.getBase64StringFromBitmap(bitmap)));
                            postParameters.add(new BasicNameValuePair("Extension", "PNG"));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToUser_UploadProfileImage, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (!jsonArrayData.getJSONObject(0).getString("Image").equals("")) {
                                    AppUtils.loadImage(act, AppUtils.imageURL() + jsonArrayData.getJSONObject(0).getString("Image"), user_profile);
                                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic_byte_code, (AppUtils.imageURL() + jsonArrayData.getJSONObject(0).getString("Image"))).commit();
                                }
                            } else {
                                //   AppUtils.alertDialog(act, jsonObject.getString("Message"));
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
}
package com.vpipl.sawma;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vpipl.sawma.Utils.AppController;
import com.vpipl.sawma.Utils.AppUtils;
import com.vpipl.sawma.Utils.QueryUtils;
import com.vpipl.sawma.Utils.SPUtils;
import com.vpipl.sawma.firbase.Config;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import ru.nikartm.support.ImageBadgeView;

public class MeetingDetailActivity extends Activity {
    private String TAG = "MeetingDetailActivity";

    Activity act;
    ImageView img_nav_back;
    ImageBadgeView mukesh_begview;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ValueAnimator animator;
    public Dialog cust_dialog;
    String s_attendance_sts = "Pending";
    LinearLayout ll_home_bo_home, ll_home_bo_attendance, ll_home_bo_profile, ll_home_bo_notification, ll_home_bo_logout;
    TextView txt_meeting_title, txt_meeting_datetime, txt_meeting_status, txt_detail, txt_feedback_proceed;
    ImageView iv_attendance;
    LinearLayout ll_Suggestion ,ll_feedback_btn;
    TextView txt_Suggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_meeting_detail);

            act = MeetingDetailActivity.this;
            img_nav_back = findViewById(R.id.img_nav_back);
            mukesh_begview = findViewById(R.id.mukesh_begview);
            txt_meeting_title = findViewById(R.id.txt_meeting_title);
            txt_meeting_datetime = findViewById(R.id.txt_meeting_datetime);
            txt_meeting_status = findViewById(R.id.txt_meeting_status);
            txt_detail = findViewById(R.id.txt_detail);
            iv_attendance = findViewById(R.id.iv_attendance);
            txt_feedback_proceed = findViewById(R.id.txt_feedback_proceed);
            ll_Suggestion = findViewById(R.id.ll_Suggestion);
            ll_feedback_btn = findViewById(R.id.ll_feedback_btn);
            txt_Suggestion = findViewById(R.id.txt_Suggestion);

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
                    // startActivity(new Intent(MainActivity.this, AddCartCheckOut_Activity.class));
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

            if (getIntent().getExtras() != null) {
                txt_meeting_title.setText("" + getIntent().getStringExtra("Title"));
                txt_meeting_datetime.setText("" + getIntent().getStringExtra("Date_time"));
                txt_detail.setText(Html.fromHtml(getIntent().getStringExtra("Desc")));
            }
            ll_Suggestion.setVisibility(View.GONE);
            if (!getIntent().getStringExtra("Suggestion").equalsIgnoreCase("")) {
                ll_Suggestion.setVisibility(View.VISIBLE);
                txt_Suggestion.setText(Html.fromHtml(getIntent().getStringExtra("Suggestion")));
            } else {
                ll_feedback_btn.setVisibility(View.VISIBLE);
            }

            if (getIntent().getStringExtra("AttendStatus").equalsIgnoreCase("") &&
                    getIntent().getStringExtra("Status").equalsIgnoreCase("Y")) {
                iv_attendance.setVisibility(View.VISIBLE);
            } else {
                iv_attendance.setVisibility(View.GONE);
            }

            iv_attendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        cust_dialog = new Dialog(act, R.style.ThemeDialogCustom);
                        cust_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        //...set cancelable false so that it's never get hidden
                        cust_dialog.setCancelable(true);
                        setFinishOnTouchOutside(true);
                        cust_dialog.setCanceledOnTouchOutside(true);
                        //...that's the layout i told you will inflate later
                        cust_dialog.setContentView(R.layout.custom_dialog_attendance);

                        //...initialize the imageView form infalted layout
                        final ImageView iv_yes = cust_dialog.findViewById(R.id.iv_yes);
                        final ImageView iv_no = cust_dialog.findViewById(R.id.iv_no);
                        TextView txt_submit_feedback = cust_dialog.findViewById(R.id.txt_submit_feedback);

                        iv_yes.setImageDrawable(getResources().getDrawable(R.drawable.ic_present_checked));
                        iv_no.setImageDrawable(getResources().getDrawable(R.drawable.ic_absent_undelete));

                        s_attendance_sts = "Present";
                        iv_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                s_attendance_sts = "Present";

                                // iv_yes.setColorFilter(ContextCompat.getColor(act, R.color.color_dialog_Present), android.graphics.PorterDuff.Mode.MULTIPLY);
                                // iv_no.setColorFilter(ContextCompat.getColor(act, R.color.color_black), android.graphics.PorterDuff.Mode.MULTIPLY);

                                iv_yes.setImageDrawable(getResources().getDrawable(R.drawable.ic_present_checked));
                                iv_no.setImageDrawable(getResources().getDrawable(R.drawable.ic_absent_undelete));
                            }
                        });
                        iv_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                s_attendance_sts = "Absent";

                                // iv_yes.setColorFilter(ContextCompat.getColor(act, R.color.color_black), android.graphics.PorterDuff.Mode.MULTIPLY);
                                // iv_no.setColorFilter(ContextCompat.getColor(act, R.color.color_dialog_Absent), android.graphics.PorterDuff.Mode.MULTIPLY);
                                iv_yes.setImageDrawable(getResources().getDrawable(R.drawable.ic_present_unchecked));
                                iv_no.setImageDrawable(getResources().getDrawable(R.drawable.ic_absent_delete));
                            }
                        });
                        final Dialog finalCust_dialog = cust_dialog;
                        txt_submit_feedback.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!s_attendance_sts.equalsIgnoreCase("Pending")) {
                                    executeSubmitattendanceRequest(s_attendance_sts);
                                }
                                finalCust_dialog.dismiss();
                            }
                        });
                        //...finaly show it
                        cust_dialog.show();
                    } catch (Exception e) {

                    }
                }
            });

            txt_feedback_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, FeedbackActivity.class)
                            .putExtra("MeetingID", "" + getIntent().getStringExtra("MeetingID")));
                }
            });

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
                    startActivity(new Intent(act, MyProfileActivity.class));
                    finish();
                }
            });
            ll_home_bo_notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, NotificationActivity.class));
                    finish();
                }
            });
            ll_home_bo_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.showDialogSignOut(act);
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

    private void executeSubmitattendanceRequest(final String AttendenceStatus) {
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
                            postParameters.add(new BasicNameValuePair("UserID", "" + AppController.getSpUserInfo().getString(SPUtils.Member_ID, "")));
                            postParameters.add(new BasicNameValuePair("MeetingID", "" + getIntent().getExtras().getString("MeetingID")));
                            postParameters.add(new BasicNameValuePair("AttendenceStatus", "" + AttendenceStatus));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToUser_AttendMeeting, TAG);
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
                                Toasty.success(act, jsonObject.getString("Message"), Toast.LENGTH_SHORT, true).show();

                                Intent intent = new Intent(act, MeetingDetailActivity.class);
                                intent.putExtra("MeetingID", "" + getIntent().getExtras().getString("MeetingID"));
                                intent.putExtra("Title", "" + getIntent().getExtras().getString("Title"));
                                intent.putExtra("Date_time", "" + getIntent().getExtras().getString("Date_time"));
                                intent.putExtra("Desc", "" + getIntent().getExtras().getString("Desc"));
                                intent.putExtra("Type", "" + getIntent().getExtras().getString("Type"));
                                intent.putExtra("AttendStatus", "" + AttendenceStatus);
                                intent.putExtra("Status", "" + getIntent().getExtras().getString("Status"));
                                intent.putExtra("Suggestion", "" + getIntent().getExtras().getString("Suggestion"));
                                startActivity(intent);
                                finish();
                            } else {
                                Toasty.error(act, jsonObject.getString("Message"), Toast.LENGTH_SHORT, true).show();
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

}
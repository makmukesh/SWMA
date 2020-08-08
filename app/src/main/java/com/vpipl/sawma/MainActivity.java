package com.vpipl.sawma;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.http.RequestQueue;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.iid.FirebaseInstanceId;
import com.vpipl.sawma.Adapter.Recent_Meeting_Adapter;
import com.vpipl.sawma.Adapter.Upcoming_Meeting_Adapter;
import com.vpipl.sawma.Utils.AppController;
import com.vpipl.sawma.Utils.AppUtils;
import com.vpipl.sawma.Utils.CircularImageView;
import com.vpipl.sawma.Utils.QueryUtils;
import com.vpipl.sawma.Utils.SPUtils;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.vpipl.sawma.firbase.Config;
import com.vpipl.sawma.slider.SliderUtils;
import com.vpipl.sawma.slider.ViewPagerAdapter;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;
import ru.nikartm.support.ImageBadgeView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "MainActivity";
    public Dialog cust_dialog;
    String s_pooling_sts = "";
    Activity act;
    // slider
    ViewPager viewPager;
    LinearLayout SliderDots;
    private int dotscount;
    private ImageView[] dots;
    RequestQueue rq_footer1;
    List<SliderUtils> sliderImg;
    ViewPagerAdapter viewPagerAdapter;
    private int currentPage = 0;
    private Timer timer;
    ImageBadgeView mukesh_begview;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ValueAnimator animator;

    TextView txt_name, txt_nav_user_name, txt_department;
    CircularImageView user_profile;
    ImageView nav_user_profile;
    //Bottom Bar
    RelativeLayout rl_footer;
    LinearLayout ll_home_bo_home, ll_home_bo_attendance, ll_home_bo_profile, ll_home_bo_notification, ll_home_bo_logout;
    ImageView iv_home_bo_home, iv_home_bo_attendance, iv_home_bo_profile, iv_home_bo_notification, iv_home_bo_logout;
    TextView txt_home_bo_home, txt_home_bo_attendance, txt_home_bo_profile, txt_home_bo_notification, txt_home_bo_logout;

    // meeting listing
    TextView txt_recent_meeting, txt_upcoming_meeting;
    LinearLayout ll_recent_meeting, ll_upcoming_meeting;

    public Recent_Meeting_Adapter adapter;
    RecyclerView recyclerView;
    LinearLayout ll_data_found, ll_no_data_found;
    public static ArrayList<HashMap<String, String>> array_list = new ArrayList<>();

    public Upcoming_Meeting_Adapter adapter_up;
    RecyclerView recyclerView_up;
    LinearLayout ll_data_found_up, ll_no_data_found_up;
    public static ArrayList<HashMap<String, String>> array_list_up = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
           /* requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            act = MainActivity.this;
       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            final LinearLayout holder = findViewById(R.id.holder);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                    //this code is the real player behind this beautiful ui
                    // basically, it's a mathemetical calculation which handles the shrinking of
                    // our content view

                    float scaleFactor = 7f;
                    float slideX = drawerView.getWidth() * slideOffset;

                    holder.setTranslationX(slideX);
                    holder.setScaleX(1 - (slideOffset / scaleFactor));
                    holder.setScaleY(1 - (slideOffset / scaleFactor));

                    super.onDrawerSlide(drawerView, slideOffset);
                }
            };
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window w = getWindow();
                w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);// will remove all possible our aactivity's window bounds
            }

            drawer.addDrawerListener(toggle);

            drawer.setScrimColor(Color.TRANSPARENT);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View headerview = navigationView.getHeaderView(0);
            nav_user_profile = (ImageView) headerview.findViewById(R.id.nav_user_profile);
            txt_nav_user_name = (TextView) headerview.findViewById(R.id.txt_nav_user_name);

            AppUtils.loadImage(act, AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, ""), nav_user_profile);
            txt_nav_user_name.setText("" + AppController.getSpUserInfo().getString(SPUtils.MemberName, ""));

            sliderImg = new ArrayList<>();
            viewPager = (ViewPager) findViewById(R.id.viewPager);
            SliderDots = (LinearLayout) findViewById(R.id.SliderDots);
            final Handler handler11 = new Handler();
            final Runnable Update11 = new Runnable() {
                public void run() {
                    if (currentPage == sliderImg.size()) {
                        currentPage = 0;
                    }
                    viewPager.setCurrentItem(currentPage++, true);
                }
            };

            timer = new Timer();
            timer.schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handler11.post(Update11);
                }
            }, 2000, 2000);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    for (int i = 0; i < dotscount; i++) {
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                    }

                    try {
                        dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

                    } catch (Exception ex) {

                        ex.printStackTrace();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            //  adview();

            /*Action Bar Work Start */
            txt_name = findViewById(R.id.txt_name);
            txt_department = findViewById(R.id.txt_department);
            user_profile = findViewById(R.id.user_profile);

            txt_name.setText("" + AppController.getSpUserInfo().getString(SPUtils.MemberName, ""));
            txt_department.setText("" + AppController.getSpUserInfo().getString(SPUtils.MemberDepartment, ""));

            AppUtils.loadImage(act, AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, ""), user_profile);

            txt_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  startActivity(new Intent(act, RemotePDFActivity.class));
                   /* Intent intent = new Intent(act, PackagePurchaseActivity.class);
                    intent.putExtra("PID", "101" );
                    intent.putExtra("Validity", "123" );
                    intent.putExtra("Amount", "5" );
                    intent.putExtra("PackageName", "TEst Dummy");
                    intent.putExtra("PackageType", "M" );
                    startActivity(intent);*/
                }
            });

            /*Action Bar Work Start */

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
                }
            });
            ll_home_bo_attendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, AttendanceHistoryActivity.class));
                }
            });
            ll_home_bo_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, MyProfileActivity.class));
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

            animator = ValueAnimator.ofFloat(0f, 1f);
            if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) > 0) {
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
                    startActivity(new Intent(MainActivity.this, NotificationActivity.class));
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
            /*Bottom Bar Work End */

            /*Recent Meeting Work Start */
            txt_recent_meeting = findViewById(R.id.txt_recent_meeting);
            txt_upcoming_meeting = findViewById(R.id.txt_upcoming_meeting);
            ll_recent_meeting = findViewById(R.id.ll_recent_meeting);
            ll_upcoming_meeting = findViewById(R.id.ll_upcoming_meeting);

            txt_recent_meeting.setText("Recent Meetings");
            txt_recent_meeting.setTextColor(getResources().getColor(R.color.color_white));
            txt_recent_meeting.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_top_rounded_red));

            txt_upcoming_meeting.setText("Upcoming Meetings");
            txt_upcoming_meeting.setTextColor(getResources().getColor(R.color.color_gray));
            txt_upcoming_meeting.setBackgroundColor(getResources().getColor(R.color.color_white));

            txt_recent_meeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txt_recent_meeting.setTextColor(getResources().getColor(R.color.color_white));
                    txt_recent_meeting.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_top_rounded_red));
                    ll_recent_meeting.setVisibility(View.VISIBLE);
                    ll_upcoming_meeting.setVisibility(View.GONE);

                    txt_upcoming_meeting.setTextColor(getResources().getColor(R.color.color_gray));
                    txt_upcoming_meeting.setBackgroundColor(getResources().getColor(R.color.color_white));
                }
            });

            txt_upcoming_meeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txt_upcoming_meeting.setTextColor(getResources().getColor(R.color.color_white));
                    txt_upcoming_meeting.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_top_rounded_red));
                    ll_upcoming_meeting.setVisibility(View.VISIBLE);
                    ll_recent_meeting.setVisibility(View.GONE);

                    txt_recent_meeting.setTextColor(getResources().getColor(R.color.color_gray));
                    txt_recent_meeting.setBackgroundColor(getResources().getColor(R.color.color_white));
                }
            });

            ll_data_found = findViewById(R.id.ll_data_found);
            ll_no_data_found = findViewById(R.id.ll_no_data_found);

            recyclerView = (RecyclerView) findViewById(R.id.listView);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            ll_data_found_up = findViewById(R.id.ll_data_found_up);
            ll_no_data_found_up = findViewById(R.id.ll_no_data_found_up);

            recyclerView_up = (RecyclerView) findViewById(R.id.listView_up);
            RecyclerView.LayoutManager mLayoutManagerup = new LinearLayoutManager(getApplicationContext());
            recyclerView_up.setLayoutManager(mLayoutManagerup);
            recyclerView_up.setItemAnimator(new DefaultItemAnimator());

            if (AppUtils.isNetworkAvailable(act)) {
                executeHomeSliderRequest();
                executePoolingSelectRequest();
                executeRecentMeetingRequest();
                executeUpcomingMeetingRequest();
                executeLoginRequest();
            } else {
                AppUtils.alertDialogWithFinish(act, getResources().getString(R.string.txt_networkAlert));
            }
          /*  array_list.clear();

            for (int i = 1; i <= 10; i++) {
                HashMap<String, String> map = new HashMap<>();
                map.put("Title", "Mukesh Luhar Mukesh Luhar  Mukesh Luhar Mukesh Luhar Mukesh Luhar Mukesh Luhar Mukesh Luhar Mukesh Luhar" + i);
                map.put("Date_time", "1" + i + " PM");
                array_list.add(map);
            }

            showListView();*/
            /*Recent Meeting Work End */
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.e(TAG, "sendRegistrationToServer: " + refreshedToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed();
            showExitDialog();
        }
    }

    @Override
    protected void onRestart() {
        mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));

        if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) == 0) {
            if (animator == null) {
            } else {
                if (animator.isStarted()) {
                    animator.pause();
                    animator.cancel();
                }
            }
        }
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

        if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) == 0) {
            if (animator == null) {
            } else {
                if (animator.isStarted()) {
                    animator.pause();
                    animator.cancel();
                }
            }
        }
    }

    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.main, menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         // Handle action bar item clicks here. The action bar will
         // automatically handle clicks on the Home/Up button, so long
         // as you specify a parent activity in AndroidManifest.xml.
         int id = item.getItemId();

         //noinspection SimplifiableIfStatement
         if (id == R.id.action_settings) {
             return true;
         }

         return super.onOptionsItemSelected(item);
     }
 */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            startActivity(new Intent(act, MyProfileActivity.class));
        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(act, NotificationActivity.class));
        } else if (id == R.id.nav_attendance) {
            startActivity(new Intent(act, AttendanceHistoryActivity.class));
        } else if (id == R.id.nav_circuler) {
            startActivity(new Intent(act, CirculerListActivity.class));
        } /*else if (id == R.id.nav_feedback) {
            startActivity(new Intent(act, FeedbackActivity.class));
        }*/ else if (id == R.id.nav_meeting) {
            startActivity(new Intent(act, MeetingListActivity.class));
        } else if (id == R.id.nav_pooling) {
            startActivity(new Intent(act, PoolingListActivity.class));
        } else if (id == R.id.nav_membership) {
            startActivity(new Intent(act, PackageListActivity.class));
        } else if (id == R.id.nav_logout) {
            AppUtils.showDialogSignOut(act);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void adview() {
        for (int i = 1; i <= 5; i++) {
            SliderUtils sliderUtils = new SliderUtils();
            try {
                // JSONObject jsonObject1 = array.getJSONObject(i);
                //  sliderUtils.setSliderImageUrl(SPUtils.imgurl + jsonObject1.getString("SliderURL").replace(" ", "%20"));
                sliderUtils.setSliderImageUrl("http://alwar1.kvk2.in/images/slide" + i + ".jpg".replace(" ", "%20"));

            } catch (Exception e) {
                e.printStackTrace();
            }
            sliderImg.add(sliderUtils);
        }

        viewPagerAdapter = new ViewPagerAdapter(sliderImg, MainActivity.this);
        viewPager.setAdapter(viewPagerAdapter);
        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int i = 0; i < dotscount; i++) {
            dots[i] = new ImageView(MainActivity.this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            SliderDots.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
    }

    private void executeRecentMeetingRequest() {
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
                            postParameters.add(new BasicNameValuePair("Type", "R"));
                            postParameters.add(new BasicNameValuePair("UserID", "" + AppController.getSpUserInfo().getString(SPUtils.Member_ID, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToRecentOrUpCommingMeeting, TAG);
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
                            array_list.clear();
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                ll_data_found.setVisibility(View.VISIBLE);
                                ll_no_data_found.setVisibility(View.GONE);
                                if (jsonObject.getJSONArray("Data").length() > 0) {
                                    getAllActivityListResult(jsonObject.getJSONArray("Data"), "R");
                                }
                            } else {
                                showListView("R");
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

    private void executeUpcomingMeetingRequest() {
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
                            postParameters.add(new BasicNameValuePair("Type", "U"));
                            postParameters.add(new BasicNameValuePair("UserID", "" + AppController.getSpUserInfo().getString(SPUtils.Member_ID, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToRecentOrUpCommingMeeting, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            //  AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);
                            array_list_up.clear();
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                ll_data_found_up.setVisibility(View.VISIBLE);
                                ll_no_data_found_up.setVisibility(View.GONE);
                                if (jsonObject.getJSONArray("Data").length() > 0) {
                                    getAllActivityListResult(jsonObject.getJSONArray("Data"), "U");
                                }
                            } else {
                                showListView("U");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //   AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //   AppUtils.showExceptionDialog(act);
        }
    }

    private void getAllActivityListResult(JSONArray jsonArray, String type) {
        try {
            if (type.equalsIgnoreCase("R")) {
                array_list.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("MeetingID", jsonObject.getString("MID"));
                    map.put("Title", jsonObject.getString("Title"));
                    map.put("Date_time", jsonObject.getString("MeetingFrom"));
                    map.put("Desc", jsonObject.getString("Descri"));
                    map.put("AttendStatus", jsonObject.getString("AttendStatus"));
                    map.put("Status", jsonObject.getString("Status"));
                    map.put("Suggestion", jsonObject.getString("Suggestion"));
                    map.put("Type", "R");
                    array_list.add(map);
                }
            } else {
                array_list_up.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("MeetingID", jsonObject.getString("MID"));
                    map.put("Title", jsonObject.getString("Title"));
                    map.put("Date_time", jsonObject.getString("MeetingFrom"));
                    map.put("Desc", jsonObject.getString("Descri"));
                    map.put("AttendStatus", jsonObject.getString("AttendStatus"));
                    map.put("Status", jsonObject.getString("Status"));
                    map.put("Suggestion", jsonObject.getString("Suggestion"));
                    map.put("Type", "U");
                    array_list_up.add(map);
                }
            }

            showListView(type);

        } catch (Exception e) {
            e.printStackTrace();
            // AppUtils.showExceptionDialog(act);
        }
    }

    private void showListView(String type) {
        try {
            if (type.equalsIgnoreCase("R")) {
                txt_recent_meeting.setText("Recent Meetings (" + array_list.size() + ")");
                if (array_list.size() > 0) {
                    adapter = new Recent_Meeting_Adapter(act, array_list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);

                    ll_data_found.setVisibility(View.VISIBLE);
                    ll_no_data_found.setVisibility(View.GONE);
                } else {
                    ll_data_found.setVisibility(View.GONE);
                    ll_no_data_found.setVisibility(View.VISIBLE);
                }
            } else {
                txt_upcoming_meeting.setText("Upcoming Meetings (" + array_list_up.size() + ")");
                if (array_list_up.size() > 0) {
                    adapter_up = new Upcoming_Meeting_Adapter(act, array_list_up);
                    recyclerView_up.setAdapter(adapter_up);
                    adapter_up.notifyDataSetChanged();
                    recyclerView_up.setVisibility(View.VISIBLE);

                    ll_data_found_up.setVisibility(View.VISIBLE);
                    ll_no_data_found_up.setVisibility(View.GONE);
                } else {
                    ll_data_found_up.setVisibility(View.GONE);
                    ll_no_data_found_up.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //  AppUtils.showExceptionDialog(act);
        }
    }

    private void showExitDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(act, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Are you sure!!! Do you want to Exit from App?"));

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Yes");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(act, CloseActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText(getResources().getString(R.string.txt_signout_no));
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeHomeSliderRequest() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        //  AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToViewSliderImage, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            // AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);
                            array_list.clear();
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                if (jsonArrayData.length() > 0) {
                                    for (int i = 0; i < jsonArrayData.length(); i++) {
                                        SliderUtils sliderUtils = new SliderUtils();
                                        try {
                                            JSONObject jsonObject1 = jsonArrayData.getJSONObject(i);
                                            sliderUtils.setSliderImageUrl(AppUtils.circluerURL() + jsonObject1.getString("Image").replace(" ", "%20"));
                                            Log.e("SliderURL" , "" + AppUtils.circluerURL() + jsonObject1.getString("Image"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        sliderImg.add(sliderUtils);
                                    }

                                    viewPagerAdapter = new ViewPagerAdapter(sliderImg, MainActivity.this);
                                    viewPager.setAdapter(viewPagerAdapter);
                                    dotscount = viewPagerAdapter.getCount();
                                    dots = new ImageView[dotscount];

                                    for (int i = 0; i < dotscount; i++) {
                                        dots[i] = new ImageView(MainActivity.this);
                                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                                        params.setMargins(8, 0, 8, 0);
                                        SliderDots.addView(dots[i], params);
                                    }

                                    dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                                }
                            } else {
                                //  showListView("R");
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

    private void executePoolingSelectRequest() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        //  AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("UserID", "" + AppController.getSpUserInfo().getString(SPUtils.Member_ID, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToSelectPoolingForUpdate, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            // AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);
                            array_list.clear();
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                // JSONArray jsonArrayData = new JSONArray("[{\"PID\":0,\"PoolingTitle\":\"PoolingTitle PoolingTitle  PoolingTitle PoolingTitle \"}]");
                                if (jsonArrayData.length() > 0) {
                                    ShowPoolingDialog(jsonArrayData);
                                }
                            } else {
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            // AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // AppUtils.showExceptionDialog(act);
        }
    }

    private void ShowPoolingDialog(final JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                cust_dialog = new Dialog(act, R.style.ThemeDialogCustom);
                cust_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //...set cancelable false so that it's never get hidden
                cust_dialog.setCancelable(true);
                setFinishOnTouchOutside(true);
                cust_dialog.setCanceledOnTouchOutside(true);
                //...that's the layout i told you will inflate later
                cust_dialog.setContentView(R.layout.custom_dialog_pooling);

                //...initialize the imageView form infalted layout
                final TextView txt_pooling_topic = cust_dialog.findViewById(R.id.txt_pooling_topic);
                final RelativeLayout rl_pooling_yes = cust_dialog.findViewById(R.id.rl_pooling_yes);
                final RelativeLayout rl_pooling_no = cust_dialog.findViewById(R.id.rl_pooling_no);

                final TextView txt_yes = cust_dialog.findViewById(R.id.txt_yes);
                final TextView txt_no = cust_dialog.findViewById(R.id.txt_no);

                final ImageView iv_yes = cust_dialog.findViewById(R.id.iv_yes);
                final ImageView iv_no = cust_dialog.findViewById(R.id.iv_no);
                final TextView txt_submit_pooling = cust_dialog.findViewById(R.id.txt_submit_pooling);

                txt_pooling_topic.setText("" + jsonArray.getJSONObject(i).getString("PoolingTitle"));

                rl_pooling_yes.setBackground(getResources().getDrawable(R.drawable.bg_button));
                rl_pooling_no.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_complate_rounded));

                txt_yes.setTextColor(getResources().getColor(R.color.color_white));
                txt_no.setTextColor(getResources().getColor(R.color.color_black));

                iv_yes.setColorFilter(ContextCompat.getColor(act, R.color.color_white), android.graphics.PorterDuff.Mode.MULTIPLY);
                iv_no.setColorFilter(ContextCompat.getColor(act, R.color.color_666666), android.graphics.PorterDuff.Mode.MULTIPLY);

                s_pooling_sts = "Agree";
                rl_pooling_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        s_pooling_sts = "Agree";
                        rl_pooling_yes.setBackground(getResources().getDrawable(R.drawable.bg_button));
                        rl_pooling_no.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_complate_rounded));

                        txt_yes.setTextColor(getResources().getColor(R.color.color_white));
                        txt_no.setTextColor(getResources().getColor(R.color.color_black));

                        iv_yes.setColorFilter(ContextCompat.getColor(act, R.color.color_white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        iv_no.setColorFilter(ContextCompat.getColor(act, R.color.color_666666), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                });
                rl_pooling_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        s_pooling_sts = "Disagree";

                        rl_pooling_yes.setBackground(getResources().getDrawable(R.drawable.bg_drop_shadow_rect));
                        rl_pooling_no.setBackground(getResources().getDrawable(R.drawable.bg_button));

                        txt_yes.setTextColor(getResources().getColor(R.color.color_black));
                        txt_no.setTextColor(getResources().getColor(R.color.color_white));

                        iv_yes.setColorFilter(ContextCompat.getColor(act, R.color.color_666666), android.graphics.PorterDuff.Mode.MULTIPLY);
                        iv_no.setColorFilter(ContextCompat.getColor(act, R.color.color_white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                });
                final Dialog finalCust_dialog = cust_dialog;
                final int finalI = i;
                txt_submit_pooling.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!s_pooling_sts.equalsIgnoreCase("")) {
                            try {
                                executeSubmitpoolingRequest(s_pooling_sts, jsonArray.getJSONObject(finalI).getString("PoolingId"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toasty.error(act, "Select Polling Status !!", Toast.LENGTH_SHORT, true).show();
                        }
                        finalCust_dialog.dismiss();
                    }
                });
                //...finaly show it
                cust_dialog.show();
            } catch (Exception e) {

            }
        }
    }

    private void executeSubmitpoolingRequest(final String PoolingStatus, final String PoolingId) {
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
                            postParameters.add(new BasicNameValuePair("PoolingID", "" + PoolingId));
                            postParameters.add(new BasicNameValuePair("PoolingStatus", "" + PoolingStatus));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToInsert_Pooling, TAG);
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
                                if (!cust_dialog.equals(null)) {
                                    cust_dialog.dismiss();
                                }
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

    private void executeLoginRequest() {
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
                                    //  AppUtils.alertDialog(act, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(act, jsonObject.getString("Message"));

                                AppController.getSpUserInfo().edit().clear().commit();
                                AppController.getSpIsLogin().edit().clear().commit();

                                Intent intent = new Intent(act, Login_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra("SendToHome",true);
                                act.startActivity(intent);
                                ((Activity) act).finish();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            // AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // AppUtils.showExceptionDialog(act);
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

           /* if (jsonArray.getJSONObject(0).getString("Status").equalsIgnoreCase("M")) {
                startActivity(new Intent(act, PackageListActivity.class).putExtra("Type", "M"));
            } else if (jsonArray.getJSONObject(0).getString("Status").equalsIgnoreCase("R")) {
                startActivity(new Intent(act, PackageListActivity.class).putExtra("Type", "R"));
            } else if (jsonArray.getJSONObject(0).getString("Status").equalsIgnoreCase("True")) {
                AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();
            } else {
                AppController.getSpUserInfo().edit().clear().commit();
                AppUtils.alertDialog(act, jsonArray.getJSONObject(0).getString("Message"));
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            //  AppUtils.showExceptionDialog(act);
        }
    }

}

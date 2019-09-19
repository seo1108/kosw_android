package kr.co.photointerior.kosw.ui.fragment;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.File;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.conf.AppConst;
import kr.co.photointerior.kosw.db.KsDb;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.listener.FragmentClickListener;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.api.CustomerService;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.ActivityRecord;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Bbs;
import kr.co.photointerior.kosw.rest.model.BeaconUuid;
import kr.co.photointerior.kosw.rest.model.Cafe;
import kr.co.photointerior.kosw.rest.model.CafeDetail;
import kr.co.photointerior.kosw.rest.model.CafeMainList;
import kr.co.photointerior.kosw.rest.model.CafeNoticeList;
import kr.co.photointerior.kosw.rest.model.CafeRankingList;
import kr.co.photointerior.kosw.rest.model.Club;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.GGRRow;
import kr.co.photointerior.kosw.rest.model.MainData;
import kr.co.photointerior.kosw.rest.model.Notice;
import kr.co.photointerior.kosw.rest.model.Profile;
import kr.co.photointerior.kosw.rest.model.RankInCafe;
import kr.co.photointerior.kosw.rest.model.Record;
import kr.co.photointerior.kosw.service.fcm.Push;
import kr.co.photointerior.kosw.service.noti.NotiService;
import kr.co.photointerior.kosw.service.noti.NotificationChannelSupport;
import kr.co.photointerior.kosw.service.stepcounter.StepCounterService;
import kr.co.photointerior.kosw.ui.BaseActivity;
import kr.co.photointerior.kosw.ui.CafeChangeActivity;
import kr.co.photointerior.kosw.ui.CafeNoticeActivity;
import kr.co.photointerior.kosw.ui.GPSAcceptActivity;
import kr.co.photointerior.kosw.ui.InfoSettingProfileActivity;
import kr.co.photointerior.kosw.ui.MainActivity;
import kr.co.photointerior.kosw.ui.NoticeEventActivity;
import kr.co.photointerior.kosw.ui.NoticeRankingActivity;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.DateUtil;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LibUtils;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import kr.co.photointerior.kosw.utils.event.BusProvider;
import kr.co.photointerior.kosw.utils.event.KsEvent;
import kr.co.photointerior.kosw.widget.RowActivityRecord;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import butterknife.Bind;
//import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;
import static java.text.DateFormat.getDateTimeInstance;
import static java.text.DateFormat.getTimeInstance;

/**
 * 메인화면 Fragment.
 */

public class MainFragment extends BaseFragment {
    // implements SensorEventListener, StepListener {
    private String TAG = LogUtils.makeLogTag(MainFragment.class);
    private BeaconUuid mToadyInfo;
    private Bbs mMainNotice;
    private Bbs mCustomerNotice;
    private int mBodyType = 0;
    private Boolean isNoAny = false;
    private ActivityRecord mActivityRecord;
    private DataHolder mDataHolder = DataHolder.instance();
    private Cafe mCafe;

    private static String stepCount = "";
    private static String floorCount = "";

    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;

    static TextView tv_stepCount;

    private String rank;
    private String tot_rank = "";
    private String tot_floor = "";
    private String tot_cal = "";
    private String tot_sec = "";

    private CafeMainList mCMList ;

    protected Toolbar mToolBar;

    private String mSelectedCafeSeq = "";

    private List<Notice> mNoticeList;

    private BroadcastReceiver mTestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Env.Action.BEACON_BEACON_TEST_INTO_ACTION.isMatch(intent.getAction())) {
                showTestMsg(intent.getStringExtra("_MSG_"));
            }
        }
    };

    private BroadcastReceiver mLogoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Env.Action.CAFE_LOGO_GAIN_ACTION.isMatch(intent.getAction())) {
                showTestMsg(intent.getStringExtra("_MSG_"));
            }
        }
    };

    public static BaseFragment newInstance(BaseActivity activity, BeaconUuid todayInfo) {
        MainFragment frag = new MainFragment();
        frag.mActivity = activity;
        frag.mToadyInfo = todayInfo;
        return frag;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        followingWorksAfterInflateView();
        mActivity.changeColors();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //BusProvider.instance().register(this);
        if (Env.Bool.SHOW_TEST_VIEW.getValue()) {
            if (mActivity != null) {
                mActivity.registerReceiver(mTestReceiver, new IntentFilter(Env.Action.BEACON_BEACON_TEST_INTO_ACTION.action()));
            }
        }

        mToolBar = mActivity.findViewById(R.id.toolbar);

        SharedPreferences prefr = mActivity.getSharedPreferences("lastSelectedCafe", MODE_PRIVATE);
        mSelectedCafeSeq = prefr.getString("cafeseq", "");

        // 현재 선택된 카페의 유효성 체크
        if (!"".equals(mSelectedCafeSeq)) {
            AppUserBase user = DataHolder.instance().getAppUserBase() ;
            if (null != user) {
                getCafeDetail();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                readHistoryData();
            }
        } else if (resultCode == 10001) {
            mActivity.displayFragment(Env.FragmentType.HOME);
        }
    }

    @Override
    protected void followingWorksAfterInflateView() {
        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void attachEvents() {
        View.OnClickListener listener = new FragmentClickListener(this);
        getView(R.id.txt_noti_one).setOnClickListener(listener);
        getView(R.id.txt_noti_two).setOnClickListener(listener);
        getView(R.id.txt_noti_three).setOnClickListener(listener);
        getView(R.id.txt_noti_four).setOnClickListener(listener);
        getView(R.id.txt_noti_five).setOnClickListener(listener);
        if (!Env.Bool.SHOW_TEST_VIEW.getValue()) {
            getView(R.id.txt_info).setVisibility(View.GONE);
        }
        getView(R.id.extract_db).setOnClickListener(listener);
        getView(R.id.txt_building_name).setOnClickListener(listener);
        getView(R.id.img_character).setOnClickListener(listener);
    }

    @Override
    protected void setInitialData() {
        getUserInfoFromServer();
        setMainScreenCharacter();
        //animateCurrentFloor(0, 678, getTextView(R.id.txt_toady_amt));

        //getUserCafeMainList();

        resetToolbar();

        if (mToadyInfo != null) {
            BusProvider.instance().post(
                    new KsEvent<BeaconUuid>()
                            .setType(KsEvent.Type.UPDATE_FLOOR_AMOUNT).setValue(mToadyInfo)
            );
        }
    }

    public void animateCurrentFloor(int initialValue, int finalValue, final TextView textview) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(800);

        valueAnimator.addUpdateListener(
                valueAnimator1 ->
                        textview.setText(LibUtils.format((Integer) valueAnimator1.getAnimatedValue(), "#,##0"))
        );
        valueAnimator.start();

    }

    private void setMainScreenCharacter() {
        LogUtils.err(TAG, "display width=" + AUtil.displayWith());
        ImageView iv = mRootView.findViewById(R.id.img_character);


        //String charUrl = KUtil.getMyCharacterUrl(true);
        String charUrl = KUtil.getMainCharacterImgUrl();
        LogUtils.err(TAG, "character url on main=" + charUrl);
        String storedUrl = KUtil.getStringPref(PrefKey.CHARACTER_MAIN_URL, "");
        KUtil.saveStringPref(PrefKey.CHARACTER_MAIN_URL, charUrl);
        Glide.with(this)
                .applyDefaultRequestOptions(KUtil.getGlideCacheOptionMainCharacter())
                .load(charUrl).thumbnail(.5f).into(iv);
    }

    @Override
    public void performFragmentClick(View view) {
        int id = view.getId();
        if (id == R.id.extract_db) {
            extractNewDatabase(new File(Environment.getExternalStorageDirectory() + "", "kosw.sqlite"));
            return;
        }
        Bbs bbs = null;
        if (id == R.id.txt_noti_one) {
            if (mMainNotice == null) {
                return;
            }
            bbs = mMainNotice;
        } else if (id == R.id.txt_noti_two) {
            if (mCustomerNotice == null) {
                return;
            }
            bbs = mCustomerNotice;
        } else if (id == R.id.txt_noti_three || id == R.id.txt_noti_four || id == R.id.txt_noti_five) {
            mActivity.callActivity(NoticeRankingActivity.class, false);
            return;
        }

        if (id == R.id.txt_building_name) {

            Bundle data = new Bundle();
            data.putBoolean("selectmode", true);
            //((MainActivity)mActivity).mFinishFlag = true ;
            mActivity.callActivity(GPSAcceptActivity.class, data, false);
            ((MainActivity) mActivity).startMeasure(false);
            ((MainActivity) mActivity).mFinishFlag = true;
            ((MainActivity) mActivity).app.push = null;
            mActivity.finish();

            return;
        }

        if (id == R.id.img_character) {
            if (DataHolder.instance().getProfile() == null) {
                return;
            }
            mActivity.callActivity(InfoSettingProfileActivity.class, false);
            return;
        }

        //Bundle b = new Bundle();
        //b.putSerializable("_NOTICE_", bbs);
        mActivity.callActivity(NoticeEventActivity.class, false);

    }

    /**
     * 서버에서 사용자 정보를 획득
     */
    private void getUserInfoFromServer() {
        showSpinner("");
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("buildCode", KUtil.getStringPref(PrefKey.BUILDING_CODE, ""));
        LogUtils.log("user info by [buildCode, token]", query);
        Call<Profile> call =
                new DefaultRestClient<UserService>(getContext())
                        .getClient(UserService.class).getUserInfo(query);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                Profile profile;
                if (response.isSuccessful()) {
                    profile = response.body();
                    //LogUtils.err(TAG, "profile=" + profile.string());
                    if (profile.isSuccess()) {
                        mDataHolder.setProfile(profile);
                    } else {
                        toast(profile.getResponseMessage());
                        //mActivity.onBackPressed();
                    }
                } else {
                    toast(R.string.warn_server_not_smooth);
                    //mActivity.onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
                //mActivity.onBackPressed();
            }
        });
    }

    private void resetToolbar() {
        getUserCafeMainList();

        //mToolBar.setBackgroundColor(getResources().getColor(R.color.colorWhite));

    }

    /**
     * 서버에서 사용자 카페 정보를 획득
    */
    private void getUserCafeMainList() {
        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        Call<CafeMainList> call =
                new DefaultRestClient<CafeService>(getContext())
                        .getClient(CafeService.class).selectMyCafeList(query);
        call.enqueue(new Callback<CafeMainList>() {
            @Override
            public void onResponse(Call<CafeMainList> call, Response<CafeMainList> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());

                TextView title = mToolBar.findViewById(R.id.action_bar_title);
                ImageView img_logo = mToolBar.findViewById(R.id.img_cafe_logo);
                if (response.isSuccessful()) {
                    CafeMainList cafelist = response.body();

                    if (cafelist.isSuccess()) {
                        if (null != cafelist.getCafelist()) {
                            mCMList = cafelist;

                            mActivity.toggleActionBarResources(true);

                            if (null == mCMList || mCMList.getCafelist().size() == 0) {
                                title.setVisibility(View.GONE);

                                Picasso.with(mActivity)
                                        .load(R.drawable.ic_logo)
                                        .placeholder(R.drawable.ic_logo)
                                        .error(R.drawable.ic_logo)
                                        .into(img_logo);
                            } else {
                                String cafename = "";
                                String logo = "";
                                boolean isSelected = false;

                                if ("".equals(mSelectedCafeSeq)) {
                                    mSelectedCafeSeq = mCMList.getCafelist().get(0).getCafeseq();
                                } else {
                                    for (int i = 0; i < mCMList.getCafelist().size(); i++) {
                                        if (mSelectedCafeSeq.equals(mCMList.getCafelist().get(i).getCafeseq())) {
                                            cafename = mCMList.getCafelist().get(i).getCafename();
                                            logo = mCMList.getCafelist().get(i).getLogo();
                                            isSelected = true;
                                            break;
                                        }
                                    }

                                    if (!isSelected) {
                                        cafename = mCMList.getCafelist().get(0).getCafename();
                                        logo = mCMList.getCafelist().get(0).getLogo();

                                        mSelectedCafeSeq = mCMList.getCafelist().get(0).getCafeseq();
                                    }
                                }

                                if (!"".equals(logo) && null != logo && logo.contains("http")) {
                                    title.setVisibility(View.GONE);
                                    Picasso.with(mActivity)
                                            .load(logo)
                                            .placeholder(R.drawable.ic_logo)
                                            .error(R.drawable.ic_logo)
                                            .into(img_logo);
                                } else {
                                    img_logo.setVisibility(View.GONE);
                                    title.setVisibility(View.VISIBLE);
                                    title.setText(cafename);
                                }
                            }

                            title.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mActivity.callActivity(CafeChangeActivity.class, false);
                                }
                            });

                            img_logo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mActivity.callActivity(CafeChangeActivity.class, false);
                                }
                            });
                        } else {
                            Picasso.with(mActivity)
                                    .load(R.drawable.ic_logo)
                                    .placeholder(R.drawable.ic_logo)
                                    .error(R.drawable.ic_logo)
                                    .into(img_logo);

                            title.setVisibility(View.GONE);
                        }
                    } else {
                        Picasso.with(mActivity)
                                .load(R.drawable.ic_logo)
                                .placeholder(R.drawable.ic_logo)
                                .error(R.drawable.ic_logo)
                                .into(img_logo);

                        title.setVisibility(View.GONE);
                    }
                } else {
                    Picasso.with(mActivity)
                            .load(R.drawable.ic_logo)
                            .placeholder(R.drawable.ic_logo)
                            .error(R.drawable.ic_logo)
                            .into(img_logo);

                    title.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(Call<CafeMainList> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);


                TextView title = mToolBar.findViewById(R.id.action_bar_title);
                ImageView img_logo = mToolBar.findViewById(R.id.img_cafe_logo);



                //mActivity.changeStatusBarColor(R.color.colorWhite);
                mActivity.toggleDrawerIcon(true);
                mActivity.toggleActionBarResources(true);

                Picasso.with(mActivity)
                        .load(R.drawable.ic_logo)
                        .placeholder(R.drawable.ic_logo)
                        .error(R.drawable.ic_logo)
                        .into(img_logo);

                img_logo.setVisibility(View.VISIBLE);
                title.setVisibility(View.GONE);
            //mActivity.onBackPressed();
            }


        });
    }


    private void extractNewDatabase(File target) {
        LogUtils.err(TAG, "filename=" + target.getAbsolutePath());
        LibUtils.copyTo(
                new File("/data/data/kr.co.photointerior.kosw.app/databases", KsDb.Meta.DB_NAME.getName()), target);
    }

    @Override
    public void onResume() {
        BusProvider.instance().register(this);

        LogUtils.err(TAG, "MainFragment#onResume()");


        SharedPreferences prefr = mActivity.getSharedPreferences("lastSelectedCafe", MODE_PRIVATE);

        if (!"".equals(mSelectedCafeSeq) && !mSelectedCafeSeq.equals(prefr.getString("cafeseq", ""))) {
            // 프래그먼트 리플래쉬
            mSelectedCafeSeq = prefr.getString("cafeseq", "");
            mActivity.displayFragment(Env.FragmentType.HOME);
        } else {
            setMainScreenCharacter();
            //requestCurrentNoti();
            isNoAny = false;
            requestToServer();
        }


        if(isMyServiceRunning(StepCounterService.class)) {
            // 자동측정중이면
            TextView tv =  mActivity.findViewById(R.id.tvPauseMent);
            tv.setText("자동측정중입니다.");
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        BusProvider.instance().unregister(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        /*try {
            BusProvider.instance().unregister(this);
        }catch(IllegalArgumentException e){

        }*/
        if (Env.Bool.SHOW_TEST_VIEW.getValue()) {
            try {
                if (mActivity != null) {
                    mActivity.unregisterReceiver(mTestReceiver);
                    mActivity.unregisterReceiver(mLogoReceiver);
                }
            } catch (RuntimeException e) {
            }
        }
        super.onDestroy();
    }

    @Override
    public int getViewResourceId() {
        return R.layout.fragment_main;
    }

    /**
     * 선택된 회사의 색상과, 현재 상태의 회원 메인화면 캐릭터 변경
     *
     * @param event
     */
    @Subscribe
    public void acceptEvent(KsEvent event) {
        if (KsEvent.Type.CHANGE_COLOR == event.getType()) {//선택된 회사 색상으로 변경
            mActivity.changeColors();//status bar, action bar, content area background color 변경
        } else if (KsEvent.Type.CHANGE_CHARACTER == event.getType()) {//현재 상태의 메인 캐릭터 변경
            setMainScreenCharacter();
        } else if (KsEvent.Type.UPDATE_FLOOR_AMOUNT == event.getType()) {//올라간 계단 숫자 반영
            BeaconUuid beacon = (BeaconUuid) event.getValue();
            animateCurrentFloor(
                    beacon.getTodayGoUpAmountToInt(),
                    beacon.getTodayGoUpAmountToInt() + beacon.getGoUpAmount(),
                    getTextView(R.id.txt_toady_amt));
            updateHealthValues(beacon);
            if (event.isMainCharacterChanged()) {
                setMainScreenCharacter();
            }
            isNoAny = true;
            requestToServer();
        } else if (KsEvent.Type.MAIN_PUSH == event.getType()) {//메인화면 푸시수신 데이터 갱신
            Push push = (Push) event.getValue();
            //refreshMainNotiTexts(push, null);
            isNoAny = false;
            requestToServer();
        } else if (KsEvent.Type.MAIN_REFRESH == event.getType()) {//메인화면 데이터 전체 갱신
            LogUtils.err(TAG, "all of main data would be refreshed.");
            isNoAny = false;
            requestToServer();
        }

    }

    /**
     * 메인화면 중앙 텍스트 데이터 변경
     *
     * @param push
     * @param mainData
     */
    private void refreshMainNotiTexts(final Push push, final MainData mainData) {
        // 선택된 카페의 저지 및 공지사항 가져오기
        getCafeNotice();
        getCafeRankingList();


        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (push != null) {//푸시로 수신한 데이터
                        String type = push.getStringData("push_type");
                        if ("NOTICE_EVENT_GLOBAL".equals(type)) {//계단왕 공지
                            TextView tv = getTextView(R.id.txt_noti_one);//첫번 째 row
                            tv.setText(push.getStringData("message"));
                        } /*else if ("NOTICE_EVENT_CUSTOMER".equals(type)) {//고객사 공지
                            TextView tv = getTextView(R.id.txt_noti_two);//두번 째 row
                            tv.setText(push.getStringData("message"));
                        } else if ("JERSEY_GOLD".equals(type)) {
                            TextView tv = getTextView(R.id.txt_noti_three);//세번 째 row
                            tv.setText(push.getStringData("message"));
                        } else if ("JERSEY_GREEN".equals(type)) {
                            TextView tv = getTextView(R.id.txt_noti_four);//네번째 row
                            tv.setText(push.getStringData("message"));
                        } else if ("JERSEY_REDDOT".equals(type)) {
                            TextView tv = getTextView(R.id.txt_noti_five);//다섯번 째 row
                            tv.setText(push.getStringData("message"));
                        }*/
                    } else {//메인 로딩으로 획득한 데이터
                        /*TextView tvg1 = getTextView(R.id.tv2101);
                        tvg1.setVisibility(View.INVISIBLE);
                        ImageView iv1 = getImageView(R.id.iv2101);
                        iv1.setVisibility(View.INVISIBLE);
                        TextView tvg2 = getTextView(R.id.tv2102);
                        tvg2.setVisibility(View.INVISIBLE);
                        ImageView iv2 = getImageView(R.id.iv2102);
                        iv2.setVisibility(View.INVISIBLE);
                        TextView tvg3 = getTextView(R.id.tv2103);
                        tvg3.setVisibility(View.INVISIBLE);
                        ImageView iv3 = getImageView(R.id.iv2103);
                        iv3.setVisibility(View.INVISIBLE);*/

                        ImageView iv41 = getImageView(R.id.iv4101);
                        iv41.setVisibility(View.INVISIBLE);
                        iv41.setTag("4101");
                        ImageView iv42 = getImageView(R.id.iv4102);
                        iv42.setVisibility(View.INVISIBLE);
                        iv42.setTag("4102");
                        ImageView iv43 = getImageView(R.id.iv4103);
                        iv43.setVisibility(View.INVISIBLE);
                        iv43.setTag("4103");
                        ImageView iv44 = getImageView(R.id.iv4104);
                        iv44.setVisibility(View.INVISIBLE);
                        iv44.setTag("4104");
                        ImageView iv45 = getImageView(R.id.iv4105);
                        iv45.setVisibility(View.INVISIBLE);
                        iv45.setTag("4105");

                        TextView tv1 = getTextView(R.id.txt_noti_one);//첫번 째 row:계단왕 공지
                        mMainNotice = mainData.getMainNotice();
                        if (mMainNotice != null) {
                            tv1.setText(mMainNotice.getTitle());
                        } else {
                            tv1.setText("");
                        }
                        /*TextView tv2 = getTextView(R.id.txt_noti_two);//두번 째 row:회사공지
                        mCustomerNotice = mainData.getCustomerNotice();
                        if (mCustomerNotice != null) {
                            tv2.setText(mCustomerNotice.getTitle());
                        } else {
                            tv2.setText("");
                        }
                        TextView tv3 = getTextView(R.id.txt_noti_three);//세번 째 row:골드저지
                        tv3.setText(mainData.getGoldJersey());

                        TextView tv4 = getTextView(R.id.txt_noti_four);//네번 번 째 row:그린
                        tv4.setText(mainData.getGreenJersey());

                        TextView tv5 = getTextView(R.id.txt_noti_five);//다섯번 째 row:레드닷
                        tv5.setText(mainData.getRedDotJersey());*/

                        AppUserBase user = DataHolder.instance().getAppUserBase();
                        // 나무심기
                        TextView tv_tree2 = getTextView(R.id.txt_tree_two);
                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                        String userCnt = formatter.format(mainData.getUserCnt());
                        tv_tree2.setText(user.getNickName() + "님과 함께 " + userCnt + "명이");
                        Spannable spanText2 = new SpannableString(tv_tree2.getText().toString());
                        spanText2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, user.getNickName().length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spanText2.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, user.getNickName().length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spanText2.setSpan(new RelativeSizeSpan(1.2f), 0, user.getNickName().length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        tv_tree2.setText(spanText2, TextView.BufferType.SPANNABLE);

                        TextView tv_tree3 = getTextView(R.id.txt_tree_three);
                        String actAmt = formatter.format((float) mainData.getActAmt() * 16.6 / 1000.0 * 0.42 * 0.27);
                        tv_tree3.setText(actAmt + "그루의 나무를 심었고");
                        //Spannable spanText3 = new SpannableString(tv_tree3.getText().toString()) ;
                        //spanText3.setSpan(new ForegroundColorSpan(Color.BLACK),0,actAmt.length() ,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //spanText3.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),0,actAmt.length() ,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //spanText3.setSpan(new RelativeSizeSpan(1.2f),0,actAmt.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //tv_tree3.setText(spanText3,TextView.BufferType.SPANNABLE);

                        TextView tv_tree4 = getTextView(R.id.txt_tree_four);
                        String ton = formatter.format((float) mainData.getActAmt() * 16.6 / 100000.0 * 0.42);
                        ton = String.format("%.2f", (float) mainData.getActAmt() * 16.6 / 100000.0 * 0.42);
                        tv_tree4.setText(ton + "kg 의 이산화탄소를");


                        user.setBuild_floor_amt(mainData.getBuild_floor_amt());

                        if (mainData.getGgrList() != null) {
                            for (GGRRow row : mainData.getGgrList()
                            ) {

                                /*if (row.getAct_kind().equals("GOLD")) {
                                    TextView tv = getTextView(R.id.tv2101);
                                    tv.setVisibility(View.VISIBLE);
                                    tv.setText(row.getNickname());
                                    ImageView iv = getImageView(R.id.iv2101);
                                    iv.setVisibility(View.VISIBLE);
                                }
                                if (row.getAct_kind().equals("GREEN")) {
                                    TextView tv = getTextView(R.id.tv2102);
                                    tv.setVisibility(View.VISIBLE);
                                    tv.setText(row.getNickname());
                                    ImageView iv = getImageView(R.id.iv2102);
                                    iv.setVisibility(View.VISIBLE);
                                }
                                if (row.getAct_kind().equals("RED")) {
                                    TextView tv = getTextView(R.id.tv2103);
                                    tv.setVisibility(View.VISIBLE);
                                    tv.setText(row.getNickname());
                                    ImageView iv = getImageView(R.id.iv2103);
                                    iv.setVisibility(View.VISIBLE);
                                }*/
                            }
                        }

                        if (mainData.getClubList() != null) {
                            for (Club row : mainData.getClubList()
                            ) {
                                int cid = Integer.parseInt(row.getClub_kind());
                                ImageView iv = mRootView.findViewWithTag(String.format("410%d", cid));
                                iv.setVisibility(View.VISIBLE);
                            }
                        }

                        updateHealthValues(mainData);


                    }
                }
            });
        }
    }

    /**
     * 메인화면 구성정보 서버 요청 처리
     */
    @Override
    public void requestToServer() {
        //showSpinner("");
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("version", "3");
        Call<MainData> call =
                new DefaultRestClient<UserService>(getActivity())
                        .getClient(UserService.class)
                        .getMainData(query);
        call.enqueue(new Callback<MainData>() {
            @Override
            public void onResponse(Call<MainData> call, Response<MainData> response) {
                closeSpinner();
                LogUtils.err(TAG, "main data : " + response.raw().toString());

                if (response.isSuccessful()) {
                    MainData data = response.body();
                    LogUtils.err(TAG, "main data : " + data.string());
                    if (data.isSuccess()) {
                        Pref pref = Pref.instance();

                        String charImageFile = pref.getStringValue(PrefKey.CHARACTER_MAIN, "");

                        pref.saveStringValue(PrefKey.CHARACTER_MAIN, data.getMainCharImageFile());
                        pref.saveStringValue(PrefKey.CHARACTER_SUB, data.getSubCharImageFile());
                        // 기존 이미지와 동일 하면 스킵
                        if (!charImageFile.equals(data.getMainCharImageFile())) {
                            setMainScreenCharacter();
                        }
                        refreshMainNotiTexts(null, data);
                    } else {
                        toast(data.getResponseMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<MainData> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, "main data : " + t);
            }
        });
    }

    /**
     * 활동기록 데이터 서버에서 획득 후 처리
     */
    public void requestgetActivityRecords() {
        RowActivityRecord totalFloor = getView(R.id.row_record_floor);
        RowActivityRecord totalCalorie = getView(R.id.row_record_calorie);
        RowActivityRecord totalLife = getView(R.id.row_record_life);

        Map<String, Object> query = KUtil.getDefaultQueryMap();
        Call<ActivityRecord> call =
                new DefaultRestClient<UserService>(mActivity).getClient(UserService.class)
                        .getActivityRecords(query);
        call.enqueue(new Callback<ActivityRecord>() {
            @Override
            public void onResponse(Call<ActivityRecord> call, Response<ActivityRecord> response) {
                LogUtils.err(TAG, response.raw().toString());
                if (response.isSuccessful()) {
                    ActivityRecord record = response.body();
                    if (record.isSuccess()) {
                        mActivityRecord = record;
                        Record total = mActivityRecord.getTotalRecord();
                        Record r_rank = mActivityRecord.getTotalRank();
                        //Record r_rank = mActivityRecord.getTodayRank();

                        totalFloor.setRecordAmount(StringUtil.format(total.getAmountToFloat(), "#,##0"));
                        totalCalorie.setRecordAmount(KUtil.calcCalorie(total.getAmountToFloat(), total.getStairAmountToFloat()));
                        totalLife.setRecordAmount(KUtil.calcLife(total.getAmountToFloat(), total.getStairAmountToFloat()));

                        if (null != r_rank) {
                            AppConst.NOTI_RANKS = StringUtil.format(Double.parseDouble(r_rank.getRank()), "#,##0");
                        } else {
                            AppConst.NOTI_RANKS = "-";
                        }
                        AppConst.NOTI_FLOORS = StringUtil.format(total.getAmountToFloat(), "#,##0");
                        AppConst.NOTI_CALS = KUtil.calcCalorie(total.getAmountToFloat(), total.getStairAmountToFloat());
                        AppConst.NOTI_SECS = KUtil.calcLife(total.getAmountToFloat(), total.getStairAmountToFloat());

                    } else {
                        totalFloor.setRecordAmount("0");
                        totalCalorie.setRecordAmount("0");
                        totalLife.setRecordAmount("0");

                        toast(record.getResponseMessage());

                        tot_rank = "0";
                        rank = "0";
                        AppConst.NOTI_FLOORS = "-";
                        AppConst.NOTI_CALS = "-";
                        AppConst.NOTI_SECS = "-";
                    }
                } else {
                    totalFloor.setRecordAmount("0");
                    totalCalorie.setRecordAmount("0");
                    totalLife.setRecordAmount("0");
                    toast(R.string.warn_commu_to_server);

                    AppConst.NOTI_RANKS = "-";
                    rank = "0";
                    AppConst.NOTI_FLOORS = "-";
                    AppConst.NOTI_CALS = "-";
                    AppConst.NOTI_SECS = "-";
                }

                updateNotification(AppConst.NOTI_RANKS, AppConst.NOTI_FLOORS, AppConst.NOTI_CALS, AppConst.NOTI_SECS);
            }

            @Override
            public void onFailure(Call<ActivityRecord> call, Throwable t) {
                //toast(R.string.warn_commu_to_server);
                LogUtils.err(TAG, t);
            }
        });
    }

    /**
     * 칼로리, 건강수명, 랭킹 반영
     *
     * @param beacon
     */
    private void updateHealthValues(BeaconUuid beacon) {
        requestgetActivityRecords();

        TextView todayAmt = getView(R.id.txt_toady_amt);//오늘 올라간 층 데이터

        LogUtils.err(TAG, "stair data=" + beacon.string());
        float floor = beacon.getStairAmountToInt();
        if (floor == 0) {
            floor = (float) 24.0;
        }
        TextView tv_userType = getTextView(R.id.txt_user_type);
        AppUserBase user = DataHolder.instance().getAppUserBase();

        int remain = beacon.getTodayGoUpAmountToInt() + beacon.getGoUpAmount();
        /*
        if ( remain  >= 10) {
            tv_userType.setText(user.getNickName() + "(" + String.valueOf(mBodyType) + "단계)" );
        } else {
            tv_userType.setText(user.getNickName() + "(" + String.valueOf(mBodyType) + "단계) : " + String.valueOf(10 - remain)  + "층 더 오르면 살이 빠져요");
        }
        */
    }

    /**
     * 선택되어 있는 카페 정보로 메인화면 구성
     *
     */

    private void getCafeNotice() {
        showSpinner("");
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("cafeseq", mSelectedCafeSeq);
        Call<CafeNoticeList> call =
                new DefaultRestClient<CafeService>(mActivity)
                        .getClient(CafeService.class).notice(query);

        call.enqueue(new Callback<CafeNoticeList>() {
            @Override
            public void onResponse(Call<CafeNoticeList> call, Response<CafeNoticeList> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                if (response.isSuccessful()) {
                    CafeNoticeList noticelist = response.body();
                    //LogUtils.err(TAG, "profile=" + profile.string());
                    if (noticelist.isSuccess()) {
                        mNoticeList = noticelist.getNotice();

                        if (null != mNoticeList && mNoticeList.size() > 0) {
                            TextView tv2 = getTextView(R.id.txt_noti_two);
                            tv2.setText(mNoticeList.get(0).getContents());
                        } else {
                            TextView tv2 = getTextView(R.id.txt_noti_two);
                            tv2.setText("카페 공지사항이 없습니다.");
                        }
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<CafeNoticeList> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);

            }
        });
    }

    private void getCafeRankingList() {
        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("cafeseq", mSelectedCafeSeq);
        query.put("period", "M");

        Call<CafeRankingList> call =
                new DefaultRestClient<CafeService>(mActivity)
                        .getClient(CafeService.class).rankingIndividual(query);
        call.enqueue(new Callback<CafeRankingList>() {
            @Override
            public void onResponse(Call<CafeRankingList> call, Response<CafeRankingList> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                if (response.isSuccessful()) {
                    if (null == response.body().getList() || response.body().getList().size() == 0) {
                    } else {
                        for (int idx = 0; idx < response.body().getList().size(); idx++) {
                            if (idx == 0) {
                                TextView tv = getTextView(R.id.tv2101);
                                tv.setVisibility(View.VISIBLE);
                                tv.setText(response.body().getList().get(idx).getNickname());
                                ImageView iv = getImageView(R.id.iv2101);
                                iv.setVisibility(View.VISIBLE);
                            } else if (idx == 1) {
                                TextView tv = getTextView(R.id.tv2102);
                                tv.setVisibility(View.VISIBLE);
                                tv.setText(response.body().getList().get(idx).getNickname());
                                ImageView iv = getImageView(R.id.iv2102);
                                iv.setVisibility(View.VISIBLE);
                            } else if (idx == 2) {
                                TextView tv = getTextView(R.id.tv2103);
                                tv.setVisibility(View.VISIBLE);
                                tv.setText(response.body().getList().get(idx).getNickname());
                                ImageView iv = getImageView(R.id.iv2103);
                                iv.setVisibility(View.VISIBLE);
                            }
                        }

                    }

                }
            }

            @Override
            public void onFailure(Call<CafeRankingList> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);

            }
        });

    }

    private void getCafeDetail() {
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("cafeseq", mSelectedCafeSeq);

        Call<CafeDetail> call =
                new DefaultRestClient<CafeService>(mActivity)
                        .getClient(CafeService.class).detail(query);

        call.enqueue(new Callback<CafeDetail>() {
            @Override
            public void onResponse(Call<CafeDetail> call, Response<CafeDetail> response) {
                if (response.isSuccessful()) {
                    CafeDetail cafedetail = response.body();
                    //LogUtils.err(TAG, "profile=" + profile.string());
                    if (cafedetail.isSuccess()) {
                        mCafe = cafedetail.getCafe();

                        if (null != mCafe && "1".equals(mCafe.getIsjoin())) {
                        } else {
                            // 유효한 카페가 아니면 리로드
                            SharedPreferences prefr = mActivity.getSharedPreferences("lastSelectedCafe", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefr.edit();
                            editor.putString("cafeseq", "");
                            editor.commit();

                            // 프래그먼트 리플래쉬
                            mSelectedCafeSeq = "";
                            mActivity.displayFragment(Env.FragmentType.HOME);
                        }

                    } else {
                    }
                }
            }

            @Override
            public void onFailure(Call<CafeDetail> call, Throwable t) {
            }
        });
    }




    /**
     * @param data 메인화면 구성 데이터
     */
    private void updateHealthValues(MainData data) {

        requestgetActivityRecords();

        TextView tx_today = getView(R.id.koswTextView);
        TextView todayAmt = getView(R.id.txt_toady_amt);//오늘 올라간 층 데이터
        TextView buildName = getView(R.id.txt_building_name);

        AppUserBase user = DataHolder.instance().getAppUserBase();
        user.setCust_build_seq(String.valueOf(data.getCust_build_seq()));

        int flooramt = data.getBuild_floor_amt(); //건물  층수
        user.setBuild_floor_amt(flooramt);

        int totalamt = data.getTotalAmt();
        user.setTotal_amt(totalamt);

        String isbuild = data.getIsbuild(); // 건물 / 등산 모드
        user.setIsbuild(isbuild);


        tx_today.setText(DateUtil.currentDate("yyyy.MM.dd hh:mm") + " " + getString(R.string.txt_today_floors));


        /*if (data.getCust_build_seq() == data.getBuild_seq() ) {
            user.setCust_name(data.getCustName());
            buildName.setText(data.getBuildingName() + "(" + data.getCustName() +
                    ")  " + String.valueOf(flooramt) + " " +  ((isbuild.equals("Y"))? "B" : "M")  ) ;
        } else {
            buildName.setText(data.getBuildingName()  +
                    "  " + String.valueOf(flooramt) + " " +  ((isbuild.equals("Y"))? "B" : "M")  ) ;
        }*/

        if (data.getCust_build_seq() == data.getBuild_seq()) {
            user.setCust_name(data.getCustName());
            if (null == data.getCustName()) {
                data.setCustName(user.getCust_name());
            }
            if (null == data.getBuildingName()) {
                data.setBuildingName(user.getBuild_name());
            }



            try {
                buildName.setText(data.getBuildingName() + "(" + data.getCustName() +
                        ")에서 오른층수  " + totalamt + " F");
            } catch  (Exception ex) {
                buildName.setText(user.getBuild_name() + "(" + user.getCust_name() +
                        ")에서 오른층수  " + totalamt + " F");
            }
        } else {
            try {
                buildName.setText(data.getBuildingName() +
                        "에서 오른층수  " + totalamt + " F");
            } catch (Exception ex) {
                buildName.setText(user.getBuild_name() +
                        "에서 오른층수  " + totalamt + " F");
            }
        }

        buildName.setVisibility(View.GONE);

        TextView tv_userType = getTextView(R.id.txt_user_type);

        if (data.getTodayRecord() != null) {

            int remain = (int) data.getTodayRecord().getAmountToFloat();
            int ranking = Integer.valueOf(data.getTodayRecord().getTodayRanking());

            rank = StringUtil.format(Integer.valueOf(data.getTodayRecord().getTodayRanking()), "#,##0");

            if (remain >= 10) {
                tv_userType.setText(user.getNickName() + "(" + data.getUserLevel() + " / " + String.valueOf(data.getBotyType()) + "단계 랭킹 " + String.valueOf(ranking) + "위) \n오늘미션성공, 조금더욕심내 볼까요?");
            } else {
                tv_userType.setText(user.getNickName() + "(" + data.getUserLevel() + " / " + String.valueOf(data.getBotyType()) + "단계 랭킹 " + String.valueOf(ranking) + "위) \n" + String.valueOf(10 - remain) + "층 더 오르면 살이 빠져요");
            }

            LogUtils.err(TAG, "main today=" + data.getTodayRecord().string());
            if (!isNoAny)
                animateCurrentFloor(0, (int) data.getTodayRecord().getAmountToFloat(), todayAmt);
            float floor = data.getTodayRecord().getStairAmountToFloat();
            if (floor == 0) {
                floor = (float) 24.0;
            }

            floorCount  = String.valueOf((int) data.getTodayRecord().getAmountToFloat());
        } else {
            todayAmt.setText("0");
            tv_userType.setText(user.getNickName() + "(" + data.getUserLevel() + " / " + String.valueOf(data.getBotyType()) + "단계) \n" + String.valueOf(10) + "층 더 오르면 살이 빠져요");
            floorCount = "0";
        }

        mBodyType = data.getBotyType();


        // 걸음수
        tv_stepCount = getTextView(R.id.koswStepTextView);

        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                        .build();
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(mActivity), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(mActivity),
                    fitnessOptions);
        } else {
            readHistoryData();
        }

        //updateNotification();

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void updateNotification(String ranking, String floor, String cal, String sec) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mActivity, AppConst.NOTIFICATION_CHANNEL_ID);

        Intent intent = new Intent(mActivity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentPendingIntent = PendingIntent.getActivity(mActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String title = "";
        /*if(!isMyServiceRunning(StepCounterService.class)) {
            title = "[수동측정]";
        } else {
            title = "[자동측정]";
        }*/

        builder.setContentTitle("[랭킹: + ranking + ]")
                .setContentText(floor + "F / " + cal + "kcal / " + sec + "sec")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.ic_floor))
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                //.setColorized(true)
                .setColor(ContextCompat.getColor(mActivity, R.color.colorPrimaryDark))
                .setContentIntent(contentPendingIntent);

        NotificationManager notificationManager = (NotificationManager) mActivity.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(AppConst.NOTIFICATION_ID, builder.build());
    }

    /*private void updateNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mActivity, AppConst.NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("오늘의 통계");
        builder.setContentText("걸음수 " + stepCount + " / 오늘의 오른 층수 " + floorCount);
        builder.setColor(Color.RED);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }*/

    private void removeNotification() {
        // Notification 제거
        NotificationManagerCompat.from(mActivity).cancel(1);
    }


    private void showTestMsg(String msg){
        if(Env.Bool.SHOW_TEST_VIEW.getValue()) {
            TextView tv = getView(R.id.txt_info);
            if (!StringUtil.isEmptyOrWhiteSpace(msg)) {
                tv.setText(msg);
            }
        }
    }








    /** Returns a {@link DataReadRequest} for all step count changes in the past week. */
    public static DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        //cal.add(Calendar.WEEK_OF_YEAR, -1);
        //long startTime = cal.getTimeInMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.getTimeInMillis();
        long startTime = calendar.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateTimeInstance();

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        // The data request can specify multiple data types to return, effectively
                        // combining multiple data queries into one call.
                        // In this example, it's very unlikely that the request is for several hundred
                        // datapoints each consisting of a few steps and a timestamp.  The more likely
                        // scenario is wanting to see how many steps were walked per day, for 7 days.
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();



        // [END build_read_data_request]

        return readRequest;
    }

    /**
     * Logs a record of the query result. It's possible to get more constrained data sets by
     * specifying a data source or data type, but for demonstrative purposes here's how one would dump
     * all the data. In this sample, logging also prints to the device screen, so we can see what the
     * query returns, but your app should not log fitness information as a privacy consideration. A
     * better option would be to dump the data you receive to a local data directory to avoid exposing
     * it to other applications.
     */
    public static void printData(DataReadResponse dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
        // [END parse_read_data_result]
    }

    // [START parse_dataset]
    private static void dumpDataSet(DataSet dataSet) {
        DateFormat dateFormat = getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            for (Field field : dp.getDataType().getFields()) {
                tv_stepCount.setText("오늘의 걸음수 : " + dp.getValue(field));
                stepCount = dp.getValue(field).toString();
            }
        }

       /* PendingResult<DataReadResult> pendingResult =
                Fitness.HistoryApi.readData(client, readRequest);

        //synchronous execution, should happen on background thread
        DataReadResult result = pendingResult.await(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);*/
    }

    private Task<DataReadResponse> readHistoryData() {
        // Begin by creating the query.
        DataReadRequest readRequest = queryFitnessData();

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(mActivity, GoogleSignIn.getLastSignedInAccount(mActivity))
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                // For the sake of the sample, we'll print the data so we can see what we just
                                // added. In general, logging fitness information should be avoided for privacy
                                // reasons.
                                printData(dataReadResponse);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "There was a problem reading the data.", e);
                            }
                        });
    }
}

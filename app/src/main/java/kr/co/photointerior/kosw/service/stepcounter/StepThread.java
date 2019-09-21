package kr.co.photointerior.kosw.service.stepcounter;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.conf.AppConst;
import kr.co.photointerior.kosw.db.KsDbWorker;
import kr.co.photointerior.kosw.global.DefaultCode;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.App;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.ActivityRecord;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.BeaconUuid;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.MainData;
import kr.co.photointerior.kosw.rest.model.Record;
import kr.co.photointerior.kosw.service.beacon.AltitudeManager;
import kr.co.photointerior.kosw.service.beacon.DirectionManager;
import kr.co.photointerior.kosw.service.beacon.MeasureObj;
import kr.co.photointerior.kosw.service.beacon.StepManager;
import kr.co.photointerior.kosw.service.net.NetworkConnectivityReceiver;
import kr.co.photointerior.kosw.ui.LoginActivity;
import kr.co.photointerior.kosw.ui.MainActivity;
import kr.co.photointerior.kosw.ui.fragment.BaseFragment;
import kr.co.photointerior.kosw.utils.DateUtil;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import kr.co.photointerior.kosw.utils.event.BusProvider;
import kr.co.photointerior.kosw.utils.event.KsEvent;
import kr.co.photointerior.kosw.widget.RowActivityRecord;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class StepThread extends Thread {
    private String TAG = LogUtils.makeLogTag(StepThread.class);

    boolean isRun = true;
    private static Context mContext = null;

    private static boolean isTest = false ;
    private Location mLocation ;
    private Address mAddr ;
    private static boolean mStarted;
    private static double mAltitude;
    private double mAltitude2;
    private static double mOrientation;
    private static double mX;
    private static double mY;
    private static double mZ;
    private static double mStep;
    private int mFloor ;

    private int mSleepCnt = 0 ;
    private int mMeasureStep = 0;
    //static private  int mMaxSleepCnt = 10 ;
    static private  int mMaxSleepCnt = 2 ;
    static private  String sleepMsg[] = {"걷기중 측정시간(30초)이 지났습니다.","계단이용 측정시간(5분)이 지났습니다."} ;
    private int sleepMode = 0 ;


    private static double mSaveStep = 0 ;
    private static Boolean isSleep = false ;
    private Boolean isSleepBtn = false ;

    private static int mTrashStep = 0;


    private  Boolean isActivity = false ;

    private static long startTime = 0 ;
    private static long endTime = 0 ;
    private static Boolean isRedDot = false ;

    private AltitudeManager mAltiManager;
    private StepManager mStepManager;
    private DirectionManager mDirectionManager ;

    private  boolean isStart = false ;
    private  boolean isTurn = false ;
    private static boolean isContinue = false ;
    // Create the Handler
    private Handler handler = new Handler(Looper.getMainLooper());
    private static ArrayList<MeasureObj> mStartList = new ArrayList<>() ;
    private ArrayList<MeasureObj> mUpList = new ArrayList<>() ;
    private static int cnt = 0 ;
    private int checkStartTimeOut = 60 ;
    private int checkRotationTimeOut = 60 ;
    private int checkUpTimeOut = 60 ;
    private static Boolean is315 = false ;
    private static int cnt315 = 0 ;
    private  Boolean isCount = false ;

    private static long goupTime = 0 ;

    // 건물 층 카운트
    private static int mBuildCount = 0 ;
    private static int mCurBuildCount = 0 ;

    // 건물 / 등산 모드
    private  static String mIsBuild = "" ; // Y :건물계단    N : 등산계단
    private  static int mClimbCount = 0 ;  // 등산 카운트 (4미터 체크)
    private  static int mLogicCount = 0 ;  // 로직 카운트 (회전 )

    private static String todayFloor = "0";

    private ActivityRecord mActivityRecord;

    private String rank;
    private String tot_floor = "";
    private String tot_cal = "";
    private String tot_sec = "";



    public StepThread(Context context){
        mContext = context;
    }

    public void stopForever(){
        synchronized (this) {
            mStarted = false;
            this.isRun = false;
        }
    }

    public void run(){
        startMeasure(true);
        handler.post(runnable) ;
        /*while(isRun){

            try{
                if (isStart)  {
                    checkFloor() ;
                }
                //Thread.sleep(10000); //10분에 한번 조회
                //Thread.sleep(100); //10분에 한번 조회

            }catch (Exception e) {}
        }*/
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Insert custom code here
            if (mStarted)  {
                checkFloor() ;
                //isTest = true;
            }

            // Repeat every 2 seconds
            handler.postDelayed(runnable, 100);
        }
    };

    /*private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Insert custom code here

            if (mStarted)  {
                checkFloor() ;
            }
            // Repeat every 2 seconds
            handler.postDelayed(runnable, 100);
        }
    };
*/
    public void checkFloor() {
        /*if (isMyServiceRunning(StepCounterService.class)) {
            checkStart();
        } else {
            handler.removeCallbacks(runnable);
            startMeasure(false);
        }*/

        checkStart();
    }


    private  void checkStart() {
        if (mAltitude == 0 )  {
            //return ;
        }

        Log.d("99999999999977777", String.valueOf(mStarted) + mTrashStep + "_______" + cnt + "___" + mSaveStep + "______" + mStep);

        // 30초 이상 걷기 없으면 잠금
        if (cnt >= 10 * 30 )  {
            mMeasureStep = 0;
            if (mSaveStep <=  0 ) { // 걷기중이아니면
                //Toast.makeText(mContext, "걷기중이아니면", Toast.LENGTH_SHORT).show();
                // 원본소스
                mSleepCnt = mMaxSleepCnt;
                sleepMode = 0 ;
                initMeasure();
                return;

                // 수정소스
                //Toast.makeText(mContext, "측정이 없으면 측정 잠금", Toast.LENGTH_SHORT).show();
                //sleepMode = 1 ;
                //mSleepCnt++ ;
                //initMeasure();
                //return;
            }

           else {  // 120초이상 측정이 없으면 측정 잠금  mSleepCnt >= 4
                Toast.makeText(mContext, "측정 잠금", Toast.LENGTH_SHORT).show();
                sleepMode = 1 ;
                mSleepCnt++ ;
                initMeasure();
                return;
            }
        }
        mSaveStep = 0 ;

        Log.d("999999999999777778888", String.valueOf(mStarted) + mTrashStep + "_______" + cnt + "___" + mSaveStep + "______" + mStep);

        MeasureObj obj = mStartList.get(cnt) ;
        obj.altitude = mAltitude ;
        obj.orientation = mOrientation ;
        obj.step = mStep ;
        obj.x = mX ;
        obj.y = mY ;
        obj.z = mZ ;

        MeasureObj obj_b = mStartList.get(0) ;
        obj.altitudeGap =  obj.altitude -  obj_b.altitude ;
        obj.orientationGap =  Math.abs(obj.orientation -  obj_b.orientation) ;
        obj.stepGap =  Math.abs(obj.step -  obj_b.step) ;
        obj.xGap =  Math.abs(obj.x -  obj_b.x) ;
        obj.yGap =  Math.abs(obj.y -  obj_b.y) ;
        obj.zGap =  Math.abs(obj.z -  obj_b.z) ;

        // 고도 합계
        double gapAlitude =   0 ;
        gapAlitude = obj.altitude - obj_b.altitude ;

        // 스텝 합계
        double gapStep =   0 ;
        gapStep = obj.step - obj_b.step ;

            if (cnt > 50) {
            MeasureObj obj_c ;
            obj_c = mStartList.get(cnt - 50 ) ;
            obj.stepGap = Math.abs(obj.step - obj_c.step);
            gapStep = obj.step - obj_c.step ;
        }

        double step = gapStep ; // 초당 걸음수
        mSaveStep = step ;
        Log.d("999999999999777779999", String.valueOf(mStarted) + mTrashStep + "_______" + cnt + "___" + mSaveStep + "______" + mStep);
        List<Double> list = Arrays.asList(obj.xGap,obj.yGap,obj.zGap ) ;
        Double mDir =  Collections.max(list) ;

        String m = String.format("높이 : %.2f , 방향 : %.2f , 걷기 : %.2f , 시간 : %d" , gapAlitude, mDir, step, cnt);

        if (!isContinue) {
            if (mDir > 135 && Math.abs(gapAlitude) > 1.5  ) {
                // 자동측정일 경우, 5초 이내 측정이면  카운트 하지 않음 엘리베이터 사용자 걸름
                // 수동측정은 2초
                long curTime = System.currentTimeMillis();
                if (cnt < 30 || (curTime - goupTime) < 6000) {
                    mSleepCnt++ ;
                    initMeasure();
                    return;
                }

                if (step > 1) { // 걷기중이면

                    AppUserBase user = DataHolder.instance().getAppUserBase() ;
                    mCurBuildCount = user.getBuild_floor_amt() ;
                    mIsBuild = user.getIsbuild() ;

                    if (gapAlitude > 0) {
                        mFloor++;
                        mLogicCount++ ;
                        mClimbCount = 0 ;

                        if  (mIsBuild.equals("Y")) {
                            mBuildCount++;
                        } else {
                            // 건물 모드 :건물높이의 1⁄2 이상이면 건물로 전환
                            if (mLogicCount >= Math.ceil(mCurBuildCount / 2)) {
                                mIsBuild = "Y";
                                mBuildCount = mLogicCount;
                            }
                        }

                        if (mBuildCount == 1) {
                            startTime = System.currentTimeMillis() ;
                        }

                        if (mBuildCount == Math.ceil(mCurBuildCount / 2)  && mBuildCount >= 4  ) {
                            endTime = System.currentTimeMillis() ;
                            isRedDot = true  ;
                        } else {
                            isRedDot = false ;
                        }
                        isCount = true ;
                        if (isTest) {
                            //getTextView(R.id.txt_m).setText(m);
                        }

                        goupTime = System.currentTimeMillis() ;
                        sendDataToServer(1);
                    } else {
                        mBuildCount = 0 ;
                        mClimbCount = 0 ;
                        mLogicCount = 0 ;

                        mFloor++;
                        isCount = true ;
                        if (isTest) {
                            //getTextView(R.id.txt_m).setText(m);
                        }
                        goupTime = System.currentTimeMillis() ;
                        sendDataToServer(1);
                    }

                    // 90이상이면 높이는 클리어  방향은 보존
                    MeasureObj obj_bb =  mStartList.get(0) ;
                    isContinue = true ;
                    mStartList.clear();
                    for (int i = 0; i < 600; i++) {
                        mStartList.add(new MeasureObj());
                    }
                    cnt = 0;
                    mStartList.get(0).x = obj_bb.x  ;
                    mStartList.get(0).y = obj_bb.y  ;
                    mStartList.get(0).z = obj_bb.z  ;
                    mStartList.get(0).xGap = obj_bb.xGap ;
                    mStartList.get(0).yGap = obj_bb.yGap ;
                    mStartList.get(0).zGap = obj_bb.zGap ;
                    mStartList.get(0).step = obj_bb.step ;
                    mStartList.get(0).altitude = mAltitude ;

                    cnt++ ;
                    return ;

                }
            }

        }

        if (mDir >  315 &&  isContinue   ) {  //
            // 315 이상이면 높이는 클리어  방향은 보존
            MeasureObj obj_bb =  mStartList.get(0) ;
            isContinue = false ;
            for (int i = 0; i < cnt; i++) {
                mStartList.get(i).x = obj_bb.x  ;
                mStartList.get(i).y = obj_bb.y  ;
                mStartList.get(i).z = obj_bb.z  ;
                mStartList.get(i).xGap = obj_bb.xGap ;
                mStartList.get(i).yGap = obj_bb.yGap ;
                mStartList.get(i).zGap = obj_bb.zGap ;
                mStartList.get(i).step = obj_bb.step ;
                mStartList.get(i).altitude = mAltitude ;
            }
            mStartList.get(cnt).x = obj_bb.x  ;
            mStartList.get(cnt).y = obj_bb.y  ;
            mStartList.get(cnt).z = obj_bb.z  ;
            mStartList.get(cnt).xGap = obj_bb.xGap ;
            mStartList.get(cnt).yGap = obj_bb.yGap ;
            mStartList.get(cnt).zGap = obj_bb.zGap ;
            mStartList.get(cnt).step = obj_bb.step ;
            mStartList.get(cnt).altitude = mAltitude ;

            is315 = true ;
            cnt315 = cnt ;

            cnt++ ;
            return ;

        }

        // 1초뒤에 방향만 클리어
        if ( is315 && cnt > (cnt315 + 10)  ) {  //
            MeasureObj obj_bb =  mStartList.get(0) ;

            for (int i = 0; i < cnt ; i++) {
                mStartList.get(i).x = mX  ;
                mStartList.get(i).y = mY  ;
                mStartList.get(i).z = mZ  ;
                mStartList.get(i).step = obj_bb.step ;
                mStartList.get(i).altitude = obj_bb.altitude ;
            }
            mStartList.get(cnt).x = mX  ;
            mStartList.get(cnt).y = mY  ;
            mStartList.get(cnt).z = mZ  ;
            mStartList.get(cnt).step = obj_bb.step ;
            mStartList.get(cnt).altitude = obj_bb.altitude ;
            is315 = false ;
            cnt315 = 0 ;

            cnt++ ;
            return ;
        }

        //=================================
        // 회전이 없고 3미터 이상이면 1층 측정
        //=================================

        if (Math.abs(gapAlitude) > 3   ) {
            long curTime = System.currentTimeMillis() ;
            if (cnt < 20  || (curTime - goupTime) < 2000 ) {
                //mSleepCnt++;
                initMeasure();
                return;
            } else {
                if (step > 1) { // 걷기중이면
                    if (gapAlitude > 0) {

                        AppUserBase user = DataHolder.instance().getAppUserBase() ;
                        mCurBuildCount = user.getBuild_floor_amt() ;
                        mIsBuild = user.getIsbuild() ;
                        mClimbCount++;
                        mLogicCount = 0 ;

                        if (mIsBuild.equals("Y")) {
                            if (mClimbCount > mCurBuildCount ) {
                                mIsBuild = "N" ;
                                mFloor++;
                                mBuildCount = mClimbCount ;

                                if (mBuildCount == 1) {
                                    startTime = System.currentTimeMillis();
                                }
                                if (mBuildCount == Math.ceil(mCurBuildCount / 2)  && mBuildCount >= 3 ) {
                                    endTime = System.currentTimeMillis();
                                    isRedDot = true;
                                } else {
                                    isRedDot = false;
                                }

                                goupTime = System.currentTimeMillis();
                                sendDataToServer(1);
                            }
                        } else {
                            mBuildCount++ ;
                            mFloor++;
                            if (mBuildCount == 1) {
                                startTime = System.currentTimeMillis();
                            }
                            if (mBuildCount == Math.ceil(mCurBuildCount / 2) && mBuildCount >= 3 ) {
                                endTime = System.currentTimeMillis();
                                isRedDot = true;
                            } else {
                                isRedDot = false;
                            }
                            if (isTest) {
                                //getTextView(R.id.txt_m).setText(m);
                            }
                            goupTime = System.currentTimeMillis();
                            sendDataToServer(1);
                        }
                    } else {
                        mBuildCount = 0 ;
                        mClimbCount = 0 ;
                        mLogicCount = 0 ;
                        if (mIsBuild.equals("Y")) {
                            //mClimbCount++;
                        } else {
                            mFloor++;
                            if (isTest) {
                                //getTextView(R.id.txt_m).setText(m);
                            }
                            goupTime = System.currentTimeMillis();
                            sendDataToServer(1);
                        }
                    }
                    initMeasure();
                    return ;


                }
            }

        }

        if ( mDir > 400 ) {
            initMeasure();
            return ;
        }

        cnt++ ;
    }


    private  void initMeasure() {
        mStartList.clear();

        if (mStartList.size() == 0) {
            for (int i = 0; i < 600; i++) {
                mStartList.add(new MeasureObj());
            }
        }

        cnt = 0 ;

        isStart = false ;
        isTurn = false ;
        isContinue = false ;
        is315 = false ;
        cnt315 = 0 ;

        // 차량 이동시 계단으로 측정되는 값을 줄이기 위한 값
        mTrashStep = 0;

        isCount = false ;
        goupTime = System.currentTimeMillis();

        // 5분 지나면 잠금
        if (mSleepCnt >= mMaxSleepCnt ) {
            Toast.makeText(mContext, "슬립모드", Toast.LENGTH_SHORT).show();
            isSleep = true;;
            mSleepCnt = 0  ;
            mStarted = false ;
            startMeasure(false);
        }

    }

    // 빌딩 층수 카운트 (빌딩 높이 )
    private void saveBuildCount(int count) {

    }

    // 측정 시작 ,멈춤
    public void startMeasure(boolean started){

        if(!started){

            saveBuildCount(mBuildCount);


            goupTime = System.currentTimeMillis();

            mAltitude = 0 ;
            mOrientation = 0 ;
            mStep = 0 ;
            mX = 0 ;
            mY = 0 ;
            mZ = 0 ;

            for (int i = 0; i < 600; i++) {
                MeasureObj obj = mStartList.get(i) ;
                obj.altitude = 0.0;
                obj.orientation = 0.0 ;
                obj.step = 0.0 ;
                obj.x = 0.0 ;
                obj.y = 0.0 ;
                obj.z = 0.0;
                obj.xGap = 0.0 ;
                obj.yGap = 0.0 ;
                obj.zGap = 0.0 ;
            }
            cnt = 0;

            if (mAltiManager != null) mAltiManager.stopMeasure();
            if (mDirectionManager != null) mDirectionManager.stopMeasure();
            //mStepManager.stopMeasure();
            //findViewById(R.id.LayoutPause).setVisibility(View.VISIBLE);

        }else{


            AppUserBase user = DataHolder.instance().getAppUserBase() ;

            if (user != null) {
                mCurBuildCount = user.getBuild_floor_amt(); // 현재 빌딩층수 (높이)
            }

            if (!isSleep) {
                initMeasure();
            }

            goupTime = System.currentTimeMillis();

            mAltitude = 0 ;
            mOrientation = 0 ;
            mStep = 0 ;
            mX = 0 ;
            mY = 0 ;
            mZ = 0 ;

            for (int i = 0; i < 600; i++) {
                MeasureObj obj = mStartList.get(i) ;
                obj.altitude = 0.0;
                obj.orientation = 0.0 ;
                obj.step = 0.0 ;
                obj.x = 0.0 ;
                obj.y = 0.0 ;
                obj.z = 0.0;
                obj.xGap = 0.0 ;
                obj.yGap = 0.0 ;
                obj.zGap = 0.0 ;

            }
            cnt = 0;

            mAltiManager = new AltitudeManager(mContext);
            mStepManager = new StepManager(mContext) ;
            mDirectionManager = new DirectionManager(mContext) ;

            isSleep = false  ;

            mAltiManager.startMeasure();
            mDirectionManager.startMeasure();
            mStepManager.startMeasure();
            //findViewById(R.id.LayoutPause).setVisibility(View.INVISIBLE);
            //mValue.setText("wait...");
        }
        mStarted = started ;
    }

    public void setCurrentAltitude(double altitude){
        mAltitude = altitude;
    }

    public void setCurrentStep(double val  ){
        mStep += val;
        mTrashStep += val;
        mMeasureStep += val;

        if (!mStarted && mMeasureStep > 20) {
            restartTracking();
        }
    }

    private void restartTracking() {
        startMeasure(false);
        mStarted = true;

        startMeasure(true);
        mStartList.clear();
        for (int i = 0; i < 600; i++) {
            mStartList.add(new MeasureObj());
        }
        cnt = 0;
        mMeasureStep = 0;
        Toast.makeText(mContext, "측정시작 " + mMeasureStep, Toast.LENGTH_SHORT).show();
    }

    public void setCurrentOrientation2(double x ,double y, double z  ){
        mX = x;
        mY = y;
        mZ = z;
        //displayAlti();
    }

    public void setCurrentAltitude2(double altitude){
        mAltitude2 = altitude;
    }

    public void setCurrentOrientation(double val){

        mOrientation = val;
    }



    /**
     * 계단을 올라간 데이터를 서버로 전송
     */

    private void sendDataToServer(int goupAmt ){
        // 5걸음 이상 걷지 않았을 경우, 계단수에서 빼도록 처리
        /*if (mTrashStep < 10) {
            return;
        }*/

        if (mSaveStep <=  10 ) {
            return;
        }



            //Toast.makeText(mContext, "Send to Server", Toast.LENGTH_SHORT).show();
        mSleepCnt = 0 ;
        mTrashStep = 0 ;

        String token = KUtil.getUserToken();
        String buildCode = KUtil.getBuildingCode();
        if(StringUtil.isEmptyOrWhiteSpace(token) || StringUtil.isEmptyOrWhiteSpace(buildCode)){
            return;
        }

        AppUserBase user =  DataHolder.instance().getAppUserBase() ;

        SharedPreferences prefr = mContext.getSharedPreferences("userInfo", MODE_PRIVATE);
        if (null == prefr) return;
        String userToken = prefr.getString("token", "");
        if ("".equals(userToken)) return;


        MediaPlayer mMediaPlayer = new MediaPlayer();
        Uri mediaPath = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.alert);
        try {
            mMediaPlayer.setDataSource(mContext.getApplicationContext(), mediaPath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //return;
        try {
            Map<String, Object> query = new HashMap<>();
            query.put("token", prefr.getString("token", "")) ;
            query.put("beacon_uuid", "");
            query.put("major_value", "");
            query.put("minor_value", "");
            query.put("install_floor", "");
            //map.put("beacon_seq", getBeaconSeq());
            query.put("beacon_seq", prefr.getInt("beacon_seq", 0));
            if ( user.getBeacon_seq() == 0 ) {
                DefaultCode.BEACON_SEQ.getValue() ;
            }
            query.put("stair_seq", prefr.getInt("stair_seq", 0));
            query.put("build_seq", prefr.getString("build_seq", "")) ;
            query.put("build_name", prefr.getString("build_name", "")) ;
            query.put("floor_amount", 0);
            query.put("stair_amount", 0);
            query.put("cust_seq", prefr.getInt("cust_seq", 0));
            query.put("cust_name", prefr.getString("cust_name", "")) ;
            query.put("build_code", prefr.getString("build_code", "")) ;
            query.put("godo", mAltitude);
            query.put("goup_amt", goupAmt);
            query.put("curBuildCount",mCurBuildCount) ;
            query.put("buildCount",mBuildCount) ;
            query.put("isbuild",mIsBuild) ;
            query.put("country",user.getCountry()) ;
            query.put("city",user.getCity()) ;

            if (isRedDot) {
                query.put("start_time", startTime);
                query.put("end_time", endTime);
            }

            final String localTime = DateUtil.currentDate("yyyyMMddHHmmss");

            if(NetworkConnectivityReceiver.isConnected(mContext)) {
                Call<BeaconUuid> call =
                        new DefaultRestClient<App>(mContext)
                                .getClient(App.class).sendStairGoUpAmountToServer(query);
                final String goupSentTime = DateUtil.currentDate("yyyyMMdd HHmmss");

                call.enqueue(new Callback<BeaconUuid>() {
                    @Override
                    public void onResponse(Call<BeaconUuid> call, Response<BeaconUuid> response) {
                        /*
                        if (isReddot) {
                            mReddotStack.clear();
                        }
                        */
                        LogUtils.e(TAG, "success-data response raw:" + response.raw().toString());
                        if (response.isSuccessful()) {
                            LogUtils.e(TAG, "success-data response body:" + response.body().string());

                            BeaconUuid uuid = response.body();
                            if (uuid != null && uuid.isSuccess()) {
                                broadcastServerResult(uuid);

                            }else {
                                saveGoUpDataToLocalDb(query, localTime);
                            }
                            //sendBeaconLog(beaconUuid, floorDiff, "go up ", goupSentTime);//층간이동 전송 성공하면 로그 전송

                            // 노티피케이션 업데이트
                            requestgetActivityRecords();




                        }else {
                            saveGoUpDataToLocalDb(query, localTime);
                        }
                    }

                    @Override
                    public void onFailure(Call<BeaconUuid> call, Throwable t) {
                        saveGoUpDataToLocalDb(query, localTime);
                    }
                });
            }else{
                saveGoUpDataToLocalDb(query, localTime);
            }
        }catch (Exception e){

        }
    }

    /**
     * 계단 올라간 데이터 서버전송 실패하면 로컬에 저장 후 네트워크가 안정화 되면 전송
     * @param goupData
     */
    private void saveGoUpDataToLocalDb(Map<String, Object> goupData, String localTime){
        goupData.put("app_time", localTime);
        if(KsDbWorker.insertFailData(mContext, goupData)){
            BusProvider.instance().post(
                    new KsEvent<Map<String, Object>>()
                            .setType(KsEvent.Type.UPDATE_FLOOR_AMOUNT_FAIL)
                            .setValue(goupData)
                            .setMainCharacterChanged(false));
        }
    }
    /**
     * 올라간 층수 데이터 서버전송 성공시 데이터 전파
     * @param beaconUuid
     */
    public static void broadcastServerResult(BeaconUuid beaconUuid){

        Pref pref = Pref.instance();

        //if (!StringUtil.isEmptyOrWhiteSpace(beaconUuid.getMainCharFilename()) ) {
        pref.saveStringValue(PrefKey.CHARACTER_MAIN, beaconUuid.getMainCharFilename());
        //}
        //if(!StringUtil.isEmptyOrWhiteSpace(beaconUuid.getSubCharFilenane())) {
        pref.saveStringValue(PrefKey.CHARACTER_SUB, beaconUuid.getSubCharFilenane());
        //}

        //LogUtils.err(TAG, beaconUuid.string());
        //String prevChar = KUtil.getStringPref(PrefKey.CHARACTER_MAIN, "");
        //boolean isMainChracterChanged = !StringUtil.isEquals(prevChar, beaconUuid.getMainCharFilename());
        boolean isMainChracterChanged = false ;

        String charImageFile = pref.getStringValue(PrefKey.CHARACTER_MAIN,"") ;

        // 기존 이미지와 동일 하면 스킵
        if ( !charImageFile.equals(beaconUuid.getMainCharFilename()) ) {
            isMainChracterChanged = true ;
        }

        if(isMainChracterChanged) {
            KUtil.saveStringPref(PrefKey.CHARACTER_MAIN, beaconUuid.getMainCharFilename());
        }

        if(!StringUtil.isEmptyOrWhiteSpace(beaconUuid.getSubCharFilenane())) {
            KUtil.saveStringPref(PrefKey.CHARACTER_SUB, beaconUuid.getSubCharFilenane());
        }

        BusProvider.instance().post(
                new KsEvent<BeaconUuid>()
                        .setType(KsEvent.Type.UPDATE_FLOOR_AMOUNT)
                        .setValue(beaconUuid)
                        .setMainCharacterChanged(isMainChracterChanged)
        );
    }

    public void requestgetActivityRecords() {
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        Call<ActivityRecord> call =
                new DefaultRestClient<UserService>(mContext).getClient(UserService.class)
                        .getActivityRecords(query);
        call.enqueue(new Callback<ActivityRecord>() {
            @Override
            public void onResponse(Call<ActivityRecord> call, Response<ActivityRecord> response) {
                LogUtils.err(TAG, response.raw().toString());
                if (response.isSuccessful()) {
                    ActivityRecord record = response.body();
                    if (record.isSuccess()) {

                        Record total = record.getTotalRecord();
                        Record r_rank = record.getTotalRank();
                        //Record r_rank = record.getTodayRank();

                        if (null != r_rank) {
                            AppConst.NOTI_RANKS = StringUtil.format(Double.parseDouble(r_rank.getRank()), "#,##0");
                        } else {
                            AppConst.NOTI_RANKS = "-";
                        }
                        AppConst.NOTI_FLOORS = StringUtil.format(total.getAmountToFloat(), "#,##0");
                        AppConst.NOTI_CALS = KUtil.calcCalorie(total.getAmountToFloat(), total.getStairAmountToFloat());
                        AppConst.NOTI_SECS = KUtil.calcLife(total.getAmountToFloat(), total.getStairAmountToFloat());

                        updateNotification(AppConst.NOTI_RANKS, AppConst.NOTI_FLOORS, AppConst.NOTI_CALS, AppConst.NOTI_SECS);
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<ActivityRecord> call, Throwable t) {
                //toast(R.string.warn_commu_to_server);
                LogUtils.err(TAG, t);
            }
        });
    }


    private static void updateNotification(String ranking, String floor, String cal, String sec) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, AppConst.NOTIFICATION_CHANNEL_ID);

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentPendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if ("-".equals(ranking)) {
            builder.setContentTitle("건강한 습관, 계단왕")
                    .setContentText("지금 시작하십시오.")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_floor))
                    .setWhen(System.currentTimeMillis())
                    .setOngoing(true)
                    //.setColorized(true)
                    .setColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
                    .setContentIntent(contentPendingIntent);
        } else {
            builder.setContentTitle("[랭킹:" + ranking + "]")
                    .setContentText(floor + "F / " + cal + "kcal / " + sec + "sec")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_floor))
                    .setWhen(System.currentTimeMillis())
                    .setOngoing(true)
                    //.setColorized(true)
                    .setColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
                    .setContentIntent(contentPendingIntent);
        }

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(AppConst.NOTIFICATION_ID, builder.build());
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

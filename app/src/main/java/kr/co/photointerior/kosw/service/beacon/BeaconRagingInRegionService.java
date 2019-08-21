package kr.co.photointerior.kosw.service.beacon;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.db.KsDbWorker;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.global.KoswApp;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.App;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.BeaconLog;
import kr.co.photointerior.kosw.rest.model.BeaconUuid;
import kr.co.photointerior.kosw.rest.model.KsBeacon;
import kr.co.photointerior.kosw.rest.model.Logo;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.service.net.NetworkConnectivityReceiver;
import kr.co.photointerior.kosw.ui.CodeAcceptActivity;
import kr.co.photointerior.kosw.ui.MainActivity;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.DateUtil;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import kr.co.photointerior.kosw.utils.event.BusProvider;
import kr.co.photointerior.kosw.utils.event.KsEvent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * beacon ranging service class.
 */
//public class BeaconRagingInRegionService extends Service implements BeaconConsumer, BootstrapNotifier {
public class BeaconRagingInRegionService extends BaseService implements BeaconConsumer {
    private String TAG = LogUtils.makeLogTag(BeaconRagingInRegionService.class);
    private String mBeaconLayout = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    //    private String mBeaconLayout = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private BeaconManager mBeaconManager;
    private Region mReigon;
    private AltitudeManager mAltitudeManager;
    //    private RegionBootstrap mRegionBootstrap;
    private KoswApp mApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mApplication = (KoswApp)getApplication();
        //Unable to start service kr.co.photointerior.kosw.service.beacon.BeaconRagingInRegionService@3e13b8b with Intent { cmp=kr.co.photointerior.kosw.app/kr.co.photointerior.kosw.service.beacon.BeaconRagingInRegionService }: java.lang.IllegalStateException: Method must be called before calling bind()
        sendServiceLog("service_create");
        mBeaconManager = mApplication.getBeaconManager();
        //mBeaconManager = BeaconManager.getInstanceForApplication(this);
        //mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(mBeaconLayout));
        mReigon = new Region("kosw-RangingUniqueId", null, null, null);

        sendServiceLog("beacon_manager_initialized");

        //mAltitudeManager = new AltitudeManager(this);
        //mApplication.bindRagingServiceToAltitudeManager(this);
        mAltitudeManager = mApplication.getAltitudeManager();
        LogUtils.err(TAG, "beacon ranging service onCreate.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAltitudeManager.startMeasure();
        mApplication.bindRagingServiceToAltitudeManager(this);
//        if(mBeaconManager.isBound(this)){
//            mBeaconManager.unbind(this);
//        }
//        mBeaconManager.setEnableScheduledScanJobs(false);//forgroud service일 경우 false
//        mBeaconManager.bind(this);// 비콘 탐지를 시작한다. 실제로는 서비스를 시작하는것.
//        mBeaconManager.setDebug(Env.Bool.BEACON_DEBUG.getValue());
//        mBeaconManager.setRegionStatePersistenceEnabled(false);
//        mBeaconManager.setBackgroundMode(true);
        mApplication.bindBeaconService(this);

//        setScanPreiods();

        mCurrentBeacon = null;
        mAltitudeGap = -1;
        mPrevIndex = -1;
        mCurrentAltitude = -99999;
        mAltiCheckLimitTime = 0L;

        mReddotStack = new ArrayList<>();
        mLastTime = 0L;

        /* ******************************* */
        //notifyServiceForForeground();
        /* ******************************* */

        sendServiceLog("beacon_service_on_started");
        checkBatteryWhiteList();

        mServiceStartTime = System.currentTimeMillis();

        return START_STICKY;
    }

//    private void setScanPreiods(){
//        mBeaconManager.setForegroundScanPeriod(1000L);
//        mBeaconManager.setForegroundBetweenScanPeriod(100L);
//        mBeaconManager.setBackgroundScanPeriod(1000L);
//        mBeaconManager.setBackgroundBetweenScanPeriod(100L);
//        try {
//            mBeaconManager.updateScanPeriods();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }

    private void notifyServiceForForeground(){

        PendingIntent pi =
                PendingIntent.getActivity(this, 0,
                        new Intent(this, CodeAcceptActivity.class),
                        PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder nb = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getString(R.string.app_name))
                //.setContentText(condition.getNotificationContent())
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pi)
                .setAutoCancel(false);
        startForeground(101011, nb.build());

//        Notification.Builder builder = new Notification.Builder(this);
//        builder.setSmallIcon(R.mipmap.ic_launcher_round);
//        builder.setContentTitle(getString(R.string.app_name));
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
//        );
//        builder.setContentIntent(pendingIntent);
//        mBeaconManager.enableForegroundServiceScanning(builder.build(), 456);

    }

    @Override
    public void onDestroy() {
        try {
            mApplication.stopRanging(mReigon);
            //mBeaconManager.stopRangingBeaconsInRegion(mReigon);
        } catch (Exception e) {
            String s = Log.getStackTraceString(e);
            sendServiceLog("beacon_service_ranging_stop_error:" + s);
        }
        mApplication.unbindBeaconService(this);
        //mBeaconManager.unbind(this);//비콘 스캔 중지
        mAltitudeManager.stopMeasure();//고도측정 중지

        /* ******************************* */
        //stopForeground(true);
        /* ******************************* */

        Log.e("TEST", "beacon destroyed");
        sendServiceLog("service_destroyed");
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Env.Action.KOSW_RANGE_RESTART_ACTION.action()));
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private BeaconBinder mBinder = new BeaconBinder();
    /** 서비스 바인드 클래스.*/
    public class BeaconBinder extends Binder{
        public BeaconRagingInRegionService getService(){
            return BeaconRagingInRegionService.this;
        }
    }

    /**
     * 이 클래스 instance 리턴
     * @return
     */
    public BeaconRagingInRegionService getBeaconService(){
        return this;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public AltitudeManager getAltitudeManager() {
        return mAltitudeManager;
    }

    /** 현재층이 설정 된 시간 */
//    private Date mMinorValueSettingTime = null;
    /** 비콘 관리번호 */
//    private int mCurrentFloorCheckValue = -1;
    /** 현재고도 보정값 */
    private double mAltitudeGap = -1;
    /** 레드닷 시작시간 */
    //private Date mReddotStartTime = null;
    /** 이전 층 minor value */
    private int mPrevIndex = -1;
    /** 현재 고도 */
    private double mCurrentAltitude = -99999;
    /** 고도 수신시 검주기를 판단하기 위한 값. */
    private long mAltiCheckLimitTime = 0L;
    /** 서비스를 재시작 제한 시간 */
    private final long SERVICE_RESTART_LIMIT_TIME = ((1000 * 60) * 15);
    private int mRangeFailCount = 0;
    private double plus;
    /** 센서에서 받은 현재 고도 */
    public void setCurrentAltitude(double altitude){
        LogUtils.err(TAG, "altitude=" + altitude);
        if(altitude > 500 || altitude < -500){
            return;
        }
        if(Env.Bool.USE_TEST_DUMMY_FLOOR_UP.isTrue()) {//가상으로 계단 올라가기 테스트 모드
            plus += 0.1;
            altitude += plus;
        }
        if(Double.isInfinite(altitude)){
            sendServiceLog("altitude_intinite");
            mCurrentAltitude = -99999;
            mAltitudeManager.restartMeasure();
            return;
        }
        if(Double.isNaN(altitude)){
            sendServiceLog("altitude_NaN");
            mCurrentAltitude = -99999;
            mAltitudeManager.restartMeasure();
            return;
        }

        /*if(Math.abs(altitude - mCurrentAltitude) < 0.2){//0.2m 차이는 reject
            //sendServiceLog("alti reject by gap");
            return;
        }
        if((System.currentTimeMillis() - mAltiCheckLimitTime) <= 200 ){//0.2초 이내에 들어오는 고도 정보 reject.
            sendServiceLog("altitude_reject_by_time");
           return;
        }*/

        mCurrentAltitude = altitude;
        checkGodoChanged();
        mAltiCheckLimitTime = System.currentTimeMillis();
    }
    public double getCurrentAltitude(){
        return mCurrentAltitude;
    }

    private KsBeacon mDummyBeacon;
    public double getDummyAlti(){
        if(mDummyBeacon !=  null) {
            return mCurrentAltitude - (mDummyBeacon.getAltitude() + mAltitudeGap);
            //return mAltitudeManager.getAltitude() - (mDummyBeacon.getAltitude() + mAltitudeGap);
        }
        return 0.0;
    }

    public KsBeacon getCurrentMatchedBeacon() {
        return mDummyBeacon;
    }

    public double getDummyGap(){
        return mAltitudeGap;
    }

    public int getPrevMinorValue(){
        return mPrevIndex;
    }

    private String mBuildingCode;
    private List<BeaconUuid> mBeaconList = new ArrayList<>();
    private double[] mGodo;
    private List<Long> mReddotStack = new ArrayList<>();
    private long mLastTime = 0L;
    private String mInfoTxt;

    public String getInfoTxt(){
        return mInfoTxt;
    }
    private int godoCnt;
    private double mPrevAlt;
    private int mLastGoUpBeaconSeq;
    /**
     * 고도 변경사항 처리
     */
    private synchronized void checkGodoChanged(){
        //if( mCurrentAltitude == -99999 ){
        if (mCurrentAltitude > 500 || mCurrentAltitude < -500) {
            return;
        }
        if(godoCnt > 0){
            return;
        }
        godoCnt++;

        if(mCurrentBeacon != null){

            int minorValue = mCurrentBeacon.getMinorValueToInt();

//            mInfoTxt = MessageFormat.format("ba:{0} / ca:{1}",
//                    LibUtils.format(mCurrentBeacon.getAltitudeToDouble(), "#,##0.0"),
//                    LibUtils.format(mCurrentAltitude, "#,##0.0"));

//            mInfoTxt = MessageFormat.format("f:{0} / ba:{1} / ca:{2}",
//                    minorValue,
//                    mCurrentBeacon != null ? LibUtils.format(mCurrentBeacon.getAltitudeToDouble(), "#,##0.0") : "current beacon is null.",
//                    LibUtils.format(mCurrentAltitude, "#,##0.0"));
//
//            sendMsg();
//            LogUtils.err(TAG, "info txt=" + mInfoTxt);
            BeaconUuid matched = checkGodo();

            if( matched != null ){
                double altitude = mDummAlt;

                int matchedMinorValue = matched.getMinorValueToInt();

                /*if(matchedMinorValue < 0 && minorValue < 0){//현재 비콘, 매칭 비콘 모두 minor가 -일때 2018.08.23 22:25
                    return;
                }*/

                int floorDiff = matchedMinorValue - minorValue;

                if( floorDiff > 0 ){
                    /*if( mNotScanedCount > 0 ){
                        sendBeaconLog(matched, -10, "notscancount > 0", null);
                        return;
                    }*/
                    //+1 이 같은 비콘일 때 리젝
                    if(mLastGoUpBeaconSeq == matched.getBeaconSeqToInt()){
                        sendBeaconLog(matched, -20, "beaconseq dupl={" + mLastGoUpBeaconSeq + "}", null);
                        return;
                    }
                    /*if(floorDiff > 1){//1층 이상 결정된 것은 reject
                        sendBeaconLog(matched, floorDiff, "amt > 1", null);
                        return;
                    }*/
                    mCurrentBeacon = matched;
                    mCurrentBeacon.setFloorSettingTime(System.currentTimeMillis());
                    for( int i = 0; i < floorDiff; i++ ){
                        if( mReddotStack.size() == (mBeaconOfBuilding.size() / 3) ){
                            mReddotStack.remove(0);
                        }
                        mReddotStack.add(System.currentTimeMillis());
                    }
                    mCurrentBeacon.setGoUpAmount(floorDiff);
                    mLastGoUpBeaconSeq = mCurrentBeacon.getBeaconSeqToInt();
                    boolean isReddot = mReddotStack.size() == (mBeaconOfBuilding.size() / 3);
                    /*sendServiceLog("goup:matched_minor=" + matchedMinorValue + " beacon_minor="+minorValue
                            + " amount:" + mCurrentBeacon.getGoUpAmount() + "reddot_size=" + mReddotStack.size());*/
                    sendDataToServer(mCurrentBeacon.deepCopy(), isReddot, altitude, floorDiff);//서버로 데이터 전송
                    //sendBeaconLog(floorDiff);
                }else if( floorDiff < 0 ){
                    /*}else{*/
                    mCurrentBeacon = matched;
                    mCurrentBeacon.setFloorSettingTime(System.currentTimeMillis());
                    /*for( int i = 0; i < floorDiff + 1; i++ ){
                        if( mReddotStack.size() > 0 ){
                            mReddotStack.remove(0);
                        }
                    }*/
                    mReddotStack.clear();
                    mReddotStack.add(System.currentTimeMillis());
                    /*sendServiceLog("godown:matched_minor=" + matchedMinorValue + " beacon_minor="+minorValue
                            + " amount:" + mCurrentBeacon.getGoUpAmount() + "reddot_size=" + mReddotStack.size());*/
                    BeaconUuid bu = mCurrentBeacon.deepCopy();
                    bu.setGoUpAmount(floorDiff);
                    //sendDataToServer(bu, false, altitude);//분석을 위해 내려간 데이터도 서버로 데이터 전송
                    sendBeaconLog(matched, floorDiff, "amt < 0", null);
                }
            }
        }
        godoCnt = 0;
    }

    private int cnt;
    private double mDummAlt;
    private List<String> mGodoList;
    private int mGodoIndex = -1;
    private synchronized BeaconUuid checkGodo(){
        if( cnt == 0 ) {
            cnt++;
            mGodoList = new ArrayList<>();
            mDummAlt = mCurrentAltitude;
            for (int i = 0, k = mBeaconOfBuilding.size(); i < k; i++) {
                BeaconUuid bu = mBeaconOfBuilding.get(i);

                double beaconGodo = bu.getAltitudeToDouble();
                mGodoList.add(String.valueOf(beaconGodo));

                //if (Math.abs(beaconGodo - mDummAlt) < 0.5) {
//                if (Math.abs(beaconGodo - mDummAlt) < 0.3) {
                if (Math.abs(beaconGodo - mDummAlt) < 0.9) {//2018.07.17 송찬호 대표 요청으로 편차 0.5 추가함.
                    mGodoIndex = i;
                    cnt = 0;
                    return bu.deepCopy();
                }
            }
            cnt = 0;
        }
        return null;
    }

    private List<BeaconUuid> mBeaconOfBuilding = new ArrayList<>();
    /** 현재 매칭된 비콘 */
    private BeaconUuid mCurrentBeacon;
    /** 2분간 고도가 변하지 않으면 리셋 */
    private final int RESET_LIMIT = (1000 * 60) * 2;
//    private boolean mBeaconScaned;
    /** 서비스가 시작된 시간 */
    private long mServiceStartTime = 0L;
    private KoswRangingNotifier mNotifier = new KoswRangingNotifier();
    /** 비콘 서비스 연결 수신 */
    @Override
    public void onBeaconServiceConnect() {
        LogUtils.err("TEST", "connected");
        sendServiceLog("beacon_service_connected");
        mBuildingCode = KUtil.getBuildingCode();
        //mBeaconManager.addRangeNotifier(new KoswRangingNotifier());
        //mBeaconManager.setRangeNotifier(new KoswRangingNotifier());
        mApplication.setRangeNotifier(mNotifier);
        sendServiceLog("beacon_ranging_notifier_added.");
        setRegions();
    }
    private int mNotScanedCount = 0;
    /** 비콘 수신 처리 */
    public class KoswRangingNotifier implements RangeNotifier{
        /** 비콘 수신 */
        @Override
        public synchronized void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            if(mNotScanedCount > 60){
                if( mCurrentBeacon != null ) {
                    mCurrentBeacon = null;
                    sendBeaconLog(null, 0, "not scaned init", null);
                }
                mReddotStack.clear();
                mNotScanedCount = -1;
            }

            if(beacons.size() > 0){
                StringBuilder sb = new StringBuilder("didRangeBeaconsInRegion=").append("\n");
                for( Beacon ba : beacons ){
                    sb.append("\t").append(ba.toString()).append(" rssi:").append(ba.getRssi()).append("\n");
                }
                LogUtils.err(TAG, sb.toString());
            }
            //LogUtils.e(TAG, "m_current_beacon=" +(mCurrentBeacon == null));

            if ( System.currentTimeMillis() - mServiceStartTime > SERVICE_RESTART_LIMIT_TIME ) {//서비스 재시작
                stopServices();
                return;
            }

            try {
                LogUtils.err(TAG,"scaned_size=" + beacons.size() + " alti=" + mCurrentAltitude);
                if (mCurrentAltitude > 500 || mCurrentAltitude < -500) {
                    return;
                }

                if (beacons.size() > 0) {

                    List<KsBeacon> dummyBeacons = pickupMyBeacon(beacons);//빌딩 교체를 위한 것.
                    if (dummyBeacons.size() > 0) {
                        mNotScanedCount = 0;
                        KsBeacon kb = dummyBeacons.get(0);//신호세기 제일 쎈거
                        if (!isEqualsCurrentBuilding(kb.getBuildingCode())) {//지정한 빌딩이 아님.
                            BeaconUuid bu =
                                    checkMyBeacon(
                                            kb.getUuid(),
                                            String.valueOf(kb.getMajor()),
                                            String.valueOf(kb.getMinor())
                                    );
                            LogUtils.err(TAG,"building_changed:" + bu.string());
                            switchBuilding(bu);
                            return;
                        }
                    }else{
                        mNotScanedCount++;
                        return;
                    }

                    List<KsBeacon> source = pickupMyBeaconByBuildingCode(beacons);//빌딩코드 기준
                    if (source.size() == 0) {
                        if (mCurrentBeacon != null) {
                            long setTime = mCurrentBeacon.getFloorSettingTime();
                            long curTime = System.currentTimeMillis();
                            if (curTime - setTime > RESET_LIMIT) {
                                LogUtils.err(TAG,"reset_current_beacon_and_reddotstack.");
                                if( mCurrentBeacon != null ) {
                                    mCurrentBeacon = null;
                                    sendBeaconLog(null, 0, "curTime - setTime > RESET_LIMIT (1)", null);
                                }
                                mReddotStack.clear();
                                mNotScanedCount = 0;
                            }
                        }
                        return;
                    }

                    if (mCurrentBeacon == null) {
                        KsBeacon kb = source.get(0);//신호세기 제일 쎈거

                        if (!isEqualsCurrentBuilding(kb.getBuildingCode())) {//지정한 빌딩이 아님.
                            mNotScanedCount = 0;
                            BeaconUuid bu =
                                    checkMyBeacon(
                                            kb.getUuid(),
                                            String.valueOf(kb.getMajor()),
                                            String.valueOf(kb.getMinor())
                                    );
                            LogUtils.err(TAG,"building_changed:" + bu.string());
                            switchBuilding(bu);
                            return;
                        } else {
                            //해당 빌딩의 비콘 보유시킴
                            mBeaconOfBuilding = KsDbWorker.getBeaconInfoOfCurrentBuildingCode(getApplicationContext(), kb.getBuildingCode());
                            StringBuilder sb = new StringBuilder("beacon_scaned=").append("\n");
                            for (BeaconUuid bu : mBeaconOfBuilding) {
                                sb.append("\t" + bu.string()).append("\n");
                            }
                            LogUtils.err(TAG, sb.toString());
                        }

                        if (kb.getRssi() > -1) {//RSSI 비정상일 때
                            return;
                        }


                        BeaconUuid fromDb = checkMyBeacon(
                                kb.getUuid(),
                                String.valueOf(kb.getMajor()),
                                String.valueOf(kb.getMinor()));

                        setGodoList(fromDb);

                        mCurrentBeacon = checkGodo();
                        mNotScanedCount = 0;
                        mCurrentBeacon.setFloorSettingTime(System.currentTimeMillis());//세팅 시간 설정
                        sendServiceLog("current_beacon=uid" + mCurrentBeacon.getUuid() +
                            " major=" + mCurrentBeacon.getMajorValue() + " minor=" + mCurrentBeacon.getMinorValue() +
                            " alti=" + mCurrentBeacon.getAltitude());
                        sendBeaconLog(mCurrentBeacon.deepCopy(), 0, "take 1st beacon", null);
                        mReddotStack.clear();
                        mReddotStack.add(System.currentTimeMillis());//레드닷 시간
                        return;
                    }
                } else {
                    mNotScanedCount++;
                    /*if (mCurrentBeacon != null) {
                        long setTime = mCurrentBeacon.getFloorSettingTime();
                        long curTime = System.currentTimeMillis();
                        if (curTime - setTime > RESET_LIMIT) {
                            mCurrentBeacon = null;
                            mReddotStack.clear();
                        }
                    }*/
                }
                if (mCurrentBeacon != null) {
                    long setTime = mCurrentBeacon.getFloorSettingTime();
                    long curTime = System.currentTimeMillis();
                    if (curTime - setTime > RESET_LIMIT) {
                        if( mCurrentBeacon != null ) {
                            mCurrentBeacon = null;
                            sendBeaconLog(null, 0, "curTime - setTime > RESET_LIMIT (2)", null);
                        }
                        mReddotStack.clear();
                        mNotScanedCount = 0;
                    }
                }
            }catch(RuntimeException e){

            }
        }
    }

    /**
     * beacon manager에 감시해야 할 비콘 등록
     */
    private void setRegions(){
        try {
            mApplication.startRanging(mReigon);
            //mBeaconManager.startRangingBeaconsInRegion(mReigon);
            //mBeaconManager.setBackgroundMode(true);
            //setBackgroundScanPeriod((1000*60), 1000L);
            //mRegionBootstrap = new RegionBootstrap(this, region);
            LogUtils.err(TAG, "beacon ranging posted!");
            LogUtils.err(TAG, "beacon_ranging_post_success");
        } catch (RemoteException e) {

        }
    }

    /*private void setBackgroundScanPeriod(long scanPeriod, long betweenScanPeriod){
        mBeaconManager.setBackgroundScanPeriod(scanPeriod);
        mBeaconManager.setBackgroundBetweenScanPeriod(betweenScanPeriod);
        try {
            mBeaconManager.updateScanPeriods();
        } catch (RemoteException e) {
            String s = Log.getStackTraceString(e);
            sendServiceLog("beacon listener post error:" + s);

            FirebaseCrash.log(s);
            LogUtils.err(TAG, Log.getStackTraceString(e));
        }
    }*/

    /*@Override
    public void didEnterRegion(Region region) {
        LogUtils.err(TAG, "beacon region enter : didRangeBeaconsInRegion");
        *//*setBackgroundScanPeriod(1000L, 500L);*//*
    }

    @Override
    public void didExitRegion(Region region) {
        LogUtils.err(TAG, "beacon region exit : didRangeBeaconsInRegion");
        *//*setBackgroundScanPeriod((1000*60), 1000L);*//*
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }*/

    private void setGodoList(BeaconUuid kb){

        double gap = mCurrentAltitude - kb.getAltitudeToDouble();
        for( int i = 0, k = mBeaconOfBuilding.size(); i < k; i++){
            BeaconUuid bc = mBeaconOfBuilding.get(i);
            bc.setAltitude(String.valueOf(bc.getAltitudeToDouble() + gap));
        }
        int count = mBeaconOfBuilding.size();
        mReddotStack.clear();
        /*for( int i = 0; i < count; i++){
            mReddotStack.add(System.currentTimeMillis());
        }*/

        double lastGap = mBeaconOfBuilding.get(count - 1).getAltitudeToDouble() - mBeaconOfBuilding.get(count - 2).getAltitudeToDouble();
        for( int i = 0; i < count; i++ ){//앞에 붙는 놈
            BeaconUuid prev = mBeaconOfBuilding.get(0);
            BeaconUuid fc = prev.deepCopy();
            fc.setBeaconSeq(String.valueOf(prev.getBeaconSeqToInt()-1));
            fc.setAltitude(String.valueOf(prev.getAltitudeToDouble()-lastGap));
            fc.setMinorValue(String.valueOf(prev.getMinorValueToInt()-1));
            fc.setInstallFloor(String.valueOf(prev.getInstallFloorToInt() - 1));
            mBeaconOfBuilding.add(0, fc);
        }

        for( int i = 0; i < count; i++ ){//뒤에 붙는 놈
            BeaconUuid prev = mBeaconOfBuilding.get(mBeaconOfBuilding.size()-1);
            BeaconUuid fc = prev.deepCopy();
            fc.setBeaconSeq(String.valueOf(prev.getBeaconSeqToInt()+1));
            fc.setAltitude(String.valueOf(prev.getAltitudeToDouble()+lastGap));
            fc.setMinorValue(String.valueOf(prev.getMinorValueToInt()+1));
            fc.setInstallFloor(String.valueOf(prev.getInstallFloorToInt() + 1));
            mBeaconOfBuilding.add(fc);

        }
        StringBuilder sb = new StringBuilder("godo_list=\n");
        for(BeaconUuid bu : mBeaconOfBuilding) {
            sb.append("\t" + bu.string()).append("\n");
            //LogUtils.err(TAG, "appended element=" + bu.string());
        }
        LogUtils.err(TAG, "two minute:\n" + sb.toString());
        //sendServiceLog(sb.toString());
        /*for( BeaconUuid bu : mBeaconOfBuilding){
            LogUtils.err(TAG, "appended element=" + bu.string());
        }*/
    }


//    /**
//     * 진입한 index
//     * @param bu
//     * @return
//     */
//    private int findoufBeaconIndex(KsBeacon bu){
//        for( int i = 0, k = mBeaconList.size(); i < k; i++){
//            BeaconUuid uu = mBeaconList.get(i);
//            if( uu.getBeaconSeq().equals(String.valueOf(bu.getBeaconSeq()))){
//                return i;
//            }
//        }
//        return -1;
//    }

//    /**
//     * 현재 회원이 설정한 빌딩에 설치된 모든 비콘 정보 로드
//     */
//    private void loadBeaconListOfCurrentBuinding(){
//        mBeaconList.clear();
//        KsDbWorker.getBeaconsByBuildingCode(getApplicationContext(), KUtil.getBuildingCode(), mBeaconList);
//        mGodo = new double[mBeaconList.size()];
//        for( int i = 0; i < mGodo.length; i++ ){
//            BeaconUuid bu = mBeaconList.get(i);
//            mGodo[i] = bu.getAltitudeToDouble();
//        }
//        double lastGap = mGodo[mGodo.length-1] - mGodo[mGodo.length-2];
//
//        double[] dummyGodo = new double[mGodo.length * 3];
//        for( int i = 0; i < dummyGodo.length; i++){
//            if( i <= mGodo.length - 1) {
//                dummyGodo[i] = mGodo[i];
//            }else{
//                dummyGodo[i] = dummyGodo[i -1] + lastGap;
//            }
//        }
//        mGodo = dummyGodo;
//        mReddotStack.clear();
//    }

    /**
     * Ranging 된 비콘 중에서 사용자가 등록한 비콘만 추출
     * @param beaconList
     * @return
     */
    private synchronized List<KsBeacon> pickupMyBeaconByBuildingCode(Collection<Beacon> beaconList){
        List<KsBeacon> list = new ArrayList<>();
        String buildCode = KUtil.getBuildingCode();
        for( Beacon beacon : beaconList ){
            BeaconUuid beaconUuid = checkMyBeaconByBuildingCode(
                    beacon.getId1().toString().toUpperCase(),
                    beacon.getId2().toString(),
                    beacon.getId3().toString(), buildCode);
            if(beaconUuid != null){
                KsBeacon kb = new KsBeacon()
                        .setUuid(beacon.getId1().toString().toUpperCase())
                        .setMajor(beacon.getId2().toString())
                        .setMinor(beacon.getId3().toString())
                        .setRssi(beacon.getRssi())
                        .setBeaconSeq(Integer.valueOf(beaconUuid.getBeaconSeq()))
                        .setBuildingCode(beaconUuid.getBuildCode())
                        .setAltitude(beaconUuid.getAltitudeToDouble());
                list.add(kb);
            }
        }
        Collections.sort(list);
        Collections.reverse(list);

        return list;
    }
    /**
     * Ranging 된 비콘 중에서 사용자가 등록한 비콘만 추출
     * @param beaconList
     * @return
     */
    private synchronized List<KsBeacon> pickupMyBeacon(Collection<Beacon> beaconList){
        List<KsBeacon> list = new ArrayList<>();
        for( Beacon beacon : beaconList ){
            BeaconUuid beaconUuid = checkMyBeacon(
                    beacon.getId1().toString().toUpperCase(),
                    beacon.getId2().toString(),
                    beacon.getId3().toString());
            /*BeaconUuid beaconUuid = checkMyBeaconByBuildingCode();*/
            if(beaconUuid != null){
                KsBeacon kb = new KsBeacon()
                        .setUuid(beacon.getId1().toString().toUpperCase())
                        .setMajor(beacon.getId2().toString())
                        .setMinor(beacon.getId3().toString())
                        .setRssi(beacon.getRssi())
                        .setBeaconSeq(Integer.valueOf(beaconUuid.getBeaconSeq()))
                        .setBuildingCode(beaconUuid.getBuildCode())
                        .setAltitude(beaconUuid.getAltitudeToDouble());
                list.add(kb);
            }
        }
        Collections.sort(list);
        Collections.reverse(list);

        return list;
    }

    private BeaconUuid checkMyBeaconByBuildingCode(String uuid, String major, String minor, String buildCode){
        BeaconUuid bu = KsDbWorker.getBeaconByUuidByBuildingCode(
                getApplicationContext(), uuid, major, minor, buildCode);
        return bu;
    }

    private BeaconUuid checkMyBeacon(String uuid, String major, String minor){
        BeaconUuid bu = KsDbWorker.getBeaconByUuid(
                getApplicationContext(), uuid, major, minor);
        return bu;
    }

    private BeaconUuid checkMyBeaconByBuildingCode(){
        String buildCode = KUtil.getBuildingCode();
        BeaconUuid bu = KsDbWorker.getBeaconByBuildingCode(getApplicationContext(), buildCode);
        return bu;
    }

    private void toast(String msg){
        new Handler(getMainLooper()).post(()->{
            Toast.makeText(getBaseContext(),
                    msg,
                    Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 비콘이 사용자가 설정한 빌딩에 설치된 비콘인가 검사.
     * @param takenBuildingCode
     * @return true-맞음.
     */
    private boolean isEqualsCurrentBuilding(String takenBuildingCode){
        return KUtil.getBuildingCode().equals(takenBuildingCode);
    }

    /**
     * 계단을 올라간 데이터를 서버로 전송
     * @param beaconUuid 매칭된 비콘
     * @param isReddot 레드닷 여부
     * @param currentAltitude checkGodo()시 고도
     * @param floorDiff 로그 전송을 위한 층간 차이
     */
    private void sendDataToServer(BeaconUuid beaconUuid, boolean isReddot, double currentAltitude, final int floorDiff){
        String token = KUtil.getUserToken();
        String buildCode = KUtil.getBuildingCode();
        if(StringUtil.isEmptyOrWhiteSpace(token) || StringUtil.isEmptyOrWhiteSpace(buildCode)){
            return;
        }
        //return;
        try {
            final Map<String, Object> query = beaconUuid.getQueryMap();
            query.put("alti", currentAltitude);//증 결정시 고도 차이를 검토하기 위해 전송하는 것.
            if (isReddot) {
                //LogUtils.err(TAG, "beacon current-floor just up=mintime:" + mMinFloorEnterTime);
                query.put("start_time", mReddotStack.get(0));
                query.put("end_time", new Date(System.currentTimeMillis()).getTime());
            }
            final String localTime = DateUtil.currentDate("yyyyMMddHHmmss");
            if(NetworkConnectivityReceiver.isConnected(getApplicationContext())) {
                Call<BeaconUuid> call =
                        new DefaultRestClient<App>(getApplicationContext())
                                .getClient(App.class).sendStairGoUpAmountToServer(query);
                final String goupSentTime = DateUtil.currentDate("yyyyMMdd HHmmss");

                call.enqueue(new Callback<BeaconUuid>() {
                    @Override
                    public void onResponse(Call<BeaconUuid> call, Response<BeaconUuid> response) {
                        if (isReddot) {
                            mReddotStack.clear();
                        }
                        LogUtils.e(TAG, "success-data response raw:" + response.raw().toString());
                        if (response.isSuccessful()) {
                            LogUtils.e(TAG, "success-data response body:" + response.body().string());
                            BeaconUuid uuid = response.body();
                            if (uuid != null && uuid.isSuccess()) {
                                broadcastServerResult(uuid);
                            }else {
                                saveGoUpDataToLocalDb(query, localTime);
                            }
                            sendBeaconLog(beaconUuid, floorDiff, "go up ", goupSentTime);//층간이동 전송 성공하면 로그 전송
                        }else {
                            saveGoUpDataToLocalDb(query, localTime);
                        }
                    }

                    @Override
                    public void onFailure(Call<BeaconUuid> call, Throwable t) {
                        if (isReddot) {
                            mReddotStack.clear();
                        }
                        saveGoUpDataToLocalDb(query, localTime);
                        //sendServiceLog("goup_err:" + Log.getStackTraceString(t));
                    }
                });
            }else{
                saveGoUpDataToLocalDb(query, localTime);
            }
        }catch (Exception e){
            sendServiceLog("goup_err:" + Log.getStackTraceString(e));
        }
    }

    /**
     * 올라간 층수 데이터 서버전송 성공시 데이터 전파
     * @param beaconUuid
     */
    public static void broadcastServerResult(BeaconUuid beaconUuid){
        //LogUtils.err(TAG, beaconUuid.string());
        String prevChar = KUtil.getStringPref(PrefKey.CHARACTER_MAIN, "");
        boolean isMainChracterChanged = !StringUtil.isEquals(prevChar, beaconUuid.getMainCharFilename());
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

    /**
     * 계단 올라간 데이터 서버전송 실패하면 로컬에 저장 후 네트워크가 안정화 되면 전송
     * @param goupData
     */
    private void saveGoUpDataToLocalDb(Map<String, Object> goupData, String localTime){
        goupData.put("app_time", localTime);
        if(KsDbWorker.insertFailData(getApplicationContext(), goupData)){
            BusProvider.instance().post(
                    new KsEvent<Map<String, Object>>()
                            .setType(KsEvent.Type.UPDATE_FLOOR_AMOUNT_FAIL)
                            .setValue(goupData)
                            .setMainCharacterChanged(false));
        }
    }

    /**
     * 활동 건물 정보를 변경한다.
     * @param data
     */
    private void switchBuilding(BeaconUuid data){
        Map<String, Object> query = data.getQueryMap();
        query.put("buildCode", data.getBuildCode());
        query.put("build_code", data.getBuildCode());
        Call<BeaconUuid> call = new DefaultRestClient<App>(getBaseContext())
                .getClient(App.class).changeBuilding(query);
        call.enqueue(new Callback<BeaconUuid>() {
            @Override
            public void onResponse(Call<BeaconUuid> call, Response<BeaconUuid> response) {
                BeaconUuid logo = null;
                if(response.isSuccessful()){
                    logo = response.body();
                    if(logo.isSuccess()){
                        saveLogoAndSwitchDeafultBuilding(logo);
                    }
                }
            }

            @Override
            public void onFailure(Call<BeaconUuid> call, Throwable t) {
                LogUtils.err(call.toString(), t);
            }
        });
    }

    /**
     * 서버에서 받은 로고 정보를 저장 후 후
     * @param data
     */
    private void saveLogoAndSwitchDeafultBuilding(BeaconUuid data){
        Logo logo = data.getCompanyLogo();
        LogUtils.err(TAG, logo.string());
        Pref.instance().saveStringValue(PrefKey.BUILDING_CODE, logo.getBuildingCode());
        Pref.instance().saveStringValue(PrefKey.COMPANY_LOGO, logo.getLogoUrl());
        Pref.instance().saveStringValue(PrefKey.COMPANY_COLOR, logo.getHexColor());
        if(data.getBeaconUuidList() != null) {
            KsDbWorker.replaceUuid(getBaseContext(), data.getBeaconUuidList());//UUID 교체
//            loadBeaconListOfCurrentBuinding();//ㅅㅐ로 받은 비콘 정보 획득 보유시킴
        }
        BusProvider.instance().post(
                new KsEvent<Object>().setType(KsEvent.Type.CHANGE_COLOR)
        );
        BusProvider.instance().post(
                new KsEvent<>().setType(KsEvent.Type.MAIN_REFRESH)
        );
    }

    private void sendToMonitoringView(List<KsBeacon> list){
        Intent it = new Intent(Env.Action.BEACON_MONITORING_ACTION.action());
        Bundle b = new Bundle();
        b.putSerializable("_KS_BEACON_", (ArrayList<KsBeacon>)list);
        it.putExtras(b);
        sendBroadcast(it);
    }

    private void sendMsg(){
        if(Env.Bool.SHOW_TEST_VIEW.getValue() && mInfoTxt != null) {
            Intent it = new Intent(Env.Action.BEACON_BEACON_TEST_INTO_ACTION.action());
            Bundle b = new Bundle();
            b.putSerializable("_MSG_", mInfoTxt);
            it.putExtras(b);
            sendBroadcast(it);
        }
    }

    /** 서버로 로그 전송 */
    public synchronized void sendBeaconLog(BeaconUuid bu, int goupAmt, String desc, String logTime){
        if(bu == null){
            return;
        }
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("alti", mDummAlt);
        query.put("device_model", AUtil.getDeviceModel() +"(" + AUtil.getOsReleaseVersion() +")");
        query.put("app_ver", AUtil.getVersionCode(this, getPackageName()));
        query.put("app_desc", "(" + bu.getOriginalBeaconSeq() + ") " + desc + " notscancnt=" + mNotScanedCount +
                " ble=" + KUtil.isBluetoothOn() + " gps=" + AUtil.isLocationEnabled(this) +
                " app_time=" + (logTime == null ? DateUtil.currentDate("yyyyMMdd HHmmss") : logTime));
        if(bu != null) {
            query.put("beacon_seq", bu.getBeaconSeq());
            query.put("minor_value", bu.getMinorValue());
            query.put("build_seq", bu.getBuildSeq());
            query.put("goup_amt", goupAmt);
            query.put("godo_list", Arrays.toString(mGodoList.toArray(new String[mGodoList.size()])));
            query.put("godo_index", mGodoIndex);
        }else{
            query.put("beacon_seq", "0");
            query.put("minor_value", "0");
            query.put("build_seq", "0");
            query.put("goup_amt", "-100");
            query.put("godo_list", "0");
            query.put("godo_index", "0");
        }

        Call<ResponseBase> call =
                new DefaultRestClient<UserService>(this).getClient(UserService.class).sendBeaconLog(query);
        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, "service log response:" +response.raw().toString());
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                LogUtils.err(TAG, "service log fail:" + Log.getStackTraceString(t));
            }
        });
    }
    /** 서버로 로그 전송 */
    public void sendServiceLog(String msg){
        /*if(!Env.Bool.SERVER_LOG_ENABLE.getValue()){
            return;
        }

        BeaconLog log = new BeaconLog(getApplicationContext());
        log.setUserId(KUtil.getStringPref(PrefKey.USER_ID, "none"));
        log.setBuildingCode(KUtil.getBuildingCode());
        log.setMessage(msg);
        //log.setStackData(takeStackPosition());
        String sourcePosition = takeStackPosition();
        log.setMessage(sourcePosition + ":" + log.string());
        LogUtils.err(TAG, "server log:"+ log.string());

        Map<String, Object> query = log.createQueryMap();

        Call<ResponseBase> call =
                new DefaultRestClient<App>(log.getContext()).getClient(App.class).sendServiceLog(query);
        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, "service log response:" +response.raw().toString());
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                LogUtils.err(TAG, "service log fail:" + Log.getStackTraceString(t));
            }
        });*/
    }

    public String takeStackPosition(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        String name = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        String lineNumber = stackTraceElement.getLineNumber() + "";
        return name +":[" + lineNumber + "]";
//        return className.substring(className.lastIndexOf(".") + 1 ) + ":" + name +":[" + lineNumber + "]";
    }

    private static BeaconRagingInRegionService mInstance;
    public static BeaconRagingInRegionService getServiceIsntance(){
        return mInstance;
    }

    /** 배터리 최적화 대상 확인 */
    private void checkBatteryWhiteList(){
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isWhiteListing = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getPackageName());
            LogUtils.err(TAG, "white list=" + isWhiteListing);
            LogUtils.err(TAG, "battery_white_list=" + isWhiteListing);
            /*if(!isWhiteListing) {
                startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
            }*/
        }
        boolean bluetoothEnabaled = KUtil.isBluetoothOn();
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = manager!= null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        LogUtils.err(TAG, "bluetooth_gps_enabaled=bluetooth:" + bluetoothEnabaled + " gps:" + gpsEnabled);
    }

    /** 비콘 서비스 재시작 */
    private void restartBeaconService(){
        mServiceStartTime = System.currentTimeMillis();
        String topActivity = AUtil.getTopActivity(this);

        if (!"kr.co.photointerior.kosw.ui.MainActivity".equals(topActivity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0 이상이면
                /*startService(new Intent(this, BeaconScanResumeService.class));
                stopSelf();*/
                //Intent dialogIntent = new Intent(this, MainActivity.class);
                Intent inten = new Intent(this, MainActivity.class);
                inten.putExtra("_FROM_", "SERVICE");
                inten.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(inten);
            }
        }

    }

    /** 비콘 스캔 제한시간 도달시 서비스 종료 */
    @Override
    public void stopServices(){
        if(isTopActivityIsMainActivity()){//Main Activity destroy를 통한 서비스 종료
            sendBroadcast(new Intent(Env.Action.EXIT_ACTION.action()));
        }else {
            stopService(new Intent(this, StepSensorService.class));
            stopSelf();
        }
    }
}
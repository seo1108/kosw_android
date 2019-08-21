package kr.co.photointerior.kosw.global;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.RemoteException;

import com.kakao.auth.KakaoSDK;
import com.kakao.util.helper.log.Logger;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.List;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.conf.AppCondition;
import kr.co.photointerior.kosw.conf.AppInitializer;
import kr.co.photointerior.kosw.rest.model.Building;
import kr.co.photointerior.kosw.rest.model.Company;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.service.beacon.AltitudeManager;
import kr.co.photointerior.kosw.service.beacon.BLocationManager;
import kr.co.photointerior.kosw.service.beacon.BeaconRagingInRegionService;
import kr.co.photointerior.kosw.service.fcm.KoswFcmMessageWorker;
import kr.co.photointerior.kosw.service.fcm.KoswFcmTokenWorker;
import kr.co.photointerior.kosw.service.fcm.Push;
import kr.co.photointerior.kosw.social.kakao.KakaoSDKAdapter;

import static okhttp3.internal.Internal.instance;

/**
 * 계단왕 Application class.
 */
public class KoswApp extends Application{
    private String TAG = "KoswApp";
    /** 비콘 서비스가 바인드 되었는가 여부 */
    private boolean beaconBinded;
    private BeaconManager mBeaconManager;
    private BeaconParser mBeaconParser;
    private String mBeaconLayout = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    private AltitudeManager altitudeManager;
    private BeaconConsumer mConsumer;
    /** 좌표 서비스 */
    private BLocationManager mBLocationManager ;
    public BLocationManager getmBLocationManager() {
        return mBLocationManager;
    }

    public Building mBuilding  = null ;
    public Company mCompany = null ;

    public Boolean isInit = false ;
    public Boolean isPop = false ;

    public Push push = null ;

    private static volatile KoswApp instance = null;
    private static volatile Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();

        instance = this;

        KakaoSDK.init(new KakaoSDKAdapter());
        prepareBeaconManager();
    }

    /**
     * 애플리케이션 종료시 singleton 어플리케이션 객체 초기화한다.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }

    public void prepareBeaconManager(){
        //mBeaconManager = BeaconManager.getInstanceForApplication(this);
        //mBeaconParser = new BeaconParser().setBeaconLayout(mBeaconLayout);
        //altitudeManager = new AltitudeManager(this);
        mBLocationManager = new BLocationManager(this) ;

    }

    public static Activity getCurrentActivity() {
        Logger.d("++ currentActivity : " + (currentActivity != null ? currentActivity.getClass().getSimpleName() : ""));
        return currentActivity;
    }

    // Activity가 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
    public static void setCurrentActivity(Activity currentActivity) {
        KoswApp.currentActivity = currentActivity;
    }

    /**
     * singleton 애플리케이션 객체를 얻는다.
     * @return singleton 애플리케이션 객체
     */
    public static KoswApp getGlobalApplicationInstance() {
        if(instance == null)
            throw new IllegalStateException();
        return instance;
    }

    /**
     * @return 비콘 서비스가 바인드 되었는가 여부
     */
    public boolean isBeaconBound(){
        return beaconBinded;
    }

    /**
     * 비콘 서비스 시작
     * @param consumer
     */
    public void bindBeaconService(BeaconConsumer consumer){
        if(!isBeaconBound()){
            mBeaconManager.setEnableScheduledScanJobs(false);//forgroud service일 경우 false
            mConsumer = consumer;
            mBeaconManager.bind(consumer);
            //mBeaconManager.bind(this);
            mBeaconManager.getBeaconParsers().add(mBeaconParser);

            BeaconManager.setDebug(Env.Bool.BEACON_DEBUG.getValue());
            mBeaconManager.setRegionStatePersistenceEnabled(true);
            mBeaconManager.setBackgroundMode(true);
            setScanPeriods();
            beaconBinded = true;
        }
    }

    private void setScanPeriods(){
        setBeaconScanPeriods(1000L, 0L, 1000L, 0L);
    }

    public void setBeaconScanPeriods(long foreground, long foregroundBetween, long background, long backgroundBetween){
        mBeaconManager.setForegroundScanPeriod(foreground);
        mBeaconManager.setForegroundBetweenScanPeriod(foregroundBetween);
        mBeaconManager.setBackgroundScanPeriod(background);
        mBeaconManager.setBackgroundBetweenScanPeriod(backgroundBetween);
        try {
            mBeaconManager.updateScanPeriods();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isBeaconManagerBound(){
        return mBeaconManager.isBound(mConsumer);
    }

    public void setModeBackground(boolean background){
        mBeaconManager.setBackgroundMode(background);
    }

    public void setRangeNotifier(RangeNotifier notifier){
        mBeaconManager.setRangeNotifier(notifier);
    }

    public void startRanging(Region region)throws RemoteException{
        mBeaconManager.startRangingBeaconsInRegion(region);
    }

    public void stopRanging(Region region)throws RemoteException{
        mBeaconManager.stopRangingBeaconsInRegion(region);
    }

    public void unbindBeaconService(BeaconConsumer consumer){
        if(isBeaconBound()) {
            mBeaconManager.unbind(consumer);
            beaconBinded = false;
        }
    }

    public BeaconManager getBeaconManager() {
        return mBeaconManager;
    }


    public AltitudeManager getAltitudeManager() {
        /*if(altitudeManager != null){
            altitudeManager.stopMeasure();
            altitudeManager = null;
        }
        altitudeManager = new AltitudeManager(this);*/
        return altitudeManager;
    }

    public void bindRagingServiceToAltitudeManager(BeaconRagingInRegionService beaconRagingInRegionService){
       // altitudeManager.setBeaconRagingService(beaconRagingInRegionService);
    }


//    public void bindRagingServiceToAltitudeManager(KoswBeaconManager manager){
//        altitudeManager.setBeaconRagingService(manager);
//    }

//    @Override
//    public void onBeaconServiceConnect() {
//        mKoswBeaconManager.setRegions();
//    }

    /** Application 환경 초기화 */
    private void initApplication(){
        AppCondition.Builder builder =
                new AppCondition.Builder()
                        .appMode(Env.APP_MODE)
                        .useRestApi(true)
                        .useSSL(false)
                        .baseUrl(Env.Url.URL_API.url())
                        .byteBufferSize(4096)
                        .diskPath(Env.EXTERNAL_PATH)
                        .fcmTokenWorker(KoswFcmTokenWorker.class)
                        .fcmMessageWorker(KoswFcmMessageWorker.class)
                        .logPrefix(Env.LOG_PREFIX);

        AppInitializer.init(getApplicationContext(), builder.build());

        int bglist[] = {R.color.color_FFEE96BF, R.color.color_FFF28922, R.color.color_FFF5DF13,R.color.color_A937AE41, R.color.color_FF35BAEC,
                R.color.color_FF223285,R.color.color_592885,R.color.color_FF888A8B,R.color.color_FF52341C,R.color.color_FF201F20} ;
        DataHolder.instance().setBgColors(bglist);

    }

}

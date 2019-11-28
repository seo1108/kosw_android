package kr.co.photointerior.kosw.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.App;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.AppVersion;
import kr.co.photointerior.kosw.service.beacon.BLocationManager;
import kr.co.photointerior.kosw.ui.dialog.DialogUpdate;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.AbstractAcceptor;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 스플래시 화면
 */
public class SplashActivity extends BaseUserActivity implements EasyPermissions.PermissionCallbacks {
    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
    final static int REQUEST_CHECK_SETTINGS = 200;

    private String TAG = LogUtils.makeLogTag(SplashActivity.class);
    private Dialog mUpdateDialog;
    private int  gifs[] = {R.drawable.intro_01,R.drawable.intro_02,R.drawable.intro_03,R.drawable.intro_04,
            R.drawable.intro_05,R.drawable.intro_06,R.drawable.intro_07,R.drawable.intro_08} ;

    private BLocationManager mBLocationManager ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        changeStatusBarColor(getCompanyColor());
        findViews();
        loadSplashGif();
        attachEvents();
        setInitialData();

        mBLocationManager = new BLocationManager(this) ;
        Pref.instance().saveStringValue(PrefKey.BUILDING_LAT, "");
        Pref.instance().saveStringValue(PrefKey.BUILDING_LNG, "");


        if(!EasyPermissions.hasPermissions(this, Env.PERMISSIONS)){
            EasyPermissions.requestPermissions(this, getString(R.string.txt_permission_desc), Env.REQ_CODE[3],
                    Env.PERMISSIONS);
        }else {
            //Gps start
            showSpinner("");
            versionCheck();
        }


    }

    /**
     * Loads splash gif image.
     */
    private void loadSplashGif(){
        ImageView splash = getView(R.id.img_splash);
        Random random = new Random() ;
        int num = random.nextInt(8)  ;
        //Glide.with(this).load(R.drawable.img_splash_g).thumbnail(.5f).into(splash);
        Glide.with(this).load(gifs[num]).into(splash);
    }

    @Override
    protected void findViews() {}

    @Override
    protected void attachEvents() {}

    @Override
    public void performClick(View view) {}

    /**
     * 앱의 버전 업데이트 여부 검사.
     */
    private void versionCheck(){
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("version", AUtil.getVersionName(getBaseContext(), getPackageName()));
        queryMap = KUtil.getQueryMap(queryMap);

        App service = new DefaultRestClient<App>(getBaseContext()).getClient(App.class);
        Call<AppVersion> call = service.checkUpdate(queryMap);
        call.enqueue(new Callback<AppVersion>() {
            @Override
            public void onResponse(Call<AppVersion> call, Response<AppVersion> response) {
                AppVersion version;
                if(response.isSuccessful()){
                    version = response.body();
                    LogUtils.err(TAG, version.string());
                    if(version.isSuccess()){
                        doNextVersionProcess(version);
                    }else{
                        checkUserIdAndToken();
                    }
                }else{
                    checkUserIdAndToken();
                }
            }

            @Override
            public void onFailure(Call<AppVersion> call, Throwable t) {
                Log.d("DDDDDDDDDDDDDD", t.getMessage());
                toast(R.string.warn_commu_to_server);
                checkUserIdAndToken();
            }
        });
    }

    /**
     * 업데이트 여부 판단하고 분기
     * @param version
     */
    private void doNextVersionProcess(AppVersion version){
        if(version.isUpdated()){
            showAppUpdateMessage(version.getMessage());
        }else{
            checkUserIdAndToken();
        }
    }

    /**
     * 앱 업데이트 공지 팝업 띄움.
     * @param updateMsg 업데이트 메세지
     */
    private void showAppUpdateMessage(String updateMsg){
        if(isFinishing()){
            return;
        }
        Acceptor acceptor = new AbstractAcceptor() {
            @Override
            public void accept() {
                finishAndMoveToMarket();
            }

            @Override
            public void deny() {
                finish();
            }
        };
        mUpdateDialog = new DialogUpdate(this, acceptor, updateMsg);
        mUpdateDialog.setCancelable(false);
        mUpdateDialog.show();
    }

    /**
     * 앱을 종료하고 마켓으로 이동.
     */
    private void finishAndMoveToMarket(){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
        finish();
        overridePendingTransition(0, 0);
    }

    /**
     * 자동 로그인 상태 검사 후 분기
     */
    private void checkUserIdAndToken(){
        if( KUtil.hasUserToken() ){//메인으로 이동
            app.isInit = true  ;
            tryLoginByToken();
        }else{//로그인 화면으로 이동.
            callActivity(LoginActivity.class, true);
        }
    }

    /**
     * 앱에 저장된 회사코드를 기준으로 회사의 로고를 적용.
     */
    @Override
    protected void setInitialData() {
        /*String logo = Pref.instance().getStringValue(PrefKey.COMPANY_LOGO, "");
        String logoUrl = KUtil.getLogoUrl(logo);*/
        ImageView logoView = getView(R.id.img_logo);
        /*//String url = Env.Url.URL_API.url() + "api/user/myLogo?token=" + KUtil.getUserToken();
        String buildingCode = KUtil.getStringPref(PrefKey.BUILDING_CODE, "");*/
        /*String url = Env.Url.URL_API.url() + "api/customer/lookupLogo?buildCode=" + KUtil.getBuildingCode();
        Glide.with(this).load(url).thumbnail(.5f).into(logoView);*/
        Glide.with(this).load(KUtil.getCompanyLogoImgUrl()).thumbnail(.5f).into(logoView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LogUtils.d(TAG,"onPermissionsGranted:" + requestCode + ":" + perms.size());
        versionCheck();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        LogUtils.d(TAG,"onPermissionsDenied:" + requestCode + ":" + perms.size());
        toast(R.string.permission_denied);
        finish();
        /*// (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }*/
    }



    //GPS 설정 체크
    private void chkGpsService() {
        String gps = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        //if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {
        if (!(gps.matches(".*gps.*"))) {
            enableLoc();
            /*// GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("위치 서비스 기능을 설정하셔야 계단왕 서비스를 이용하실 수 있습니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    }).create().show();
            return false;*/

        } else {
            //return true;
        }
    }


    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(SplashActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error","Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().

                                status.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (googleApiClient != null) googleApiClient.disconnect();
        } catch (Exception e) { }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == Activity.RESULT_OK) && (requestCode == REQUEST_CHECK_SETTINGS)) {
        } else if ((resultCode == Activity.RESULT_CANCELED) && (requestCode == REQUEST_CHECK_SETTINGS)) {
            finish();
        }
    }
}

package kr.co.photointerior.kosw.ui;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.DefaultCode;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.global.KoswApp;
import kr.co.photointerior.kosw.global.ResultType;
import kr.co.photointerior.kosw.listener.ClickListener;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.RestManager;
import kr.co.photointerior.kosw.rest.api.App;
import kr.co.photointerior.kosw.rest.api.CustomerService;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.AppUser;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Beacon;
import kr.co.photointerior.kosw.rest.model.Building;
import kr.co.photointerior.kosw.rest.model.BuildingStair;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.Logo;
import kr.co.photointerior.kosw.rest.model.MapBuildingUser;
import kr.co.photointerior.kosw.rest.model.Place;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.service.beacon.BLocationManager;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.Log;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 최초 실행시 회사코드(계단코드)를 입렿 받는 화면
 */
public class GPSAcceptActivity extends BaseUserActivity {
        //implements EasyPermissions.PermissionCallbacks, BLocationManager.DelegateFindLocation {
    private String TAG = LogUtils.makeLogTag(GPSAcceptActivity.class);
    public  Location mLocation  ;
    private Place mPlace = null ;
    private Building mBuilding = null ;
    private BuildingStair mBuildingStair = null ;
    private Beacon mBeacon = null ;
    private MapBuildingUser mMapBuildingUser = null ;
    private Boolean isSelectMode ;

    private static  int CUST_SEQ = DefaultCode.CUST_SEQ.getValue();
    private static  int ADMIN_SEQ = DefaultCode.ADMIN_SEQ.getValue() ;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_accept);

        isSelectMode = getIntent().getBooleanExtra("selectmode",false) ;

        app.isPop = true ;

        String lat = Pref.instance().getStringValue(PrefKey.BUILDING_LAT,"") ;
        String lng = Pref.instance().getStringValue(PrefKey.BUILDING_LAT,"") ;

        changeStatusBarColor(getCompanyColor());
        findViews();
        attachEvents();
        //doNextStep();

        //loadRecentBuilding() ;

        //showSpinner("");
        // 측정된 GPS 좌표가 있으면


        if (lat != "" && lng != "") {
            loadRecentBuilding() ;
        } else {

//            if (!EasyPermissions.hasPermissions(this, Env.PERMISSIONS)) {
//                EasyPermissions.requestPermissions(this, getString(R.string.txt_permission_desc), Env.REQ_CODE[3],
//                        Env.PERMISSIONS);
//            } else {
//                //Gps start
//                app.getmBLocationManager().registerLocationUpdates();
//                app.getmBLocationManager().mDelegateFindLocation = this;
//            }
        }


    }

    private  void loadRecentBuilding() {


        String s_name = Pref.instance().getStringValue(PrefKey.BUILDING_NAME, "");
        String s_addr = Pref.instance().getStringValue(PrefKey.BUILDING_ADDR, "");
        String s_lat = Pref.instance().getStringValue(PrefKey.BUILDING_LAT, "");
        String s_lng = Pref.instance().getStringValue(PrefKey.BUILDING_LNG, "");
        String s_id = Pref.instance().getStringValue(PrefKey.BUILDING_ID, "");

//        if ( StringUtil.isEmpty(s_lat) || StringUtil.isEmpty(s_lng) ||  s_lat.equals("null")  || s_lng.equals("null") ) {
////            app.getmBLocationManager().registerLocationUpdates();
////            app.getmBLocationManager().mDelegateFindLocation = this ;
////
////            return;
////        }
        lookupRecentBuilding(s_lat,s_lng);

    }

    /**
     * 500미터 이내 최근 가장 많이 오른  건물
     */
    private void lookupRecentBuilding(final String lat , final String lng  ){
        // 수동 건물 선택모드이면 리턴
        if (isSelectMode) {
            return ;
        }

        AppUserBase user =  DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("lat", lat);
        query.put("lng", lng);
        query.put("user_seq", user.getUser_seq() );
        Call<Building> call = new DefaultRestClient<CustomerService>(getBaseContext())
                .getClient(CustomerService.class).selectRecentBuilding(query);
        call.enqueue(new Callback<Building>() {
            @Override
            public void onResponse(Call<Building> call, Response<Building> response) {
                //closeSpinner();
                Building building = null;
                if(response.isSuccessful()){
                    building = response.body();
                    if(building.isSuccess()){

                        building.setBuildingName("도원빌딩");
                        building.setBuildingAddr("대한민국 서울특별시 마포구 성산동");
                        building.setLatitude(37.5666447);
                        building.setLongitude(126.9122815);
                        building.setPlace_id("ChIJtwmC7eGYfDURUWALZJGt1bE");
                        building.setBuildingSeq("118");
                        building.setBuildingCode("100001");


                       Place place = new Place() ;
                        place.name = building.getBuildingName() ;
                        place.vicinity = building.getBuildingAddr() ;
                        place.lat = Double.valueOf(building.getLatitude()) ;
                        place.lng = Double.valueOf(building.getLongitude()) ;
                        place.place_id = building.getPlace_id() ;


                        mPlace = place ;

                        getTextView(R.id.tv_address).setText(mPlace.name);
                        checkCode();

                    }else {
                        //showWarn(R.id.input_warn, building.getResponseMessage());
                    }
                }else{
                    //showWarn(R.id.input_warn,  building.getResponseMessage());
                }
            }

            @Override
            public void onFailure(Call<Building> call, Throwable t) {
                //closeSpinner();
                LogUtils.err(call.toString(), t);
                //showWarn(R.id.input_warn, R.string.warn_code_error);
                //callActivity(SplashActivity.class, true);
            }
        });

    }


    /**
     * 저장된 회사코드, 로고 URL, 칼라값 존재 유무에 따라 화면분기
     */
    private void doNextStep(){
        String code = Pref.instance().getStringValue(PrefKey.BUILDING_ADDR, "");
        if(!StringUtil.hasEmptyOrWhiteSpace(code)){
           lookupCompanyCode(code);
        }
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void attachEvents() {
        View.OnClickListener clickListener = new ClickListener(this);
        getView(R.id.btn_execute).setOnClickListener(clickListener);
        getView(R.id.btn_help_floor_code).setOnClickListener(clickListener);
        getEditText(R.id.input_code).setOnEditorActionListener(mEditorAtionListener);
        getView(R.id.btn_gps).setOnClickListener(clickListener);
        getView(R.id.btn_map).setOnClickListener(clickListener);
        getView(R.id.tv_address).setOnClickListener(clickListener);
    }

    @Override
    public void performClick(View view) {

        int id = view.getId();
        if(id == R.id.btn_execute){
            checkCode();
        }else if(id == R.id.btn_gps){
            Intent intent = new Intent(this,BuildingSelectActivity.class) ;
            intent.putExtra("loc",mLocation) ;
            startActivityForResult (intent, ResultType.BUILDING_SELECT.getValue());
        }else if(id == R.id.btn_map  || id == R.id.tv_address  ){
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try {
                showSpinner("");
                startActivityForResult(builder.build(this),PLACE_AUTOCOMPLETE_REQUEST_CODE );
            } catch (GooglePlayServicesRepairableException e) {
                closeSpinner();
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                closeSpinner();
                e.printStackTrace();
            }
        }else{
            //openWebBrowser(Env.UrlPath.STAIR_CODE.url(), false);
            Bundle bun = new Bundle();
            bun.putString("_WHERE_IS_", "CODE_INPUT");
            callActivity(HelpActivity.class, bun, false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("888888888888888888888", isSelectMode + "___");
        closeSpinner();

        if (requestCode == ResultType.BUILDING_SELECT.getValue()) {
            hideWarn(R.id.input_warn);

            if (resultCode == RESULT_OK) {

                mPlace = (Place) data.getSerializableExtra("place") ;
                if (mPlace != null) {
                    getTextView(R.id.tv_address).setText(mPlace.name);
                }
            } else {
                mPlace = null ;
                getTextView(R.id.tv_address).setText("건물을 선택해주세요.");
            }
        }

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                com.google.android.gms.location.places.Place place = PlaceAutocomplete.getPlace(this, data);
                //String toastMsg = String.format("Place: %s", place.getName());
                //Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                Geocoder geocoder = new Geocoder(this) ;
                mPlace = new Place() ;

                List<Address> addrList = null;
                try {
                    addrList = geocoder.getFromLocation(place.getLatLng().latitude ,place.getLatLng().longitude,1 ) ;

                    for (Address addr : addrList
                         ) {

                        mPlace.country = addr.getCountryName() ;
                        mPlace.countrycd = addr.getCountryCode() ;
                        if (addr.getLocality() != null) {
                            mPlace.city = addr.getLocality() ;
                        } else {
                            mPlace.city = addr.getAdminArea() ;
                        }
                        if (mPlace.country == null) {
                            mPlace.country = "대한민국" ;
                        }
                        if (mPlace.city == null ) {
                            mPlace.city = "" ;
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                android.util.Log.i(TAG, "Place: " + place.getName());

                mPlace.lat = place.getLatLng().latitude ;
                mPlace.lng = place.getLatLng().longitude ;
                mPlace.name = place.getName().toString() ;
                mPlace.vicinity = place.getAddress().toString() ;
                mPlace.place_id = place.getId() ;
                mPlace.checkDB = false ;

                getTextView(R.id.tv_address).setText(mPlace.name);

                // 좌표로 현위치 선택한경우 무효처리
                List placetypes = place.getPlaceTypes() ;
                if (placetypes.size() > 0) {
                    if  ( (int)placetypes.get(0) == 0 ) {
                        mPlace = null ;
                        getTextView(R.id.tv_address).setText("건물명을 선택해주세요.");
                    }
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                android.util.Log.i(TAG, status.getStatusMessage());
                mPlace = null ;
                getTextView(R.id.tv_address).setText("건물을 선택해주세요.");
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                //mPlace = null ;
                //getTextView(R.id.tv_address).setText("건물을 선택해주세요.");
            }

        }

    }

    /**
     * 입력한 회사코드 유효성 검증
     */
    private void checkCode(){
        String inputCode = getTextFromTextView(R.id.tv_address);
        if(mPlace == null){
            showWarn(R.id.input_warn, "선택된 건물(빌딩)이 없습니다.!") ;
            return;
        }

        addBuilding();

    }

    /**
     * 입력 되었거나 Pref에서 획득한 회사코드로 로고, 칼라 조회
     */
    private void lookupCompanyCode(final String inputCode){
        //showSpinner("");

        Call<Logo> call = new DefaultRestClient<App>(getBaseContext())
                .getClient(App.class).lookupCompanyCode(Env.DEVICE_TYPE, inputCode);
        call.enqueue(new Callback<Logo>() {
            @Override
            public void onResponse(Call<Logo> call, Response<Logo> response) {
                //closeSpinner();
                Logo logo = null;
                if(response.isSuccessful()){
                    logo = response.body();
                    if(logo.isSuccess()){
                        saveLogoAndMoveToNext(logo);
                    }else {
                        showWarn(R.id.input_warn, logo.getResponseMessage());
                    }
                }else{
                    showWarn(R.id.input_warn, R.string.warn_code_error);
                }
            }

            @Override
            public void onFailure(Call<Logo> call, Throwable t) {
                //closeSpinner();
                LogUtils.err(call.toString(), t);
                //showWarn(R.id.input_warn, R.string.warn_code_error);
                //callActivity(SplashActivity.class, true);
            }
        });

    }

    private void addBuilding(){

        showSpinner("");

        AppUserBase user = DataHolder.instance().getAppUserBase() ;

        Map<String, Object> query =  new HashMap<>();
        query.put("cust_seq", CUST_SEQ );
        query.put("admin_seq", ADMIN_SEQ  );
        query.put("build_code", 0  );
        query.put("build_name", mPlace.name  );
        query.put("place_id", mPlace.place_id  );
        query.put("build_floor_amt", 0  );
        query.put("build_stair_amt", 0  );
        query.put("build_addr", mPlace.vicinity  );
        query.put("latitude", mPlace.lat  );
        query.put("longitude", mPlace.lng  );
        query.put("country", mPlace.country  );
        query.put("city", mPlace.city  );
        Call<Building> call =
                new DefaultRestClient<CustomerService>(getBaseContext())
                        .getClient(CustomerService.class).getBuilding(query);
        call.enqueue(new Callback<Building>() {
            @Override
            public void onResponse(Call<Building> call, Response<Building> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful() && response.body().isSuccess()) {
                    mBuilding =  response.body() ;
                    android.util.Log.v("kmj",mBuilding.toString()) ;
                    //lookupCompanyCode(mBuilding.getBuildingCode());
                    app.mBuilding = mBuilding ;

                    query.put("build_seq",mBuilding.getBuildingSeq()) ;

                    Pref.instance().saveStringValue(PrefKey.BUILDING_NAME, mBuilding.getBuildingName());
                    Pref.instance().saveStringValue(PrefKey.BUILDING_ADDR, mBuilding.getBuildingAddr());
                    Pref.instance().saveStringValue(PrefKey.BUILDING_LAT, String.valueOf( mPlace.lat) );
                    Pref.instance().saveStringValue(PrefKey.BUILDING_LNG,  String.valueOf(mPlace.lng) );
                    Pref.instance().saveStringValue(PrefKey.BUILDING_ID,  mPlace.place_id );
                    mBuilding.setLatitude(mPlace.lat);
                    mBuilding.setLongitude(mPlace.lng);
                    mBuilding.setPlace_id(mPlace.place_id);

                    addBuildingStair(query);
                  }
            }

            @Override
            public void onFailure(Call<Building> call, Throwable t) {
                closeSpinner();
                mBuilding = null ;
            }
        });
    }

    private void addBuildingStair(Map<String, Object> query){
        query.put("stair_seq", 0 );
        query.put("stair_name", mPlace.name  );
        Call<BuildingStair> call =
                new DefaultRestClient<CustomerService>(getBaseContext())
                        .getClient(CustomerService.class).getBuildingStair(query);
        call.enqueue(new Callback<BuildingStair>() {
            @Override
            public void onResponse(Call<BuildingStair> call, Response<BuildingStair> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful() && response.body().isSuccess()) {
                    mBuildingStair =  response.body() ;
                    query.put("stair_seq",mBuildingStair.getStair_seq()) ;
                    addBeacon(query);
                    //ndroid.util.Log.v("kmj",mBuildingStair.toString()) ;
                    //lookupCompanyCode(mBuilding.getBuildingCode());
                }
            }

            @Override
            public void onFailure(Call<BuildingStair> call, Throwable t) {

                mBuildingStair = null ;
                closeSpinner();

            }
        });
    }

    private void addBeacon(Map<String, Object> query){
        query.put("beacon_seq", 0 );
        query.put("manufac_seq", 2  );
        query.put("model_name", "BEACON"  );
        query.put("beacon_uuid", "1111111111" );
        query.put("major_value", 0 );
        query.put("minor_value", 0 );
        query.put("install_floor", 0 );

        Call<Beacon> call =
                new DefaultRestClient<CustomerService>(getBaseContext())
                        .getClient(CustomerService.class).getBeacon(query);
        call.enqueue(new Callback<Beacon>() {
            @Override
            public void onResponse(Call<Beacon> call, Response<Beacon> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful() && response.body().isSuccess()) {
                    mBeacon =  response.body() ;
                    //android.util.Log.v("kmj",mBuildingStair.toString()) ;
                    //lookupCompanyCode(mBuilding.getBuildingCode());
                    mapBuildingAndUder() ;
                }
            }

            @Override
            public void onFailure(Call<Beacon> call, Throwable t) {
                closeSpinner();

                mBeacon  = null ;
            }
        });
    }

    private MapBuildingUser mapBuildingAndUder() {

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        user.setBuild_seq(mBuilding.getBuildingSeq());
        user.setBuildingCode(mBuilding.getBuildingCode());
        user.setCust_seq(CUST_SEQ);
        user.setDept_seq(0);

        Map<String, Object> query =  new HashMap<>();
        query.put("userId", user.getUserId() );
        query.put("user_seq", user.getUser_seq() );
        query.put("token", user.getUserToken() );
        query.put("build_seq", mBuilding.getBuildingSeq()  );
        query.put("build_code", mBuilding.getBuildingCode()  );
        query.put("buildCode", mBuilding.getBuildingCode()  );
        query.put("cust_seq", CUST_SEQ  );
        query.put("dept_seq", 0  );
        query.put("default_flag", "Y"  );
        query.put("approval_flag", "Y"  );
        query.put("cust_remarks", ""  );

        Call<MapBuildingUser> call =
                new DefaultRestClient<CustomerService>(getBaseContext())
                        .getClient(CustomerService.class).getBuildingAndUser(query);
        call.enqueue(new Callback<MapBuildingUser>() {
            @Override
            public void onResponse(Call<MapBuildingUser> call, Response<MapBuildingUser> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful() && response.body().isSuccess()) {
                    mMapBuildingUser =  response.body() ;
                    Log.d("99999999999999", "777777777777777777" + user.string());
                    tryLoginByTokenToMain();

                }
            }

            @Override
            public void onFailure(Call<MapBuildingUser> call, Throwable t) {

                closeSpinner();
                mMapBuildingUser = null ;
            }

        });

        return  mMapBuildingUser ;

    }

    public void tryLoginByTokenToMain(){

        if (mBuilding == null) {
            showWarn(R.id.input_warn, "건물 선택 오류가 발생했습니다.");
            return ;
        }

        final String email = Pref.instance().getStringValue(PrefKey.USER_ID, "");
        Map<String, Object> queryMap = KUtil.getDefaultQueryMap();
        queryMap.put("id", email);
        queryMap.put("build_seq", mBuilding.getBuildingSeq());
        LogUtils.log(queryMap);
        Call<AppUserBase> call =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class).tryLoginByTokenAndIdBuild(queryMap);
        call.enqueue(new Callback<AppUserBase>() {
            @Override
            public void onResponse(Call<AppUserBase> call, Response<AppUserBase> response) {
                LogUtils.e(TAG, response.raw().toString());
                AppUserBase user;
                if(response.isSuccessful()){
                    closeSpinner();

                    user = response.body();
                    if(user.isSuccess()){
                        //LogUtils.e(TAG, "login data on splash=" + response.body().getTodayActivity().string());
                        LogUtils.err(TAG, "login data on splash=" + response.body().string());
                        if (mBuilding != null) {
                            user.setBuild_seq(mBuilding.getBuildingSeq());
                            user.setBuildingCode(mBuilding.getBuildingCode());
                            user.setBuild_lat(mBuilding.getLatitude());
                            user.setBuild_lng(mBuilding.getLongitude());
                            user.setBuild_name(mBuilding.getBuildingName());
                            user.setBuild_addr(mBuilding.getBuildingAddr());
                            user.setPlace_id(mBuilding.getPlace_id());
                            user.setCountry(mBuilding.getCountry());
                            user.setCity(mBuilding.getCity());
                            user.setBuild_floor_amt(mBuilding.getBuild_floor_amt()) ;
                            user.setBuild_stair_amt(mBuilding.getBuild_stair_amt());
                            user.setCountry(mBuilding.getCountry());
                            user.setCity(mBuilding.getCity());
                            user.setIsbuild(mBuilding.getIsbuild());

                            // 도원빌딩 강제 매핑
                            user.setBuild_seq("118");
                            user.setBuildingCode("100001");
                            user.setBuild_lat(37.5666447);
                            user.setBuild_lng(126.9122815);
                            user.setBuild_name("도원빌딩");
                            user.setBuild_addr("대한민국 서울특별시 마포구 성산동");
                            user.setPlace_id("ChIJtwmC7eGYfDURUWALZJGt1bE");

                        }
                        if (mBuildingStair != null) {
                            user.setStair_seq(mBuildingStair.getStair_seq());

                            // 도원빌딩 강제 매핑
                            user.setStair_seq(93);
                        }
                        if (mBeacon != null) {
                            user.setBeacon_seq(mBeacon.getBeacon_seq());

                            // 도원빌딩 강제 매핑
                            user.setBeacon_seq(140);
                        } else {
                            user.setBeacon_seq(DefaultCode.BEACON_SEQ.getValue());

                            // 도원빌딩 강제 매핑
                            user.setBeacon_seq(140);
                        }

                        if (mMapBuildingUser != null) {
                            user.setCust_seq(mMapBuildingUser.getCust_seq());
                            user.setCust_name(mMapBuildingUser.getCust_name());
                            user.setDept_seq(mMapBuildingUser.getDept_seq());
                            user.setDept_name(mMapBuildingUser.getDept_name());

                            // 도원빌딩 강제 매핑
                            user.setCust_seq(50);
                            user.setCust_name(mMapBuildingUser.getCust_name());
                            user.setDept_seq(84);
                            user.setDept_name(mMapBuildingUser.getDept_name());
                        }

                        storeUserTokenAndMovesToMain(user);
                    }else{
                        closeSpinner();

                        callActivity(LoginActivity.class, true);
                    }
                }else{
                    closeSpinner();

                    callActivity(LoginActivity.class, true);
                }
            }

            @Override
            public void onFailure(Call<AppUserBase> call, Throwable t) {
                closeSpinner();

                callActivity(LoginActivity.class, true);
            }
        });
    }

    /**
     * 서버에서 받은 로고 정보를 저장 후 Splash 화면으로 이동.
     * @param logo
     */
    private void saveLogoAndMoveToNext(Logo logo){

        if (logo != null) {
            LogUtils.err(TAG, logo.string());
            Pref.instance().saveStringValue(PrefKey.BUILDING_ADDR, logo.getBuildingCode());
            Pref.instance().saveStringValue(PrefKey.BUILDING_CODE, logo.getBuildingCode());
            Pref.instance().saveStringValue(PrefKey.COMPANY_LOGO, logo.getLogoUrl());
            Pref.instance().saveStringValue(PrefKey.COMPANY_COLOR, logo.getHexColor());
        }

        // kmj mod
        if (mBuilding == null) {
            //Toast.makeText(this, "건물이 등록되지 않았습니다", Toast.LENGTH_SHORT).show();
            return ;
        }

        Bundle bu = new Bundle();
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        bu.putSerializable("_TODAY_ACTIVITY_", user.getTodayActivity());

        callActivity2(MainActivity.class, bu,true);
    }

    @Override
    protected void setInitialData() {

    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        // EasyPermissions handles the request result.
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }
//
//    @Override
//    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
//        //Gps start
//
//        app.getmBLocationManager().registerLocationUpdates();
//        app.getmBLocationManager().mDelegateFindLocation = this ;
//
//        LogUtils.d(TAG,"onPermissionsGranted:" + requestCode + ":" + perms.size());
//    }
//
//    @Override
//    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
//        LogUtils.d(TAG,"onPermissionsDenied:" + requestCode + ":" + perms.size());
//        toast(R.string.permission_denied);
//        finish();
//        /*// (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
//        // This will display a dialog directing them to enable the permission in app settings.
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            new AppSettingsDialog.Builder(this).build().show();
//        }*/
//    }
//
//    @Override
//    public void findLocation(Location loc, Address addr ) {
//
//        closeSpinner();
//
//
//        mLocation = loc ;
//
//        if (loc != null ) {
//            Pref.instance().saveStringValue(PrefKey.BUILDING_LAT, String.valueOf(loc.getLatitude()));
//            Pref.instance().saveStringValue(PrefKey.BUILDING_LNG, String.valueOf(loc.getLongitude()));
//        }
//
//        String s_lat = Pref.instance().getStringValue(PrefKey.BUILDING_LAT, "");
//        String s_lng = Pref.instance().getStringValue(PrefKey.BUILDING_LNG, "");
//
//        android.util.Log.v("kmj",s_lat ) ;
//
//        if ( StringUtil.isEmptyOrWhiteSpace(s_lat) || StringUtil.isEmptyOrWhiteSpace(s_lng)  || s_lat.equals("null")  || s_lng.equals("null")  ) {
//            app.getmBLocationManager().registerLocationUpdates();
//            app.getmBLocationManager().mDelegateFindLocation = this ;
//            return;
//        }
//
//        lookupRecentBuilding(s_lat,s_lng );
//
//    }
}

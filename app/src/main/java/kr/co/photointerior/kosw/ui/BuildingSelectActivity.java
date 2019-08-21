package kr.co.photointerior.kosw.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.DefaultCode;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.RestManager;
import kr.co.photointerior.kosw.rest.api.App;
import kr.co.photointerior.kosw.rest.api.CustomerService;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.Building;
import kr.co.photointerior.kosw.rest.model.Buildings;
import kr.co.photointerior.kosw.rest.model.Character;
import kr.co.photointerior.kosw.rest.model.Company;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.Depart;
import kr.co.photointerior.kosw.rest.model.Logo;
import kr.co.photointerior.kosw.rest.model.Place;
import kr.co.photointerior.kosw.rest.model.Profile;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.service.beacon.BLocationManager;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.AbstractAcceptor;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import kr.co.photointerior.kosw.utils.event.BusProvider;
import kr.co.photointerior.kosw.utils.event.KsEvent;
import kr.co.photointerior.kosw.widget.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 프로필 변경
 */
public class BuildingSelectActivity extends BaseUserActivity  implements EasyPermissions.PermissionCallbacks, BLocationManager.DelegateFindLocation {
    private String TAG = LogUtils.makeLogTag(BuildingSelectActivity.class);
    private RecyclerView mRecyclerView;
    private List<Place> mPlaces = new ArrayList<>();
    private PlaceAdapter mAdapter;
    private Location mLocation ;
    private Address mAddr ;
    private Place mPlace = null  ;
    private boolean isExt = false ;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_building);


        if(!EasyPermissions.hasPermissions(this, Env.PERMISSIONS)){
            EasyPermissions.requestPermissions(this, getString(R.string.txt_permission_desc), Env.REQ_CODE[3],
                    Env.PERMISSIONS);
        }else {
            //Gps start
            showSpinner("");
            app.getmBLocationManager().registerLocationUpdates();
            app.getmBLocationManager().mDelegateFindLocation = this ;
        }

        //Intent in = getIntent() ;
        //mLocation = in.getParcelableExtra("loc") ;

        changeStatusBarColor(getCompanyColor());
        findViews();
        attachEvents();
        //setInitialData();
    }

    @Override
    protected void findViews() {


        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                com.google.android.gms.location.places.Place place = PlaceAutocomplete.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }

        }
    }

    @Override
    protected void attachEvents() {

        findViewById(R.id.btn_check_dupl).setOnClickListener(v -> {


            mPlaces.clear();
            searchBuilding();


            /*
            try {
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .build(this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
            }
            */


            /*
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try {
                startActivityForResult(builder.build(this),PLACE_AUTOCOMPLETE_REQUEST_CODE );
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
            */

        } );
        findViewById(R.id.popup_close).setOnClickListener(v->{
            finish();
            overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
        });
        findViewById(R.id.btn_change).setOnClickListener(v->{

            Intent intent = new Intent() ;
            intent.putExtra("place",mPlace)  ;
            setResult( (mPlace != null) ? RESULT_OK : RESULT_CANCELED,intent);
            finish();
            overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
        });
        findViewById(R.id.btn_cancel).setOnClickListener(v->{
            finish();
            overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
        });
    }

    @Override
    protected void setInitialData() {

        mPlaces.clear();
        lookupBuilding();

    }


    protected void searchBuilding() {

        AUtil.toggleSoftKeyboard(getBaseContext(), getEditText(R.id.input_nickname), false);

        //String preName = mAddr.getLocality() + " " + mAddr.getSubLocality()   ;
        String buildingName = getEditText(R.id.input_nickname).getText().toString();

        mPlaces.clear();
        lookupBuilding();

        /*
        if (StringUtil.isEmpty(buildingName) )     {
            setInitialData() ;
            return ;
        }

        RestManager.getInstance().getPlaceList( new RestManager.CallbackPlaces() {
            @Override
            public void result(ArrayList<Place> r) {

                for (Place p : r) {
                    if (Arrays.asList(p.types).contains("point_of_interest"))  {
                        if ( p.name.contains(buildingName)) {
                            mPlaces.add(p);
                        }
                    }
                }

                placeList();
            }
        }  ,mLocation.getLatitude() , mLocation.getLongitude() ,  buildingName );
        */
        //37.4931741,127.0301982
    }


    protected void searchBuildingExt() {

        mPlaces.clear();

        String preName = mAddr.getLocality()   ;
        String buildingName = getEditText(R.id.input_nickname).getText().toString();

        if (StringUtil.isEmpty(buildingName) )     {
            setInitialData() ;
            return ;
        }

        RestManager.getInstance().getPlaceList(this, new RestManager.CallbackPlaces() {
            @Override
            public void result(ArrayList<Place> r) {

                for (Place p : r) {
                    //if (Arrays.asList(p.types).contains("address"))  {
                    //if ( p.name.contains(buildingName)) {
                    mPlaces.add(p);
                    //}
                    //}
                }

                placeList();
            }
        }  , preName + " " + buildingName );

        //37.4931741,127.0301982
    }


    /** 빌딩코드 조회 */
    private void lookupBuilding(){
        String buildingName = getEditText(R.id.input_nickname).getText().toString();
        if (buildingName == null ) {
            buildingName = "" ;
        }
        /*
        if(StringUtil.isEmptyOrWhiteSpace(buildingName) ){
            showWarn(R.id.input_warn, R.string.warn_search_too_short);
            return;
        }
        */

        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("buildingname", buildingName);

        LogUtils.log("building name lookup", query);

        Call<Buildings> call =
                new DefaultRestClient<CustomerService>(getBaseContext())
                        .getClient(CustomerService.class).getBuildings(query);
        String finalBuildingName = buildingName;
        call.enqueue(new Callback<Buildings>() {
            @Override
            public void onResponse(Call<Buildings> call, Response<Buildings> response) {
                LogUtils.err("kmj", response.raw().toString());
                LogUtils.err("kmj", response.body().string());
                Buildings buildings = null;
                if(response.isSuccessful()){
                    buildings = response.body();
                    if(buildings.isSuccess()){


                    }else{
                        //showWarn(R.id.input_warn, buildings.getResponseMessage());
                        //toast(buildings.getResponseMessage());
                    }
                }else{
                    //showWarn(R.id.input_warn, R.string.warn_build_error);
                }
                hideWarn(R.id.input_warn);

                if (buildings != null) {
                    for (Building b : buildings.getBuilding() ) {

                        Place p = new Place() ;
                        p.name = b.getBuildingName() ;
                        p.vicinity = b.getBuildingAddr() ;
                        p.lat = b.getLatitude() ;
                        p.lng = b.getLongitude() ;
                        p.checkDB = true ;
                        p.place_id = b.getPlace_id() ;

                        if (p.lat != null && p.lng != null ) {
                            Location loc = new Location("dbplace");
                            loc.setLatitude(p.lat);
                            loc.setLongitude(p.lng);

                            if (mLocation.distanceTo(loc) <  DefaultCode.GPS_RANGE.getValue()) {

                                mPlaces.add(p);
                            }
                        }

                    }
                }

                RestManager.getInstance().getPlaceList(new RestManager.CallbackPlaces() {
                    @Override
                    public void result(ArrayList<Place> r) {

                        for (Place p : r) {
                            if (Arrays.asList(p.types).contains("point_of_interest")) {
                                mPlaces.add(p) ;
                            }
                        }
                        placeList();
                    }
                } , mLocation.getLatitude() , mLocation.getLongitude() , finalBuildingName);


            }

            @Override
            public void onFailure(Call<Buildings> call, Throwable t) {
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });

    }

    /** 서버에서 받은 건물 리스트 구성 */
    private void placeList(){
        mAdapter = new PlaceAdapter(this, mPlaces);
        mAdapter.setClickListener(mItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder>{
        private Context context;
        private List<Place> list;
        private LayoutInflater inflater;
        private ItemClickListener itemClickListener;

        PlaceAdapter(Context context, List<Place> list){
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public PlaceAdapter.PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.row_building_select, parent, false);
            PlaceAdapter.PlaceHolder viewHolder = new PlaceAdapter.PlaceHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(PlaceAdapter.PlaceHolder holder, int position) {
            Place item = getItem(position);
            String name = item.name ;
            //String imgUrl = KUtil.getSubCharacterImgUrl(item.getCharImageFile());
            //LogUtils.err(TAG, "character image url=" + imgUrl);
            if(item.check){
                holder.icon_check.setImageResource(R.drawable.ic_check);
            }else{
                holder.icon_check.setImageResource(0);
            }


            holder.nameTextView.setText(item.name);
            if (item.vicinity == null)  item.vicinity = "" ;
            holder.addrTextView.setText(item.vicinity);
            holder.dbTextView.setText((item.checkDB) ?  "등록" : "미등록") ;
            holder.dbTextView.setTextColor((item.checkDB) ? Color.RED : Color.BLACK); ;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public Place getItem(int id) {
            return list.get(id);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        class PlaceHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            ImageView icon_check ;
            TextView  nameTextView ;
            TextView  addrTextView ;
            TextView  dbTextView ;

            public PlaceHolder(View itemView) {
                super(itemView);

                icon_check =  itemView.findViewById(R.id.icon_check) ;
                nameTextView = itemView.findViewById(R.id.txt_name) ;
                dbTextView = itemView.findViewById(R.id.txt_db) ;
                addrTextView =itemView.findViewById(R.id.txt_addr) ;

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(view, getAdapterPosition());
                }
            }
        }
    }

    /**
     * 캐릭터 선택상태 제어
     * @param position
     */
    private void placeSelectStatusControl(int position){
        resetSelected();
        mPlaces.get(position).check = true;
        mAdapter.notifyDataSetChanged();
        mPlace =  mPlaces.get(position) ;

        Geocoder geocoder = new Geocoder(this) ;
        List<Address> list = new ArrayList<>() ;
        try {
            list = geocoder.getFromLocationName(mPlace.vicinity,1)  ;
            if (list != null) {
                if (list.size() > 0 )  {
                    Address a =  list.get(0)   ;
                    mPlace.lat = a.getLatitude() ;
                    mPlace.lng = a.getLongitude() ;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void resetSelected(){
        for( Place ch : mPlaces ){
            ch.check = false;
        }
    }

    private ItemClickListener mItemClickListener = (view, position) -> placeSelectStatusControl(position);

    interface ItemClickListener {
        void onItemClick(View view, int position);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
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
        //Gps start

        showSpinner("");
        
        app.getmBLocationManager().registerLocationUpdates();
        app.getmBLocationManager().mDelegateFindLocation = this ;

        LogUtils.d(TAG,"onPermissionsGranted:" + requestCode + ":" + perms.size());
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

    @Override
    public void findLocation(Location loc, Address addr ) {

        closeSpinner();

        mLocation = loc ;
        mAddr = addr ;

        String s_name = Pref.instance().getStringValue(PrefKey.BUILDING_NAME, "");
        String s_addr = Pref.instance().getStringValue(PrefKey.BUILDING_ADDR, "");
        String s_lat = Pref.instance().getStringValue(PrefKey.BUILDING_LAT, "");
        String s_lng = Pref.instance().getStringValue(PrefKey.BUILDING_LNG, "");
        android.util.Log.v("kmj",s_lat ) ;
        
        setInitialData() ;
    }

}

package kr.co.photointerior.kosw.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.Character;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.Depart;
import kr.co.photointerior.kosw.rest.model.Profile;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.AbstractAcceptor;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import kr.co.photointerior.kosw.utils.event.BusProvider;
import kr.co.photointerior.kosw.utils.event.KsEvent;
import kr.co.photointerior.kosw.widget.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 프로필 변경
 */
public class InfoSettingProfileActivity extends BaseUserActivity {
    private String TAG = LogUtils.makeLogTag(InfoSettingProfileActivity.class);
    private Acceptor mAcceptor;
    private RecyclerView mRecyclerView;
    private CharAdapter mAdapter;
    /** 기본 캐릭터 리스트 */
    private List<Character> mChars = new ArrayList<>();

    private Spinner mDeptSpinner;
    private EditText mNickName;
    private Profile mProfile = DataHolder.instance().getProfile();
    private String mOldNick;
    private  ViewGroup mRootView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_setting_profile);
        changeStatusBarColor(getCompanyColor());
        mContentRootView = getWindow().getDecorView().findViewById(android.R.id.content) ;
        mRootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        findViews();
        attachEvents();
        setInitialData();

    }

    @Override
    protected void findViews() {
        mNickName = findViewById(R.id.input_nickname);
        mNickName.setOnFocusChangeListener((view, focus)-> {
            LogUtils.err(TAG, "edit text focused=" + focus);
            if(focus){
                AUtil.toggleSoftKeyboard(this, mNickName, true);
            }
        });
        mDeptSpinner = findViewById(R.id.spinner_depart);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    protected void attachEvents() {
        findViewById(R.id.popup_close).setOnClickListener(v->{
            finish();
            overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
        });
        findViewById(R.id.btn_change).setOnClickListener(v->{
            saveNickAndCharacterToServer();
        });
        findViewById(R.id.btn_cancel).setOnClickListener(v->{
            finish();
            overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
        });
        findViewById(R.id.btn_check_dupl).setOnClickListener(v->{
            checkNickName();
        });
        getEditText(R.id.input_nickname).setOnEditorActionListener(mEditorAtionListener);

        for(int i=0 ; i< 10 ;i++ ) {
            Button tv = (Button) mRootView.findViewWithTag("21" + String.format("%02d",i+1) )  ;
            if (tv == null ) {
                continue;
            }
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int tag = Integer.valueOf(view.getTag().toString()) - 2101 ;
                    Pref.instance().saveIntValue (PrefKey.COMPANY_COLOR_NUM,tag);
                    selectBgColor(tag);
                    changeColors();
                }
            });
        }


    }

    @Override
    protected void setInitialData() {
        mOldNick = mProfile.getNickName();
        mNickName.setText(mProfile.getNickName());
        TextView companyName = getView(R.id.input_company_name);
        companyName.setText(mProfile.getCompanyName());
        drawCharacterList();
        drawDepartSpinner();
        selectBgColor(0);
    }

    private void selectBgColor(int n) {
        int bglist[] = DataHolder.instance().getBgColors() ;
        for(int i=0 ; i< 10 ;i++ ) {
            Button tv = (Button) mRootView.findViewWithTag("21" + String.format("%02d",i+1) )  ;
            if (tv == null ) {
                continue;
            }
            if ( i == n) {
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(getResources().getColor(bglist[i]));
                gd.setCornerRadius(0);
                gd.setStroke(8, 0xFF28B8F5);
                tv.setBackground(gd);

            } else {
                tv.setBackgroundColor(getResources().getColor(bglist[i]));
            }
        }

    }

    /** 부서정보 드럽다운 구성 */
    private void drawDepartSpinner(){
        List<Depart> depart = mProfile.getDeparts();
        depart.add(0, new Depart(0, getString(R.string.txt_select_depart)));
        int selected = 0;
        List<String> list = new ArrayList<>();
        for(int i = 0; i < depart.size(); i++ ){
            Depart d = depart.get(i);
            list.add(d.getDepartName());
            if(StringUtil.isEquals(mProfile.getDepartName(), d.getDepartName())){
                selected = i;
            }
        }
        mDeptSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.row_depart_spinner, list));
        mDeptSpinner.setSelection(selected);
    }

    /** 서버에서 받은 캐릭터 리스트 구성 */
    private void drawCharacterList(){
        mChars = mProfile.getCharacters();

        for( Character c : mProfile.getCharacters() ){
            if(c.getCharCode().equals(mProfile.getCharacterCode())) {
                c.setSelected(true);
                mProfile.setCharacter(c);
            }
        }

        mAdapter = new CharAdapter(this, mChars);
        mAdapter.setClickListener(mItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    class CharAdapter extends RecyclerView.Adapter<CharAdapter.CharHolder>{
        private Context context;
        private List<Character> list;
        private LayoutInflater inflater;
        private ItemClickListener itemClickListener;

        CharAdapter(Context context, List<Character> list){
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public CharAdapter.CharHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.row_character_image, parent, false);
            CharAdapter.CharHolder viewHolder = new CharAdapter.CharHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CharAdapter.CharHolder holder, int position) {
            Character item = getItem(position);
            String imageName = item.getCharImageFile();
            String imgUrl = KUtil.getSubCharacterImgUrl(item.getCharImageFile());
            LogUtils.err(TAG, "character image url=" + imgUrl);
            if(item.isSelected()){
                holder.charImage.setBackgroundResource(R.drawable.circle_00c5ed);
            }else{
                holder.charImage.setBackgroundResource(R.drawable.circle);
            }
            Glide.with(context).load(imgUrl).thumbnail(.5f).into(holder.charImage);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public Character getItem(int id) {
            return list.get(id);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        class CharHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            CircleImageView charImage;
            public CharHolder(View itemView) {
                super(itemView);
                pickupViews(itemView);
            }

            private void pickupViews(View view){
                charImage = (CircleImageView)view;
                charImage.setOnClickListener(this);
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
    private void characterSelectStatusControl(int position){
        resetSelected();
        mChars.get(position).setSelected(true);
        mProfile.setCharacter(mChars.get(position));
        mAdapter.notifyDataSetChanged();
    }

    private void resetSelected(){
        for( Character ch : mChars ){
            ch.setSelected(false);
        }
    }

    private ItemClickListener mItemClickListener = (view, position) -> characterSelectStatusControl(position);

    interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    /** 닉네임과 캐릭터 정보 서버 저장 */
    private void saveNickAndCharacterToServer(){
        final String nick = mNickName.getText().toString();
        if(!StringUtil.isEquals(mProfile.getNickName(), nick)){
            showWarn(R.id.input_warn, R.string.warn_required_check_nick_dupl);
            return;
        }
        Map<String, Object> map = KUtil.getDefaultQueryMap();
        if( mProfile.getCharacter() != null) {
            map.put("charSeq", mProfile.getCharacter().getCharSeq());
            map.put("charCode", mProfile.getCharacter().getCharCode());
        }else{
            map.put("charSeq", mProfile.getCharacterSeq());
            map.put("charCde", mProfile.getCharacterCode());
        }
        map.put("nick", nick);

        int pos = mDeptSpinner.getSelectedItemPosition();
        if( pos > 0 ){
            map.put("deptSeq", mProfile.getDeparts().get(pos).getDepartSeq());
        }else{
            map.put("deptSeq", mProfile.getDepartSeq());
        }
        LogUtils.log("nick change", map);
        Call<ResponseBase> call =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class)
                        .saveNickAndCharacter(map);
        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ResponseBase result = response.body();
                    if(result.isSuccess()){
                        alertSaved(nick);
                    }else{
                        toast(result.getResponseMessage());
                    }
                }else{
                    toast(R.string.warn_fail_save_nick);
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                LogUtils.err(TAG, t);
                toast(R.string.warn_fail_save_nick);
            }
        });
    }

    /**
     * 서버적용 끝 팝업
     */
    private void alertSaved(String nick){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txt_warn);
        builder.setMessage(R.string.txt_profile_changer);
        builder.setPositiveButton(R.string.txt_confirm, (dialogInterface, i) -> {
            KUtil.saveStringPref(PrefKey.USER_NICK, nick);
            KUtil.saveStringPref(PrefKey.CHARACTER_SUB, mProfile.getCharacter().getCharImageFile());
            BusProvider.instance().post(new KsEvent().setType(KsEvent.Type.CHANGE_CHARACTER));
            tryReLoginByToken(new AbstractAcceptor(){
                @Override
                public void accept() {
                    //LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Env.Action.CHARACTER_CHANGED_ACTION.action()));
                    finish();
                    overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
                }
            });//회원정보 다시 획득

        });
        builder.create().show();
    }

    /** 닉네임 중복 검사. */
    private void checkNickName(){

        AUtil.toggleSoftKeyboard(this, mNickName, false);
        final String nick = mNickName.getText().toString();

        if(StringUtil.isEquals(mOldNick, nick)){
            showWarn(R.id.input_warn, R.string.warn_not_changed_nick);
            mProfile.setNickName(mOldNick);
            return;
        }

        if(StringUtil.isEmptyOrWhiteSpace(nick)){
            showWarn(R.id.input_warn, R.string.warn_required_nick);
            return;
        }

        UserService service = new DefaultRestClient<UserService>(getBaseContext()).getClient(UserService.class);
        Map<String, Object> queryMap = KUtil.getDefaultQueryMap();
        queryMap.put("nick", nick);
        Call<ResponseBase> call = service.checkNickName(queryMap);
        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ResponseBase base = response.body();
                    if(base.isSuccess()) {
                        showWarn(R.id.input_warn, R.string.warn_available_nick);
                        mProfile.setNickName(nick);
                        mProfile.setCheckNick(true);
                    }else{
                        showWarn(R.id.input_warn, R.string.warn_unavailable_nick);
                    }
                }else{
                    toast(R.string.warn_fail_lookup_nick);
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                LogUtils.err(TAG, t);
                toast(R.string.warn_fail_lookup_nick);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
    }
}

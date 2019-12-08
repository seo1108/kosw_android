package kr.co.photointerior.kosw.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.bumptech.glide.Glide;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.listener.ClickListener;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.AppUser;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Character;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.ui.dialog.DialogCommon;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.AbstractAcceptor;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import kr.co.photointerior.kosw.widget.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 회원가입 후 추가정보 입력 화면
 */
public class SignUpExtraInfoActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(SignUpExtraInfoActivity.class);
    private AppUser mAppUser;
    private CheckBox mNickOpen;
    private RecyclerView mRecyclerView;
    private CharAdapter mAdapter;
    /** 기본 캐릭터 리스트 */
    private List<Character> mChars = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_extra);
        changeStatusBarColor(getCompanyColor());
        mAppUser = DataHolder.instance().getAppUser();
        //mAppUser = new AppUser();
        findViews();
        setNickName();
        setInitialData();
        //drawCharacterList();
        attachEvents();
    }

    @Override
    protected void findViews() {
        mRecyclerView = getView(R.id.recycler_view);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL, false));

        mNickOpen = findViewById(R.id.check_privacy_open);
    }

    private void setNickName(){
        String nick = KUtil.getNickName();
        getEditText(R.id.input_nickname).setText(nick);
    }

    @Override
    protected void attachEvents() {
        View.OnClickListener listener = new ClickListener(this);
        getView(R.id.btn_back).setOnClickListener(listener);
        getView(R.id.btn_check_dupl).setOnClickListener(listener);
        getView(R.id.btn_start).setOnClickListener(listener);
        getView(R.id.btn_next).setOnClickListener(listener);
        getEditText(R.id.input_nickname).setOnEditorActionListener(mEditorAtionListener);

        findViewById(R.id.check_privacy_open).setOnClickListener(v->{
            if (mNickOpen.isChecked()) {
                // TODO : CheckBox is checked.
                mNickOpen.setChecked(true);
            } else {
                // TODO : CheckBox is unchecked.
                mNickOpen.setChecked(false);
            }
        });
    }

    @Override
    public void performClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.btn_back:
            case R.id.btn_next:
                //finish();
                setNickName() ;
                mAppUser.setNickName(getEditText(R.id.input_nickname).getText().toString());
                mAppUser.setNickLookupFinish(true);
                saveNickAndCharacterToServer();
                break;
            case R.id.btn_start:
                saveNickAndCharacterToServer();
                break;
            case R.id.btn_check_dupl:
                checkNickName();
        }
    }

    /** 닉네임과 캐릭터 정보 서버 저장 */
    private void saveNickAndCharacterToServer(){
        final String nick = getEditText(R.id.input_nickname).getText().toString();
        if(!mAppUser.isNickLookupFinish() || !StringUtil.isEquals(mAppUser.getNickName(), nick)){
            showWarn(R.id.input_warn, R.string.warn_required_check_nick_dupl);
            return;
        }

        Map<String, Object> query = mAppUser.createCharacterQueryMap();
        query.put("deptSeq", (mAppUser.getDepart() != null ? mAppUser.getDepart().getDepartSeq() : "null"));

        if (mNickOpen.isChecked()) {
            query.put("show_nickname", "Y");
        }else{
            query.put("show_nickname", "N");
        }

        LogUtils.log(query);
        //query.put("token", "ubwsFctayocWGRPvmQ2q8P1WgwAWRZnHGelQhUllkTx4HPaXkuNU6UW7Zy1nz68l");
        Call<ResponseBase> call =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class)
                        .saveNickAndCharacter(query);
        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ResponseBase result = response.body();
                    if(result.isSuccess()){
                        finish();
                        //showNickSaveResultPopup();
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
                toast(R.string.warn_fail_lookup_nick);
            }
        });

    }
    private Dialog mDialog;
    /** 닉네임, 캐릭터 정보 저장 성공 후 안내 팝업 */
    private void showNickSaveResultPopup(){
        if(!isFinishing()){
            if(mDialog != null){
                mDialog.dismiss();
            }
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {
                    finish();
                }
            };
            String msg = getString(R.string.txt_signup_extra_success);
            msg = MessageFormat.format(msg, mAppUser.getUserId());
            mDialog =
                    new DialogCommon(this,
                            acceptor,
                            getString(R.string.txt_warn),
                            msg,
                            new String[]{null, null, getString(R.string.txt_confirm)});
            mDialog.setCancelable(false);
            mDialog.show();
        }
    }

    /** 닉네임 중복 검사. */
    private void checkNickName(){
        AUtil.toggleSoftKeyboard(getBaseContext(), getEditText(R.id.input_nickname), false);
        final String nick = getEditText(R.id.input_nickname).getText().toString();
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
                        mAppUser.setNickName(nick);
                        mAppUser.setNickLookupFinish(true);
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
    protected void setInitialData() {
        mNickOpen.setChecked(true);

        //서버에서 기본 캐릭터 선택
        UserService service = new DefaultRestClient<UserService>(getBaseContext()).getClient(UserService.class);
        Call<AppUserBase> call = service.getDefaultCharacter(KUtil.getDefaultQueryMap());
        call.enqueue(new Callback<AppUserBase>() {
            @Override
            public void onResponse(Call<AppUserBase> call, Response<AppUserBase> response) {
                AppUserBase user;
                if(response.isSuccessful()){
                    user = response.body();
                    if(user != null && user.isSuccess()){
                        mChars = user.getCharacters();
                        if(mChars != null && mChars.size() > 0 ) {
                            mChars.get(0).setSelected(true);
                            mAppUser.setCharacter(mChars.get(0));
                        }
                    }
                }
                drawCharacterList();
            }

            @Override
            public void onFailure(Call<AppUserBase> call, Throwable t) {
                drawCharacterList();
            }
        });
    }

    /** 서버에서 받은 캐릭터 리스트 구성 */
    private void drawCharacterList(){
        if(mChars == null){
            mChars = new ArrayList<>();
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
        public CharHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.row_character_image, parent, false);
            CharHolder viewHolder = new CharHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CharHolder holder, int position) {
            Character item = getItem(position);
            String imageName = item.getCharImageFile();
            String imgUrl = KUtil.getSubCharacterImgUrl(imageName);
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
        mAppUser.setCharacter(mChars.get(position));
        mAdapter.notifyDataSetChanged();
    }

    private void resetSelected(){
        //mChars.forEach(e -> e.setSelected(false)); // not available for api 21.. hell
        for( Character ch : mChars ){
            ch.setSelected(false);
        }
    }

    private ItemClickListener mItemClickListener = (view, position) -> characterSelectStatusControl(position);

    interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}

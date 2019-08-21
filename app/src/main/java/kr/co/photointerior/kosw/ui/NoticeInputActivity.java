package kr.co.photointerior.kosw.ui;

import android.app.Dialog;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.listener.ClickListener;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CustomerService;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.Admin;
import kr.co.photointerior.kosw.rest.model.AppUser;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Company;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.rest.model.TodayRank;
import kr.co.photointerior.kosw.ui.dialog.DialogCommon;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.AbstractAcceptor;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 계단왕 회원가입 화면.
 */
public class NoticeInputActivity extends BaseActivity {
    private String TAG = "kmj" ;
    private AppUser mAppUser;
    private Dialog mSendSuccessDialog;
    private Admin mAdmin ;
    private String mInputType ;
    private Boolean isCmt  ;
    private TodayRank mTodayRank ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_input);
        changeStatusBarColor(getCompanyColor());
        DataHolder.instance().setAppUser(new AppUser());
        mAppUser = DataHolder.instance().getAppUser();
        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {
    }

    @Override
    protected void attachEvents() {
        ClickListener clickListener = new ClickListener(this);
        getView(R.id.btn_back).setOnClickListener(clickListener);
        getView(R.id.btn_signup).setOnClickListener(clickListener);
        getView(R.id.btn_del).setOnClickListener(clickListener);
        //getEditText(R.id.ed1101).setOnEditorActionListener(mEditorAtionListener);
        try {
            int length = getEditText(R.id.ed1101).getText().toString().getBytes("EUC-KR").length;
            getTextView(R.id.tv1101).setText(String.format("(%d/108)",length));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        getEditText(R.id.ed1101).addTextChangedListener(new TextWatcher() {

            String beforeText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeText = s.toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    int length = getEditText(R.id.ed1101).getText().toString().getBytes("EUC-KR").length;
                    getTextView(R.id.tv1101).setText(String.format("(%d/108)",length));

                    if(length > 108 )
                        getEditText(R.id.ed1101).setText(beforeText);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     * 글
     */
    @Override
    protected void setInitialData() {
        mInputType = getIntent().getStringExtra("type") ;
        isCmt = getIntent().getBooleanExtra("iscmt",false) ;
        String msg = getIntent().getStringExtra("msg") ;
        mTodayRank = (TodayRank) getIntent().getSerializableExtra("todayrank") ;

        ImageButton btnDel = findViewById(R.id.btn_del) ;
        btnDel.setVisibility(View.INVISIBLE);

        if (mInputType.equals("MOD"))  {
            getEditText(R.id.ed1101).setText(msg);
            Button btn = findViewById(R.id.btn_signup) ;
            //btn.setText("수   정");
            btnDel.setVisibility(View.VISIBLE);

        }

        if (isCmt) {

            getTextView(R.id.title_signup).setText("댓글 달기");
        }

    }


    @Override
    public void performClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_signup:
                checkInput();
                break;
            case R.id.btn_del:
                showPopup("커뮤니티 글을 삭제 하시겠습니까?. ");
                break;
        }
    }

    /**
     * 입력사항 유효성 검사.
     */
    private void checkInput(){
        AUtil.toggleSoftKeyboard(getBaseContext(), getEditText(R.id.ed1101), false);
        //내용
        String name = getEditText(R.id.ed1101).getText().toString();
        if(StringUtil.isEmptyOrWhiteSpace(name)){
            showWarn(R.id.input_warn, R.string.warn_input);
            return;
        }
        submitUserDataToServer(mInputType);
    }


    /**
     * 커뮤니티 게시글  전송
     */


    private void submitUserDataToServer(String type){
        toggleViewEnable(R.id.btn_signup, false);
        HashMap<String,Object> query = new HashMap<>() ;

        showSpinner("");
        AppUserBase usr =  DataHolder.instance().getAppUserBase() ;


        UserService service =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class);
        Call<ResponseBase> call = null ;

        if (type.equals("ADD")) {
            query.put("user_seq",usr.getUser_seq()) ;
            query.put("cust_seq",usr.getCust_seq()) ;
            query.put("build_seq",usr.getBuild_seq()) ;
            query.put("msg",getEditText(R.id.ed1101).getText().toString()) ;
            query.put("noti_type","input") ;
            if (isCmt) {
                query.put("mnoti_seq",mTodayRank.getRnoti_seq()) ;
            }
            call = service.addRankNotice(query);
        }
        if (type.equals("MOD")) {
            query.put("rnoti_seq",mTodayRank.getRnoti_seq()) ;
            query.put("msg",getEditText(R.id.ed1101).getText().toString()) ;
            call = service.modRankNotice(query);
        }
        if (type.equals("DEL")) {
            query.put("rnoti_seq",mTodayRank.getRnoti_seq()) ;
            call = service.delRankNotice(query);
        }
        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {

                Intent intent = new Intent() ;
                setResult(RESULT_OK,intent);

                finish();
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                toast(R.string.warn_server_not_smooth);
                toggleViewEnable(R.id.btn_signup, true);

            }
        });
    }

    public void showPopup(String msg){
        if(!isFinishing()){
            if(mSendSuccessDialog != null){
                mSendSuccessDialog.dismiss();
            }
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {

                    submitUserDataToServer("DEL");
                }
            };
            msg = MessageFormat.format(msg, mAppUser.getUserId());
            mSendSuccessDialog =
                    new DialogCommon(this,
                            acceptor,
                            getString(R.string.txt_warn),
                            msg,
                            new String[]{"취소", null, getString(R.string.txt_confirm)});
            mSendSuccessDialog.setCancelable(true);
            mSendSuccessDialog.show();
        }
    }



}

package kr.co.photointerior.kosw.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Cafe;
import kr.co.photointerior.kosw.rest.model.CafeDetail;
import kr.co.photointerior.kosw.rest.model.CafeSubCategory;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.widget.KoswButton;
import kr.co.photointerior.kosw.widget.KoswEditText;
import kr.co.photointerior.kosw.widget.KoswTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeConfigActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(CafeConfigActivity.class);
    private Cafe mCafe;
    private List<CafeSubCategory> mCate;

    private ImageView btn_back;
    private KoswTextView txt_cafekey, txt_privacy;
    private TextView txt_category, txt_notice, txt_member;
    private KoswEditText txt_cafename, txt_cafedesc;
    private KoswButton btn_invite, btn_edit_cafe;
    private CheckBox check_privacy_hide, check_privacy_open;
    private RelativeLayout rl_category, rl_notice, rl_member;

    private String mCafeseq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_config);

        mCafeseq = getIntent().getStringExtra("cafeseq");

        getCafeDetail();

        findViews();
        attachEvents();
    }

    @Override
    protected void findViews() {
        txt_cafename = findViewById(R.id.txt_cafename);
        txt_cafedesc = findViewById(R.id.txt_cafedesc);

        txt_cafekey = findViewById(R.id.txt_cafekey);
        txt_cafekey.setTypeface(txt_cafekey.getTypeface(), Typeface.BOLD);

        btn_invite = findViewById(R.id.btn_invite);

        txt_privacy = findViewById(R.id.txt_privacy);
        txt_privacy.setTypeface(txt_privacy.getTypeface(), Typeface.BOLD);

        txt_category = findViewById(R.id.txt_category);
        txt_category.setTypeface(txt_category.getTypeface(), Typeface.BOLD);

        txt_notice = findViewById(R.id.txt_notice);
        txt_notice.setTypeface(txt_notice.getTypeface(), Typeface.BOLD);

        txt_member = findViewById(R.id.txt_member);
        txt_member.setTypeface(txt_member.getTypeface(), Typeface.BOLD);

        check_privacy_hide = findViewById(R.id.check_privacy_hide);
        check_privacy_open = findViewById(R.id.check_privacy_open);

        rl_category = findViewById(R.id.rl_category);
        rl_notice = findViewById(R.id.rl_notice);
        rl_member = findViewById(R.id.rl_member);

        btn_edit_cafe = findViewById(R.id.btn_edit_cafe);
        btn_back = findViewById(R.id.btn_back);
    }

    @Override
    protected void attachEvents() {
        btn_invite.setOnClickListener(v->{
            shareCafe();
        });

        check_privacy_hide.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(txt_cafename.getWindowToken(), 0);
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(txt_cafedesc.getWindowToken(), 0);

                // TODO : process the click event.
                if (check_privacy_hide.isChecked()) {
                    // TODO : CheckBox is checked.
                    check_privacy_hide.setChecked(true);
                    check_privacy_open.setChecked(false);

                    updateConfirm("Y");
                } else {
                    // TODO : CheckBox is unchecked.
                    check_privacy_hide.setChecked(true);
                    check_privacy_open.setChecked(false);
                }
            }
        }) ;

        check_privacy_open.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(txt_cafename.getWindowToken(), 0);
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(txt_cafedesc.getWindowToken(), 0);

                // TODO : process the click event.
                if (check_privacy_open.isChecked()) {
                    // TODO : CheckBox is checked.
                    check_privacy_hide.setChecked(false);
                    check_privacy_open.setChecked(true);

                    updateConfirm("N");
                } else {
                    // TODO : CheckBox is unchecked.
                    check_privacy_hide.setChecked(false);
                    check_privacy_open.setChecked(true);
                }
            }
        });

        rl_category.setOnClickListener(v->{
            Bundle bu = new Bundle();
            bu.putSerializable("cafeseq", mCafe.getCafeseq());
            callActivity(CafeCategoryActivity.class, bu,false);
        });

        rl_notice.setOnClickListener(v->{
            Bundle bu = new Bundle();
            bu.putSerializable("cafeseq", mCafe.getCafeseq());
            bu.putSerializable("type", "MODIFY");
            callActivity(CafeNoticeActivity.class, bu,false);
        });

        rl_member.setOnClickListener(v->{
            Bundle bu = new Bundle();
            bu.putSerializable("cafeseq", mCafe.getCafeseq());
            bu.putSerializable("adminseq", mCafe.getAdminseq());
            callActivity(CafeMemberActivity.class, bu,false);
        });

        btn_edit_cafe.setOnClickListener(v->{
            Intent intent = new Intent() ;
            setResult(RESULT_OK, intent);
            finish();
        });

        btn_back.setOnClickListener(v->{
            Intent intent = new Intent() ;
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    @Override
    protected void setInitialData() {
        txt_cafename.setText(mCafe.getCafename());
        txt_cafedesc.setText(mCafe.getCafedesc());
        txt_cafekey.setText(getResources().getString(R.string.txt_cafekey) + " : " + mCafe.getCafekey());
        if ("Y".equals(mCafe.getConfirm())) {
            check_privacy_hide.setChecked(true);
            check_privacy_open.setChecked(false);
        } else {
            check_privacy_hide.setChecked(false);
            check_privacy_open.setChecked(true);
        }
    }

    private void getCafeDetail() {
        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        if (null != mCafeseq && !"".equals(mCafeseq)) {
            query.put("cafeseq", mCafeseq);
        }

        Call<CafeDetail> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).detail(query);

        call.enqueue(new Callback<CafeDetail>() {
            @Override
            public void onResponse(Call<CafeDetail> call, Response<CafeDetail> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                if (response.isSuccessful()) {
                    CafeDetail cafedetail = response.body();
                    //LogUtils.err(TAG, "profile=" + profile.string());
                    if (cafedetail.isSuccess()) {
                        mCafe = cafedetail.getCafe();
                        mCafeseq = mCafe.getCafeseq();
                        mCate = mCafe.getCategory();

                        setInitialData();
                    } else {
                    }
                }

                if (!"0000".equals(response.body().getResponseCode())) {
                    toast(response.body().getResponseMessage());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CafeDetail> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    private void shareCafe() {
        String subject = "계단왕 '" + mCafe.getCafename() + "' 카페로 초대합니다.";

        String text = "계단왕 '" + mCafe.getCafename() + "' 카페로 초대합니다.\n\n저희 계단왕 '" + mCafe.getCafename() + "' 카페에 가입해 주세요.\n"
                + "계단왕 앱 메뉴의 카페 페이지에서 아래의 키값으로 바로 가입할 수 있습니다. (비공개 카페는 키값을 통해서만 카페 가입이 가능합니다.)\n\n"
                + "카페키값 : " + mCafe.getCafekey() + "\n\n"
                + "계단왕은 그룹 멤버들과 함께 운동하며 이야기를 나누는 공간입니다. 아이폰, 안드로이드에서 무료로 다운받아 사용해 보세요.\n\n"
                + Env.Url.URL_SHARE.url();

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        Intent chooser = Intent.createChooser(intent, "공유할 앱을 선택하세요.");
        startActivity(chooser);
    }

    private void updateConfirm(String confirm) {
        showSpinner("");

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("confirm", confirm);
        query.put("cafeseq", mCafeseq);

        Call<ResponseBase> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).updateConfirm(query);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ResponseBase base = response.body();
                    if(base.isSuccess()) {
                        if ("Y".equals(confirm)) {
                            toast(R.string.cafe_close);
                        } else {
                            toast(R.string.cafe_open);
                        }
                    }else{
                        toast(R.string.warn_cafe_member_fail_kick);
                    }
                }else{
                    toast(R.string.warn_cafe_member_fail_kick);
                }

                closeSpinner();
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent() ;
        setResult(RESULT_OK, intent);
        finish();
    }
}

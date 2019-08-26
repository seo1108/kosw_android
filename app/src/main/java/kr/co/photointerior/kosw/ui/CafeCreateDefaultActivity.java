package kr.co.photointerior.kosw.ui;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.rest.model.AppUser;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.widget.KoswButton;
import kr.co.photointerior.kosw.widget.KoswEditText;
import kr.co.photointerior.kosw.widget.KoswTextView;

public class CafeCreateDefaultActivity extends BaseActivity {
    private String TAG = "CafeCreateDefault" ;
    private AppUser mAppUser;
    private Activity mActivity;

    private KoswEditText et_name_length, et_input_name;
    private KoswEditText et_desc_length, et_desc;
    private CheckBox check_privacy_hide, check_privacy_open;
    private KoswButton btn_make_cafe;
    private ImageView btn_back;

    private KoswTextView txt_privacy, txt_hide, txt_open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_cafe_default_create);
        changeStatusBarColor(getCompanyColor());
        DataHolder.instance().setAppUser(new AppUser());
        mAppUser = DataHolder.instance().getAppUser();
        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {
        txt_privacy = findViewById(R.id.txt_privacy);
        txt_hide = findViewById(R.id.txt_hide);
        txt_open = findViewById(R.id.txt_open);

        txt_privacy.setTypeface(txt_privacy.getTypeface(), Typeface.BOLD);
        txt_hide.setTypeface(txt_hide.getTypeface(), Typeface.BOLD);
        txt_open.setTypeface(txt_open.getTypeface(), Typeface.BOLD);

        et_input_name = findViewById(R.id.input_name);
        et_name_length = findViewById(R.id.input_name_length);

        et_name_length.setFocusable(false);
        et_name_length.setClickable(false);
        et_input_name.addTextChangedListener(mTextEditorWatcher);

        et_desc = findViewById(R.id.input_desc);
        et_desc_length = findViewById(R.id.input_desc_length);

        et_desc_length.setFocusable(false);
        et_desc_length.setClickable(false);
        et_desc.addTextChangedListener(mTextEditorWatcher1);


        check_privacy_hide = findViewById(R.id.check_privacy_hide);
        check_privacy_open = findViewById(R.id.check_privacy_open);

        check_privacy_hide.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(et_input_name.getWindowToken(), 0);
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(et_desc_length.getWindowToken(), 0);

                // TODO : process the click event.
                if (check_privacy_hide.isChecked()) {
                    // TODO : CheckBox is checked.
                    check_privacy_hide.setChecked(true);
                    check_privacy_open.setChecked(false);
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
                        .hideSoftInputFromWindow(et_input_name.getWindowToken(), 0);
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(et_desc_length.getWindowToken(), 0);

                // TODO : process the click event.
                if (check_privacy_open.isChecked()) {
                    // TODO : CheckBox is checked.
                    check_privacy_hide.setChecked(false);
                    check_privacy_open.setChecked(true);
                } else {
                    // TODO : CheckBox is unchecked.
                    check_privacy_hide.setChecked(false);
                    check_privacy_open.setChecked(true);
                }
            }
        }) ;

        btn_make_cafe = findViewById(R.id.btn_make_cafe);
        btn_make_cafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v->{
            finish();
        });
    }

    @Override
    protected void attachEvents() {

    }

    @Override
    protected void setInitialData() {
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            et_name_length.setText(String.valueOf(s.length())+ "/50");
            if (s.length() > 50) {
                et_input_name.setText(String.valueOf(s));
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher mTextEditorWatcher1 = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            et_desc_length.setText(String.valueOf(s.length())+ "/200");
            if (s.length() > 200) {
                et_desc.setText(String.valueOf(s));
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private void goNext() {
        // 카페명
        String cafename = "";
        // 카페설명
        String cafedesc = "";
        // 공개/비공개
        String confirm = "";

        cafename = et_input_name.getText().toString();

        cafedesc = et_desc.getText().toString();

        if (null == cafename || "".equals(cafename.trim())) {
            toast(R.string.warn_cafe_name);
            return;
        }

        if (null == cafedesc || "".equals(cafedesc.trim())) {
            toast(R.string.warn_cafe_desc);
            return;
        }

        if (check_privacy_hide.isChecked()) {
            confirm = "Y";
        } else if (check_privacy_open.isChecked()) {
            confirm = "N";
        } else if (!check_privacy_hide.isChecked() && !check_privacy_open.isChecked()) {
            toast(R.string.warn_privacy_not_selected);
            return;
        }

        Bundle bu = new Bundle();
        bu.putSerializable("cafename", cafename);
        bu.putSerializable("cafedesc", cafedesc);
        bu.putSerializable("confirm", confirm);

        // kmj mod
        callActivity(CafeCreateOptionActivity.class, bu,false);
    }

    public void clear() {
        finish();
    }
}
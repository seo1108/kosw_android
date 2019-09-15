package kr.co.photointerior.kosw.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.ui.BaseActivity;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.event.Keyboard;
import kr.co.photointerior.kosw.widget.KoswButton;
import kr.co.photointerior.kosw.widget.KoswEditText;

public class DialogBbsUpdate extends BaseDialog {
    private Acceptor mAcceptor;
    private String mMsg;
    KoswEditText mMessageText;
    public DialogBbsUpdate (
            BaseActivity context,
            Acceptor acceptor,
            String message){
        super(context, R.style.custom_dialog_style);
        mAcceptor = acceptor;
        mMsg = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bbs_update);

        setStatusBarColorIfPossible(mActivity.getCompanyColor());

        setTitleAndMessage();
        attachEvents();

    }

    private void setTitleAndMessage() {
        mMessageText = (KoswEditText)findViewById(R.id.text_content);
        mMessageText.setText(mMsg);
    }

    private void attachEvents() {
        KoswButton btn_edit = findViewById(R.id.btn_edit);
        KoswButton btn_cancel = findViewById(R.id.btn_cancel);
        ImageView btn_close = (ImageView)findViewById(R.id.btn_close);

        /*mMessageText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Keyboard.hide(mActivity);
                } else {
                    Keyboard.show(mActivity);
                }
            }
        });*/

        mMessageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mgr = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(v, InputMethodManager.SHOW_FORCED);
                mMessageText.setFocusable(true);
            }
        });

        btn_edit.setOnClickListener(v->{
            mAcceptor.action(mMessageText.getText());
            dismiss();
        });

        btn_cancel.setOnClickListener(v->{
            dismiss();
            mAcceptor.deny();
        });

        btn_close.setOnClickListener(v->{
            dismiss();
            mAcceptor.deny();
        });
    }

}

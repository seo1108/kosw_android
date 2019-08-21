package kr.co.photointerior.kosw.ui.dialog;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.ui.BaseActivity;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.LibUtils;

/**
 * 일반목적의 다이알로그 클래스입니다.
 */
public class DialogCommon extends BaseDialog {
    private Acceptor mAcceptor;
    private String mTitle;
    private String mMsg;
    private String[] mBtnTxts;
    public DialogCommon(
            BaseActivity context,
            Acceptor acceptor,
            String title,
            String message, String[] btnTxts){
        super(context, R.style.custom_dialog_style);
        mAcceptor = acceptor;
        mTitle = title;
        mMsg = message;
        mBtnTxts = btnTxts;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common);

        setStatusBarColorIfPossible(mActivity.getCompanyColor());

        setTitleAndMessage();
        setButtonText();
        attachButtonEvents();

    }

    private void setTitleAndMessage(){
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(mTitle);
        TextView messageText = (TextView)findViewById(R.id.text_content);
        messageText.setText(mMsg);
    }

    private void setButtonText(){
        TextView btnLeft = (TextView)findViewById(R.id.btn_left);
        TextView btnCenter = (TextView)findViewById(R.id.btn_center);
        TextView btnRight = (TextView)findViewById(R.id.btn_right);
        if(LibUtils.isEmptyOrWhiteSpace(mBtnTxts[0])){
            btnLeft.setVisibility(View.GONE);
        }else{
            btnLeft.setVisibility(View.VISIBLE);
            btnLeft.setText(mBtnTxts[0]);
        }
        if(LibUtils.isEmptyOrWhiteSpace(mBtnTxts[1])){
            btnCenter.setVisibility(View.GONE);
            btnCenter.setText(mBtnTxts[1]);
        }else{
            btnCenter.setVisibility(View.VISIBLE);
        }
        if(LibUtils.isEmptyOrWhiteSpace(mBtnTxts[2])){
            btnRight.setVisibility(View.GONE);
        }else{
            btnRight.setVisibility(View.VISIBLE);
            btnRight.setText(mBtnTxts[2]);
        }
    }

    private void attachButtonEvents(){
        TextView btnLeft = findViewById(R.id.btn_left);
        TextView btnCenter = findViewById(R.id.btn_center);
        TextView btnRight = findViewById(R.id.btn_right);
        if(!LibUtils.isEmptyOrWhiteSpace(mBtnTxts[0])){
            btnLeft.setOnClickListener(v->{
                dismiss();
                mAcceptor.deny();
            });
        }
        if(!LibUtils.isEmptyOrWhiteSpace(mBtnTxts[1])){
            btnCenter.setOnClickListener(v-> {
                mAcceptor.action();
                dismiss();
            });
        }
        if(!LibUtils.isEmptyOrWhiteSpace(mBtnTxts[2])){
            btnRight.setOnClickListener(v-> {
                dismiss();
                mAcceptor.accept();
            });
        }
    }
}

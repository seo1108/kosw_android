package kr.co.photointerior.kosw.ui.dialog;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.ui.BaseActivity;
import kr.co.photointerior.kosw.utils.Acceptor;

/**
 * 일반목적의 다이알로그 클래스입니다.
 */
public class DialogUpdate extends BaseDialog {
    private Acceptor mAcceptor;
    private String mMsg;
    public DialogUpdate(
            BaseActivity context,
            Acceptor acceptor, String message){
        super(context, R.style.custom_dialog_style);
        mAcceptor = acceptor;
        mMsg = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update);

        setStatusBarColorIfPossible(mActivity.getCompanyColor());

        TextView tv = findViewById(R.id.txt_message);
        tv.setText(mMsg);
        tv.setMovementMethod(new ScrollingMovementMethod());

        findViewById(R.id.btn_top_close).setOnClickListener(v->{
            mAcceptor.deny();
            dismiss();
        });
        findViewById(R.id.btn_update).setOnClickListener(v->{
            mAcceptor.accept();
            dismiss();
        });
        findViewById(R.id.btn_cancel).setOnClickListener(v->{
            mAcceptor.deny();
            dismiss();
        });
    }

}

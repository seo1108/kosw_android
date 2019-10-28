package kr.co.photointerior.kosw.ui.dialog;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.ui.BaseActivity;
import kr.co.photointerior.kosw.utils.LibUtils;

/**
 * Progress spinner Dialog class입니다.
 * Created by kugie.
 */
public class ProgressSpinnerDialog extends BaseDialog {
    private String mMessage;

    public ProgressSpinnerDialog(BaseActivity context, String msg){
        super(context, R.style.custom_dialog_style_1);
        mMessage = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_spinner);

        setStatusBarColorIfPossible(mActivity.getCompanyColor());

        if( !LibUtils.isEmptyOrWhiteSpace(mMessage)){
            TextView tv = findViewById(R.id.txt_msg);
            tv.setVisibility(View.VISIBLE);
            tv.setText(mMessage);
        }
    }
}

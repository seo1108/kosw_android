package kr.co.photointerior.kosw.ui.dialog;

import android.app.AlertDialog;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import kr.co.photointerior.kosw.ui.BaseActivity;

public class BaseDialog extends AlertDialog {
    protected BaseActivity mActivity;
    public BaseDialog(BaseActivity context, int themeResId) {
        super(context, themeResId);
        mActivity = context;
    }

    public void setStatusBarColorIfPossible(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(color);
        }
    }
    /**
     * 각 입력 필드의 유효성 경고 텍스트 노출
     * @param textViewId 노출할 텍스트 뷰 id
     * @param stringId 노출할 문구 id
     */
    public void showWarn(int textViewId, int stringId){
        showWarn(textViewId, mActivity.getString(stringId));
    }

    public void showWarn(int textViewId, String msg){
        TextView tv = findViewById(textViewId);
        tv.setText(msg);
        tv.setVisibility(View.VISIBLE);
    }
    /**
     * 노출했던 경고 텍스트 숨김.
     * @param textViewId
     */
    public void hideWarn(int textViewId){
        findViewById(textViewId).setVisibility(View.INVISIBLE);
    }
}

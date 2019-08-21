package kr.co.photointerior.kosw.listener;

import android.view.View;

import kr.co.photointerior.kosw.ui.BaseActivity;

/**
 * Process click event for Activity.
 * Created by kugie.
 * 2018. 4. 30.
 */
public class ClickListener implements View.OnClickListener {
    private BaseActivity mActivity;
    private int mPosition = -1;
    public ClickListener(BaseActivity activity){
        mActivity = activity;
    }
    public ClickListener(BaseActivity activity, int position){
        mActivity = activity;
        mPosition = position;
    }
    @Override
    public void onClick(View view) {
        if( mPosition < 0 ) {
            mActivity.performClick(view);
        }else{
            mActivity.performClick(view, mPosition);
        }
    }
}

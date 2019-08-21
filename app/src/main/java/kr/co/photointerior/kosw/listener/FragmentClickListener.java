package kr.co.photointerior.kosw.listener;

import android.view.View;

import kr.co.photointerior.kosw.ui.fragment.BaseFragment;

/**
 * Process click event for Fragment.
 * Created by kugie.
 * 2018. 4. 30.
 */
public class FragmentClickListener implements View.OnClickListener {
    private BaseFragment mFragment;
    private int mPositon = -1;
    public FragmentClickListener(BaseFragment frag){
        mFragment = frag;
    }
    public FragmentClickListener(BaseFragment frag, int position){
        this(frag);
        mPositon = position;
    }
    @Override
    public void onClick(View view) {
        if( mPositon < 0 ) {
            mFragment.performFragmentClick(view);
        }else{
            mFragment.performFragmentClick(view, mPositon);
        }
    }
}

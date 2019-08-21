package kr.co.photointerior.kosw.ui.fragment;

import kr.co.photointerior.kosw.R;

/**
 * 도움말 2페이지
 */
public class FragmentHelpTwo extends BaseFragment {

    public static BaseFragment newInstance(){
        return new FragmentHelpTwo();
    }

    @Override
    protected void followingWorksAfterInflateView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_help_two;
    }
}

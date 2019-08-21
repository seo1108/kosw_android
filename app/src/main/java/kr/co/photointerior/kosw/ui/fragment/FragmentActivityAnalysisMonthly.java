package kr.co.photointerior.kosw.ui.fragment;

import java.util.List;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.rest.model.ActivityPeriod;
import kr.co.photointerior.kosw.ui.BaseActivity;

/**
 * 도움말 1페이지
 */
public class FragmentActivityAnalysisMonthly extends BaseFragment {
    private List<ActivityPeriod> mSpinnerList;

    public static BaseFragment newInstance(BaseActivity activity, List<ActivityPeriod> spinnerList){
        FragmentActivityAnalysisMonthly frag = new FragmentActivityAnalysisMonthly();
        frag.mActivity = activity;
        frag.mSpinnerList = spinnerList;
        return frag;
    }

    @Override
    protected void followingWorksAfterInflateView() {

    }

    @Override
    public int getViewResourceId() {
        return R.layout.fragment_activity_analysis_monthly;
    }
}

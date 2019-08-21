package kr.co.photointerior.kosw.ui.fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import java.util.List;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.PeriodType;
import kr.co.photointerior.kosw.listener.FragmentClickListener;
import kr.co.photointerior.kosw.rest.model.ActivityPeriod;
import kr.co.photointerior.kosw.ui.BaseActivity;
import kr.co.photointerior.kosw.utils.DateUtil;
import kr.co.photointerior.kosw.utils.LogUtils;

/**
 * 회원의 계단 활동 기록
 */
public class FragmentActivityAnalysis extends BaseFragment {
    private final String TAG = LogUtils.makeLogTag(FragmentActivityAnalysis.class);
    private int mCurrentBtn;
    private List<ActivityPeriod> mWeekList;
    private List<ActivityPeriod> mMonthList;
    private PeriodType mType = PeriodType.WEEKLY;

    private ViewPager mPager;
    private AnaPagerAdapter mPagerAdapter;
    public static BaseFragment newInstance(BaseActivity context){
        FragmentActivityAnalysis frag = new FragmentActivityAnalysis();
        frag.mActivity = context;
        return frag;
    }

    @Override
    protected void followingWorksAfterInflateView() {
        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {
        mActivity.toggleActionBarResources(false);
        mPager = getView(R.id.viewPager);
    }

    @Override
    protected void attachEvents() {
        View.OnClickListener clickListener = new FragmentClickListener(this);
        getView(R.id.btn_weekly).setOnClickListener(clickListener);
        getView(R.id.btn_monthly).setOnClickListener(clickListener);
        mCurrentBtn = R.id.btn_weekly;
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPagerAdapter.getItem(position).toggleSpinnerBox(View.GONE);
            }

            @Override
            public void onPageSelected(int position) {
                togglePeriodBtn(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void setInitialData() {
        mWeekList = DateUtil.createWeeklyPeriod(24);//24주
        mMonthList = DateUtil.createMonthlyPeriod(2);//2년
        mPagerAdapter = new AnaPagerAdapter(mActivity.getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    private class AnaPagerAdapter extends FragmentStatePagerAdapter {
        private FragmentActivityAnalysisByPeriod[] mFrags = {
                FragmentActivityAnalysisByPeriod.newInstance(mActivity, mWeekList, PeriodType.WEEKLY),
                FragmentActivityAnalysisByPeriod.newInstance(mActivity, mMonthList, PeriodType.MONTHLY)
        };
        AnaPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public FragmentActivityAnalysisByPeriod getItem(int position) {
            return mFrags[position];
        }
    }

    @Override
    public void performFragmentClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_weekly:
                mType = PeriodType.WEEKLY;
                mPager.setCurrentItem(0);
                break;
            case R.id.btn_monthly:
                mType = PeriodType.MONTHLY;
                mPager.setCurrentItem(1);
                break;
        }
        mCurrentBtn = id;
    }

    private void togglePeriodBtn(int index){
        if( index == 0 ){
            Button weeklyBtn = getView(R.id.btn_weekly);
            weeklyBtn.setTextColor(getResources().getColor(R.color.colorWhite));
            weeklyBtn.setBackgroundResource(R.color.color_1a1a1a);
            Button monthlyBtn = getView(R.id.btn_monthly);
            monthlyBtn.setTextColor(getResources().getColor(R.color.color_545454));
            monthlyBtn.setBackgroundResource(R.color.color_cccccc);
        }else{
            Button weeklyBtn = getView(R.id.btn_weekly);
            weeklyBtn.setTextColor(getResources().getColor(R.color.color_545454));
            weeklyBtn.setBackgroundResource(R.color.color_cccccc);

            Button monthlyBtn = getView(R.id.btn_monthly);
            monthlyBtn.setTextColor(getResources().getColor(R.color.colorWhite));
            monthlyBtn.setBackgroundResource(R.color.color_1a1a1a);
        }
    }

    @Override
    public int getViewResourceId() {
        return R.layout.fragment_activity_analysis;
    }
}

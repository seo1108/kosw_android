package kr.co.photointerior.kosw.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.ui.fragment.FragmentHelpOne;
import kr.co.photointerior.kosw.ui.fragment.FragmentHelpTwo;
import kr.co.photointerior.kosw.utils.LogUtils;

/**
 * 도움말
 */
public class HelpActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(HelpActivity.class);

    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        changeStatusBarColor(getCompanyColor());
        initToolbar();

        String from = getIntent().getStringExtra("_WHERE_IS_");
        if( "CODE_INPUT".equals(from)){
            toggleActionBarRightIconVisible(false);
            setNavigationTitle(R.string.txt_help);
        }else{
            toggleActionBarRightIconVisible(true);
            setNavigationTitle(R.string.txt_help);
        }
        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {
        mPager = getView(R.id.viewPager);
    }

    @Override
    protected void attachEvents() {}

    @Override
    public void performClick(View view) {}

    /**
     * 초기 데이터 설정
     */
    @Override
    protected void setInitialData() {
        HelpPagerAdapter adapter = new HelpPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
    }

    private class HelpPagerAdapter extends FragmentStatePagerAdapter{
        private Fragment[] mFrags = {
            FragmentHelpOne.newInstance(),
            FragmentHelpTwo.newInstance()
        };
        HelpPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Fragment getItem(int position) {
            return mFrags[position];
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
    }
}

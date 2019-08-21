package kr.co.photointerior.kosw.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.db.KsDbWorker;
import kr.co.photointerior.kosw.rest.model.Bbs;
import kr.co.photointerior.kosw.ui.BaseActivity;
import kr.co.photointerior.kosw.utils.LogUtils;

/**
 * 공지사상, 이벤트 열람.
 */
public class NoticeEventViewFragment extends BaseFragment{
    private String TAG = LogUtils.makeLogTag(NoticeEventViewFragment.class);

    private Bbs mArticle;

    public static BaseFragment newInstance(BaseActivity activity, Bbs article){
        NoticeEventViewFragment frag = new NoticeEventViewFragment();
        frag.mActivity = activity;
        frag.mArticle = article;
        return frag;
    }

    @Override
    protected void followingWorksAfterInflateView() {
        findViews();

        attachEvents();
        //setInitialData();

    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void attachEvents() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setInitialData();
    }

    @Override
    protected void setInitialData() {
        TextView title = getTextView(R.id.txt_title);
        TextView date = getTextView(R.id.txt_date);
        TextView content = getTextView(R.id.content);
        if(mArticle != null) {
            title.setText(mArticle.getTitle());
            date.setText(mArticle.getRegisterTime("yyyy.MM.dd."));
            content.setText(mArticle.getContent());
            KsDbWorker.updateBbsReadStatus(mActivity, mArticle.getBbsSeq());
        }
    }

    @Override
    public void performFragmentClick(View view) {

    }

    @Override
    public int getViewResourceId() {
        return R.layout.fragment_notice_event_view;
    }

}

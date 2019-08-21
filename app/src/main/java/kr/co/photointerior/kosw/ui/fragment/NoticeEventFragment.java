package kr.co.photointerior.kosw.ui.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.db.KsDbWorker;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.listener.ItemClickListener;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.App;
import kr.co.photointerior.kosw.rest.model.Bbs;
import kr.co.photointerior.kosw.rest.model.BbsOpen;
import kr.co.photointerior.kosw.rest.model.Contents;
import kr.co.photointerior.kosw.ui.BaseActivity;
import kr.co.photointerior.kosw.ui.NoticeEventActivity;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 공지사상, 이벤트 리스트.
 */
public class NoticeEventFragment extends BaseFragment implements ItemClickListener{
    private String TAG = LogUtils.makeLogTag(NoticeEventFragment.class);
    private RecyclerView mRecyclerView;
    private BbsAdapter mAdapter;
    private Contents mContents;

    public static BaseFragment newInstance(BaseActivity activity){
        BaseFragment frag = new NoticeEventFragment();
        frag.mActivity = activity;
        return frag;
    }

    @Override
    protected void followingWorksAfterInflateView() {
        findViews();
        requestToServer();
        attachEvents();


    }

    @Override
    protected void findViews() {
        mRecyclerView = getView(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void attachEvents() {

    }

    @Override
    public void requestToServer() {
        showSpinner("");
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        Call<Contents> call =
                new DefaultRestClient<App>(mActivity).getClient(App.class).getBbsList(query);
        call.enqueue(new Callback<Contents>() {
            @Override
            public void onResponse(Call<Contents> call, Response<Contents> response) {
                closeSpinner();
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    mContents = response.body();
                    if(mContents.isSuccess()){
                        setInitialData();
                    }else{
                        close(R.string.warn_commu_to_server);
                    }
                }else{
                    close(R.string.warn_commu_to_server);
                }
            }

            @Override
            public void onFailure(Call<Contents> call, Throwable t) {
                closeSpinner();
                close(R.string.warn_commu_to_server);
            }
        });
    }

    private void close(int strId){
        toast(strId);
        //mActivity.onBackPressed();
    }

    @Override
    protected void setInitialData() {
        LogUtils.err(TAG, mContents.string());
        if(mContents == null || !mContents.hasBbsArticle()){
            close(R.string.warn_bbs_not_exists);
            return;
        }
        lookupReadStatus();
        drawList();

    }

    private void lookupReadStatus(){
        KsDbWorker.replaceReadFlag(getActivity(), mContents.getBbsList());
        /*for( Bbs bbs : mContents.getBbsList()){
            BbsOpen open = KsDbWorker.getLocalBbsReadFlags(getActivity(), bbs.getBbsSeq());
            if(open == null){
                open = new BbsOpen();
                open.setReadFlag("N");
                open.setBbsSeq(Integer.valueOf(bbs.getBbsSeq()));
                KsDbWorker.insertBbsReadStatus(mActivity, open);
                bbs.setNewerFlag("Y");
            }else{
                bbs.setNewerFlag("N");
            }
        }*/
        KsDbWorker.selectAllReadFlag(mActivity);
    }

    private void drawList(){
        mAdapter = new BbsAdapter(mActivity, mContents.getBbsList());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void performFragmentClick(View view) {

    }

    @Override
    public int getViewResourceId() {
        return R.layout.fragment_notice_event_list;
    }

    @Override
    public void onItemClick(View view, int position) {
        ((NoticeEventActivity)mActivity).openBbsArticle(mContents.getBbs(position));
    }

    class BbsAdapter extends RecyclerView.Adapter<RowHolder>{
        Context mContext;
        LayoutInflater mInflater;
        List<Bbs> mList;

        BbsAdapter(Context context, List<Bbs> list){
            mContext = context;
            mList = list;
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.row_notice_event, parent, false);
            return new RowHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RowHolder holder, int position) {
            Bbs item = mList.get(position);
            holder.title.setText(Html.fromHtml(item.getTitle()));
            holder.date.setText(item.getRegisterTime("yyyy.MM.dd."));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    class RowHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView title;
        private TextView date;
        public RowHolder(View itemView) {
            super(itemView);
            pickupVies();
        }

        void pickupVies() {
            title = itemView.findViewById(R.id.txt_title);
            date = itemView.findViewById(R.id.txt_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClick(view, getAdapterPosition());
        }
    }
}

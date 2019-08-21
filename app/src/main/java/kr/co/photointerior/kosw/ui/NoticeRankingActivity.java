package kr.co.photointerior.kosw.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.ResultType;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Bbs;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.TodayRank;
import kr.co.photointerior.kosw.utils.DateUtil;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 메인화면 중앙에 표시되는 랭킹과 관련한 알림 내용 뷰
 */

public class NoticeRankingActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(NoticeRankingActivity.class);
    private RecyclerView mRecyclerView;
    private RankingAdapter mRecyclerAdapter;
    private Bbs mBbs;
    private String mInputType ;
    private String mMsg ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_ranking);
        changeStatusBarColor(getCompanyColor());
        initToolbar();
        toggleActionBarRightIconVisible(true);
        setActionBarRightIcon(R.drawable.ic_notice_add);
        setNavigationTitle(R.string.txt_title_today_ranking);

        mToolBar.findViewById(R.id.action_bar_notice).setOnClickListener(v-> {
            mInputType = "ADD" ;
            mMsg = "" ;
            Intent intent = new Intent(this, NoticeInputActivity.class);
            intent.putExtra("type",mInputType) ;
            intent.putExtra("msg",mMsg) ;

            startActivityForResult(intent, ResultType.NOTICE_INPUT.getValue());

        });

        app.push = null ;

        findViews();
        requestToServer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ResultType.NOTICE_INPUT.getValue()) {
            findViews();
            requestToServer();

        }
    }

    public void selectRow(TodayRank rank,Boolean isCmt , Boolean isADD) {

        AppUserBase user = DataHolder.instance().getAppUserBase() ;

        mInputType = "MOD" ;
        if (isADD) {
            mInputType = "ADD" ;
        }
        mMsg = rank.getContent() ;

        Intent intent = new Intent(this, NoticeInputActivity.class);
        intent.putExtra("type",mInputType) ;
        intent.putExtra("msg",mMsg) ;
        intent.putExtra("todayrank",rank) ;
        intent.putExtra("iscmt",isCmt) ;

        if ( !rank.getNoti_type().equals("input")) {
            // 작성한 글이 아니고 공지이면 리턴
            return ;
        }
        if (!mInputType.equals("ADD")  ) {
            if ( user.getUser_seq() != Integer.parseInt(rank.getUser_seq()) ) {
                // 본인이 작성한 글이 아니면 리턴
                return ;
            }
        }

        startActivityForResult(intent, ResultType.NOTICE_INPUT.getValue());
    }

    /**
     * 초기 데이터 설정
     */
    @Override
    protected void setInitialData() {
        if(mBbs != null && mBbs.getTodayRankList() != null && mBbs.getTodayRankList().size() > 0){
            mRecyclerAdapter = new RankingAdapter(this, mBbs.getTodayRankList());
            mRecyclerView.setAdapter(mRecyclerAdapter);
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }


    private class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RowHolder>  {
        private Context mContext;
        private List<TodayRank> mList;
        private LayoutInflater mInflater;


        RankingAdapter(Context context, List<TodayRank> list){
            mContext = context;
            mList = list;
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = null ;
            if (viewType == 0 ) {
               view = mInflater.inflate(R.layout.row_notice_ranking, parent, false);
            }
            if (viewType == 1 ) {
                view = mInflater.inflate(R.layout.row_notice_ranking_cmt, parent, false);
            }
            return new RowHolder(view);
        }

        @Override
        public int getItemViewType(int position) {

            TodayRank item = mList.get(position);
            if (item.getMnoti_seq() != null) {
                return 1 ;
            }

            return 0;
        }

        @Override
        public void onBindViewHolder(@NonNull RowHolder holder, int position) {

            TodayRank item = mList.get(position);
            //LogUtils.err(TAG, "ranking item : " + item.string());

            holder.content.setText(item.getContent());
            holder.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoticeRankingActivity act = (NoticeRankingActivity) mContext ;
                    act.selectRow(item, false, false);
                }
            });


            holder.datetime.setText(item.getDatetime("yyyy.MM.dd HH:mm") + "  " + item.getNickname() );
            holder.datetime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoticeRankingActivity act = (NoticeRankingActivity) mContext ;
                    act.selectRow(item,false,false);
                }
            });

            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoticeRankingActivity act = (NoticeRankingActivity) mContext ;
                    item.setNoti_type("input");
                    act.selectRow(item,true,true);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class RowHolder extends RecyclerView.ViewHolder{
            TextView content;
            TextView datetime;
            Button btnAdd ;
            RowHolder(View view){
                super(view);
                pickup();
            }

            void pickup(){
                content = itemView.findViewById(R.id.txt_title);
                datetime = itemView.findViewById(R.id.txt_date);
                btnAdd = itemView.findViewById(R.id.btn1101) ;
            }
        }
    }

    private void requestToServer(){
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        //query.put("baseDate", "20180616");
        query.put("baseDate", DateUtil.currentDate("yyyyMMdd"));
        Call<Bbs> call = new DefaultRestClient<UserService>(getBaseContext())
                    .getClient(UserService.class).todayRanks(query);
        call.enqueue(new Callback<Bbs>() {
            @Override
            public void onResponse(Call<Bbs> call, Response<Bbs> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    mBbs = response.body();
                    /*for( TodayRank tr : mBbs.getTodayRankList()){
                        LogUtils.err(TAG, "ranking : " + tr.string());
                    }*/
                }
                setInitialData();
            }

            @Override
            public void onFailure(Call<Bbs> call, Throwable t) {
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    @Override
    protected void findViews() {
        mRecyclerView = getView(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void attachEvents() {}

    @Override
    public void performClick(View view) {}

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
    }
}

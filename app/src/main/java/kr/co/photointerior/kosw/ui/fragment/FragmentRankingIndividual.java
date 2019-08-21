package kr.co.photointerior.kosw.ui.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.PeriodType;
import kr.co.photointerior.kosw.listener.FragmentClickListener;
import kr.co.photointerior.kosw.listener.ItemClickListener;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.ActivityRecord;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.Ranking;
import kr.co.photointerior.kosw.rest.model.SpinnerRow;
import kr.co.photointerior.kosw.ui.BaseActivity;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 개인랭킹
 */
public class FragmentRankingIndividual extends BaseFragment implements ItemClickListener{
    private final String TAG = LogUtils.makeLogTag(FragmentRankingIndividual.class);

    private int[] mBtnResId = {
      R.id.btn_daily,
      R.id.btn_weekly,
      R.id.btn_monthly,
      R.id.btn_all
    };

    private int mSelectedBtnId = mBtnResId[0];
    private SpinnerRow mSelectedSpinner;
    private RecyclerView mRecyclerView;
    private SpinnerAdapter mAdapter;
    private List<SpinnerRow> mSpinnerItems = new ArrayList<>();
    private TextView mSpinnerText;
    private View mSpinnerListBox;
    private PeriodType mPeriodType = PeriodType.DAILY;

    private ActivityRecord mActivityRecord;
    private RecyclerView mRankingRecyclerView;
    private RankingAdapter mRankingAdapter;

    public static BaseFragment newInstance(BaseActivity context){
        FragmentRankingIndividual frag = new FragmentRankingIndividual();
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
        mRecyclerView = getView(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mSpinnerListBox = getView(R.id.drop_list_box);
        mSpinnerText = getView(R.id.txt_spinner);

        mRankingRecyclerView = getView(R.id.rankingRecyclerView);
        mRankingRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

    }

    @Override
    protected void attachEvents() {
        View.OnClickListener listener = new FragmentClickListener(this);
        for( int rs : mBtnResId ){
            getView(rs).setOnClickListener(listener);
        }
        getView(R.id.box_spinner).setOnClickListener(listener);
    }

    @Override
    public void performFragmentClick(View view) {
        int id = view.getId();
        if(id == R.id.box_spinner){
            if(mSpinnerListBox.getVisibility() == View.GONE){
                mSpinnerListBox.setVisibility(View.VISIBLE);
            }else{
                mSpinnerListBox.setVisibility(View.GONE);
            }
        }else {
            if (id == mSelectedBtnId) {
                return;
            }
            toggleBtn(id);
        }
    }

    @Override
    public void requestToServer() {
        showSpinner("");
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("period", mPeriodType.getValue());
        //query.put("div", mSelectedSpinner.getBasis());
        /* 전체 사용자만 보여주도록 */
        query.put("div", "A");

        Call<ActivityRecord> call =
                new DefaultRestClient<UserService>(mActivity)
                        .getClient(UserService.class)
                        .getRankingPrivate(query);
        call.enqueue(new Callback<ActivityRecord>() {
            @Override
            public void onResponse(Call<ActivityRecord> call, Response<ActivityRecord> response) {
                closeSpinner();
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()) {
                    LogUtils.err(TAG, response.body().string());
                    ActivityRecord rank = response.body();
                    if(rank.isSuccess()){
                        mActivityRecord = rank;
                        drawList();
                    }
                }
            }

            @Override
            public void onFailure(Call<ActivityRecord> call, Throwable t) {
                closeSpinner();
            }
        });
    }

    @Override
    protected void setInitialData() {

        AppUserBase  user = DataHolder.instance().getAppUserBase() ;
        mSpinnerItems.clear();

        if ( user.getCust_build_seq().equals(user.getBuild_seq())) {
            mSpinnerItems.add(new SpinnerRow("C", getString(R.string.rank_individual_0)));//회사 내 개인랭킹
            mSpinnerItems.add(new SpinnerRow("D", getString(R.string.rank_individual_1)));//부서 내 개인랭킹
            mSpinnerItems.add(new SpinnerRow("A", getString(R.string.rank_individual_2)));//전체 사용자 내 개인랭킹
        } else {
            mSpinnerItems.add(new SpinnerRow("C", getString(R.string.rank_individual_3)));//회사 내 개인랭킹
            mSpinnerItems.add(new SpinnerRow("A", getString(R.string.rank_individual_2)));//전체 사용자 내 개인랭킹
            //mSpinnerItems.add(new SpinnerRow("D", getString(R.string.rank_individual_1)));//부서 내 개인랭킹
        }

        drawSpinner();
        requestToServer();
    }

//    /**
//     * 개인 랭킹 서버 요청
//     */
//    private void requestRankingToServer(){
//        showSpinner("");
//        Map<String, Object> query = KUtil.getDefaultQueryMap();
//        query.put("period", mPeriodType.getValue());
//        query.put("div", mSelectedSpinner.getBasis());
//        Call<ActivityRecord> call =
//                new DefaultRestClient<UserService>(mActivity)
//                        .getClient(UserService.class)
//                        .getRankingPrivate(query);
//        call.enqueue(new Callback<ActivityRecord>() {
//            @Override
//            public void onResponse(Call<ActivityRecord> call, Response<ActivityRecord> response) {
//                closeSpinner();
//                LogUtils.err(TAG, response.raw().toString());
//                if(response.isSuccessful()) {
//                    LogUtils.err(TAG, response.body().string());
//                    ActivityRecord rank = response.body();
//                    if(rank.isSuccess()){
//                        mActivityRecord = rank;
//                        drawList();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ActivityRecord> call, Throwable t) {
//                closeSpinner();
//            }
//        });
//    }

    /**
     * 랭킹 리스트 표시
     */
    private void drawList(){
        List<Ranking> ranks;
        if(mActivityRecord != null && mActivityRecord.getRankingPrivateList() != null){
            ranks = mActivityRecord.getRankingPrivateList();
        }else{
            ranks = new ArrayList<>();
        }
        //ranks.forEach(v -> LogUtils.err(TAG, v.string()));
        for( Ranking rk : ranks ){
            LogUtils.err(TAG, rk.string());
        }
        int myRankingPosition = pickupMyRankingPosition(ranks);
        if(myRankingPosition > -1 ){//나의 랭킹 없음.
            if(myRankingPosition > 3 ){//3위 안에 없을 경우 0번째로 이동
                //Ranking myRanking = ranks.remove(myRankingPosition);
                Ranking myRanking = ranks.get(myRankingPosition);
                ranks.add(0, myRanking);
            }
        }
        mRankingAdapter = new RankingAdapter(mActivity, ranks, this);
        mRankingRecyclerView.setAdapter(mRankingAdapter);
    }

    /**
     * 나의 랭킹이 있는 index 반환
     * @param list
     * @return
     */
    private int pickupMyRankingPosition(List<Ranking> list){
        int index = -1;
        for( int i = 0, k = list.size(); i < k; i++){
            Ranking rk = list.get(i);
            if( rk.isMe() ){
                index = i;
                break;
            }
        }
        return index;
    }

    private class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingRowHolder>{
        private Context mContext;
        private List<Ranking> mItems;
        private LayoutInflater mInflater;
        private ItemClickListener mItemClickListener;
        private int[] mRankIcons = {
                0,
                R.drawable.ic_ran_num1,
                R.drawable.ic_ran_num2,
                R.drawable.ic_ran_num3
        };
        RankingAdapter(Context context, List<Ranking> list, ItemClickListener listener){
            mContext = context;
            mItems = list;
            mItemClickListener = listener;
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public RankingRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if( viewType == 0 ){//ME
                view = mInflater.inflate(R.layout.row_ranking_my, parent, false);
            }else if( viewType == 1 || viewType == 2 || viewType == 3 ){//1,2,3위
                view = mInflater.inflate(R.layout.row_ranking_top, parent, false);
            }else{//그외
                view = mInflater.inflate(R.layout.row_ranking_extra, parent, false);
            }
            return new RankingRowHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RankingRowHolder holder, int position) {
            Ranking rank = mItems.get(position);
            //if(position == 0 && rank.getRankingToInt() > 4){
            if(rank.getRankingToInt() > 3){
                holder.ranking.setText(String.valueOf(rank.getRankingToInt()));
            }else{
                if(rank.getRankingToInt() == 1 || rank.getRankingToInt() == 2 || rank.getRankingToInt() == 3 ){
                    if( rank.getRankingToInt() == 1 && PeriodType.WEEKLY.equals(mPeriodType)){
                        if("C".equalsIgnoreCase(mSelectedSpinner.getBasis())){
                            holder.icon.setImageResource(R.drawable.ic_ran_wnum1);
                            holder.kosw.setVisibility(View.VISIBLE);
                            if(rank.isMe()){
                                holder.kosw.setTextColor(getResources().getColor(R.color.colorWhite));
                            }else{
                                holder.kosw.setTextColor(getResources().getColor(R.color.color_1a1a1a));
                            }
                        }else {
                            holder.icon.setImageResource(mRankIcons[rank.getRankingToInt()]);
                            holder.kosw.setVisibility(View.GONE);
                        }
                    }else {
                        holder.icon.setImageResource(mRankIcons[rank.getRankingToInt()]);
                        holder.kosw.setVisibility(View.GONE);
                    }
                }
            }
            setTextBackground(holder, rank);

            if(StringUtil.isEmptyOrWhiteSpace(rank.getNickname())){
                holder.nickname.setText(rank.getUserName());
            }else {
                holder.nickname.setText(rank.getNickname());
            }
            if("A".equalsIgnoreCase(mSelectedSpinner.getBasis())){
                //holder.departName.setText(rank.getCustomerName());
                holder.departName.setText("");
            }else{
                holder.departName.setText(rank.getDepartName());
            }
            holder.recordAmount.setText(rank.getAmount() + "F");
        }

        private void setTextBackground(RankingRowHolder holder, Ranking rank){
            int color;
            if(rank.isMe()){
                holder.itemView.setBackgroundResource(R.color.color_35c2ef);
                color = getResources().getColor(R.color.colorWhite);
            }else{
                holder.itemView.setBackgroundResource(R.drawable.button_border_bottom_cccccc);
                color = getResources().getColor(R.color.color_1a1a1a);
            }
            if(holder.ranking != null) {
                holder.ranking.setTextColor(color);
            }
            holder.nickname.setTextColor(color);
            holder.departName.setTextColor(color);
            holder.recordAmount.setTextColor(color);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            if(mItems.get(position).isMe() && mItems.get(position).getRankingToInt() > 4){
                return 0;
            }
            return mItems.get(position).getRankingToInt();
        }

        class RankingRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            ImageView icon;
            TextView kosw;
            TextView ranking;
            TextView nickname;
            TextView departName;
            TextView recordAmount;

            RankingRowHolder(View view, int viewType){
                super(view);
                pickupViews(viewType);
            }

            /**
             * @param type 0:me, ranking 1,2,3 and else
             */
            private void pickupViews(int type){
                icon = itemView.findViewById(R.id.icon_rank);
                kosw = itemView.findViewById(R.id.txt_kosw);
                ranking = itemView.findViewById(R.id.txt_ranking);
                nickname = itemView.findViewById(R.id.txt_name);
                departName = itemView.findViewById(R.id.txt_depart);
                recordAmount = itemView.findViewById(R.id.txt_amount);
            }

            @Override
            public void onClick(View view) {
                //mItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }





    /**
     * 부서선택 스피너
     */
    private void drawSpinner(){
        mSelectedSpinner = mSpinnerItems.get(0);
        selectSpinner(0);
        mAdapter = new SpinnerAdapter(getActivity(), this, mSpinnerItems);
        mRecyclerView.setAdapter(mAdapter);
    }
    private void selectSpinner(int position){
        mSelectedSpinner = mSpinnerItems.get(position);
        mSpinnerText.setText(mSelectedSpinner.getName());
        requestToServer();
    }

    @Override
    public void onItemClick(View view, int position) {
        mSpinnerListBox.setVisibility(View.GONE);
        selectSpinner(position);
//        requestRankingToServer();
    }

    private class SpinnerAdapter extends RecyclerView.Adapter<SpinnerAdapter.BaseRowHolder>{
        private Context mContext;
        private LayoutInflater mInflater;
        private List<SpinnerRow> mItems;
        private ItemClickListener mItemClickListener;

        SpinnerAdapter(Context context, ItemClickListener listener, List<SpinnerRow> list){
            mContext = context;
            mItemClickListener = listener;
            mInflater = LayoutInflater.from(context);
            mItems = list;
        }
        @NonNull
        @Override
        public BaseRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.row_depart_spinner1, parent, false);
            return new BaseRowHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BaseRowHolder holder, int position) {
            holder.title.setText(mItems.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        class BaseRowHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
            TextView title;
            public BaseRowHolder(View itemView) {
                super(itemView);
                pickupView();
            }

            protected void pickupView(){
                title = itemView.findViewById(R.id.text1);
                itemView.setOnClickListener(this);
                itemView.setTag("spinner");
            }

            @Override
            public void onClick(View view) {
                if(mItemClickListener != null){
                    mItemClickListener.onItemClick(view, getAdapterPosition());
                }
            }
        }
    }

    private void toggleBtn(int clickId){
        for( int rs : mBtnResId ){
            TextView tv = getView(rs);
            if(rs == clickId){
                mSelectedBtnId = rs;
                tv.setTextColor(getResources().getColor(R.color.colorWhite));
                tv.setBackgroundColor(getResources().getColor(R.color.color_1a1a1a));
            }else{
                tv.setTextColor(getResources().getColor(R.color.color_545454));
                tv.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
            }
        }
        if(mSelectedBtnId == R.id.btn_daily){
            mPeriodType = PeriodType.DAILY;
        }else if(mSelectedBtnId == R.id.btn_weekly){
            mPeriodType = PeriodType.WEEKLY;
        }else if(mSelectedBtnId == R.id.btn_monthly){
            mPeriodType = PeriodType.MONTHLY;
        }else if(mSelectedBtnId == R.id.btn_all){
            mPeriodType = PeriodType.ALL;
        }
        requestToServer();
    }

    @Override
    public int getViewResourceId() {
        return R.layout.fragment_ranking_individual;
    }
}

package kr.co.photointerior.kosw.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.ActivityRecord;
import kr.co.photointerior.kosw.rest.model.Club;
import kr.co.photointerior.kosw.rest.model.Record;
import kr.co.photointerior.kosw.ui.BaseActivity;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import kr.co.photointerior.kosw.widget.RowActivityRecord;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 회원의 계단 활동 기록
 */
public class FragmentActivityRecord extends BaseFragment {
    private final String TAG = LogUtils.makeLogTag(FragmentActivityRecord.class);
    private ActivityRecord mActivityRecord;
    private RecyclerView mRecyclerView;
    private RecordAdapter mAdapter;
    public static BaseFragment newInstance(BaseActivity context){
        FragmentActivityRecord frag = new FragmentActivityRecord();
        frag.mActivity = context;
        return frag;
    }

    @Override
    protected void followingWorksAfterInflateView() {
        findViews();
        attachEvents();
        requestToServer();
    }

    @Override
    protected void findViews() {
        mActivity.toggleActionBarResources(false);
        mRecyclerView = getView(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    @Override
    protected void attachEvents() {

    }

    /** 활동기록 데이터 서버에서 획득 후 처리 */
    @Override
    public void requestToServer() {
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        Log.d("TTTTTTTTTTTTTTTTTT", query.get("token").toString());
        Log.d("TTTTTTTTTTTTTTTTTT", query.get("device").toString());
        Log.d("TTTTTTTTTTTTTTTTTT", query.get("buildCode").toString());
        Call<ActivityRecord> call =
                new DefaultRestClient<UserService>(mActivity).getClient(UserService.class)
                    .getActivityRecords(query);
        call.enqueue(new Callback<ActivityRecord>() {
            @Override
            public void onResponse(Call<ActivityRecord> call, Response<ActivityRecord> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ActivityRecord record = response.body();
                    if(record.isSuccess()){
                        mActivityRecord = record;
                        setInitialData();
                    }else{
                        toast(record.getResponseMessage());
                    }
                }else{
                    toast(R.string.warn_commu_to_server);
                }
            }

            @Override
            public void onFailure(Call<ActivityRecord> call, Throwable t) {
                //toast(R.string.warn_commu_to_server);
                LogUtils.err(TAG, t);
            }
        });
    }

    @Override
    protected void setInitialData() {
        setActivityData();
        drawList();
    }

    /** 화면상단 활동 기록 반영 */
    private void setActivityData(){
        TextView period = getView(R.id.txt_activity_period);
        RowActivityRecord totalFloor = getView(R.id.row_record_floor);
        RowActivityRecord totalWalk = getView(R.id.row_record_walk);
        RowActivityRecord totalCalorie = getView(R.id.row_record_calorie);
        RowActivityRecord totalLife = getView(R.id.row_record_life);
        Record total = mActivityRecord.getTotalRecord();
        LogUtils.err(TAG, "total : " + total.string());
        period.setText(total.getStartDate().concat("~").concat(total.getEndDate()));
        totalFloor.setRecordAmount(StringUtil.format(total.getAmountToFloat(), "#,##0"));
        totalWalk.setRecordAmount(StringUtil.format(total.getWalkAmountToFloat(), "#,##0"));
        totalCalorie.setRecordAmount(KUtil.calcCalorie(total.getAmountToFloat(), total.getStairAmountToFloat()));
        totalLife.setRecordAmount(KUtil.calcLife(total.getAmountToFloat(), total.getStairAmountToFloat()));
    }

    private void drawList(){
        List<Club> clubList = mActivityRecord.getClubList() ;
        List<Record> list = mActivityRecord.getDailyRecords();
        if(list != null ) {
            list.add(0, mActivityRecord.getMaxAmount());
            list.add(0, mActivityRecord.getDailyAverage());

            if (mAdapter == null) {
                mAdapter = new RecordAdapter(mActivity, list ,clubList);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RowHolder>{
        private Context mContext;
        private List<Record> mList;
        private List<Club> mClubList;
        private LayoutInflater mInflater;
        RecordAdapter(Context context, List<Record> list , List<Club> clubList ){
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mList = list;
            mClubList = clubList ;
        }

        @NonNull
        @Override
        public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;
            if(viewType < 3 ){
                view = mInflater.inflate(R.layout.row_activity_record_summary, parent, false);
            }else{
                view = mInflater.inflate(R.layout.row_activity_record_daily, parent, false);
            }
            if (viewType == 1 ) {
                view.setBackgroundResource( R.color.color_FFCCECFC);
            } else {
                view.setBackgroundResource(R.color.colorWhite );
            }
            RowHolder holder = new RowHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RowHolder holder, int position) {

            int clubCount = mClubList.size() ;
            holder.topLine.setVisibility(View.INVISIBLE);

            //클럽이 먼저 나오고 활동 기록이 나옴
            if (position < clubCount) {
                Club item = mClubList.get(position);
                holder.title.setText(Club.getClubName(item.getClub_kind()) + " " + item.getRegi_date());
                holder.floor.setText(StringUtil.format(item.getStair_amtToFloat(), "#,##0"));
                holder.calorie.setText(KUtil.calcCalorieDefault(item.getStair_amtToFloat()) );
                holder.life.setText(KUtil.calcLifeDefault(item.getStair_amtToFloat()) );

                holder.ll_walk.setVisibility(View.GONE);
            } else {
                Record item = mList.get(position - clubCount);
                if(position == clubCount ) {
                    holder.title.setText("매일평균");
                }else if(position== clubCount + 1){
                    holder.title.setText("최고기록 (".concat(item.getDate("yyyy.MM.dd") + "/" + item.getWalkDate("yyyy.MM.dd")) + ")");
                }else{
                    holder.title.setText(item.getDate("yyyy.MM.dd"));
                }

                holder.floor.setText(StringUtil.format(item.getAmountToFloat(), "#,##0"));
                holder.walk.setText(StringUtil.format(item.getWalkAmountToFloat(), "#,##0"));
                holder.calorie.setText(KUtil.calcCalorie(item.getAmountToFloat(), item.getStairAmountToFloat()));
                holder.life.setText(KUtil.calcLife(item.getAmountToFloat(), item.getStairAmountToFloat()));
            }

        }

        @Override
        public int getItemViewType(int position) {
            if(position < mClubList.size() ){//클럽달성기록
                return 1;
            }else if(position - mClubList.size()  ==  0){//평균기록
                return 2;
            }else if(position - mClubList.size()  ==  1){//최고기록
                return 2;
            }
            return 3;
        }

        @Override
        public int getItemCount() {
            return mList.size() + mClubList.size() ;
        }

        class RowHolder extends RecyclerView.ViewHolder{
            private View topLine;
            private TextView title;
            private TextView floor;
            private TextView walk;
            private TextView calorie;
            private TextView life;
            private LinearLayout ll_walk;

            RowHolder(View view){
                super(view);
                pickupView();
            }

            void pickupView(){
                topLine = itemView.findViewById(R.id.top_line);
                title = itemView.findViewById(R.id.txt_title);
                floor = itemView.findViewById(R.id.amt_floor);
                walk = itemView.findViewById(R.id.amt_walk);
                calorie = itemView.findViewById(R.id.amt_calorie);
                life = itemView.findViewById(R.id.amt_health);
                ll_walk = itemView.findViewById(R.id.ll_walk);
            }
        }
    }

    @Override
    public int getViewResourceId() {
        return R.layout.fragment_activity_record;
    }
}

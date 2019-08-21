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
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.PeriodType;
import kr.co.photointerior.kosw.listener.FragmentClickListener;
import kr.co.photointerior.kosw.listener.ItemClickListener;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.ActivityPeriod;
import kr.co.photointerior.kosw.rest.model.ActivityRecord;
import kr.co.photointerior.kosw.rest.model.Record;
import kr.co.photointerior.kosw.ui.BaseActivity;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import kr.co.photointerior.kosw.widget.RowActivityRecord;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 도움말 1페이지
 */
public class FragmentActivityAnalysisByPeriod extends BaseFragment implements ItemClickListener {
    private String TAG = LogUtils.makeLogTag(FragmentActivityAnalysisByPeriod.class);
    private List<ActivityPeriod> mSpinnerItems;
    private ActivityPeriod mSelectedSpinner;
    private RecyclerView mRecyclerView;
    private SpinnerAdapter mAdapter;
    private TextView mSpinnerText;
    private View mSpinnerListBox;
    private PeriodType mPeriodType = PeriodType.WEEKLY;
    private ActivityRecord mActivityRecord;
    private RowActivityRecord[] mSummaries = new RowActivityRecord[3];
    private BarChart mBarChart;
    public static FragmentActivityAnalysisByPeriod newInstance(
            BaseActivity activity, List<ActivityPeriod> spinnerList, PeriodType periodType){
        FragmentActivityAnalysisByPeriod frag = new FragmentActivityAnalysisByPeriod();
        frag.mActivity = activity;
        frag.mSpinnerItems = spinnerList;
        frag.mPeriodType = periodType;
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
        mRecyclerView = getView(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mSpinnerListBox = getView(R.id.drop_list_box);
        mSpinnerText = getView(R.id.txt_spinner);
        mSummaries[0] = getView(R.id.row_record_calorie);
        mSummaries[1] = getView(R.id.row_record_life);
        mSummaries[2] = getView(R.id.row_record_ranking);
        mBarChart = getView(R.id.bar_chart);
    }

    @Override
    protected void attachEvents() {
        View.OnClickListener listener = new FragmentClickListener(this);
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
        }
    }

    @Override
    protected void setInitialData() {
        drawSpinner();
        requestToServer();
    }

    @Override
    public void requestToServer() {
        showSpinner("");
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("period", mPeriodType.getValue());
        query.put("baseDate", mSelectedSpinner.getBaseDate());

        Call<ActivityRecord> call = new DefaultRestClient<UserService>(mActivity).getClient(UserService.class)
                .getAnalysisData(query);
        call.enqueue(new Callback<ActivityRecord>() {
            @Override
            public void onResponse(Call<ActivityRecord> call, Response<ActivityRecord> response) {
                closeSpinner();
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ActivityRecord record = response.body();
                    if(record.isSuccess()){
                        mActivityRecord = record;

                        drawViewData();
                    }else{
                        toast(record.getResponseMessage());
                    }
                }else {
                    toast(R.string.warn_commu_to_server);
                }

            }

            @Override
            public void onFailure(Call<ActivityRecord> call, Throwable t) {
                toast(R.string.warn_commu_to_server);
                LogUtils.err(TAG, t);
            }
        });
    }

    private void drawViewData(){
        if(mActivityRecord == null || mActivityRecord.getAnalysisRecords() == null ||
                mActivityRecord.getAnalysisRecords().size() == 0 ){
            toast("분석 데이터가 없습니다.");
            return;
        }

        setDataIntoViews();
    }

    /**
     * 칼로리, 건강수명, 랭킹정보 표시
     */
    private void setDataIntoViews(){
        mSummaries[0].setRecordAmount(
                KUtil.calcCalorieDefault(mActivityRecord.getAnalysisTotalToInt()));
        mSummaries[1].setRecordAmount(
                KUtil.calcLifeDefault(mActivityRecord.getAnalysisTotalToInt()));
        mSummaries[2].setRecordAmount(mActivityRecord.getAnalysisRanking());
        drawChart();
    }

    private int[] mWeeklyBarColor = {
            Color.rgb(239, 50, 41),
            Color.rgb(255, 136, 0),
            Color.rgb(255, 190, 0),
            Color.rgb(0, 166, 81),
            Color.rgb(0, 190, 243),
            Color.rgb(70, 68, 156),
            Color.rgb(166, 79, 158)
    };

    private int[] mWeeklyBarColorNoElement = {
            Color.rgb(204, 204, 204),
            Color.rgb(204, 204, 204),
            Color.rgb(204, 204, 204),
            Color.rgb(204, 204, 204),
            Color.rgb(204, 204, 204),
            Color.rgb(204, 204, 204),
            Color.rgb(204, 204, 204)
    };

    private List<Integer> mXValuecolor;
    {
        mXValuecolor = new ArrayList<>();
        mXValuecolor.add(Color.rgb(239, 50, 41));
        mXValuecolor.add(Color.rgb(255, 136, 0));
        mXValuecolor.add(Color.rgb(255, 190, 0));
        mXValuecolor.add(Color.rgb(0, 166, 81));
        mXValuecolor.add(Color.rgb(0, 190, 243));
        mXValuecolor.add(Color.rgb(70, 68, 156));
        mXValuecolor.add(Color.rgb(166, 79, 158));
    }
    /**
     * 막대그래프 그림.
     */
    private void drawChart(){
        String[] xTexts;
        if(PeriodType.WEEKLY.equals(mPeriodType)){
            xTexts = mActivity.getResources().getStringArray(R.array.week_days);
        }else{
            LogUtils.err(TAG, "analysis record size=" + mActivityRecord.getAnalysisRecords().size());
            xTexts = new String[mActivityRecord.getAnalysisRecords().size()];
            for(int i = 0; i < xTexts.length ; i++){
                xTexts[i] = String.valueOf(i+1);
            }
        }

        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawValueAboveBar(true);

        mBarChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be drawn
        mBarChart.setMaxVisibleValueCount(60);

        mBarChart.setPinchZoom(false);
        mBarChart.setDrawGridBackground(false);
        mBarChart.setHighlightPerTapEnabled(false);

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(xTexts.length);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                int index = (int)value;
                LogUtils.err(TAG, "x value=" + value + ", index=" + index);
                if(index >= xTexts.length){
                    return "";
                }
                return xTexts[index];
            }
        });
        //YAxis leftAxis = mBarChart.getAxisLeft();

        YAxis leftAxis = mBarChart.getAxisLeft();
        leftAxis.setLabelCount(10, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setEnabled(false);

        YAxis rightAxis = mBarChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend l = mBarChart.getLegend();
        l.setEnabled(false);

        float start = 0f;

        List<Record> stepValues = mActivityRecord.getAnalysisRecords();
        List<BarEntry> yVals1 = new ArrayList<>();
        boolean hasChartValue = hasChartValues(stepValues);
        if(hasChartValue) {
            for (int i = (int) start; i < stepValues.size(); i++) {
                yVals1.add(new BarEntry(i, stepValues.get(i).getAmountToFloat()));
            }
        }else{
            for (int i = (int) start; i < stepValues.size(); i++) {
                yVals1.add(new BarEntry(i, 100f));
            }
        }

        BarDataSet set1 = new BarDataSet(yVals1, "period analysis");
        set1.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if(value > 0.0f && hasChartValue) {
                    return StringUtil.format(value, "#,##0");
                }
                return "";
            }
        });
        if(PeriodType.WEEKLY.equals(mPeriodType)) {
            set1.setValueTextColors(mXValuecolor);//값 텍스트 칼라
        }else{
            //set1.setValueTextColors(ColorTemplate.VORDIPLOM_COLORS);//값 텍스트 칼라
        }

        set1.setDrawIcons(false);
        set1.setHighlightEnabled(false);
        //set1.setColor(Color.rgb(159, 206, 239));
        //set1.setColor(Color.rgb(55, 163, 210));
        if(PeriodType.WEEKLY.equals(mPeriodType)) {
            if(hasChartValue) {
                set1.setColors(mWeeklyBarColor);
            }else{
                set1.setColors(mWeeklyBarColorNoElement);
            }
        }else{
            if(hasChartValue) {
                List<Integer> monthColors = getMonthlyColors(stepValues);
                set1.setColors(monthColors);
            }else{
                set1.setColor(getResources().getColor(R.color.color_cccccc));
            }
        }
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);

        mBarChart.setData(data);

        mBarChart.invalidate();
        mBarChart.notifyDataSetChanged();
    }

    private List<Integer> getMonthlyColors(List<Record> values){
        List<Integer> list = new ArrayList<>();
        for( Record rc : values){
            if("일".equals(rc.getWeekName())){
                list.add(mWeeklyBarColor[0]);
            }else if("월".equals(rc.getWeekName())){
                list.add(mWeeklyBarColor[1]);
            }else if("화".equals(rc.getWeekName())){
                list.add(mWeeklyBarColor[2]);
            }else if("수".equals(rc.getWeekName())){
                list.add(mWeeklyBarColor[3]);
            }else if("목".equals(rc.getWeekName())){
                list.add(mWeeklyBarColor[4]);
            }else if("금".equals(rc.getWeekName())){
                list.add(mWeeklyBarColor[5]);
            }else if("토".equals(rc.getWeekName())){
                list.add(mWeeklyBarColor[6]);
            }
        }
        return list;
    }

    /**
     * 챠트 데이터 값이 있는가 여부 검사
     * @param values
     * @return
     */
    private boolean hasChartValues(List<Record> values){
        for( Record rd : values){
            if(rd.getAmountToFloat() > 0f){
                return true;
            }
        }
        return false;
    }

    /**
     * 부서선택 스피너
     */
    private void drawSpinner(){
        //mSelectedSpinner = mSpinnerItems.get(0);
        mAdapter = new SpinnerAdapter(mActivity, this, mSpinnerItems);
        mRecyclerView.setAdapter(mAdapter);
        selectSpinner(0);
    }

    private void selectSpinner(int position){
        if(mSpinnerItems == null){
            return;
        }
        ActivityPeriod ap = mSpinnerItems.get(position);
        if(mSelectedSpinner != null &&
                mSelectedSpinner.getVisibleString().equals(ap.getVisibleString())){
            return;
        }
        mSelectedSpinner = ap;
        mSpinnerText.setText(mSelectedSpinner.getVisibleString());
        requestToServer();
    }

    @Override
    public void onItemClick(View view, int position) {
        toggleSpinnerBox(View.GONE);
        selectSpinner(position);
    }

    public void toggleSpinnerBox(int visibility){
        if(mSpinnerListBox != null) {
            mSpinnerListBox.setVisibility(visibility);
        }
    }

    private class SpinnerAdapter extends RecyclerView.Adapter<SpinnerAdapter.BaseRowHolder>{
        private Context mContext;
        private LayoutInflater mInflater;
        private List<ActivityPeriod> mItems;
        private ItemClickListener mItemClickListener;

        SpinnerAdapter(Context context, ItemClickListener listener, List<ActivityPeriod> list){
            mContext = context;
            mItemClickListener = listener;
            mInflater = LayoutInflater.from(context);
            mItems = list;
        }
        @NonNull
        @Override
        public SpinnerAdapter.BaseRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.row_depart_spinner1, parent, false);
            return new SpinnerAdapter.BaseRowHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SpinnerAdapter.BaseRowHolder holder, int position) {
            holder.title.setText(mItems.get(position).getVisibleString());
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

    @Override
    public int getViewResourceId() {
        return R.layout.fragment_activity_analysis_weekly;
    }
}

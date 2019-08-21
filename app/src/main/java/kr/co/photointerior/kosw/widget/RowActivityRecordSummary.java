package kr.co.photointerior.kosw.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.photointerior.kosw.R;

/**
 * 각 화면에서 사용되는 활동기록 매일평균, 최고기록 뷰
 */
public class RowActivityRecordSummary extends LinearLayout {
    private LinearLayout mBox;
    private TextView mTitle;
    private TextView mAmountFloor;
    private TextView mAmountCalorie;
    private TextView mAmountHealth;
    private View mTopLine;
    private View mBottomLine;

    public RowActivityRecordSummary(Context context) {
        super(context);
        initView(context);
    }

    public RowActivityRecordSummary(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        getAttr(attrs);
    }

    public RowActivityRecordSummary(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        getAttr(attrs, defStyleAttr);
    }

    public RowActivityRecordSummary(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
        getAttr(attrs, defStyleAttr);
    }

    private void initView(Context context){
        inflate(context, R.layout.row_activity_record_summary, this);
        mBox = findViewById(R.id.box_of_row);
        mTitle = findViewById(R.id.txt_title);
        mAmountFloor = findViewById(R.id.amt_floor);
        mAmountCalorie = findViewById(R.id.amt_calorie);
        mAmountHealth = findViewById(R.id.amt_health);
        mTopLine = findViewById(R.id.top_line);
        mBottomLine = findViewById(R.id.bottom_line);
    }

    private void getAttr(AttributeSet attrs){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RowActivityRecordSummary);
        setTypedArray(typedArray);
    }
    private void getAttr(AttributeSet attrs, int defStyle){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RowActivityRecordSummary, defStyle, 0);
        setTypedArray(typedArray);
    }

    private void setTypedArray(TypedArray typedArray){
//        int bgColor = typedArray.getResourceId(R.styleable.ActivityRecord_recordBg, R.color.colorWhite);
//        mBox.setBackgroundResource(bgColor);

//        int iconId = typedArray.getResourceId(R.styleable.RowActivityRecord_recordIcon, 0);
//        mIcon.setImageResource(iconId);

//        String textColor = typedArray.getString(R.styleable.RowActivityRecord_recordTitle);
//        mTitle.setText(textColor);
//
//        String textRecord = typedArray.getString(R.styleable.RowActivityRecord_recordAmt);
//        mAmount.setText(textRecord);
//
//        int textRecordColor = typedArray.getColor(R.styleable.RowActivityRecord_recordAmtColor, 0);
//        mAmount.setTextColor(textRecordColor);
//
//        String textUnit = typedArray.getString(R.styleable.RowActivityRecord_recordUnit);
//        mUnit.setText(textUnit);
        if(typedArray != null) {
            String title = typedArray.getString(R.styleable.RowActivityRecordSummary_recordTitle);
            mTitle.setText(title);

            boolean showTopLine = typedArray.getBoolean(R.styleable.RowActivityRecordSummary_showLineTop, true);
            boolean showBottomLine = typedArray.getBoolean(R.styleable.RowActivityRecordSummary_showLineBottom, true);
            mTopLine.setVisibility(showTopLine ? View.VISIBLE : View.GONE);
            mBottomLine.setVisibility(showBottomLine ? View.VISIBLE : View.GONE);
            typedArray.recycle();
        }
    }

    public TextView getRecordTitle() {
        return mTitle;
    }

    public void setRecordTitle(String title) {
        this.mTitle.setText(title);
    }

    public TextView getAmountFloor() {
        return mAmountFloor;
    }

    public void setAmountFloor(String amountFloor) {
        this.mAmountFloor.setText(amountFloor);
    }

    public TextView getAmountCalorie() {
        return mAmountCalorie;
    }

    public void setAmountCalorie(String amountCalorie) {
        this.mAmountCalorie.setText(amountCalorie);
    }

    public TextView getAmountHealth() {
        return mAmountHealth;
    }

    public void setAmountHealth(String amountHealth) {
        this.mAmountHealth.setText(amountHealth);
    }

    public void toggleTopLine(boolean isShow){
        this.mTopLine.setVisibility(isShow? View.VISIBLE: View.GONE);
    }
    public void toggleBottomLine(boolean isShow){
        this.mBottomLine.setVisibility(isShow? View.VISIBLE: View.GONE);
    }
}

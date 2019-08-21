package kr.co.photointerior.kosw.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.photointerior.kosw.R;

/**
 * 각 화면에서 사용되는 활동기록 구성 뷰
 */
public class RowActivityRecord extends LinearLayout {
    private LinearLayout mBox;
    private ImageView mIcon;
    private TextView mTitle;
    private TextView mAmount;
    private TextView mUnit;

    public RowActivityRecord(Context context) {
        super(context);
        initView(context);
    }

    public RowActivityRecord(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        getAttr(attrs);
    }

    public RowActivityRecord(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        getAttr(attrs, defStyleAttr);
    }

    public RowActivityRecord(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
        getAttr(attrs, defStyleAttr);
    }

    private void initView(Context context){
        inflate(context, R.layout.row_activity_record, this);
        mBox = findViewById(R.id.box_of_row);
        mIcon = findViewById(R.id.icon);
        mTitle = findViewById(R.id.txt_title);
        mAmount = findViewById(R.id.txt_amt);
        mUnit = findViewById(R.id.txt_unit);
    }

    private void getAttr(AttributeSet attrs){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RowActivityRecord);
        setTypedArray(typedArray);
    }
    private void getAttr(AttributeSet attrs, int defStyle){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RowActivityRecord, defStyle, 0);
        setTypedArray(typedArray);
    }

    private void setTypedArray(TypedArray typedArray){
        int bgColor = typedArray.getResourceId(R.styleable.RowActivityRecord_recordBg, -1);
        if(bgColor > 0 ) {
            mBox.setBackgroundResource(bgColor);
        }

        int iconId = typedArray.getResourceId(R.styleable.RowActivityRecord_recordIcon, 0);
        mIcon.setImageResource(iconId);

        String textColor = typedArray.getString(R.styleable.RowActivityRecord_recordTitle);
        mTitle.setText(textColor);

        String textRecord = typedArray.getString(R.styleable.RowActivityRecord_recordAmt);
        mAmount.setText(textRecord);

        int textRecordColor = typedArray.getColor(R.styleable.RowActivityRecord_recordAmtColor, 0);
        mAmount.setTextColor(textRecordColor);

        String textUnit = typedArray.getString(R.styleable.RowActivityRecord_recordUnit);
        mUnit.setText(textUnit);

        typedArray.recycle();
    }

    public ImageView getRecordIcon() {
        return mIcon;
    }

    public void setRecordIcon(int iconRes) {
        this.mIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), iconRes));
    }

    public TextView getRecordTitle() {
        return mTitle;
    }

    public void setRecordTitle(String title) {
        this.mTitle.setText(title);
    }

    public TextView getRecordAmount() {
        return mAmount;
    }

    public void setRecordAmount(String amount) {
        this.mAmount.setText(amount);
    }

    public TextView getRecordUnit() {
        return mUnit;
    }

    public void setRecordUnit(String unit) {
        this.mUnit.setText(unit);
    }
}

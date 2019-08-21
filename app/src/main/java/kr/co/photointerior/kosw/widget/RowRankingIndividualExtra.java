package kr.co.photointerior.kosw.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.photointerior.kosw.R;

/**
 * 개인, 그룹랭킹 1,2,3위 제외한 일반 랭킹
 */
public class RowRankingIndividualExtra extends LinearLayout {
    private LinearLayout mBox;
    private TextView mRanking;
    private TextView mNickname;
    private TextView mDepart;
    private TextView mAmount;

    public RowRankingIndividualExtra(Context context) {
        super(context);
        initView(context);
    }

    public RowRankingIndividualExtra(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        getAttr(attrs);
    }

    public RowRankingIndividualExtra(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        getAttr(attrs, defStyleAttr);
    }

    public RowRankingIndividualExtra(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
        getAttr(attrs, defStyleAttr);
    }

    private void initView(Context context){
        inflate(context, R.layout.row_ranking_extra, this);
        mBox = findViewById(R.id.box_of_row);
        mRanking = findViewById(R.id.txt_ranking);
        mNickname = findViewById(R.id.txt_name);
        mDepart = findViewById(R.id.txt_depart);
        mAmount = findViewById(R.id.txt_amount);
    }

    private void getAttr(AttributeSet attrs){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RowRankingIndividualExtra);
        setTypedArray(typedArray);
    }
    private void getAttr(AttributeSet attrs, int defStyle){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RowRankingIndividualExtra, defStyle, 0);
        setTypedArray(typedArray);
    }

    private void setTypedArray(TypedArray typedArray){
        if( typedArray!=null ) {
            int bgColor = typedArray.getResourceId(R.styleable.RowRankingIndividualExtra_recordBg, -1);
            if (bgColor > 0) {
                mBox.setBackgroundResource(bgColor);
            }
            int textColor = typedArray.getColor(R.styleable.RowRankingIndividualExtra_recordTextColor, -1);
            this.mRanking.setTextColor(textColor);
            this.mNickname.setTextColor(textColor);
            this.mDepart.setTextColor(textColor);
            this.mAmount.setTextColor(textColor);
            typedArray.recycle();
        }
    }

    public TextView getRanking(){
        return mRanking;
    }

    public void setRanking(String ranking) {
        this.mRanking.setText(ranking);
    }

    public TextView getNickname() {
        return mNickname;
    }

    public void setNickname(String nickname) {
        this.mNickname.setText(nickname);
    }

    public TextView getDepart() {
        return mDepart;
    }

    public void setDepart(String depart) {
        this.mDepart.setText(depart);
    }

    public TextView getAmount() {
        return mAmount;
    }

    public void setAmount(String amount) {
        this.mAmount.setText(amount);
    }

    public void setTextColors(int color){
        this.mRanking.setTextColor(color);
        this.mNickname.setTextColor(color);
        this.mDepart.setTextColor(color);
        this.mAmount.setTextColor(color);
    }

    private int getColor(int color){
        return getResources().getColor(color);
    }

    public void setCustomFontStyle(int typeface){
        this.mRanking.setTypeface(mRanking.getTypeface(), typeface);
        this.mNickname.setTypeface(mNickname.getTypeface(), typeface);
        this.mDepart.setTypeface(mDepart.getTypeface(), typeface);
        this.mAmount.setTypeface(mAmount.getTypeface(), typeface);
    }

    public void setFontSize(int fontSize){
        this.mRanking.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
        this.mNickname.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
        this.mDepart.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
        this.mAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
    }
}

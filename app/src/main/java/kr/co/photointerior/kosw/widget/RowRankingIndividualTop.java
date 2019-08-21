package kr.co.photointerior.kosw.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.photointerior.kosw.R;

/**
 * 개인, 그룹랭킹 1,2,3위
 */
public class RowRankingIndividualTop extends LinearLayout {
    private LinearLayout mBox;
    private ImageView mIcon;
    private TextView mNickname;
    private TextView mDepart;
    private TextView mAmount;

    public RowRankingIndividualTop(Context context) {
        super(context);
        initView(context);
    }

    public RowRankingIndividualTop(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        getAttr(attrs);
    }

    public RowRankingIndividualTop(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        getAttr(attrs, defStyleAttr);
    }

    public RowRankingIndividualTop(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
        getAttr(attrs, defStyleAttr);
    }

    private void initView(Context context){
        inflate(context, R.layout.row_ranking_top, this);
        mBox = findViewById(R.id.box_of_row);
        mIcon = findViewById(R.id.icon_rank);
        mNickname = findViewById(R.id.txt_name);
        mDepart = findViewById(R.id.txt_depart);
        mAmount = findViewById(R.id.txt_amount);
    }

    private void getAttr(AttributeSet attrs){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RowRankingIndividualTop);
        setTypedArray(typedArray);
    }
    private void getAttr(AttributeSet attrs, int defStyle){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RowRankingIndividualTop, defStyle, 0);
        setTypedArray(typedArray);
    }

    private void setTypedArray(TypedArray typedArray){
        if( typedArray!=null ) {
            int bgColor = typedArray.getResourceId(R.styleable.RowRankingIndividualTop_recordBg, -1);
            if (bgColor > 0) {
                mBox.setBackgroundResource(bgColor);
            }
            int iconId = typedArray.getResourceId(R.styleable.RowRankingIndividualTop_recordIcon, 0);
            mIcon.setImageResource(iconId);
            typedArray.recycle();
        }
    }

    public ImageView getIcon() {
        return mIcon;
    }

    public void setIcon(int iconResId) {
        this.mIcon.setImageResource(iconResId);
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
}

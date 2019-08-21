package kr.co.photointerior.kosw.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.co.photointerior.kosw.R;

/**
 * Drawer menu row view.
 */
public class MenuRow extends RelativeLayout {
    private Context mContext;
    private RelativeLayout mBox;
    private TextView mTitle;
    private ImageView mIconRight;
    public MenuRow(Context context) {
        super(context);
        initView(context);
    }

    public MenuRow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        getAttr(attrs);
    }

    public MenuRow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        getAttr(attrs, defStyleAttr);
    }

    public MenuRow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
        getAttr(attrs, defStyleAttr);
    }

    private void initView(Context context){
        mContext = context;
        inflate(context, R.layout.row_menu, this);
        mBox = findViewById(R.id.box_menu_row);
        mTitle = findViewById(R.id.menu_title);
        mIconRight = findViewById(R.id.ic_right);
    }

    private void getAttr(AttributeSet attrs){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MenuRow);
        setTypedArray(typedArray);
    }
    private void getAttr(AttributeSet attrs, int defStyle){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MenuRow, defStyle, 0);
        setTypedArray(typedArray);
    }

    private void setTypedArray(TypedArray typedArray){
        int bgColor = typedArray.getResourceId(R.styleable.MenuRow_menuBg, 0);
        if(bgColor > 0) {
            mBox.setBackgroundResource(bgColor);
        }

        String textColor = typedArray.getString(R.styleable.MenuRow_menuTitle);
        mTitle.setText(textColor);
        int textRecordColor = typedArray.getColor(R.styleable.MenuRow_menuTitleColor, 0);
        mTitle.setTextColor(textRecordColor);

        int iconId = typedArray.getResourceId(R.styleable.MenuRow_menuIconRight, R.drawable.ic_arrow);
        mIconRight.setImageResource(iconId);

        boolean hideRightIcon = typedArray.getBoolean(R.styleable.MenuRow_menuIconRightHide, false);
        if(hideRightIcon){
            mIconRight.setVisibility(View.INVISIBLE);
        }

        typedArray.recycle();
    }

    public RelativeLayout getMenuBox() {
        return mBox;
    }

    public void setMenuBox(RelativeLayout mBox) {
        this.mBox = mBox;
    }

    public String getMenuTitle() {
        return mTitle.getText().toString();
    }

    public void setMenuTitle(String title) {
        this.mTitle.setText(title);
    }
    public void setMenuTitleColor(int color){
        this.mTitle.setTextColor(color);
    }

    public void setIconRight(int iconRightResId) {
        //this.mIconRight.setBackgroundResource(iconRightResId);
        this.mIconRight.setImageBitmap(BitmapFactory.decodeResource(getResources(), iconRightResId));
    }
}

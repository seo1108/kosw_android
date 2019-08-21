package kr.co.photointerior.kosw.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.co.photointerior.kosw.R;

/**
 * Drawer menu row view.
 */
public class MenuRowExtra extends RelativeLayout {
    private RelativeLayout mBox;
    private TextView mTitle;
    private TextView mDesc;

    public MenuRowExtra(Context context) {
        super(context);
        initView(context);
    }

    public MenuRowExtra(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        getAttr(attrs);
    }

    public MenuRowExtra(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        getAttr(attrs, defStyleAttr);
    }

    public MenuRowExtra(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
        getAttr(attrs, defStyleAttr);
    }

    private void initView(Context context){
        inflate(context, R.layout.row_menu_extra, this);
        mBox = findViewById(R.id.box_menu_row);
        mTitle = findViewById(R.id.menu_title);
        mDesc = findViewById(R.id.menu_desc);
    }

    private void getAttr(AttributeSet attrs){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MenuRowExtra);
        setTypedArray(typedArray);
    }
    private void getAttr(AttributeSet attrs, int defStyle){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MenuRowExtra, defStyle, 0);
        setTypedArray(typedArray);
    }

    private void setTypedArray(TypedArray typedArray){
        int bgColor = typedArray.getResourceId(R.styleable.MenuRowExtra_menuExtraBg, 0);
        if(bgColor > 0) {
            mBox.setBackgroundResource(bgColor);
        }

        String textTile = typedArray.getString(R.styleable.MenuRowExtra_menuExtraTitle);
        mTitle.setText(textTile);
        int textTitleColor = typedArray.getColor(R.styleable.MenuRowExtra_menuExtraTitleColor, 0);
        if(textTitleColor > 0) {
            mTitle.setTextColor(textTitleColor);
        }

        String textDesc = typedArray.getString(R.styleable.MenuRowExtra_menuExtraDesc);
        mDesc.setText(textDesc);

        int textDescColor = typedArray.getColor(R.styleable.MenuRowExtra_menuExtraDescColor, 0);
        if(textDescColor > 0) {
            mDesc.setTextColor(textDescColor);
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
}

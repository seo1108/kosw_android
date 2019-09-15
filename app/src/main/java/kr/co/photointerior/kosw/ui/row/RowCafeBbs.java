package kr.co.photointerior.kosw.ui.row;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import kr.co.photointerior.kosw.R;

public class RowCafeBbs extends LinearLayout {
    Context mContext;

    public RowCafeBbs(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public RowCafeBbs(Context context) {
        super(context);
        mContext = context;
        init(context);
    }
    private void init(Context context){
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.row_cafe_bbs, this, true);
    }
}

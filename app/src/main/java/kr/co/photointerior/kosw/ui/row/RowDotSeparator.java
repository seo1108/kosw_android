package kr.co.photointerior.kosw.ui.row;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import kr.co.photointerior.kosw.R;

public class RowDotSeparator extends LinearLayout {
    Context mContext;

    public RowDotSeparator(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public RowDotSeparator(Context context) {
        super(context);
        mContext = context;
        init(context);
    }
    private void init(Context context){
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.row_dot_separator, this, true);
    }
}


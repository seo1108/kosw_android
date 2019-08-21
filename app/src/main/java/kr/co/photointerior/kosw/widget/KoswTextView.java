package kr.co.photointerior.kosw.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class KoswTextView extends AppCompatTextView {

    public KoswTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public KoswTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KoswTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Spoqa_Han_Sans_Regular.ttf");
            setTypeface(tf);
        }
    }

}

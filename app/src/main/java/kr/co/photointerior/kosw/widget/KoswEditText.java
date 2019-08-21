package kr.co.photointerior.kosw.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class KoswEditText extends AppCompatEditText {

    public KoswEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public KoswEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KoswEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Spoqa_Han_Sans_Regular.ttf");
        setTypeface(tf);
    }

}

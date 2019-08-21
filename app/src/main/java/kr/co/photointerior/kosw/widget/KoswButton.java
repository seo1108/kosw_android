package kr.co.photointerior.kosw.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.Button;

public class KoswButton extends AppCompatButton {

    public KoswButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public KoswButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KoswButton(Context context) {
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

package xyz.moviseries.moviseries.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import xyz.moviseries.moviseries.R;


/**
 * Created by DARWIN on 12/4/2017.
 */

public class DMTextView extends TextView {
    private String font;
    private int pos;

    public DMTextView(Context context) {
        super(context);
    }

    public DMTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DMTextView, 0, 0);
        pos = a.getInt(R.styleable.DMTextView_dm_font, 62);
        a.recycle();
        init();
    }


    public void init() {

        String[] fonts = getResources().getStringArray(R.array.fonts);
        font = fonts[pos];
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + font);
        setTypeface(tf, 1);

    }
}

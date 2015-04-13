package co.cheez.cheez;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.joanzapata.android.iconify.Iconify;

/**
 * Created by jiho on 4/5/15.
 */
public class IconTextView extends TextView {
    public IconTextView(Context context) {
        super(context);
        initialize();
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        Iconify.addIcons(this);
    }
}

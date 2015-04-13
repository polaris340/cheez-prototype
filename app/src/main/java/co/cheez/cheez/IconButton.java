package co.cheez.cheez;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.joanzapata.android.iconify.Iconify;

/**
 * Created by jiho on 4/5/15.
 */
public class IconButton extends Button {
    public IconButton(Context context) {
        super(context);
        initialize();
    }

    public IconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public IconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        Iconify.addIcons(this);
    }
}

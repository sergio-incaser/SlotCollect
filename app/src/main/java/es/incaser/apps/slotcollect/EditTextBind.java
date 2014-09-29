package es.incaser.apps.slotcollect;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by sergio on 29/09/14.
 */
public class EditTextBind extends EditText {
    private String fieldBind = "";

    public EditTextBind(Context context) {
        super(context);
    }

    public EditTextBind(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextBind(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EditText);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i)
        {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.EditText_fieldXX: {
                    if (context.isRestricted()) {
                        throw new IllegalStateException("The " + getClass().getCanonicalName() + ":required attribute cannot "
                                + "be used within a restricted context");
                    }

                    String defaultValue = "";
                    final String field = a.getString(attr);
                    //DO SOMETHING
                }
                break;
            }
        }
        a.recycle();
    }
}
